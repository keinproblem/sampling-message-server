package de.dhbw.ravensburg.verteiltesysteme.client;


import de.dhbw.ravensburg.verteiltesysteme.de.dhbw.ravensburg.verteiltesysteme.rpc.SamplingMessageGrpc;
import de.dhbw.ravensburg.verteiltesysteme.de.dhbw.ravensburg.verteiltesysteme.rpc.SamplingMessageGrpcService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

/**
 * This class is the CLI Client implementation for communications with the server.
 * It extends the abstract {@link Client} class and parses command line parameters.
 */
@Slf4j
public class CommandLineClient extends Client {

    private Options options;
    private HelpFormatter helpFormatter;


    CommandLineClient() {
        options = new Options();

        options.addOption(Option.builder("h")
                .longOpt("help")
                .hasArg(false)
                .required(false)
                .desc("print help")
                .build());

        options.addOption(Option.builder("n")
                .longOpt("name")
                .hasArg(true)
                .required(true)
                .desc("message name")
                .build());

        options.addOption(Option.builder("c")
                .longOpt("content")
                .hasArg(true)
                .desc("message content")
                .build());

        options.addOption(Option.builder("d")
                .longOpt("duration")
                .hasArg(true)
                .desc("duration of the message")
                .build());

        options.addOption(Option.builder("m")
                .longOpt("method")
                .hasArg(true)
                .required(true)
                .desc("client method to be used")
                .build());

        options.addOption(Option.builder("a")
                .longOpt("address")
                .hasArg(true)
                .required(true)
                .desc("sampling message server address")
                .build());

        options.addOption(Option.builder("p")
                .longOpt("port")
                .hasArg(true)
                .required(true)
                .desc("sampling message server port")
                .build());
    }

    /**
     * Parse the passed command line arguments to create a RPC request with the SamplingMessageGrpcService.
     * In case a mandatory argument is missing, the program shuts down with an error message.
     *
     * @param args Command line arguments passed to the JAR call
     */
    public void run(String[] args) {
        CommandLineParser commandLineParser = new DefaultParser();
        helpFormatter = new HelpFormatter();
        CommandLine commandLine;

        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            exitWithError(e.getMessage());
            return;
        }

        if (commandLine.hasOption("help")) {
            exitWithHelpScreen(0);
        }

        final String address = commandLine.getOptionValue("address");
        final int port = Integer.parseInt(commandLine.getOptionValue("port"));

        final int method = Integer.parseInt(commandLine.getOptionValue("method"));

        ManagedChannelBuilder<?> managedChannelBuilder = ManagedChannelBuilder.forAddress(address, port).usePlaintext();
        ManagedChannel managedChannel = managedChannelBuilder.build();
        SamplingMessageGrpc.SamplingMessageBlockingStub samplingMessageBlockingStub = SamplingMessageGrpc.newBlockingStub(managedChannel);

