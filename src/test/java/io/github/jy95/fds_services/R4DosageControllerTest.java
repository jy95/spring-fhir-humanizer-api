package io.github.jy95.fds_services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jy95.fds.common.types.DisplayOrder;
import io.github.jy95.fds_services.dto.DosageRequestDto;
import io.github.jy95.fds_services.dto.ParamsDto;
import io.github.jy95.fds_services.enum_.OutputFormat;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
class R4DosageControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testAsHumanReadableText_basicExample() {

        // Prepare data
        ParamsDto paramsDto = new ParamsDto();
        paramsDto.setDisplayOrders(List.of(DisplayOrder.TEXT));
        JsonNode dosageNode = mapper
                .createObjectNode()
                .put("text", "Free text posology");
        var dosageArray = List.of(dosageNode);
        var requestDto = DosageRequestDto.builder()
                .dosages(dosageArray)
                .params(paramsDto)
                .build();

        // Act & Assert
        webTestClient
                .post()
                .uri("/r4/dosage/asHumanReadableText")
                .body(Mono.just(requestDto), DosageRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectHeader().exists("BelGov-Trace-Id")
                .expectBody()
                .jsonPath("$.items").isArray()
                .jsonPath("$.items.length()").isEqualTo(1)
                .jsonPath("$.items[0].translations").exists()
                .jsonPath("$.items[0].translations.en").isEqualTo("Free text posology")
                .jsonPath("$.issues").isArray()
                .jsonPath("$.issues.length()").isEqualTo(0);

    }

    @Test
    void testAsHumanReadableText_badRequest() {
        String invalidJsonPayload = """
        {
        }
        """;

        webTestClient
                .post()
                .uri("/r4/dosage/asHumanReadableText")
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
                .jsonPath("$.issues").exists()
                .jsonPath("$.instance").exists();
    }

    @Test
    void testAsHumanReadableText_invalidPayload() {
        String invalidJsonPayload = """
        {
            "dosages": [ { ]
        }
        """;

        webTestClient
                .post()
                .uri("/r4/dosage/asHumanReadableText")
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

        // Raw JSON for a single dosage object
        String singleDosageJson = """
        {
          "timing": {
            "repeat": {
              "frequency": 2,
              "period": 1,
              "periodUnit": "d",
              "when": ["AC"],
              "offset": 30,
              "boundsDuration": {
                "value": 10,
                "code": "d",
                "system": "http://hl7.org/fhir/ValueSet/duration-units"
              }
            }
          }
        }
        """;

        // Parse a single dosage object and put it inside an ArrayNode
        var dosageNode = mapper.readTree(singleDosageJson);
        var dosageArray = List.of(dosageNode);

        var requestDto = DosageRequestDto.builder()
                .dosages(dosageArray)
                .params(paramsDto)
                .build();

        // Act & Assert
        webTestClient
                .post()
                .uri("/r4/dosage/asHumanReadableText")
                .body(Mono.just(requestDto), DosageRequestDto.class)
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

    @SneakyThrows
    @Test
    void testAsHumanReadableText_customParams() {

        // Prepare data
        ParamsDto paramsDto = new ParamsDto();
        paramsDto.setDisplaySeparator(" | ");
        paramsDto.setOutputFormat(OutputFormat.DETAILED);

        // Raw JSON for a single dosage object
        String singleDosageJson = """
        {
          "timing": {
            "repeat": {
              "frequency": 2,
              "period": 1,
              "periodUnit": "d",
              "when": ["AC"],
              "offset": 30,
              "boundsDuration": {
                "value": 10,
                "code": "d",
                "system": "http://hl7.org/fhir/ValueSet/duration-units"
              }
            }
          }
        }
        """;

        // Parse a single dosage object and put it inside an ArrayNode
        var dosageNode = mapper.readTree(singleDosageJson);
        var dosageArray = List.of(dosageNode);

        var requestDto = DosageRequestDto.builder()
                .dosages(dosageArray)
                .params(paramsDto)
                .build();

        // Act & Assert
        webTestClient
                .post()
                .uri("/r4/dosage/asHumanReadableText")
                .body(Mono.just(requestDto), DosageRequestDto.class)
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
                        (String actual) -> assertThat(actual).isEqualToNormalizingWhitespace("2 times every day | 30 minutes before meal | for 10 days")
                );

    }
}
