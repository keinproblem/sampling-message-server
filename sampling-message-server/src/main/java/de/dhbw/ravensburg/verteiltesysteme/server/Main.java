package de.dhbw.ravensburg.verteiltesysteme.server;

import de.dhbw.ravensburg.verteiltesysteme.server.service.ServiceConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    //TODO; implement Apache CLI https://stackoverflow.com/a/367714/876724
    public static void main(String[] args) {
        final ServiceConfig serviceConfig = new ServiceConfig(255, 32, 32, 8080);
        final ServiceEndpoint serviceEndpoint = new ServiceEndpoint(serviceConfig);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting Hook received.");
            serviceEndpoint.shutdown();
            log.info("Bye bye...");
        }));

        serviceEndpoint.init();
    }
}
