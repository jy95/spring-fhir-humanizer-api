package io.github.jy95.fds_services.controller;

import io.github.jy95.fds_services.dto.R4DosageRequestDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.jy95.fds.r4.config.FDSConfigR4;

@RestController
@RequestMapping("/r4")
public class R4DosageController {

    // Default config for lib
    private final FDSConfigR4 configR4 = FDSConfigR4.builder().build();

    @PostMapping(
            value = "/asHumanReadableText",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String asHumanReadableText(
            @RequestBody R4DosageRequestDto requestDto
    ) {
        // Extract parameters
        var params = requestDto.getParams();

        // Configurations for each locale
        var configsR4 = params
                .getLocales()
                .stream()
                .map(lng -> {
                    return FDSConfigR4
                            .builder()
                            .locale(lng)
                            .displayOrder(params.getDisplayOrders())
                            .displaySeparator(params.getDisplaySeparator())
                            .build();
                })
                .toList();
        return  "";
    }

}
