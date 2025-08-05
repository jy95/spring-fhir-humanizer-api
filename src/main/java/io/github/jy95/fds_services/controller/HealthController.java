package io.github.jy95.fds_services.controller;

import io.github.jy95.fds_services.dto.HealthResponseDto;
import io.github.jy95.fds_services.service.HealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/health")
@Tag(
        name = "Miscellaneous"
)
public class HealthController {

    @Autowired
    HealthService healthService;

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Check service current status",
            description = "Returns the health status of the service. Typically used by monitoring tools to verify if the service is UP, DEGRADED, or DOWN.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Service is healthy (UP or DEGRADED)"
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Service is unavailable (DOWN)"
                    )
            }
    )
    public Mono<ResponseEntity<HealthResponseDto>> health() {
        return healthService
                .checkSystemHealth()
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
}
