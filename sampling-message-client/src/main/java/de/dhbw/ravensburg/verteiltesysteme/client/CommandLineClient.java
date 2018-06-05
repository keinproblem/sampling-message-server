package de.dhbw.ravensburg.verteiltesysteme.client;


import de.dhbw.ravensburg.verteiltesysteme.de.dhbw.ravensburg.verteiltesysteme.rpc.SamplingMessageGrpc;
import de.dhbw.ravensburg.verteiltesysteme.de.dhbw.ravensburg.verteiltesysteme.rpc.SamplingMessageGrpcService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

@Slf4j
public class CommandLineClient {

    private Options options;
    private HelpFormatter helpFormatter;


    CommandLineClient() {
        options = new Options();

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

    public void run(String[] args) throws Exception {
        CommandLineParser commandLineParser = new DefaultParser();
        helpFormatter = new HelpFormatter();
        CommandLine commandLine;

        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            exitWithError(e.getMessage());
            return;
        }

        final String address = commandLine.getOptionValue("address");
        final int port = Integer.parseInt(commandLine.getOptionValue("port"));

        final int method = Integer.parseInt(commandLine.getOptionValue("method"));

        ManagedChannelBuilder<?> managedChannelBuilder = ManagedChannelBuilder.forAddress(address, port).usePlaintext();
        ManagedChannel managedChannel = managedChannelBuilder.build();
        SamplingMessageGrpc.SamplingMessageStub samplingMessageStub = SamplingMessageGrpc.newStub(managedChannel);
        SamplingMessageGrpc.SamplingMessageBlockingStub samplingMessageBlockingStub = SamplingMessageGrpc.newBlockingStub(managedChannel);

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

        managedChannel.shutdown();
    }

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



    private void exitWithError(String errorMessage) {
        System.out.println(errorMessage);
        helpFormatter.printHelp("sampling-message-client", options);
        System.exit(1);
    }
}
