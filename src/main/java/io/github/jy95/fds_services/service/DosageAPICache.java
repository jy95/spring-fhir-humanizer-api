package io.github.jy95.fds_services.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface DosageAPICache<P, V> {

    /**
     * Provides the cache key extractor for parameters.
     */
    default List<Object> getKey(P obj) {
        return List.of(obj);
    }

    /**
     * Constructs a unique cache key from locale and key-extracted param subset.
     */
    default String buildKey(Locale locale, P params) {
        var keyComponents = getKey(params);
        var subKey = keyComponents
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining("_"));
        return String
                .join(
                        "::",
                        locale.toLanguageTag(),
                        subKey
                );
    }

    /**
     * Provides the fallback creator logic in case of cache miss.
     */
    V getCreator(Locale locale, P params);

    /**
     * Default logic to fetch values for multiple locales.
     */
    default Map<Locale, V> getResolversForLocalesWithParam(List<Locale> locales, P param) {
        return locales
                .stream()
                .distinct()
                .collect(Collectors.toMap(
                        Function.identity(),
                        locale -> getCreator(locale, param)
                ));
    }

}
