package net.vanroy.cloud.dashboard.repository;

import net.vanroy.cloud.dashboard.model.Application;
import net.vanroy.cloud.dashboard.model.Instance;
import net.vanroy.cloud.dashboard.model.InstanceHistory;

import java.util.Collection;
import java.util.List;

/**
 * Application repository interface
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

    /**
     * Return list of last registered instance in registry
     * @return List of 1000 last instance registered
     */
    List<InstanceHistory> getRegisteredInstanceHistory();

    /**
     * Return list of last canceled instance in registry
     * @return List of 1000 last instance cancelled
     */
    List<InstanceHistory> getCanceledInstanceHistory();
}
