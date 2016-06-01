package com.github.vanroy.cloud.dashboard.config;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Cloud Dashboard Caching configuration
 * @author Julien Roy
 */
@Configuration
@EnableCaching(mode = AdviceMode.ASPECTJ)
public class CacheConfig {

	@Bean
	public CacheManagerCustomizer<GuavaCacheManager> allCacheManagerCustomizer() {
		return cacheManager -> {
			cacheManager.setCacheBuilder(
				CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(10, TimeUnit.MINUTES)
			);
		};
	}
}
