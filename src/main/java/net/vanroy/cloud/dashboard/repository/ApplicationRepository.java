package net.vanroy.cloud.dashboard.repository;

import net.vanroy.cloud.dashboard.model.Application;

import java.util.Collection;

/**
 * Application repository interface
 */
public interface ApplicationRepository {

    /**
     * @return all Applications registered;
     */
    Collection<Application> findAll();

    /**
     * @param id the applications id
     * @return the Application with the specified id;
     */
    Application find(String id);

    /**
     * @param name the applications name
     * @return all Applications with the specified name;
     */
    Collection<Application> findByName(String name);
}
