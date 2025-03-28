package team.brown.sharding.storage.node.storage.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Запрос на установку значения для ключа.
 */
@Schema(description = "Запрос на установку значения для ключа")
public class KeyValueRequest {

    @Schema(description = "Значение для хранения", example = "new data", required = true)
    private String value;

    public KeyValueRequest() {
    }

    public KeyValueRequest(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
