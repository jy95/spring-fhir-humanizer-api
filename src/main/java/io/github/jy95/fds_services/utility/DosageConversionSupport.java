package io.github.jy95.fds_services.utility;

import ca.uhn.fhir.parser.IParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.jy95.fds.common.types.DosageAPI;
import io.github.jy95.fds_services.dto.DosageResponseDto;
import io.github.jy95.fds_services.dto.LocalizedDto;
import io.github.jy95.fds_services.enum_.OutputFormat;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.http.ProblemDetail;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.vavr.control.Either;

import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Support interface for converting FHIR Dosage elements into human-readable text across multiple locales.
 */
public interface DosageConversionSupport {

    ObjectMapper MAPPER = new ObjectMapper();
    URI INTERNAL_SERVER_ERROR = URI.create("urn:problem-type:belgif:internalServerError");

    /**
     * Validates and extracts Dosage elements from a JSON node, using HAPI FHIR parsing.
     *
     * @param dosageArray   JSON array representing the dosages.
     * @param parser        HAPI FHIR parser.
     * @param wrapperClass  FHIR resource class that wraps the dosageInstruction (e.g. MedicationRequest).
     * @param extractFunction Function to extract dosage instructions from the parsed resource.
     * @param outputFormat  Determines if output is grouped (SUMMARY) or split (DETAILED).
     * @param <T>           Type of the dosage element.
     * @return List of dosage lists, grouped based on outputFormat.
     * @throws IllegalArgumentException if the input is invalid or parsing fails.
     */
    default <T extends IBase> List<List<T>> validateAndExtractDosages(
            List<JsonNode> dosageArray,
            IParser parser,
            Class<? extends IBaseResource> wrapperClass,
            Function<IBaseResource, List<T>> extractFunction,
            OutputFormat outputFormat
    ) {

        try {
            ObjectNode workingObj = MAPPER.createObjectNode();
            var workingArr = MAPPER.createArrayNode();
            workingArr.addAll(dosageArray);
            workingObj.set("dosageInstruction", workingArr);
            workingObj.put("resourceType", "MedicationRequest");

            String json = MAPPER.writeValueAsString(workingObj);
            IBaseResource resource = parser.parseResource(wrapperClass, json);
            var dosageList = extractFunction.apply(resource);

            return switch (outputFormat) {
                case SUMMARY -> List.of(dosageList);
                case DETAILED -> dosageList.stream().map(List::of).toList();
            };
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * Creates a resolver map for each requested locale.
     *
     * @param locales         List of locales to support.
     * @param resolverFactory Factory function to create a resolver for each locale.
     * @param <D>             Dosage item type.
     * @param <A>             API type implementing DosageAPI.
     * @return Map of locales to their corresponding resolver.
     */
    default <D, A extends DosageAPI<?, D>> Map<Locale, A> createResolversForLocales(
            List<Locale> locales,
            Function<Locale, A> resolverFactory
    ) {
        return locales.stream().collect(Collectors.toMap(Function.identity(), resolverFactory));
    }

    /**
     * Translates all dosage inputs to human-readable text per locale and returns both translations and issues.
     *
     * @param dosages  List of dosage groups (each group is a list of dosage items).
     * @param locales  Locales to generate translations for.
     * @param resolvers Map of resolvers per locale.
     * @param <D>       Dosage type.
     * @return A Mono emitting a DosageResponseDto containing translations and any issues.
     */
    default <D> Mono<DosageResponseDto> translateDosagesWithIssues(
            List<List<D>> dosages,
            List<Locale> locales,
            Map<Locale, ? extends DosageAPI<?, D>> resolvers
    ) {
        return Flux.fromIterable(dosages)
                .flatMap(dosageList ->
                        translateSingleDosageListForLocales(dosageList, locales, resolvers)
                                .map(this::partitionTranslationResults)
                                .map(partition -> new TranslationResult(
                                        LocalizedDto.builder()
                                                .translations(partition.translations())
                                                .build(),
                                        partition.issues()
                                ))
                )
                .collectList()
                .map(results -> {
                    List<LocalizedDto> items = results.stream()
                            .map(TranslationResult::dto)
                            .toList();

                    List<ProblemDetail> allIssues = results.stream()
                            .flatMap(r -> r.issues().stream())
                            .toList();

                    return DosageResponseDto.builder()
                            .items(items)
                            .issues(allIssues)
                            .build();
                });
    }

    /**
     * Helper: Translates a single group of dosage items across all requested locales.
     */
    private <D> Mono<List<Either<ProblemDetail, Map.Entry<String, String>>>> translateSingleDosageListForLocales(
            List<D> dosageList,
            List<Locale> locales,
            Map<Locale, ? extends DosageAPI<?, D>> resolvers
    ) {
        return Flux
                .fromIterable(locales)
                .flatMap(locale -> {
                    var resolver = resolvers.get(locale);
                    return Mono
                            .fromFuture(resolver.asHumanReadableText(dosageList))
                            .map(result -> Either.<ProblemDetail, Map.Entry<String, String>>right(
                                    new AbstractMap.SimpleEntry<>(locale.getLanguage(), result)))
                            .onErrorResume(e -> Mono.just(Either.left(createProblemDetail(e, locale))));
                })
                .collectList();
    }

    /**
     * Helper: Partitions translation results into successful translations and errors.
     */
    private TranslationPartition partitionTranslationResults(
            List<Either<ProblemDetail, Map.Entry<String, String>>> results
    ) {
        Map<String, String> translations = results
                .stream()
                .filter(Either::isRight)
                .map(Either::get)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b));

        List<ProblemDetail> issues = results
                .stream()
                .filter(Either::isLeft)
                .map(Either::getLeft)
                .toList();

        return new TranslationPartition(translations, issues);
    }

    /**
     * Builds a ProblemDetail from an exception and locale.
     *
     * @param e      The exception to convert.
     * @param locale The locale associated with the error.
     * @return ProblemDetail object for error reporting.
     */
    default ProblemDetail createProblemDetail(Throwable e, Locale locale) {
        ProblemDetail problem = ProblemDetail.forStatus(500);
        problem.setType(INTERNAL_SERVER_ERROR);
        problem.setDetail(e.getMessage());
        problem.setProperty("locale", locale.toLanguageTag());
        return problem;
    }

    /**
     * Internal structure holding a partitioned translation result.
     *
     * @param translations Map of locale language codes to translations.
     * @param issues       List of translation issues as ProblemDetail objects.
     */
    record TranslationPartition(
            Map<String, String> translations,
            List<ProblemDetail> issues
    ) {}

    /**
     * Internal structure combining the DTO with issues for one dosage group.
     *
     * @param dto    Localized DTO containing translations.
     * @param issues List of translation issues as ProblemDetail objects.
     */
    record TranslationResult(
            LocalizedDto dto,
            List<ProblemDetail> issues
    ) {}
}