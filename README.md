spring-cloud-dashboard
================================

This application provides a simple GUI to administrate Spring Cloud applications infrastructure.
It's a fork of [Spring Boot Admin](https://github.com/codecentric/spring-boot-admin) to manage applications registered in service registry (like Eureka).

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

Any specific spring cloud information are also available in dashboard

* Application registry history ( from Eureka Server )
* Circuit Breaker dashboard ( from Hystrix or Turbine )

## Easy Setup
Add the following dependency to your pom.xml after you have build this project locally.

```
<dependency>
	<groupId>com.github.vanroy</groupId>
	<artifactId>spring-cloud-dashboard</artifactId>
	<version>1.0.0.RELEASE</version>
</dependency>
```

Create the Spring Cloud Dashboard with only one single Annotation.
```
@SpringBootApplication
@EnableEurekaServer
@EnableDiscoveryClient
@EnableCloudDashboard
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
```

#### Samples:

Samples are available in this repository : https://github.com/VanRoy/spring-cloud-dashboard-samples

#### Screenshot:

##### Application registry:
[](url "title") 
<img src="https://raw.githubusercontent.com/vanroy/spring-cloud-dashboard/master/screenshot.png">

##### Circuit breaker:
[](url "title")
<img src="https://raw.githubusercontent.com/vanroy/spring-cloud-dashboard/master/screenshot-circuit-breaker.png">

##### Registry history:
[](url "title")
<img src="https://raw.githubusercontent.com/vanroy/spring-cloud-dashboard/master/screenshot-history.png">
