package io.github.jy95.fds_services.service;

import io.github.jy95.fds_services.enum_.HealthStatus;
import reactor.core.publisher.Mono;

public interface HealthService {
    Mono<HealthStatus> checkSystemHealth();
}
