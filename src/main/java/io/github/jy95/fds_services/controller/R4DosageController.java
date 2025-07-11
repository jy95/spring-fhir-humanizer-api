package io.github.jy95.fds_services.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import io.github.jy95.fds.r4.DosageAPIR4;
import io.github.jy95.fds_services.dto.DosageRequestDto;
import io.github.jy95.fds_services.dto.LocalizedStringDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import io.github.jy95.fds.r4.config.FDSConfigR4;
import org.hl7.fhir.r4.model.MedicationRequest;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/r4")
@Tag(name = "R4", description = "APIs for FHIR R4")
public class R4DosageController implements DosageController {

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
    public Mono<String> asHumanReadableText(
            @Valid @RequestBody Mono<DosageRequestDto> requestDtoMono,
            @RequestHeader(name = HttpHeaders.ACCEPT_LANGUAGE, required = false) String acceptLanguage
    ) {
        // Extract requested languages
        List<Locale> locales = parseAcceptLanguageHeader(acceptLanguage);

        return requestDtoMono.map(
                requestDto -> {

                    // Extract parameters
                    var params = requestDto.getParams();

                    // Extract dosages
                    var dosageList = validateAndExtractDosages(
                            requestDto.getDosages(),
                            JSON_PARSER,
                            MedicationRequest.class,
                            r -> ((MedicationRequest) r).getDosageInstruction()
                    );

                    // Create resolvers
                    Map<Locale, DosageAPIR4> resolvers = locales.stream()
                            .collect(Collectors.toMap(
                                    lng -> lng,
                                    lng -> new DosageAPIR4(
                                            FDSConfigR4.builder()
                                                    .locale(lng)
                                                    .displayOrder(params.getDisplayOrders())
                                                    .displaySeparator(params.getDisplaySeparator())
                                                    .build()
                                    )
                            ));

                    // Depending on what the user asked, we need to have
                    // List<List<Dosage1, Dosage2, ...>> or List<List<Dosage1>, List<Dosage2>, ...>
                    var dosages = switch (params.getOutputFormat()) {
                        case SUMMARY -> List.of(dosageList);
                        case DETAILED -> dosageList
                                .stream()
                                .map(List::of)
                                .toList();
                    };

                    var results = dosages
                            .stream()
                            .map(listOfDosages -> {

                                var translations = LocalizedStringDto.builder();

                                for(var locale : locales) {
                                    var result = resolvers
                                            .get(locale)
                                            .asHumanReadableText(listOfDosages)
                                            .get();
                                    translations.entry(
                                            locale.getLanguage(),
                                            result
                                    );
                                }

                                return translations.build();
                            })
                            .toList();

                    return results;
                }
        );

    }
}
