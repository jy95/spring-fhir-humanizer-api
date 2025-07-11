package io.github.jy95.fds_services.dto;

// https://www.belgif.be/specification/rest/api-guide/#multi-language-descriptions

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.Map;

/**
 * An immutable DTO representing localized values using ISO 639 language codes.
 * Serialized as a flat object like: { "en": "Hello", "fr": "Bonjour" }
 */
@Value
@Builder(toBuilder = true)
public class LocalizedStringDto {

    @Singular("entry")
    Map<String, String> translations;

    @JsonAnyGetter
    public Map<String, String> flatten() {
        return translations;
    }

    public String get(String langCode) {
        return translations.get(langCode);
    }
}

