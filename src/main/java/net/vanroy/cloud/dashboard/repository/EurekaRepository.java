package net.vanroy.cloud.dashboard.repository;

import com.netflix.eureka.PeerAwareInstanceRegistry;
import net.vanroy.cloud.dashboard.model.Application;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public Application find(String id) {
        return null;
    }

    @Override
    public Collection<Application> findByName(String name) {
        return null;
    }

    private Function<com.netflix.discovery.shared.Application, Application> TO_APPLICATION = new Function<com.netflix.discovery.shared.Application, Application>() {
        @Override
        public Application apply(com.netflix.discovery.shared.Application app) {
            return new Application(app.getName(), null);
        }
    };
}
