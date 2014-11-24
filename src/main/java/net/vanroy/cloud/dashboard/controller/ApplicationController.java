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
package net.vanroy.cloud.dashboard.controller;

import net.vanroy.cloud.dashboard.model.Application;
import net.vanroy.cloud.dashboard.repository.ApplicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * REST controller for retrieve applications information.
 */
@RestController
public class ApplicationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);

	@Autowired
	private ApplicationRepository repository;

	/**
	 * Get a single application.
	 * 
	 * @param id The application identifier.
	 * @return The application.
	 */
	@RequestMapping(value = "/api/application/{id}", method = RequestMethod.GET)
	public ResponseEntity<Application> get(@PathVariable String id) {
		LOGGER.debug("Deliver application with ID '{}'", id);
		Application application = repository.find(id);
		if (application != null) {
			return new ResponseEntity<Application>(application, HttpStatus.OK);
		} else {
			return new ResponseEntity<Application>(application, HttpStatus.NOT_FOUND);
		}
	}

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
			return repository.findByName(name);
		}
	}
}
