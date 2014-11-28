spring-cloud-dashboard
================================

This application provides a simple GUI to administrate Spring Cloud applications infrastructure.
It's a fork of [Spring Boot Admin](https://github.com/codecentric/spring-boot-admin) to manage applications registred in service registry (like Eureka).

At the moment it provides the following features for every registered application (most of then inherited of spring-boot-admin).

* Show name/id and version number
* Show health status
* Show details, like
 * Java System- / Environment- / Spring properties
 * JVM & memory metrics
 * Counter & gauge Metrics
 * Datasource Metrics
* Easy loggerlevel management
* Interact with JMX-Beans
* View Threaddump


## Easy Setup
Add the following dependency to your pom.xml.

```
<dependency>
	<groupId>net.vanroy</groupId>
	<artifactId>spring-cloud-dashboard</artifactId>
	<version>1.0.0-SNAPSHOT</version>
</dependency>
```

Create the Spring Cloud Dashboard with only one single Annotation.
```
@Configuration
@EnableAutoConfiguration
@EnableCloudDashboard
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
```

#### Screenshot:

[](url "title") 
<img src="https://raw.githubusercontent.com/vanroy/spring-cloud-dashboard/master/screenshot.png">
