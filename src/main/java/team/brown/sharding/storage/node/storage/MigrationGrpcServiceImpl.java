package team.brown.sharding.storage.node.storage;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;
import team.brown.sharding.storage.proto.MigrateDataRequest;
import team.brown.sharding.storage.proto.MigrateDataResponse;
import team.brown.sharding.storage.proto.MigrationGrpcServiceGrpc;

@Service
@GrpcService
@RequiredArgsConstructor
@Slf4j
public class MigrationGrpcServiceImpl extends MigrationGrpcServiceGrpc.MigrationGrpcServiceImplBase {

    private final StorageService storageService;

    @Override
    public void migrateData(MigrateDataRequest request, StreamObserver<MigrateDataResponse> responseObserver) {
        log.info("Received migration request: keys={}", request.getDataMap().keySet());
        try {
            storageService.putAll(request.getDataMap());
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
