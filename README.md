spring-cloud-dashboard
================================

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
