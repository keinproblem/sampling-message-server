import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import service.SamplingMessageService;

import java.io.IOException;

@Slf4j
public class ServiceEndpoint {
    private final Server server;

    public ServiceEndpoint() {
        log.info("Starting Service Endpoint");
        this.server = ServerBuilder.forPort(8088).addService(new SamplingMessageService()).build();
        try {
            this.server.start();
            this.server.awaitTermination();
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }
}
