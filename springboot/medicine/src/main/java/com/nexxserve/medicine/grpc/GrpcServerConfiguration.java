package com.nexxserve.medicine.grpc;

    import com.nexxserve.medicine.grpc.MedicineGrpcService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class GrpcServerConfiguration implements CommandLineRunner {

    private final MedicineGrpcService medicineGrpcService;

    @Value("${grpc.server.port:9090}")
    private int port;

    private Server server;

    @Override
    public void run(String... args) throws Exception {
        start();
        log.info("gRPC server started, listening on port {}", port);

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down gRPC server");
            stop();
        }));
    }

    private void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(medicineGrpcService)
                .build()
                .start();
    }

    @PreDestroy
    private void stop() {
        if (server != null) {
            server.shutdown();
            log.info("gRPC server stopped");
        }
    }
}