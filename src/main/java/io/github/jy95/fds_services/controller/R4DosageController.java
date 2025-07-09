package io.github.jy95.fds_services.controller;

import io.github.jy95.fds.r4.DosageAPIR4;
import io.github.jy95.fds_services.dto.DosageRequestDto;
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
public class R4DosageController implements DosageController {

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
            @Valid @RequestBody DosageRequestDto requestDto,
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
}
