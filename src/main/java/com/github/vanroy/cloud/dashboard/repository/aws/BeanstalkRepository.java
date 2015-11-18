package com.github.vanroy.cloud.dashboard.repository.aws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient;
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest;
import com.amazonaws.services.cloudformation.model.DescribeStacksResult;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalk;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClient;
import com.amazonaws.services.elasticbeanstalk.model.ApplicationDescription;
import com.amazonaws.services.elasticbeanstalk.model.DescribeApplicationsRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeApplicationsResult;
import com.amazonaws.services.elasticbeanstalk.model.DescribeEnvironmentResourcesRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeEnvironmentsRequest;
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentDescription;
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentResourceDescription;
import com.github.vanroy.cloud.dashboard.model.Application;
import com.github.vanroy.cloud.dashboard.model.Instance;
import com.github.vanroy.cloud.dashboard.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.StringUtils;

/**
 * Amazon Web Service Beanstalk registry implementation of application repository
 * @author Julien Roy
 */
@EnableConfigurationProperties({BeanstalkProperties.class})
public class BeanstalkRepository implements ApplicationRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanstalkRepository.class);

    private final LoadingCache<String, AWSElasticBeanstalk> awsElasticBeanstalk = CacheBuilder.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build(
            new CacheLoader<String, AWSElasticBeanstalk>() {
                @Override
                public AWSElasticBeanstalk load(@Nullable String endpoint) throws Exception {

                    AWSCredentials credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
                    AWSElasticBeanstalk beanstalk = new AWSElasticBeanstalkClient(credentials);
                    beanstalk.setEndpoint(properties.getEndpoint());
                    return beanstalk;
                }
            });

    private final LoadingCache<String, AmazonEC2> amazonEC2 = CacheBuilder.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build(
                new CacheLoader<String, AmazonEC2>() {
                    @Override
                    public AmazonEC2 load(@Nullable String endpoint) throws Exception {

                        AWSCredentials credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
                        AmazonEC2Client ec2 = new AmazonEC2Client(credentials);
                        ec2.setEndpoint(properties.getInstances().getEndpoint());

                        return ec2;
                    }
                });

    private final LoadingCache<String, List<Stack>> cloudformationStacks = CacheBuilder.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build(
                new CacheLoader<String, List<Stack>>() {
                    @Override
                    public List<Stack> load(@Nullable String endpoint) throws Exception {

                        AWSCredentials credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
                        AmazonCloudFormation cf = new AmazonCloudFormationClient(credentials);
                        cf.setEndpoint(properties.getCloudFormation().getEndpoint());

                        List<Stack> stacks = new ArrayList<>();
                        DescribeStacksResult stacksResults = cf.describeStacks();
                        stacks.addAll(stacksResults.getStacks());

                        for (int i=0 ; i<100 ; i++) {
                            if(stacksResults.getNextToken() == null) {
                                return stacks;
                            } else {
                                stacksResults = cf.describeStacks(new DescribeStacksRequest().withNextToken(stacksResults.getNextToken()));
                                stacks.addAll(stacksResults.getStacks());
                            }
                        }

                        return stacks;
                    }
                });

    private final LoadingCache<String, String> instanceDns = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build(
            new CacheLoader<String, String>() {
                @Override
                public String load(@Nullable String id) {
                    return getEC2().describeInstances(new DescribeInstancesRequest().withInstanceIds(id)).getReservations().get(0).getInstances().get(0).getPrivateDnsName();
                }
            });

    private final LoadingCache<String, Application> applications = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(
                new CacheLoader<String, Application>() {
                    @Override
                    public Application load(@Nullable String applicationName) {
                        return new Application(applicationName, getInstances(applicationName));
                    }
                });

    @Autowired
    private BeanstalkProperties properties;

    public BeanstalkRepository() {
    }

    private AWSElasticBeanstalk getBeanstalk() {
        try {
            return awsElasticBeanstalk.get("");
        } catch (ExecutionException e) {
            LOGGER.error("Connot retreive beanstalk client", e);
            throw new RuntimeException(e);
        }
    }

    private AmazonEC2 getEC2() {
        try {
            return amazonEC2.get("");
        } catch (ExecutionException e) {
            LOGGER.error("Connot retreive EC2 client", e);
            throw new RuntimeException(e);
        }
    }

    private List<Stack> getStacks() {
        try {
            return cloudformationStacks.get("");
        } catch (ExecutionException e) {
            LOGGER.error("Connot retreive Cloudformation stacks", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Application> findAll() {

        DescribeApplicationsResult result = getBeanstalk().describeApplications();

        LOGGER.info("Find applications : {}", result.getApplications().size());

        return result.getApplications().parallelStream()
            .map(TO_APPLICATION)
            .sorted(Comparator.comparing(Application::getName))
            .collect(Collectors.toList());
    }

    @Override
    public Application findByName(String name) {
        return TO_APPLICATION.apply(
            getBeanstalk().describeApplications(new DescribeApplicationsRequest().withApplicationNames(name)
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
            getEC2().describeInstances(new DescribeInstancesRequest().withInstanceIds(id)
            ).getReservations().get(0).getInstances().get(0));
    }

    @Override
    public String getInstanceManagementUrl(String id) {

        try {
            BeanstalkProperties.Management management = properties.getInstances().getManagement();
            String privateDns = instanceDns.get(id);
            return  management.getScheme()+"://"+privateDns+":"+management.getPort()+management.getPath();
        } catch (ExecutionException e) {
            LOGGER.error("Cannot retreive instance DNS", e);
            throw new RuntimeException(e);
        }
    }

    protected List<Instance> getInstances(String appName) {

        DescribeEnvironmentsRequest envRequest = new DescribeEnvironmentsRequest().withApplicationName(appName);

        List<EnvironmentDescription> environments = getBeanstalk().describeEnvironments(envRequest).getEnvironments();
        if (environments == null || environments.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        DescribeEnvironmentResourcesRequest request = new DescribeEnvironmentResourcesRequest();

        if(StringUtils.hasText(properties.getEnvironment())) {
            Optional<EnvironmentDescription> environment = environments.stream().filter(e -> e.getEnvironmentName().matches(properties.getEnvironment())).findFirst();
            if (!environment.isPresent()) {
                return Collections.EMPTY_LIST;
            }

            request.withEnvironmentName(environment.get().getEnvironmentName());
        }

        if(properties.getEnvironmentTags() != null && !properties.getEnvironmentTags().isEmpty()) {

            List<String> environmentIds = environments.stream().map(EnvironmentDescription::getEnvironmentId).collect(Collectors.toList());

            Optional<String> oEnvironmentId = getStacks().stream()
                    .map(Stack::getTags)
                    .map(tags -> tags.stream().collect(Collectors.toMap(com.amazonaws.services.cloudformation.model.Tag::getKey, com.amazonaws.services.cloudformation.model.Tag::getValue)))
                    .filter(tags -> {

                        for (Map.Entry<String, String> entry : properties.getEnvironmentTags().entrySet()) {
                            //Check value with existing stack tags
                            if (!entry.getValue().equalsIgnoreCase(tags.get(entry.getKey()))) {
                                return false;
                            }
                        }

                        return true;

                    })
                    .map(tags -> tags.get("elasticbeanstalk:environment-id"))
                    .filter(environmentIds::contains)
                    .findFirst();

            if (!oEnvironmentId.isPresent()) {
                return Collections.EMPTY_LIST;
            }

            request.withEnvironmentId(oEnvironmentId.get());
        }

        EnvironmentResourceDescription resources = getBeanstalk().describeEnvironmentResources(request).getEnvironmentResources();
        return resources.getInstances().stream().map(instance -> new Instance("", instance.getId(), instance.getId(), getInstanceStatus(instance.getId()))).collect(Collectors.toList());
    }

    protected String getInstanceStatus(String... id) {
        try {
            return TO_STATUS.apply(getEC2().describeInstanceStatus(new DescribeInstanceStatusRequest().withInstanceIds(id))
                    .getInstanceStatuses().get(0)
                    .getInstanceState());
        } catch (AmazonServiceException e) {
            LOGGER.warn("Instance not found", e);
            return "UNKNOWN";
        }
    }

    protected Function<ApplicationDescription, Application> TO_APPLICATION = app -> {
        if(app == null) { return null; }
        try {
            return applications.get(app.getApplicationName());
        } catch (ExecutionException e) {
            LOGGER.error("Cannot retreive application", e);
            throw new RuntimeException(e);
        }
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
