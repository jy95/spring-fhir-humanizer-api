package io.github.jy95.fds_services.utility;

import io.github.jy95.fds.common.types.DosageAPI;
import io.vavr.Tuple2;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generic cache for (Locale, ParamSubset) -> Value objects, using a key extractor to reduce Param.
 *
 * @param <P> Full parameter type
 * @param <V> Value type to cache
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DosageAPICache<P, V> {

    // The cache
    @Builder.Default
    private Map<Tuple2<Locale, ?>, V> cache = new ConcurrentHashMap<>();

    // To specific a custom key for storage
    @Builder.Default
    private Function<P, List<Object>> keyExtractor = (List::of);

    public V getOrCreate(Locale locale, P params, BiFunction<Locale, P, V> creator) {
        var key = keyExtractor.apply(params);
        var compositeKey = new Tuple2<>(locale, key);
        return cache.computeIfAbsent(compositeKey, k -> creator.apply(locale, params));
    }

    public Map<Locale, V> getResolversForLocalesWithParam(
            List<Locale> locales,
            P param,
            BiFunction<Locale, P, V> resolverFactory
    ) {
        return locales
                .stream()
                .distinct()
                .collect(Collectors.toMap(
                        Function.identity(),
                        locale -> getOrCreate(locale, param, resolverFactory)
                ));
    }
}
