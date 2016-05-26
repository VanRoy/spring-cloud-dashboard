package com.github.vanroy.cloud.dashboard.config;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.cloud.dashboard.http")
public class HttpClientProperties {
	private String username;
	private String password;
	private Integer maxConnection;
	private Integer connectTimeout;
	private Integer socketTimeout;
	private Integer requestTimeout;

	@PostConstruct
	public void init() {
		if (maxConnection == null)
			maxConnection = 100;
		if (connectTimeout == null)
			connectTimeout = 1000;
		if (socketTimeout == null)
			socketTimeout = 2000;
		if (requestTimeout == null)
			requestTimeout = 1000;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public Integer getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(Integer socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public Integer getRequestTimeout() {
		return requestTimeout;
	}

	public void setRequestTimeout(Integer requestTimeout) {
		this.requestTimeout = requestTimeout;
	}
}
