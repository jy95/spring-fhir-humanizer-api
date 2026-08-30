package io.github.jy95.fds_services.utility;

import ca.uhn.fhir.parser.IParser;
import io.github.jy95.fds_services.enum_.OutputFormat;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseResource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.function.Function;

public interface TimingConversionSupport {

    /**
     * The JsonMapper for JSON parsing.
     */
    JsonMapper MAPPER = JsonMapper.builder().build();

    // From a JSON payload to FHIR Dosage object
    default <T extends IBase> List<List<T>> validateAndExtractTiming(
            List<JsonNode> timingArray,
            IParser parser,
            Class<? extends IBaseResource> wrapperClass,
            Function<IBaseResource, List<T>> extractFunction,
            OutputFormat outputFormat
    ) {

        var dosageList = timingArray
                .stream()
                .map(timingNode -> parseTimingNodeToDosageList(timingNode, parser, wrapperClass, extractFunction))
                .toList();

        return switch (outputFormat) {
            case SUMMARY -> List
                    .of(
                            dosageList
                                    .stream()
                                    .flatMap(List::stream)
                                    .toList()
                    );
            case DETAILED -> dosageList;
        };
    }

    private <T extends IBase> List<T> parseTimingNodeToDosageList(
            JsonNode timingNode,
            IParser parser,
            Class<? extends IBaseResource> wrapperClass,
            Function<IBaseResource, List<T>> extractFunction
    ) {
        ObjectNode medReqNode = MAPPER.createObjectNode();
        medReqNode.put("resourceType", "MedicationRequest");
        medReqNode.set(
                "dosageInstruction",
                MAPPER
                        .createArrayNode()
                        .add(
                                MAPPER
                                        .createObjectNode()
                                        .set("timing", timingNode)
                        )
        );

        try {
            String json = MAPPER.writeValueAsString(medReqNode);
            IBaseResource resource = parser.parseResource(wrapperClass, json);
            return extractFunction.apply(resource);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse timing node: " + timingNode, e);
        }
    }

}
