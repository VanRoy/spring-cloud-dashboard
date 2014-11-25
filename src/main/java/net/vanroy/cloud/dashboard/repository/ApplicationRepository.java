package net.vanroy.cloud.dashboard.repository;

import java.util.Collection;

import net.vanroy.cloud.dashboard.model.Application;
import net.vanroy.cloud.dashboard.model.Instance;

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
    Collection<Application> findByName(String name);

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
