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
package com.github.vanroy.cloud.dashboard;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.github.vanroy.cloud.dashboard.repository.RegistryRepository;
import com.github.vanroy.cloud.dashboard.turbine.MockStreamServlet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.common.collect.ImmutableList;

import com.github.vanroy.cloud.dashboard.config.EnableCloudDashboard;
import com.github.vanroy.cloud.dashboard.model.Application;
import com.github.vanroy.cloud.dashboard.model.Instance;
import com.github.vanroy.cloud.dashboard.model.InstanceHistory;
import com.github.vanroy.cloud.dashboard.repository.ApplicationRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * 
 * Integration test to verify the correct functionality of the REST API.
 * 
 * @author Dennis Schulte
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DashboardApplicationTest.DashboardApplication.class)
@WebAppConfiguration
@IntegrationTest({ "server.port=0" })
public class DashboardApplicationTest {

	@Value("${local.server.port}")
	private int port = 0;

	@Test
	public void testGetApplications() {
		@SuppressWarnings("rawtypes")
		ResponseEntity<List> entity = new TestRestTemplate().getForEntity("http://localhost:" + port + "/api/applications", List.class);
        assertNotNull(entity.getBody());
        assertEquals(2, entity.getBody().size());
        Map<String, Object> application = (Map<String, Object>) entity.getBody().get(0);
        assertEquals("MESSAGES", application.get("name"));
        assertEquals(3, ((List)application.get("instances")).size());
		assertEquals(HttpStatus.OK, entity.getStatusCode());
	}

	@Configuration
	@EnableAutoConfiguration
    @EnableCloudDashboard
	public static class DashboardApplication {

		public static void main(String[] args) {
			SpringApplication.run(DashboardApplicationTest.DashboardApplication.class, args);
		}


        @Bean
        public ServletRegistrationBean hystrixStreamServlet() {
            return new ServletRegistrationBean(new MockStreamServlet("/hystrix.stream"), "/hystrix.stream");
        }

        @Bean
        public ApplicationRepository eurekaRepository() {
            return new ApplicationRepository() {

                @Override
                public Collection<Application> findAll() {
                    return ImmutableList.of(
                        new Application("MESSAGES",
                                ImmutableList.of(
                                new Instance("http://localhost:8761", "INSTANCE 1", "ID1", "UP"),
                                new Instance("http://localhost:8002", "INSTANCE 2", "ID2", "DOWN"),
                                new Instance("http://localhost:8003", "INSTANCE 3", "ID3", "STARTING")
                            )),
                        new Application("FRONT",
                            ImmutableList.of(
                                new Instance("http://localhost:8001", "FRONT INSTANCE 1", "FRONT ID1","OUT_OF_SERVICE"),
                                new Instance("http://localhost:8002", "FRONT INSTANCE 2", "FRONT ID2","DOWN"),
                                new Instance("http://localhost:8003", "FRONT INSTANCE 3", "FRONT ID3","UNKNOWN")
                            ))
                    );
                }

                @Override
                public Application findByName(String name) {
                    return new Application(name, null);
                }

                @Override
                public String getApplicationCircuitBreakerStreamUrl(String name) {
                    if(name.equalsIgnoreCase("MESSAGES")) {
                        return "http://localhost:8761/hystrix.stream";
                    } else {
                        return "http://localhost:8761/hystrixFail.stream";
                    }
                    //return "http://localhost:8001/turbine.stream?cluster="+name;
                }

                @Override
                public String getInstanceCircuitBreakerStreamUrl(String instanceId) {
                    if(instanceId.equalsIgnoreCase("ID1")) {
                        return "http://localhost:8761/hystrix.stream";
                    } else {
                        return "http://localhost:8761/hystrixFail.stream";
                    }
                }

                @Override
                public Instance findInstance(String id) {
                    return new Instance("http://localhost:8002", "INSTANCE "+id, id, "UP");
                }

                @Override
                public String getInstanceManagementUrl(String id) {
                    return "http://localhost:8761/";
                }
            };
        }

        @Bean
        public RegistryRepository eurekaRegistryRepository() {
            
            return new RegistryRepository() {

                @Override
                public List<InstanceHistory> getRegisteredInstanceHistory() {
                    return ImmutableList.of(
                            new InstanceHistory("INSTANCE 1", new Date()),
                            new InstanceHistory("INSTANCE 2", new Date()),
                            new InstanceHistory("INSTANCE 3", new Date())
                    );
                }

                @Override
                public List<InstanceHistory> getCanceledInstanceHistory() {
                    return ImmutableList.of(
                            new InstanceHistory("CANCELLED INSTANCE 1", new Date()),
                            new InstanceHistory("CANCELLED INSTANCE 2", new Date()),
                            new InstanceHistory("CANCELLED INSTANCE 3", new Date())
                    );
                }
            };
        }
	}

}
