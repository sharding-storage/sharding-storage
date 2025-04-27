package team.brown.sharding.storage.node.storage;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;
import team.brown.sharding.storage.proto.MigrateDataRequest;
import team.brown.sharding.storage.proto.MigrateDataResponse;
import team.brown.sharding.storage.proto.MigrationGrpcServiceGrpc;

@Service
@GrpcService
@RequiredArgsConstructor
public class MigrationGrpcServiceImpl extends MigrationGrpcServiceGrpc.MigrationGrpcServiceImplBase {

    private final StorageService storageService;

    @Override
    public void migrateData(MigrateDataRequest request, StreamObserver<MigrateDataResponse> responseObserver) {
        try {
            storageService.putAll(request.getDataMap());
            responseObserver.onNext(MigrateDataResponse.newBuilder()
                    .setSuccess(true)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(MigrateDataResponse.newBuilder()
                    .setSuccess(false)
                    .build());
            responseObserver.onCompleted();
        }
    }
}
