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

public class ApplicationControllerTest {

	private ApplicationController controller;
	                      /*
	@Before
	public void setup() {
		controller = new RegistryController(new ApplicationRegistry(new SimpleApplicationStore(),
				new HashingApplicationUrlIdGenerator()));
	}

	@Test
	public void get() {
		Application app = new Application("http://localhost", "FOO");
		app = controller.register(app).getBody();

		ResponseEntity<Application> response = controller.get(app.getId());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("http://localhost", response.getBody().getUrl());
		assertEquals("FOO", response.getBody().getName());
	}

	@Test
	public void get_notFound() {
		controller.register(new Application("http://localhost", "FOO"));

		ResponseEntity<?> response = controller.get("unknown");
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	public void applications() {
		Application app = new Application("http://localhost", "FOO");
		app = controller.register(app).getBody();

		Collection<Application> applications = controller.applications(null);
		assertEquals(1, applications.size());
		assertTrue(applications.contains(app));
	}

	@Test
	public void applicationsByName() {
		Application app = new Application("http://localhost:2", "FOO");
		app = controller.register(app).getBody();
		Application app2 = new Application("http://localhost:1", "FOO");
		app2 = controller.register(app2).getBody();
		Application app3 = new Application("http://localhost:3", "BAR");
		controller.register(app3).getBody();

		Collection<Application> applications = controller.applications("FOO");
		assertEquals(2, applications.size());
		assertTrue(applications.contains(app));
		assertTrue(applications.contains(app2));
		assertFalse(applications.contains(app3));
	}
	*/
}
