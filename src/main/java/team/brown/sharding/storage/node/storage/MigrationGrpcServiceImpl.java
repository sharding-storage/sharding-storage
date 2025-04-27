package team.brown.sharding.storage.node.storage;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import team.brown.sharding.storage.proto.MigrateDataRequest;
import team.brown.sharding.storage.proto.MigrateDataResponse;
import team.brown.sharding.storage.proto.MigrationGrpcServiceGrpc;

@Slf4j
@Service
@GrpcService
@RequiredArgsConstructor
public class MigrationGrpcServiceImpl extends MigrationGrpcServiceGrpc.MigrationGrpcServiceImplBase {

    private final StorageService storageService;

    @Override
    public void migrateData(MigrateDataRequest request, StreamObserver<MigrateDataResponse> responseObserver) {
        log.info("Received migration request: keys={}", request.getDataMap().keySet());
        try {
            log.info("Get new data by migration: {}", request.getDataMap());

            storageService.putAll(request.getDataMap());
            storageService.updateVersion(request.getVersion());
            log.info("Migration completed successfully");
            responseObserver.onNext(MigrateDataResponse.newBuilder()
                    .setSuccess(true)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Migration failed: error={}", e.getMessage());
            responseObserver.onNext(MigrateDataResponse.newBuilder()
                    .setSuccess(false)
                    .build());
            responseObserver.onCompleted();
        }
    }
}
