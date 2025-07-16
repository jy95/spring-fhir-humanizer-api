package io.github.jy95.fds_services;

import io.github.jy95.fds_services.controller.HealthController;
import io.github.jy95.fds_services.dto.HealthResponseDto;
import io.github.jy95.fds_services.enum_.HealthStatus;
import io.github.jy95.fds_services.service.HealthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = HealthController.class)
public class HealthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private HealthService healthService;

    @Test
    public void health_shouldReturnUpStatusWith200() {

        // Override checkSystemHealth() to return UP
        when(healthService.checkSystemHealth()).thenReturn(Mono.just(HealthStatus.UP));

        webTestClient
                .get()
                .uri("/health")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(HealthResponseDto.class)
                .value(response -> {
                    assertThat(response.getStatus()).isEqualTo(HealthStatus.UP);
                });
    }

    @Test
    public void health_shouldReturnDownStatusWith503() {

        // Override checkSystemHealth() to return UP
        when(healthService.checkSystemHealth()).thenReturn(Mono.just(HealthStatus.DOWN));

        webTestClient
                .get()
                .uri("/health")
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(HealthResponseDto.class)
                .value(response -> {
                    assertThat(response.getStatus()).isEqualTo(HealthStatus.DOWN);
                });
    }

}
