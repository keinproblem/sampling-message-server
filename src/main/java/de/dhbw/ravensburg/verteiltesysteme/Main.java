package de.dhbw.ravensburg.verteiltesysteme;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    //TODO; implement Apache CLI https://stackoverflow.com/a/367714/876724
    public static void main(String[] args) {
        ServiceEndpoint serviceEndpoint = new ServiceEndpoint();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting Hook received.");
            serviceEndpoint.shutdown();
            log.info("Bye bye...");
        }));

        serviceEndpoint.init();
    }
}
