package com.github.vanroy.cloud.dashboard.repository.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClient;
import com.amazonaws.services.elasticbeanstalk.model.*;
import com.github.vanroy.cloud.dashboard.model.Application;
import com.github.vanroy.cloud.dashboard.model.Instance;
import com.github.vanroy.cloud.dashboard.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Amazon Web Service Beanstalk registry implementation of application repository
 * @author Julien Roy
 */
//@Configuration
@EnableConfigurationProperties({BeanstalkProperties.class})
public class BeanstalkRepository implements ApplicationRepository {

    private final AmazonEC2Client ec2;
    private final AWSElasticBeanstalkClient beanstalk;

    @Autowired
    private BeanstalkProperties properties;

    public BeanstalkRepository() {
        AWSCredentials credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
        this.beanstalk = new AWSElasticBeanstalkClient(credentials);
        this.ec2 = new AmazonEC2Client(credentials);
    }

    @Override
    public Collection<Application> findAll() {
        return beanstalk.describeApplications()
            .getApplications().stream()
            .map(TO_APPLICATION)
            .collect(Collectors.toList());
    }

    @Override
    public Application findByName(String name) {
        return TO_APPLICATION.apply(
            beanstalk.describeApplications(
                new DescribeApplicationsRequest().withApplicationNames(name)
            ).getApplications().get(0));
    }

    @Override
    public String getApplicationCircuitBreakerStreamUrl(String name) {
        return null;
    }

    @Override
    public String getInstanceCircuitBreakerStreamUrl(String instanceId) {
        return null;
    }

    @Override
    public Instance findInstance(String id) {
        return TO_INSTANCE.apply(
            ec2.describeInstances(
                new DescribeInstancesRequest().withInstanceIds(id)
            ).getReservations().get(0).getInstances().get(0));
    }

    @Override
    public String getInstanceManagementUrl(String id) {

        BeanstalkProperties.Management management = properties.getInstances().getManagement();

        String privateDns = ec2.describeInstances(new DescribeInstancesRequest().withInstanceIds(id)).getReservations().get(0).getInstances().get(0).getPrivateDnsName();

        return  management.getScheme()+"://"+privateDns+management.getPort()+management.getPath();
    }

    protected List<Instance> getInstances(String appName) {

        DescribeEnvironmentsRequest envRequest = new DescribeEnvironmentsRequest().withApplicationName(appName);

        if( !StringUtils.isEmpty(properties.getEnvironment()) ) {
            envRequest.withEnvironmentNames(properties.getEnvironment());
        }

        List<EnvironmentDescription> environments = beanstalk.describeEnvironments(envRequest).getEnvironments();
        if( environments == null || environments.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        EnvironmentDescription environment = environments.get(0);
        EnvironmentResourceDescription resources = beanstalk.describeEnvironmentResources(new DescribeEnvironmentResourcesRequest().withEnvironmentName(environment.getEnvironmentName())).getEnvironmentResources();
        return resources.getInstances().stream().map(instance -> new Instance("", instance.getId(), instance.getId(), getInstanceStatus(instance.getId()))).collect(Collectors.toList());
    }

    protected String getInstanceStatus(String... id) {
        return TO_STATUS.apply(ec2.describeInstanceStatus(new DescribeInstanceStatusRequest().withInstanceIds(id))
            .getInstanceStatuses().get(0)
            .getInstanceState());
    }

    protected Function<ApplicationDescription, Application> TO_APPLICATION = app -> {
        if(app == null) { return null; }
        return new Application(app.getApplicationName(), getInstances(app.getApplicationName()));
    };

    protected Function<com.amazonaws.services.ec2.model.Instance, Instance> TO_INSTANCE = instance -> {
        if(instance == null) { return null; }
        return new Instance("", instance.getInstanceId(), instance.getInstanceId(), getInstanceStatus(instance.getInstanceId()));
    };

    protected Function<InstanceState, String> TO_STATUS = state -> {
        switch (state.getName()) {
            case "pending": return "STARTING";
            case "running": return "UP";
            case "shutting-down": return "OUT_OF_SERVICE";
            case "terminated": return "OUT_OF_SERVICE";
            case "stopping": return "DOWN";
            case "stopped": return "DOWN";
            default: return "UNKNOWN";
        }
    };
}
