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
package net.vanroy.cloud.dashboard.config;

import net.vanroy.cloud.dashboard.repository.ApplicationRepository;
import net.vanroy.cloud.dashboard.repository.EurekaRepository;
import net.vanroy.cloud.dashboard.stream.CircuitBreakerStreamServlet;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Spring Cloud Dashboard WebApp configuration
 * @author Julien Roy
 */
@Configuration
@ComponentScan("net.vanroy.cloud.dashboard")
public class CloudDashboardConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private ApplicationRepository repository;

    @Bean
    @ConditionalOnClass(EurekaDiscoveryClient.class)
    @ConditionalOnMissingBean(ApplicationRepository.class)
    public EurekaRepository eurekaRepository() {
        return new EurekaRepository();
    }

    @Bean
    public ServletRegistrationBean circuitBreakerStreamServlet() {
        return new ServletRegistrationBean(new CircuitBreakerStreamServlet(HttpClient(), repository), "/circuitBreaker.stream");
    }

    @Bean
    public HttpClient HttpClient() {
        return HttpClients.custom().setMaxConnTotal(20).build();
    }
}