        try {
            if (method == 0) {
                createSamplingMessage(commandLine, samplingMessageBlockingStub);
            } else if (method == 1) {
                writeSamplingMessage(commandLine, samplingMessageBlockingStub);
            } else if (method == 2) {
                clearSamplingMessage(commandLine, samplingMessageBlockingStub);
            } else if (method == 3) {
                readSamplingMessage(commandLine, samplingMessageBlockingStub);
            } else if (method == 4) {
                getSamplingMessageStatus(commandLine, samplingMessageBlockingStub);
            } else if (method == 5) {
                deleteSamplingMessage(commandLine, samplingMessageBlockingStub);
            } else {
                exitWithError("Unknown method: " + method);
            }
        } catch (StatusRuntimeException e) {
            exitWithError("Server not reachable");
        }
        finally {
            managedChannel.shutdown();
        }
    }

    /**
     * Create an empty sampling message.
     *
     * Expected arguments are:
     *    -d for the duration the message should be valid
     *    -n for the name the message should bare
     *
     * If the message name is already in use, an error is returned by the server with the status code CONFLICT.
     * A successful request returns a status code SUCCESS.
     *
     * @param commandLine CommandLine Object for argument extraction
     * @param samplingMessageBlockingStub The message stub that executes the sampling message request
     */
    private void createSamplingMessage(CommandLine commandLine, SamplingMessageGrpc.SamplingMessageBlockingStub samplingMessageBlockingStub) {
        String name = commandLine.getOptionValue("name");
        long duration;

        if (commandLine.getOptionValue("duration") == null) {
            exitWithError("Missing argument -d: no sampling message duration given");
            // only required to have the IDE not complain about a possibly not initialized duration variable
            return;
        } else {
            duration = Long.parseLong(commandLine.getOptionValue("duration"));
        }

        SamplingMessageGrpcService.CreateSamplingMessageRequest request =
                SamplingMessageGrpcService.CreateSamplingMessageRequest.newBuilder()
                        .setMessageName(name)
                        .setLifetimeInSec(duration)
                        .build();
        log.info("creating sampling message " + request.getMessageName());

        SamplingMessageGrpcService.CreateSamplingMessageResponse response = samplingMessageBlockingStub.createSamplingMessage(request);
        log.info("createSamplingMessageResponse Status Code: " + response.getStatusCode().name());
    }

    /**
     * Write an existing sampling message.
     *
     * Expected arguments are:
     *    -d for the duration the message should be valid
     *    -c for the content of the message
     *    -n for the name the message should bare
     *
     * If the message name is unknown the server returns a status code NOT_FOUND and SUCCESS otherwise.
     *
     * @param commandLine CommandLine Object for argument extraction
     * @param samplingMessageBlockingStub The message stub that executes the sampling message request
     */
    private void writeSamplingMessage(CommandLine commandLine, SamplingMessageGrpc.SamplingMessageBlockingStub samplingMessageBlockingStub) {
        String name = commandLine.getOptionValue("name");
        String content = commandLine.getOptionValue("content");

        if (content == null) {
            exitWithError("Missing argument -c: no sampling message content given");
        }

        SamplingMessageGrpcService.WriteSamplingMessageRequest request =
                SamplingMessageGrpcService.WriteSamplingMessageRequest.newBuilder()
                        .setMessageName(name)
                        .setMessageContent(content)
                        .build();
        log.info("writing sampling message " + request.getMessageName());

        SamplingMessageGrpcService.WriteSamplingMessageResponse response = samplingMessageBlockingStub.writeSamplingMessage(request);
        log.info("writeSamplingMessageResponse Status Code: " + response.getStatusCode().name());
    }

    /**
     * Clear an existing sampling message of its content. The message is also invalidated.
     *
     * Expected arguments are:
     *    -n for the name of the message to be cleared
     *
     * If the message name is unknown the server returns a status code NOT_FOUND and SUCCESS otherwise.
     *
     * @param commandLine CommandLine Object for argument extraction
     * @param samplingMessageBlockingStub The message stub that executes the sampling message request
     */
    private void clearSamplingMessage(CommandLine commandLine, SamplingMessageGrpc.SamplingMessageBlockingStub samplingMessageBlockingStub) {
        String name = commandLine.getOptionValue("name");
        SamplingMessageGrpcService.ClearSamplingMessageRequest request =
                SamplingMessageGrpcService.ClearSamplingMessageRequest.newBuilder()
                        .setMessageName(name)
                        .build();
        log.info("clearing sampling message " + request.getMessageName());

        SamplingMessageGrpcService.ClearSamplingMessageResponse response = samplingMessageBlockingStub.clearSamplingMessage(request);
        log.info("writeSamplingMessageResponse Status Code: " + response.getStatusCode().name());
    }

    /**
     * Read an existing sampling message.
     *
     * Expected arguments are:
     *    -n for the name of the message to be read
     *
     * If the message name is unknown the server returns a status code NOT_FOUND and SUCCESS otherwise.
     *
     * @param commandLine CommandLine Object for argument extraction
     * @param samplingMessageBlockingStub The message stub that executes the sampling message request
     */
    private void readSamplingMessage(CommandLine commandLine, SamplingMessageGrpc.SamplingMessageBlockingStub samplingMessageBlockingStub) {
        String name = commandLine.getOptionValue("name");

        SamplingMessageGrpcService.ReadSamplingMessageRequest request =
                SamplingMessageGrpcService.ReadSamplingMessageRequest.newBuilder()
                        .setMessageName(name)
                        .build();
        log.info("reading sampling message " + request.getMessageName());

        SamplingMessageGrpcService.ReadSamplingMessageResponse response = samplingMessageBlockingStub.readSamplingMessage(request);
        log.info("readSamplingMessageResponse Status Code: " + response.getStatusCode().name());
        log.info("readSamplingMessageResponse Content: " + response.getMessageContent());
        log.info("readSamplingMessageResponse Valid: " + response.getMessageIsValid());
    }

    /**
     * Read the current status of an existing sampling message.
     *
     * @param commandLine CommandLine Object for argument extraction
     * @param samplingMessageBlockingStub The message stub that executes the sampling message request
     */
    private void getSamplingMessageStatus(CommandLine commandLine, SamplingMessageGrpc.SamplingMessageBlockingStub samplingMessageBlockingStub) {
        String name = commandLine.getOptionValue("name");

        SamplingMessageGrpcService.GetSamplingMessageStatusRequest request =
                SamplingMessageGrpcService.GetSamplingMessageStatusRequest.newBuilder()
                        .setMessageName(name)
                        .build();
        log.info("getting status code for message " + request.getMessageName());

        SamplingMessageGrpcService.GetSamplingMessageStatusResponse response = samplingMessageBlockingStub.getSamplingMessageStatus(request);
        log.info("getSamplingMessageStatusResponse Status Code: " + response.getStatusCode().name());
    }

    /**
     * Delete an existing sampling message, permanently removing it from the server storage.
     *
     * Expected arguments are:
     *    -n for the name of the message to be deleted
     *
     * If the message name is unknown the server returns a status code NOT_FOUND and SUCCESS otherwise.
     *
     * @param commandLine CommandLine Object for argument extraction
     * @param samplingMessageBlockingStub The message stub that executes the sampling message request
     */
    private void deleteSamplingMessage(CommandLine commandLine, SamplingMessageGrpc.SamplingMessageBlockingStub samplingMessageBlockingStub) {
        String name = commandLine.getOptionValue("name");

        SamplingMessageGrpcService.DeleteSamplingMessageRequest request =
                SamplingMessageGrpcService.DeleteSamplingMessageRequest.newBuilder()
                        .setMessageName(name)
                        .build();
        log.info("deleting status code for message " + request.getMessageName());

        SamplingMessageGrpcService.DeleteSamplingMessageResponse response = samplingMessageBlockingStub.deleteSamplingMessage(request);
        log.info("getSamplingMessageStatusResponse Status Code: " + response.getStatusCode().name());
    }


    /**
     * Prints an error message before exiting the program with an error exit code of 1.
     *
     * @param errorMessage Message to be displayed on the console during the program shutdown
     */
    private void exitWithError(String errorMessage) {
        log.error(errorMessage);
        this.exitWithHelpScreen(1);
    }

    private void exitWithHelpScreen(int exitCode) {
        helpFormatter.printHelp("sampling-message-client", options);
        System.exit(exitCode);
    }
}
