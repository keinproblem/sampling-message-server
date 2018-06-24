package de.dhbw.ravensburg.verteiltesysteme.server;

import de.dhbw.ravensburg.verteiltesysteme.server.service.ServiceConfig;
import de.dhbw.ravensburg.verteiltesysteme.server.util.ServerCommandLineParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

/**
 * Class providing the application entry point.
 */
@Slf4j
public class Main {

    /**
     * Application Entry Point
     * Parsing the CLI arguments and initializing a {@link ServiceEndpoint}
     *
     * @param args CLI arguments
     */
    public static void main(String[] args) {
        final ServiceConfig serviceConfig;
        try {
            serviceConfig = ServerCommandLineParser.fromCliArgs(args);
        } catch (ParseException e) {
            e.printStackTrace();
            log.debug("ParsingError: ", e.getMessage());
            log.error(e.getMessage());
            new HelpFormatter().printHelp("sampling-message-client", ServerCommandLineParser.defaultOptions());
            System.exit(1);
            //dead code to satisfy for debugger
            return;
        }
        //final ServiceConfig serviceConfig = new ServiceConfig(255, 32, 32, 8080);

        final ServiceEndpoint serviceEndpoint = new ServiceEndpoint(serviceConfig);

        /*
            Register graceful shutdown hook
         */
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting Hook received.");
            serviceEndpoint.shutdown();
            log.info("Bye bye...");
        }));

        serviceEndpoint.init();
        try {
            serviceEndpoint.awaitTermination();
        } catch (InterruptedException e) {
            log.error("Interrupted while running: ", e);
        }
    }
}
