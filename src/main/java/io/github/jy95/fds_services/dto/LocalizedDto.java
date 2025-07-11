package io.github.jy95.fds_services.dto;

// https://www.belgif.be/specification/rest/api-guide/#multi-language-descriptions

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.Map;

/**
 * An immutable DTO representing localized values using ISO 639 language codes.
 */
@Value
@Builder(toBuilder = true)
public class LocalizedDto {

    @Singular("entry")
    Map<String, String> translations;

}

