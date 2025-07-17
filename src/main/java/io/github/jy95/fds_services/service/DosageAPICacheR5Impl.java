package io.github.jy95.fds_services.service;

import io.github.jy95.fds.r5.DosageAPIR5;
import io.github.jy95.fds.r5.config.FDSConfigR5;
import io.github.jy95.fds_services.dto.ParamsDto;
import jakarta.annotation.PostConstruct;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class DosageAPICacheR5Impl implements DosageAPICache<ParamsDto, DosageAPIR5> {

    @Override
    @Cacheable(cacheNames = "dosageApiCacheR5", keyGenerator = "dosageApiKeyGenerator")
    public DosageAPIR5 getCreator(Locale locale, ParamsDto params) {
        return new DosageAPIR5(
                FDSConfigR5
                        .builder()
                        .locale(locale)
                        .displayOrder(params.getDisplayOrders())
                        .displaySeparator(params.getDisplaySeparator())
                        .build()
        );
    }

    @Override
    public List<Object> getKey(ParamsDto paramsDto) {
        return List.of(
                paramsDto.getDisplaySeparator(),
                paramsDto.getDisplayOrders()
        );
    }

    @PostConstruct
    public void preloadDefaults() {

        List<Locale> defaultLocales = List.of(
                Locale.ENGLISH,
                Locale.FRENCH,
                Locale.GERMAN,
                Locale.forLanguageTag("nl")
        );

        ParamsDto defaultParams = ParamsDto.builder().build();

        for (Locale locale : defaultLocales) {
            getCreator(locale, defaultParams);
        }
    }
}
