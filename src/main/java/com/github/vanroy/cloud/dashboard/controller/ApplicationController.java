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
package com.github.vanroy.cloud.dashboard.controller;

import com.google.common.collect.Lists;
import com.github.vanroy.cloud.dashboard.model.Application;
import com.github.vanroy.cloud.dashboard.model.Instance;
import com.github.vanroy.cloud.dashboard.repository.ApplicationRepository;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;

/**
 * REST controller for retrieve applications information.
 * @author Julien Roy
 */
@RestController
public class ApplicationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);

	@Autowired
	private ApplicationRepository repository;

    @Autowired
    private HttpClient httpClient;

    /**
   	 * List all applications with name
   	 *
   	 * @return List.
   	 */
   	@RequestMapping(value = "/api/applications", method = RequestMethod.GET)
   	public Collection<Application> applications(@RequestParam(value = "name", required = false) String name) {
   		LOGGER.debug("Deliver applications with name= {}", name);
   		if (name == null || name.isEmpty()) {
   			return repository.findAll();
   		} else {
   			return Lists.newArrayList(repository.findByName(name));
   		}
   	}

	/**
	 * Get a single application.
	 * 
	 * @param id The application identifier.
	 * @return The application.
	 */
	@RequestMapping(value = "/api/instance/{id}", method = RequestMethod.GET)
	public ResponseEntity<Instance> instance(@PathVariable String id) {
		LOGGER.debug("Deliver application with ID '{}'", id);
		Instance instance = repository.findInstance(id);
		if (instance != null) {
			return new ResponseEntity<Instance>(instance, HttpStatus.OK);
		} else {
			return new ResponseEntity<Instance>(HttpStatus.NOT_FOUND);
		}
	}

    /**
     * Proxy call instance with specific management method
     * @param id id of instance
     * @param method Management method name
     * @return Return directly from instance
     */
    @RequestMapping(value = "/api/instance/{id}/{method}", method = RequestMethod.GET)
   	public ResponseEntity<String> proxy(@PathVariable String id, @PathVariable String method) {

        String managementUrl = repository.getInstanceManagementUrl(id);
        if(managementUrl == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            HttpResponse response = httpClient.execute(new HttpGet(managementUrl+"/"+method));
            return ResponseEntity.status(response.getStatusLine().getStatusCode()).body(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            LOGGER.debug("Cannot proxy metrics to instance", e);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
   	}

    /**
     * Proxy call instance with specific management method  (with Http POST method)
     * @param id id of instance
     * @param method Management method name
     * @return Return directly from instance
     */
    @RequestMapping(value = "/api/instance/{id}/{method}", method = RequestMethod.POST)
   	public ResponseEntity<String> proxyPost(@PathVariable String id, @PathVariable String method, @RequestBody String body) {

        String managementUrl = repository.getInstanceManagementUrl(id);
        if(managementUrl == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            HttpPost post = new HttpPost(managementUrl+"/"+method);
            post.setEntity(new StringEntity(body));
            HttpResponse response = httpClient.execute(post);
            return ResponseEntity.status(response.getStatusLine().getStatusCode()).body(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            LOGGER.debug("Cannot proxy metrics to instance", e);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
   	}
}
