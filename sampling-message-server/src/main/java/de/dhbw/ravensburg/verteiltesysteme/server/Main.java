package de.dhbw.ravensburg.verteiltesysteme.server;

import de.dhbw.ravensburg.verteiltesysteme.server.service.ServiceConfig;
import de.dhbw.ravensburg.verteiltesysteme.server.util.ServerCommandLineParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import static de.dhbw.ravensburg.verteiltesysteme.server.util.ServerCommandLineParser.defaultOptions;

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
            final CommandLineParser serverCommandLineParser = new DefaultParser();
            final CommandLine commandLine = serverCommandLineParser.parse(defaultOptions(), args);
            if (commandLine.hasOption("help")) {
                exitWithHelpScreen(0);
            }
            serviceConfig = ServerCommandLineParser.fromCliArgs(commandLine);
        } catch (ParseException e) {
            e.printStackTrace();
            log.debug("ParsingError: ", e.getMessage());
            exitWithError(e.getMessage());
            //dead code to satisfy for debugger
            return;
        }

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

    /**
     * Prints an error message before exiting the program with an error exit code of 1.
     *
     * @param errorMessage Message to be displayed on the console during the program shutdown
     */
    private static void exitWithError(final String errorMessage) {
        log.error(errorMessage);
        exitWithHelpScreen(1);
    }

    private static void exitWithHelpScreen(final int exitCode) {
        new HelpFormatter().printHelp("sampling-message-server", defaultOptions());
        System.exit(exitCode);
    }
}
