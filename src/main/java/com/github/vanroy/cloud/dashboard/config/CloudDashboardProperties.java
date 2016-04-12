package com.github.vanroy.cloud.dashboard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Global dashboard properties
 * @author Julien Roy
 */
@Component
@ConfigurationProperties("spring.cloud.dashboard")
public class CloudDashboardProperties {

	// Global refresh timeout in milliseconds
	private int refreshTimeout = 30000;

	public int getRefreshTimeout() {
		return refreshTimeout;
	}

	public void setRefreshTimeout(int refreshTimeout) {
		this.refreshTimeout = refreshTimeout;
	}
}
