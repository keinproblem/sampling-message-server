package de.dhbw.ravensburg.verteiltesysteme.server.util;

import de.dhbw.ravensburg.verteiltesysteme.server.service.ServiceConfig;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Utility class for CLI related stuff
 */
public class ServerCommandLineParser {

    /**
     * Provides default Command Line Options
     *
     * @return Options object containing the defaults
     */
    public static Options defaultOptions() {
        final Options options = new Options();

        options.addOption(Option.builder("h")
                .longOpt("help")
                .hasArg(false)
                .required(false)
                .desc("print help")
                .build());

        options.addOption(Option.builder("p")
                .longOpt("port")
                .hasArg(true)
                .required(false)
                .desc("local TCP Server Port - default is" + ServiceConfig.DEFAULT_SERVICE_ENDPOINT_PORT)
                .build());

        options.addOption(Option.builder("cl")
                .longOpt("content-length")
                .hasArg(true)
                .required(false)
                .desc("maximum length of a message's content - default is unlimited")
                .build());

        options.addOption(Option.builder("nl")
                .longOpt("name-length")
                .hasArg(true)
                .required(false)
                .desc("maximum length of a message's name - default is unlimited")
                .build());

        options.addOption(Option.builder("mc")
                .longOpt("message-count")
                .hasArg(true)
                .required(false)
                .desc("maximum number of messages the server can hold - default is unlimited")
                .build());

        return options;
    }

    /**
     * Produce a {@link ServiceConfig} from command line information
     *
     * @param commandLine Command line object from Apache CLI
     * @return appropriate service configuration object
     */
    public static ServiceConfig fromCliArgs(final CommandLine commandLine) {

        final Long maxNameLength = commandLine.hasOption("name-length") ? Long.valueOf(commandLine.getOptionValue("name-length")) : ServiceConfig.DEFAULT_MAXIMUM_SAMPLING_MESSAGE_NAME_SIZE;
        final Long maxContentLength = commandLine.hasOption("content-length") ? Long.valueOf(commandLine.getOptionValue("content-length")) : ServiceConfig.DEFAULT_MAXIMUM_SAMPLING_MESSAGE_CONTENT_SIZE;
        final Long maxMessageCount = commandLine.hasOption("message-count") ? Long.valueOf(commandLine.getOptionValue("message-count")) : ServiceConfig.DEFAULT_MAXIMUM_SAMPLING_MESSAGE_COUNT;
        final Integer serverPort = commandLine.hasOption("port") ? Integer.valueOf(commandLine.getOptionValue("port")) : ServiceConfig.DEFAULT_SERVICE_ENDPOINT_PORT;

        return new ServiceConfig(maxNameLength, maxContentLength, maxMessageCount, serverPort);
    }
}
