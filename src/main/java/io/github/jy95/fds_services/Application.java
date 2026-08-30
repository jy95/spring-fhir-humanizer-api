package io.github.jy95.fds_services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

@EnableCaching
@SpringBootApplication
public class Application {

	@Bean
	CacheManager cacheManager() {
		return new ConcurrentMapCacheManager("dosageApiCacheR4", "dosageApiCacheR5");
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
