package team.brown.sharding.storage.node.storage;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team.brown.sharding.storage.MigrationRequest;
import team.brown.sharding.storage.proto.MigrateDataChunk;
import team.brown.sharding.storage.proto.MigrateDataResponse;
import team.brown.sharding.storage.proto.MigrationGrpcServiceGrpc;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MigrationService {

    private final StorageService storageService;
    private static final int CHUNK_SIZE = 3;

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
            MigrationGrpcServiceGrpc.MigrationGrpcServiceStub stub = MigrationGrpcServiceGrpc.newStub(channel);
            CountDownLatch finishLatch = new CountDownLatch(1);

            StreamObserver<MigrateDataResponse> responseObserver = new StreamObserver<>() {
                @Override
                public void onNext(MigrateDataResponse response) {
                    log.info("Received response: success={}", response.getSuccess());
                    if (response.getSuccess()) {
                        log.info("Migration successful, removing migrated keys");
                        storageService.removeAll(keysToMigrate);
                        storageService.updateVersion(request.getVersion());
                    } else {
                        log.error("Migration failed at target address: {}", request.getTargetAddress());
                    }
                }

                @Override
                public void onError(Throwable t) {
                    log.error("Migration failed with error: {}", t.getMessage(), t);
                    finishLatch.countDown();
                }

                @Override
                public void onCompleted() {
                    log.info("Migration stream completed");
                    finishLatch.countDown();
                }
            };

            StreamObserver<MigrateDataChunk> requestObserver = stub.migrateData(responseObserver);
            for (Map<String, String> chunk : splitIntoChunks(dataToMigrate, CHUNK_SIZE)) {
                MigrateDataChunk grpcRequest = MigrateDataChunk.newBuilder()
                        .putAllData(chunk)
                        .setVersion(request.getVersion())
                        .build();
                requestObserver.onNext(grpcRequest);
            }
            requestObserver.onCompleted();

            finishLatch.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Migration interrupted", e);
        } finally {
            channel.shutdown();
        }
    }

    private List<Map<String, String>> splitIntoChunks(Map<String, String> data, int chunkSize) {
        List<Map<String, String>> chunks = new ArrayList<>();
        Map<String, String> currentChunk = new HashMap<>();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            currentChunk.put(entry.getKey(), entry.getValue());
            if (currentChunk.size() >= chunkSize) {
                chunks.add(new HashMap<>(currentChunk));
                currentChunk.clear();
            }
        }
        if (!currentChunk.isEmpty()) {
            chunks.add(currentChunk);
        }
        return chunks;
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
