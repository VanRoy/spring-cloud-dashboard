package net.vanroy.cloud.dashboard.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Pair;
import com.netflix.eureka.PeerAwareInstanceRegistry;

import net.vanroy.cloud.dashboard.model.Application;
import net.vanroy.cloud.dashboard.model.Instance;
import net.vanroy.cloud.dashboard.model.InstanceHistory;

/**
 * Eureka registry implementation of application repository
 */
public class EurekaRepository implements ApplicationRepository {

    @Override
    public Collection<Application> findAll() {
        return PeerAwareInstanceRegistry.getInstance().getSortedApplications().stream()
            .map(TO_APPLICATION)
            .collect(Collectors.toList());
    }

    @Override
    public Collection<Application> findByName(String name) {
        return null;
    }

    @Override
    public Instance findInstance(String id) {
        return TO_INSTANCE.apply(findInstanceInfo(id));
    }

    @Override
    public String getInstanceManagementUrl(String id) {

        InstanceInfo info = findInstanceInfo(id);
        if(info == null) {
            return null;
        }

        String url = info.getHomePageUrl();
        if(info.getMetadata().containsKey("managementPath")) {
            url += info.getMetadata().get("managementPath");
        }

        return url;
    }

    @Override
    public List<InstanceHistory> getCanceledInstanceHistory() {
        return PeerAwareInstanceRegistry.getInstance().getLastNCanceledInstances().stream().map(TO_REGISTRY_HISTORY).collect(Collectors.toList());
    }

    @Override
    public List<InstanceHistory> getRegisteredInstanceHistory() {
        return PeerAwareInstanceRegistry.getInstance().getLastNRegisteredInstances().stream().map(TO_REGISTRY_HISTORY).collect(Collectors.toList());
    }

    private InstanceInfo findInstanceInfo(String id) {
        String[] instanceIds = id.split("_", 2);
        return PeerAwareInstanceRegistry.getInstance().getInstanceByAppAndId(instanceIds[0], instanceIds[1].replaceAll("_", "."));
    }


    private Function<com.netflix.discovery.shared.Application, Application> TO_APPLICATION = new Function<com.netflix.discovery.shared.Application, Application>() {
        @Override
        public Application apply(com.netflix.discovery.shared.Application app) {
            if(app == null) { return null; }
            return new Application(app.getName(), app.getInstances().stream().map(TO_INSTANCE).sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).collect(Collectors.toList()));
        }
    };

    private Function<InstanceInfo, Instance> TO_INSTANCE = instance -> {
        if(instance == null) { return null; }
        return new Instance(instance.getHomePageUrl(), instance.getId(), instance.getAppName()+"_"+instance.getId().replaceAll("\\.","_"), instance.getStatus().toString());
    };

    private Function<Pair<Long, String>, InstanceHistory> TO_REGISTRY_HISTORY = history -> new InstanceHistory(history.second(), new Date(history.first()));
}
