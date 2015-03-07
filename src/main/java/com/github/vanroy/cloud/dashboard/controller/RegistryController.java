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

import com.google.common.collect.ImmutableMap;
import com.github.vanroy.cloud.dashboard.repository.RegistryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for retrieve registry information.
 * @author Julien Roy
 */
@RestController
public class RegistryController {

	private static final Logger LOGGER = LoggerFactory.getLogger(RegistryController.class);

	@Autowired(required = false)
	private RegistryRepository repository;
    
    /**
     * Return instance history registration/cancellation
     * @return List of registered/cancelled instances
     */
    @RequestMapping(value = "/api/registry/history", method = RequestMethod.GET)
    public ResponseEntity instancesHistory() {

        if(repository == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(ImmutableMap.of(
                "lastRegistered", repository.getRegisteredInstanceHistory(),
                "lastCancelled", repository.getCanceledInstanceHistory()
        ));
    }
}
