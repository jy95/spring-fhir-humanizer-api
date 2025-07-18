package io.github.jy95.fds_services.config;

import io.github.jy95.fds_services.generator.DosageApiKeyGenerator;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheKeyConfig {

    @Bean("dosageApiKeyGenerator")
    public KeyGenerator dosageApiKeyGenerator() {
        return new DosageApiKeyGenerator();
    }
}