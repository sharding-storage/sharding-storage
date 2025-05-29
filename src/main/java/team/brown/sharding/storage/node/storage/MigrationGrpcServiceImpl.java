package team.brown.sharding.storage.node.storage;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;
import team.brown.sharding.storage.proto.MigrateDataChunk;
import team.brown.sharding.storage.proto.MigrateDataResponse;
import team.brown.sharding.storage.proto.MigrationGrpcServiceGrpc;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@GrpcService
@RequiredArgsConstructor
public class MigrationGrpcServiceImpl extends MigrationGrpcServiceGrpc.MigrationGrpcServiceImplBase {

    private final StorageService storageService;

    @Override
    public StreamObserver<MigrateDataChunk> migrateData(StreamObserver<MigrateDataResponse> responseObserver) {
        Map<String, String> allData = new HashMap<>();
        final int[] version = new int[1];

        return new StreamObserver<>() {
            @Override
            public void onNext(MigrateDataChunk chunk) {
                log.info("Received chunk with {} keys", chunk.getDataMap().size());
                allData.putAll(chunk.getDataMap());
                version[0] = chunk.getVersion();
            }

            @Override
            public void onError(Throwable t) {
                log.error("Migration stream error: {}", t.getMessage(), t);
            }

            @Override
            public void onCompleted() {
                try {
                    log.info("Final assembled data size: {}", allData.size());
                    storageService.putAll(allData);
                    storageService.updateVersion(version[0]);
                    log.info("Migration completed successfully");
                    responseObserver.onNext(MigrateDataResponse.newBuilder().setSuccess(true).build());
                    responseObserver.onCompleted();
                } catch (Exception e) {
                    log.error("Migration failed during final write: {}", e.getMessage(), e);
                    responseObserver.onNext(MigrateDataResponse.newBuilder().setSuccess(false).build());
                    responseObserver.onCompleted();
                }
            }
        };
    }
}
