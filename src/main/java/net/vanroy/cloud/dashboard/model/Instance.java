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

/**
 * The domain model for all application at the spring cloud dashboard application.
 * @author Julien Roy
 */
public class Instance {

	private final String id;
	private final String url;
	private final String name;
    private final String status;

    public Instance(String url, String name, String id, String status) {
        this.url = url.replaceFirst("/+$", "");
		this.name = name;
		this.id = id;
        this.status = status;
	}

	public String getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public String getName() {
		return name;
	}

    public String getStatus() {
        return status;
    }
}
