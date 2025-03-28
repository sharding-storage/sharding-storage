package team.brown.sharding.storage.node.storage.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Ответ с хранимым значением.
 */
@Schema(description = "Ответ с хранимым значением")
public record ValueResponse(
        @Schema(description = "Хранимое значение", example = "sample data") String value) {
}