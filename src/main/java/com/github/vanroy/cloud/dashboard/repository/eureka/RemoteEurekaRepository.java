package com.github.vanroy.cloud.dashboard.repository.eureka;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryManager;
import com.github.vanroy.cloud.dashboard.model.Application;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Eureka registry implementation of application repository
 * @author Julien Roy
 */
public class RemoteEurekaRepository extends EurekaRepository {

    @Override
    public Collection<Application> findAll() {
        return DiscoveryManager.getInstance().getDiscoveryClient().getApplications().getRegisteredApplications().stream()
            .map(TO_APPLICATION)
            .collect(Collectors.toList());
    }

    @Override
    public Application findByName(String name) {
        return TO_APPLICATION.apply(DiscoveryManager.getInstance().getDiscoveryClient().getApplications().getRegisteredApplications(name));
    }

    protected InstanceInfo findInstanceInfo(String id) {
        String[] instanceIds = id.split("_", 2);
        return DiscoveryManager.getInstance().getDiscoveryClient().getApplication(instanceIds[0]).getByInstanceId(instanceIds[1].replaceAll("_", "."));
    }
}
