import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import service.SamplingMessageService;

import java.io.IOException;

@Slf4j
public class ServiceEndpoint {
    private final Server server;

    public ServiceEndpoint() {
        log.info("Starting Service Endpoint");
        this.server = ServerBuilder.forPort(8088).addService(new SamplingMessageService()).intercept(new ServerInterceptor() {
            @Override
            public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
                log.info(serverCall.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR).toString());
                return serverCallHandler.startCall(serverCall, metadata);
            }
        }).build();
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
