package de.dhbw.ravensburg.verteiltesysteme.server;

import de.dhbw.ravensburg.verteiltesysteme.server.persistence.DatabaseAccessObjectImpl;
import de.dhbw.ravensburg.verteiltesysteme.server.persistence.FakePersistence;
import de.dhbw.ravensburg.verteiltesysteme.server.rpc.RpcService;
import de.dhbw.ravensburg.verteiltesysteme.server.service.ContractValidator;
import de.dhbw.ravensburg.verteiltesysteme.server.service.SamplingMessageServiceImpl;
import de.dhbw.ravensburg.verteiltesysteme.server.service.ServiceConfig;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.SocketAddress;

@Slf4j
public class ServiceEndpoint {
    private final Server server;
    private final ServiceConfig serviceConfig;

    ServiceEndpoint(final ServiceConfig serviceConfig) {
        log.info("Preparing Service Endpoint");
        this.serviceConfig = serviceConfig;

        final RpcService rpcService = new RpcService(new SamplingMessageServiceImpl(new DatabaseAccessObjectImpl(new FakePersistence<>()), new ContractValidator(this.serviceConfig)));

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

    public void init() {
        try {
            log.info("Starting Service Endpoint");
            this.server.start();
            log.info(String.format("Server listening on TCP port: %s", this.serviceConfig.getServiceEndpointListeningPort()));
            this.server.awaitTermination();
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    public void shutdown() {
        log.info("Shutting down gRPC Server");
        this.server.shutdownNow();
        log.info("gRPC Server shut down.");
    }
}
