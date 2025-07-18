package io.github.jy95.fds_services.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import io.github.jy95.fds_services.dto.DosageResponseDto;
import io.github.jy95.fds_services.dto.DosageRequestDto;
import io.github.jy95.fds_services.service.DosageAPICacheR4Impl;
import io.github.jy95.fds_services.utility.DosageConversionSupport;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import org.hl7.fhir.r4.model.MedicationRequest;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/r4/dosage")
@Tag(
        name = "R4 Dosage",
        description = "APIs for FHIR R4 Dosage",
        externalDocs = @ExternalDocumentation(
                url = "https://www.hl7.org/fhir/R4/dosage.html",
                description = "HL7 Definition"
        )
)
public class R4DosageController implements DosageConversionSupport {

    /**
     * The shared cache, for reusable requests
     */
    @Autowired
    private DosageAPICacheR4Impl cache;

    /**
     * The FHIR context for R4.
     */
    private static final IParser JSON_PARSER = FhirContext.forR4().newJsonParser();

    @PostMapping(
            value = "/asHumanReadableText",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Turn dosage(s) into text",
            description = "Convert dosage(s) into human readable-text into requested languages"
    )
    public Mono<DosageResponseDto> asHumanReadableText(
            @Valid @RequestBody Mono<DosageRequestDto> requestDtoMono
    ) {
        return requestDtoMono
                .flatMap(requestDto -> {
                    // Extract parameters
                    var params = requestDto.getParams();

                    // Extract requested languages
                    var locales = params.getLocales();

                    // Extract dosages
                    var dosages = validateAndExtractDosages(
                            requestDto.getDosages(),
                            JSON_PARSER,
                            MedicationRequest.class,
                            r -> ((MedicationRequest) r).getDosageInstruction(),
                            params.getOutputFormat()
                    );

                    // Get resolvers
                    var resolvers = locales
                            .stream()
                            .distinct()
                            .map(locale -> Map.entry(locale, cache.getCreator(locale, params)))
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue
                            ));

                    return translateDosagesWithIssues(
                            dosages,
                            locales,
                            resolvers
                    );
                });
    }
}
