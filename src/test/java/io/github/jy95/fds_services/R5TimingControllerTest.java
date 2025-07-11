package io.github.jy95.fds_services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jy95.fds_services.controller.R5TimingController;
import io.github.jy95.fds_services.dto.ParamsDto;
import io.github.jy95.fds_services.dto.TimingRequestDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@WebFluxTest(controllers = R5TimingController.class)
class R5TimingControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testAsHumanReadableText_basicExample() {

        // Prepare data
        var repeatNode = mapper.createObjectNode();

        repeatNode.put("frequency", 2);
        repeatNode.put("period", 1);
        repeatNode.put("periodUnit", "d");
        repeatNode.putArray("when").add("AC");
        repeatNode.put("offset", 30);

        var boundsDuration = mapper.createObjectNode();
        boundsDuration.put("value", 10);
        boundsDuration.put("code", "d");
        boundsDuration.put("system", "http://hl7.org/fhir/ValueSet/duration-units");
        repeatNode.set("boundsDuration", boundsDuration);

        var timingArray = mapper.createArrayNode();
        timingArray.add(
                mapper
                        .createObjectNode()
                        .set(
                                "repeat",
                                repeatNode
                        )
        );
        var requestDto = TimingRequestDto
                .builder()
                .timings(timingArray)
                .build();

        // Act & Assert
        webTestClient
                .post()
                .uri("/r5/timing/asHumanReadableText")
                .body(Mono.just(requestDto), TimingRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectHeader().exists("BelGov-Trace-Id")
                .expectBody()
                .jsonPath("$.items").isArray()
                .jsonPath("$.items.length()").isEqualTo(1)
                .jsonPath("$.items[0].translations").exists()
                .jsonPath("$.items[0].translations.en").value(
                        (String actual) -> assertThat(actual).isEqualToNormalizingWhitespace("2 times every day - 30 minutes before meal - for 10 days")
                )
                .jsonPath("$.issues").isArray()
                .jsonPath("$.issues.length()").isEqualTo(0);

    }

    @Test
    void testAsHumanReadableText_invalidPayload() {
        String invalidJsonPayload = """
        {
            "timings": [ { ]
        }
        """;

        webTestClient
                .post()
                .uri("/r5/timing/asHumanReadableText")
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

    @SneakyThrows
    @Test
    void testAsHumanReadableText_multipleLanguages() {

        // Prepare data
        ParamsDto paramsDto = new ParamsDto();
        paramsDto.setLocales(
                List.of(
                        Locale.ENGLISH,
                        Locale.FRENCH,
                        Locale.forLanguageTag("nl"),
                        Locale.GERMAN
                )
        );

        // Prepare data
        var repeatNode = mapper.createObjectNode();

        repeatNode.put("frequency", 2);
        repeatNode.put("period", 1);
        repeatNode.put("periodUnit", "d");
        repeatNode.putArray("when").add("AC");
        repeatNode.put("offset", 30);

        var boundsDuration = mapper.createObjectNode();
        boundsDuration.put("value", 10);
        boundsDuration.put("code", "d");
        boundsDuration.put("system", "http://hl7.org/fhir/ValueSet/duration-units");
        repeatNode.set("boundsDuration", boundsDuration);

        var timingArray = mapper.createArrayNode();
        timingArray.add(
                mapper
                        .createObjectNode()
                        .set(
                                "repeat",
                                repeatNode
                        )
        );
        var requestDto = TimingRequestDto
                .builder()
                .params(paramsDto)
                .timings(timingArray)
                .build();

        // Act & Assert
        webTestClient
                .post()
                .uri("/r5/timing/asHumanReadableText")
                .body(Mono.just(requestDto), TimingRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectHeader().exists("BelGov-Trace-Id")
                .expectBody()
                .jsonPath("$.issues").isArray()
                .jsonPath("$.issues.length()").isEqualTo(0)
                .jsonPath("$.items").isArray()
                .jsonPath("$.items.length()").isEqualTo(1)
                .jsonPath("$.items[0].translations").exists()
                .jsonPath("$.items[0].translations.en").value(
                    (String actual) -> assertThat(actual).isEqualToNormalizingWhitespace("2 times every day - 30 minutes before meal - for 10 days")
                )
                .jsonPath("$.items[0].translations.fr").value(
                        (String actual) -> assertThat(actual).isEqualToNormalizingWhitespace("2 fois chaque jour - 30 minutes avant le repas - pour 10 jours")
                )
                .jsonPath("$.items[0].translations.nl").value(
                        (String actual) -> assertThat(actual).isEqualToNormalizingWhitespace("2 keer elke dag - 30 minuten voor de maaltijd - gedurende 10 dagen")
                )
                .jsonPath("$.items[0].translations.de").value(
                        (String actual) -> assertThat(actual).isEqualToNormalizingWhitespace("2 Mal jede Tag - 30 Minuten vor den Mahlzeiten - für 10 Tage")
                );

    }
}
