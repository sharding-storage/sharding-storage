package team.brown.sharding.storage.node.storage.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Запрос на установку значения для ключа.
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "Запрос на установку значения для ключа")
public class KeyValueRequest {

    @Schema(description = "Значение для хранения", example = "new data", required = true)
    private String value;
    private int version;

}
