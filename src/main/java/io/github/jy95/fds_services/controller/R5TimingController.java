package io.github.jy95.fds_services.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import io.github.jy95.fds.r5.DosageAPIR5;
import io.github.jy95.fds.r5.config.FDSConfigR5;
import io.github.jy95.fds_services.dto.TimingRequestDto;
import io.github.jy95.fds_services.dto.TimingResponseDto;
import io.github.jy95.fds_services.service.DosageAPICacheR5Impl;
import io.github.jy95.fds_services.utility.DosageConversionSupport;
import io.github.jy95.fds_services.utility.TimingConversionSupport;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.hl7.fhir.r5.model.MedicationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/r5/timing")
@Tag(
        name = "R5 Timing",
        description = "APIs for FHIR R5 Timing",
        externalDocs = @ExternalDocumentation(
                url = "https://www.hl7.org/fhir/R5/datatypes.html#timing",
                description = "HL7 Definition"
        )
)
public class R5TimingController implements DosageConversionSupport, TimingConversionSupport {

    /**
     * The FHIR context for R4.
     */
    private static final IParser JSON_PARSER = FhirContext.forR5().newJsonParser();

    /**
     * The shared cache, for reusable requests
     */
    @Autowired
    private DosageAPICacheR5Impl cache;

    @PostMapping(
            value = "/asHumanReadableText",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Turn timing(s) into text",
            description = "Convert timing(s) into human readable-text into requested languages"
    )
    public Mono<TimingResponseDto> asHumanReadableText(
            @Valid @RequestBody Mono<TimingRequestDto> requestDtoMono
    ) {
        return requestDtoMono
                .flatMap(requestDto -> {
                    // Extract parameters
                    var params = requestDto.getParams();

                    // Extract requested languages
                    var locales = params.getLocales();

                    // Extract dosages
                    var dosages = validateAndExtractTiming(
                            requestDto.getTimings(),
                            JSON_PARSER,
                            MedicationRequest.class,
                            r -> ((MedicationRequest) r).getDosageInstruction(),
                            params.getOutputFormat()
                    );

                    // Create resolvers
                    var resolvers = cache
                            .getResolversForLocalesWithParam(locales, params);

                    return translateDosagesWithIssues(
                            dosages,
                            locales,
                            resolvers
                    ).map(dosageResponseDto -> TimingResponseDto
                            .builder()
                            .issues(dosageResponseDto.getIssues())
                            .items(dosageResponseDto.getItems())
                            .build()
                    );
                });
    }
}
