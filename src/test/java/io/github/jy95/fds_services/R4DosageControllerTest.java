package io.github.jy95.fds_services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jy95.fds.common.types.DisplayOrder;
import io.github.jy95.fds_services.controller.R4DosageController;
import io.github.jy95.fds_services.dto.DosageRequestDto;
import io.github.jy95.fds_services.dto.ParamsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

@WebFluxTest(controllers = R4DosageController.class)
class R4DosageControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testAsHumanReadableText_basicExample() {

        // Prepare data
        ParamsDto paramsDto = new ParamsDto();
        paramsDto.setDisplayOrders(List.of(DisplayOrder.TEXT));
        var dosageArray = mapper.createArrayNode();
        dosageArray.add(mapper.createObjectNode()
                .put("text", "Free text posology")
        );
        var requestDto = DosageRequestDto.builder()
                .dosages(dosageArray)
                .params(paramsDto)
                .build();

        // Act & Assert
        webTestClient.post()
                .uri("/r4/asHumanReadableText")
                .body(Mono.just(requestDto), DosageRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectHeader().exists("BelGov-Trace-Id");

    }

    @Test
    void testAsHumanReadableText_invalidPayload() {
        String invalidJsonPayload = """
        {
            "dosages": [ { ]
        }
        """;

        webTestClient.post()
                .uri("/r4/asHumanReadableText")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(invalidJsonPayload)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectHeader().exists("BelGov-Trace-Id")
                .expectBody()
                .jsonPath("$.type").isEqualTo("urn:problem-type:belgif:badRequest")
                .jsonPath("$.title").isEqualTo("Bad Request")
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.href").isEqualTo("https://www.belgif.be/specification/rest/api-guide/problems/badRequest.html")
                .jsonPath("$.timestamp").exists()
                //.jsonPath("$.issues").isArray()
                .jsonPath("$.instance").exists();
    }
}
