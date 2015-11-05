package com.github.vanroy.cloud.dashboard.repository.aws;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Beanstalk configuration properties
 * @author Julien Roy
 */
@Component
@ConfigurationProperties("spring.cloud.dashboard.beanstalk")
public class BeanstalkProperties {

    private String endpoint = "https://elasticbeanstalk.us-east-1.amazonaws.com";

    private String environment;
    private Map<String, String> environmentTags;

    private CloudFormation cloudFormation = new CloudFormation();

    private Instance instances = new Instance();

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public Instance getInstances() {
        return instances;
    }

    public void setInstances(Instance instances) {
        this.instances = instances;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Map<String, String> getEnvironmentTags() {
        return environmentTags;
    }

    public void setEnvironmentTags(Map<String, String> environmentTags) {
        this.environmentTags = environmentTags;
    }

    public CloudFormation getCloudFormation() {
        return cloudFormation;
    }

    public void setCloudFormation(CloudFormation cloudFormation) {
        this.cloudFormation = cloudFormation;
    }

    public static class Instance {

        private Management management = new Management();
        private String endpoint = "https://ec2.us-east-1.amazonaws.com";

        public Management getManagement() {
            return management;
        }

        public void setManagement(Management management) {
            this.management = management;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }
    }

    public static class Management {

        private String scheme = "http";
        private Integer port = 80;
        private String path = "/";

        public String getScheme() {
            return scheme;
        }

        public void setScheme(String scheme) {
            this.scheme = scheme;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public static class CloudFormation {

        private String endpoint = "https://cloudformation.us-east-1.amazonaws.com";

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }
    }

}
