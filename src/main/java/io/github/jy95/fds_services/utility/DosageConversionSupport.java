package io.github.jy95.fds_services.utility;

import ca.uhn.fhir.parser.IParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.jy95.fds.common.types.DosageAPI;
import io.github.jy95.fds_services.dto.DosageReponseDto;
import io.github.jy95.fds_services.dto.LocalizedDto;
import io.github.jy95.fds_services.enum_.OutputFormat;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.http.ProblemDetail;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.vavr.control.Either;
import reactor.util.function.Tuple2;

import java.net.URI;
import java.util.AbstractMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface DosageConversionSupport {

    /**
     * The ObjectMapper for JSON parsing.
     */
    ObjectMapper MAPPER = new ObjectMapper();

    /**
     * For internal error
     */
    URI INTERNAL_SERVER_ERROR = URI.create("urn:problem-type:belgif:internalServerError");

    // From a JSON payload to FHIR Dosage object
    default <T extends IBase> List<List<T>> validateAndExtractDosages(
            JsonNode dosageArray,
            IParser parser,
            Class<? extends IBaseResource> wrapperClass,
            Function<IBaseResource, List<T>> extractFunction,
            OutputFormat outputFormat
    ) {

        if (dosageArray == null || !dosageArray.isArray()) {
            throw new IllegalArgumentException("Missing 'dosages' array");
        }

        try {
            ObjectNode workingCopy = MAPPER.createObjectNode();
            workingCopy.set("dosageInstruction", dosageArray);

            String json = MAPPER.writeValueAsString(workingCopy);
            IBaseResource resource = parser.parseResource(wrapperClass, json);

            var dosageList = extractFunction.apply(resource);

            // Depending on what the user asked, we need to have
            // List<List<Dosage1, Dosage2, ...>> or List<List<Dosage1>, List<Dosage2>, ...>
            return switch (outputFormat) {
                case SUMMARY -> List.of(dosageList);
                case DETAILED -> dosageList
                        .stream()
                        .map(List::of)
                        .toList();
            };
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    // Create resolvers for locales
    default <D, A extends DosageAPI<?, D>> Map<Locale, A> createResolversForLocales(
            List<Locale> locales,
            Function<Locale, A> resolverFactory
    ){
        return locales.stream()
                .collect(
                        Collectors.toMap(
                                Function.identity(),
                                resolverFactory
                        )
                );
    }

    /**
     * Translates multiple lists of dosages into localized human-readable strings.
     * For each dosage list, produces a {@link LocalizedDto} per language.
     * Errors encountered per locale are recorded in {@link ProblemDetail}s.
     *
     * @param dosages  A list of dosage lists to translate
     * @param locales  The requested output locales (e.g., "en", "fr")
     * @param resolvers A map from locale to a corresponding {@link DosageAPI} resolver
     * @param <D>      The FHIR dosage type (e.g., Dosage)
     * @return A Mono emitting a {@link DosageReponseDto} that includes successfully translated items and issues
     */
    default <D> Mono<DosageReponseDto> translateDosagesWithIssues(
            List<List<D>> dosages,
            List<Locale> locales,
            Map<Locale, ? extends DosageAPI<?, D>> resolvers
    ) {
        return Flux
                .fromIterable(dosages)
                .flatMap(dosageList ->
                        translateSingleDosageListForLocales(dosageList, locales, resolvers)
                                .map(this::partitionTranslationResults)
                                .map(partition -> LocalizedDto.builder().translations(partition.translations()).build())
                                .zipWith(
                                        translateSingleDosageListForLocales(dosageList, locales, resolvers)
                                                .map(this::partitionTranslationResults)
                                )
                )
                .collectList()
                .map(results -> {
                    List<LocalizedDto> items = results
                            .stream()
                            .map(Tuple2::getT1)
                            .toList();

                    List<ProblemDetail> allIssues = results
                            .stream()
                            .flatMap(t -> t.getT2().issues().stream())
                            .toList();

                    return DosageReponseDto.builder()
                            .items(items)
                            .issues(allIssues)
                            .build();
                });
    }

    /**
     * Translates a single dosage list into multiple languages using the provided resolvers.
     * Each result is returned as a {@link Either}:
     * - Right = translation success (Map.Entry of language → text)
     * - Left = translation failure (as {@link ProblemDetail})
     *
     * @param dosageList The list of dosages to translate
     * @param locales    The locales to translate to
     * @param resolvers  Map of resolvers per locale
     * @param <D>        The FHIR dosage type
     * @return A Mono containing a list of Either success or error per locale
     */
    private <D> Mono<List<Either<ProblemDetail, Map.Entry<String, String>>>> translateSingleDosageListForLocales(
            List<D> dosageList,
            List<Locale> locales,
            Map<Locale, ? extends DosageAPI<?, D>> resolvers
    ) {
        return Flux.fromIterable(locales)
                .flatMap(locale -> {
                    var resolver = resolvers.get(locale);
                    return Mono.fromFuture(resolver.asHumanReadableText(dosageList))
                            .map(result -> Either.<ProblemDetail, Map.Entry<String, String>>right(
                                    new AbstractMap.SimpleEntry<>(locale.getLanguage(), result)))
                            .onErrorResume(e -> Mono.just(Either.left(createProblemDetail(e, locale))));
                })
                .collectList();
    }

    /**
     * Partitions a list of translation results into successful translations and problems.
     *
     * @param results List of Either representing translation success or failure
     * @return A {@link TranslationPartition} with maps of translations and issues
     */
    private TranslationPartition partitionTranslationResults(
            List<Either<ProblemDetail, Map.Entry<String, String>>> results
    ) {
        Map<String, String> translations = results.stream()
                .filter(Either::isRight)
                .map(Either::get)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b));

        List<ProblemDetail> issues = results.stream()
                .filter(Either::isLeft)
                .map(Either::getLeft)
                .toList();

        return new TranslationPartition(translations, issues);
    }

    /**
     * Immutable record used to store separated translation results.
     *
     * @param translations The successfully translated locale → text entries
     * @param issues       The list of errors encountered
     */
    record TranslationPartition(
            Map<String, String> translations,
            List<ProblemDetail> issues
    ) {}

    /**
     * Creates a standardized {@link ProblemDetail} from a failed locale-specific translation.
     *
     * @param e      The exception that occurred
     * @param locale The locale for which the translation failed
     * @return A structured {@link ProblemDetail}
     */
    default ProblemDetail createProblemDetail(Throwable e, Locale locale) {
        ProblemDetail problem = ProblemDetail.forStatus(500);
        problem.setType(INTERNAL_SERVER_ERROR);
        problem.setDetail(e.getMessage());
        problem.setProperty("locale", locale.toLanguageTag());
        return problem;
    }

}
