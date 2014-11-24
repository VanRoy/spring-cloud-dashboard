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
package net.vanroy.cloud.dashboard.model;

import java.util.Set;

/**
 * The domain model for all application at the spring cloud dashboard application.
 */
public class Application {

	private final String name;
	private final Set<Instance> instances;

	public Application(String name, Set<Instance> instances) {
		this.name = name;
		this.instances = instances;
	}

	public String getName() {
		return name;
	}

	public Set<Instance> getInstances() {
		return instances;
	}
}
