package io.github.jy95.fds_services.enum_;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HealthStatus {
    UP("UP"),
    DOWN("DOWN"),
    DEGRADED("DEGRADED");

    // The status type, according Belgif
    private final String status;
}
