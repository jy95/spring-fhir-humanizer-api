package io.github.jy95.fds_services.service;

import io.github.jy95.fds_services.enum_.HealthStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class HealthServiceImpl implements HealthService {
    @Override
    public Mono<HealthStatus> checkSystemHealth() {
        return Mono.just(HealthStatus.UP);
    }
}
