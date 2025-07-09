package io.github.jy95.fds_services.controller;

import io.github.jy95.fds.r4.DosageAPIR4;
import io.github.jy95.fds_services.dto.R4DosageRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import io.github.jy95.fds.r4.config.FDSConfigR4;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/r4")
@Tag(name = "R4", description = "APIs for FHIR R4")
public class R4DosageController {

    @PostMapping(
            value = "/asHumanReadableText",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Turn dosage(s) into text",
            description = "Convert dosage(s) into human readable-text into requested languages"
    )
    public String asHumanReadableText(
            @Valid @RequestBody R4DosageRequestDto requestDto,
            @RequestHeader(name = HttpHeaders.ACCEPT_LANGUAGE, required = false) String acceptLanguage
    ) {
        // Extract requested locales
        List<Locale> locales = parseAcceptLanguageHeader(acceptLanguage);

        // Extract parameters
        var params = requestDto.getParams();

        // Create a map of resolver per locale
        Map<Locale, DosageAPIR4> resolvers = locales
                .stream()
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

        // TODO

        return  "";
    }

    /**
     * Parse request headers into locales
     * @param header The ACCEPT_LANGUAGE header
     * @return List of locales parsed
     */
    private List<Locale> parseAcceptLanguageHeader(String header) {
        if (header == null || header.isBlank()) {
            return List.of(Locale.ENGLISH); // fallback to default
        }

        return Stream
                .of(header.split(","))
                .map(lang -> lang.split(";")[0].trim()) // ignore q values
                .distinct()
                .map(Locale::forLanguageTag)
                .toList();
    }

}
