package team.brown.sharding.storage.node.storage;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team.brown.sharding.storage.MigrationRequest;
import team.brown.sharding.storage.proto.MigrateDataRequest;
import team.brown.sharding.storage.proto.MigrateDataResponse;
import team.brown.sharding.storage.proto.MigrationGrpcServiceGrpc;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MigrationService {

    private final StorageService storageService;

    public void migrateDirectly(MigrationRequest request) {
        log.info("Start migration: targetAddress={}, startHash={}, endHash={}", 
            request.getTargetAddress(), request.getStartHash(), request.getEndHash());
        
        Set<String> keysToMigrate = storageService.getKeysInRange(request.getStartHash(), request.getEndHash());
        Map<String, String> dataToMigrate = storageService.getBulkData(keysToMigrate);
        
        if (dataToMigrate.isEmpty()) {
            log.info("No data to migrate");
            return;
        }
        
        log.info("Migrating data: keys={}", dataToMigrate.keySet());
        
        ManagedChannel channel = ManagedChannelBuilder
                .forTarget(convertToGrpcAddress(request.getTargetAddress()))
                .usePlaintext()
                .build();
        try {
            MigrationGrpcServiceGrpc.MigrationGrpcServiceBlockingStub migrationStub =
                    MigrationGrpcServiceGrpc.newBlockingStub(channel);
            MigrateDataRequest grpcRequest = MigrateDataRequest.newBuilder()
                    .putAllData(dataToMigrate)
                    .build();
            MigrateDataResponse response = migrationStub.migrateData(grpcRequest);
            if (response.getSuccess()) {
                log.info("Migration successful, removing migrated keys");
                storageService.removeAll(keysToMigrate);
            } else {
                log.error("Migration failed at target address: targetAddress={}", request.getTargetAddress());
                throw new RuntimeException("Remote migration failed at " + request.getTargetAddress());
            }
        } finally {
            channel.shutdown();
        }
    }

    private String convertToGrpcAddress(String restAddress) {
        if (restAddress.contains(":")) {
            String[] parts = restAddress.split(":");
            if (parts.length == 2) {
                String host = parts[0];
                int grpcPort = Integer.parseInt(parts[1]) - 1000;
                return host + ":" + grpcPort;
            }
        }
        return restAddress.replace("8085", "7085");
    }
}
