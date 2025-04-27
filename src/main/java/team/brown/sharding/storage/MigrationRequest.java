package team.brown.sharding.storage;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MigrationRequest {
    private final String targetAddress;
    private final Long startHash;
    private final Long endHash;
    private Integer version;
}