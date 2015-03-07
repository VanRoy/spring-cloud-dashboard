package com.github.vanroy.cloud.dashboard.repository;

import com.github.vanroy.cloud.dashboard.model.Application;
import com.github.vanroy.cloud.dashboard.model.Instance;

import java.util.Collection;

/**
 * Application repository interface
 * @author Julien Roy
 */
public interface ApplicationRepository {

    /**
     * @return all Applications registered;
     */
    Collection<Application> findAll();

    /**
     * @param name the applications name
     * @return all Applications with the specified name;
     */
    Application findByName(String name);

    /**
     * Return circuit breaker url to application
     * @param name Name of application
     * @return Circuit Breaker Stream
     */
    String getApplicationCircuitBreakerStreamUrl(String name);

    /**
     * Return circuit breaker url to instance
     * @param instanceId Id of instance
     * @return Circuit Breaker Stream
     */
    String getInstanceCircuitBreakerStreamUrl(String instanceId);

    /**
     * @param id the instance by id
     * @return the Instance with the specified id;
     */
    Instance findInstance(String id);

    /**
     * Return management url to service
     * @param id Id of instance
     * @return Management URL
     */
    String getInstanceManagementUrl(String id);
}
