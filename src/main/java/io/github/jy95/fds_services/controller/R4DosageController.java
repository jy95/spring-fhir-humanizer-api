package io.github.jy95.fds_services.controller;

import io.github.jy95.fds.r4.DosageAPIR4;
import io.github.jy95.fds_services.dto.DosageRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import io.github.jy95.fds.r4.config.FDSConfigR4;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/r4")
@Tag(name = "R4", description = "APIs for FHIR R4")
public class R4DosageController implements DosageController {

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

                    // TODO: perform the actual conversion using resolvers and return a result
                    return "";
                }
        );

    }
}
