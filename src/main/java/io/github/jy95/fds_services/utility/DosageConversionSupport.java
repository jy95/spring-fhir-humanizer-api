package io.github.jy95.fds_services.utility;

import ca.uhn.fhir.parser.IParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.jy95.fds.common.types.DosageAPI;
import io.github.jy95.fds_services.enum_.OutputFormat;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseResource;

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

}
