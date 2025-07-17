package io.github.jy95.fds_services.generator;

import io.github.jy95.fds_services.dto.ParamsDto;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.stream.Collectors;

public class DosageApiKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {

        Locale locale = (Locale) params[0];
        ParamsDto dto = (ParamsDto) params[1];

        var order = dto
                .getDisplayOrders()
                .stream()
                .map(Enum::toString)
                .collect(Collectors.joining("_"));

        return String.join(
                "::",
                locale.toLanguageTag(),
                dto.getDisplaySeparator(),
                order
        );
    }
}
