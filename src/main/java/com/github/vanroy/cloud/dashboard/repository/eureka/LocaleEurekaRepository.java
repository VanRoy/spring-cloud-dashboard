package com.github.vanroy.cloud.dashboard.repository.eureka;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Pair;
import com.netflix.eureka.PeerAwareInstanceRegistry;
import com.github.vanroy.cloud.dashboard.model.Application;
import com.github.vanroy.cloud.dashboard.model.InstanceHistory;
import com.github.vanroy.cloud.dashboard.repository.RegistryRepository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Eureka registry implementation of application repository
 * @author Julien Roy
 */
public class LocaleEurekaRepository extends EurekaRepository implements RegistryRepository {

    @Override
    public Collection<Application> findAll() {
        return PeerAwareInstanceRegistry.getInstance().getSortedApplications().stream()
            .map(TO_APPLICATION)
            .collect(Collectors.toList());
    }

    @Override
    public Application findByName(String name) {
        return TO_APPLICATION.apply(PeerAwareInstanceRegistry.getInstance().getApplication(name));
    }
    
    @Override
    public List<InstanceHistory> getCanceledInstanceHistory() {
        return PeerAwareInstanceRegistry.getInstance().getLastNCanceledInstances().stream().map(TO_REGISTRY_HISTORY).collect(Collectors.toList());
    }

    @Override
    public List<InstanceHistory> getRegisteredInstanceHistory() {
        return PeerAwareInstanceRegistry.getInstance().getLastNRegisteredInstances().stream().map(TO_REGISTRY_HISTORY).collect(Collectors.toList());
    }
    protected InstanceInfo findInstanceInfo(String id) {
        String[] instanceIds = id.split("_", 2);
        return PeerAwareInstanceRegistry.getInstance().getInstanceByAppAndId(instanceIds[0], instanceIds[1].replaceAll("_", "."));
    }

    private Function<Pair<Long, String>, InstanceHistory> TO_REGISTRY_HISTORY = history -> new InstanceHistory(history.second(), new Date(history.first()));
}
