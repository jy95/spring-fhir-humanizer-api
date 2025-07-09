package io.github.jy95.fds_services.controller;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

// Shared code between controllers
public interface DosageController {

    /**
     * Parse request headers into locales
     * @param header The ACCEPT_LANGUAGE header
     * @return List of locales parsed
     */
    default List<Locale> parseAcceptLanguageHeader(String header) {
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
