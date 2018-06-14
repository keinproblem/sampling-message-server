package de.dhbw.ravensburg.verteiltesysteme.server;

import de.dhbw.ravensburg.verteiltesysteme.server.persistence.DatabaseAccessObject;
import de.dhbw.ravensburg.verteiltesysteme.server.persistence.DatabaseAccessObjectImpl;
import de.dhbw.ravensburg.verteiltesysteme.server.persistence.FakePersistence;
import de.dhbw.ravensburg.verteiltesysteme.server.rpc.RpcService;
import de.dhbw.ravensburg.verteiltesysteme.server.service.ContractValidator;
import de.dhbw.ravensburg.verteiltesysteme.server.service.SamplingMessageService;
import de.dhbw.ravensburg.verteiltesysteme.server.service.SamplingMessageServiceImpl;
import de.dhbw.ravensburg.verteiltesysteme.server.service.ServiceConfig;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * This class represents a high level application service.
 * It will initialize a gRPC server with a respective gRPC service implementation.
 */
@Slf4j
public class ServiceEndpoint {
    private final Server server;
    private final ServiceConfig serviceConfig;


    /**
     * Instantiate a ServiceEndpoint object with the given {@see service.ServiceConfig}
     * Prepares the gRPC server to be initialized afterwards.
     *
     * @param serviceConfig Basic service configuration
     */
    public ServiceEndpoint(final ServiceConfig serviceConfig) {
        log.info("Preparing Service Endpoint");
        this.serviceConfig = serviceConfig;

        final DatabaseAccessObject databaseAccessObject = new DatabaseAccessObjectImpl(new FakePersistence<>());
        final ContractValidator contractValidator = new ContractValidator(this.serviceConfig);
        final SamplingMessageService samplingMessageService = new SamplingMessageServiceImpl(databaseAccessObject, contractValidator);

        final RpcService rpcService = new RpcService(samplingMessageService);

        this.server = ServerBuilder
                .forPort(this.serviceConfig.getServiceEndpointListeningPort())
                .addService(rpcService)
                .intercept(socketAddressLoggingServerInterceptor())
                .build();
    }

    private static ServerInterceptor socketAddressLoggingServerInterceptor() {
        return new ServerInterceptor() {
            @Override
            public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
                final SocketAddress socketAddress = serverCall.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR);
                log.info(String.format("Receiving request from: %s", socketAddress == null ? "Unavailable" : socketAddress.toString()));
                return serverCallHandler.startCall(serverCall, metadata);
            }
        };
    }

    /**
     * Initialize the ServiceEndpoint after instantiation.
     */
    public void init() {
        try {
            log.info("Starting Service Endpoint");
            this.server.start();
            log.info(String.format("Server listening on TCP port: %s", this.serviceConfig.getServiceEndpointListeningPort()));
        } catch (IOException e) {
            log.error(e.getMessage());
            this.shutdown();
        }
    }

    public void awaitTermination() throws InterruptedException {
        this.server.awaitTermination();
    }

    /**
     * Perform graceful shutdown of the endpoint.
     */
    public void shutdown() {
        log.info("Shutting down gRPC Server");
        this.server.shutdownNow();
        log.info("gRPC Server shut down.");
    }
}
