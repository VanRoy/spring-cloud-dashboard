/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.vanroy.cloud.dashboard.config;

import com.github.vanroy.cloud.dashboard.repository.ApplicationRepository;
import com.github.vanroy.cloud.dashboard.repository.eureka.LocaleEurekaRepository;
import com.github.vanroy.cloud.dashboard.repository.eureka.RemoteEurekaRepository;
import com.github.vanroy.cloud.dashboard.stream.CircuitBreakerStreamServlet;

import java.util.Base64;
import java.util.Collections;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Spring Cloud Dashboard WebApp configuration
 * @author Julien Roy
 */
@Configuration
@ComponentScan("com.github.vanroy.cloud.dashboard")
public class CloudDashboardConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private HttpClientProperties httpClientProperties;
        
    @Bean
    @ConditionalOnClass(name="com.netflix.eureka.PeerAwareInstanceRegistry")
    @ConditionalOnMissingBean(ApplicationRepository.class)
    public ApplicationRepository eurekaRepository() {
        return new LocaleEurekaRepository();
    }

    @Bean
    @ConditionalOnMissingClass(name="com.netflix.eureka.PeerAwareInstanceRegistry")
    @ConditionalOnClass(name="com.netflix.discovery.DiscoveryClient")
    @ConditionalOnMissingBean(ApplicationRepository.class)
    public ApplicationRepository remoteEurekaRepository() {
        return new RemoteEurekaRepository();
    }

    @Bean
    @Autowired
    public ServletRegistrationBean circuitBreakerStreamServlet(ApplicationRepository repository) {
        return new ServletRegistrationBean(new CircuitBreakerStreamServlet(HttpClient(), repository), "/circuitBreaker.stream");
    }

    @Bean
    public HttpClient HttpClient() {

        HttpClientBuilder builder = HttpClients.custom()
            .setMaxConnTotal(httpClientProperties.getMaxConnection())
            .setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(httpClientProperties.getSocketTimeout()).build())
            .setDefaultRequestConfig(RequestConfig.custom()
               .setSocketTimeout(httpClientProperties.getSocketTimeout())
               .setConnectTimeout(httpClientProperties.getConnectTimeout())
               .setConnectionRequestTimeout(httpClientProperties.getRequestTimeout())
            .build());

        if (httpClientProperties.getUsername() != null && httpClientProperties.getPassword() != null) {
            builder.setDefaultHeaders(Collections.singletonList(
                createBasicAuthHeader(httpClientProperties.getUsername(),httpClientProperties.getPassword())
            ));
        }

        return builder.build();
    }

    private static Header createBasicAuthHeader(String username, String password) {
        String basicAuth = new String(Base64.getEncoder().encode((username + ":" + password).getBytes()));
        return new BasicHeader("Authorization", "Basic " + basicAuth);
    }
}
