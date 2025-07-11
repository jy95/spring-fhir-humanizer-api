package io.github.jy95.fds_services.controller;

import io.github.jy95.fds_services.dto.HealthResponseDto;
import io.github.jy95.fds_services.enum_.HealthStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/health")
@Tag(
        name = "Miscellaneous"
)
public class HealthController {

    @GetMapping
    public Mono<ResponseEntity<HealthResponseDto>> health() {
        return checkSystemHealth()
                .map(status -> {
                    HttpStatus httpStatus = switch (status) {
                        case DOWN -> HttpStatus.SERVICE_UNAVAILABLE;
                        case UP, DEGRADED -> HttpStatus.OK;
                    };

                    return ResponseEntity
                            .status(httpStatus)
                            .body(
                                    HealthResponseDto
                                            .builder()
                                            .status(status)
                                            .build()
                            );
                });
    }

    private Mono<HealthStatus> checkSystemHealth() {
        // Whatever logic, with real checks (DB, external service, etc.)
        return Mono.just(HealthStatus.UP);
    }
}
