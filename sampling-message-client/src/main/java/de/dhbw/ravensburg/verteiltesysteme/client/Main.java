package de.dhbw.ravensburg.verteiltesysteme.client;

import de.dhbw.ravensburg.verteiltesysteme.de.dhbw.ravensburg.verteiltesysteme.rpc.SamplingMessageGrpc;
import de.dhbw.ravensburg.verteiltesysteme.de.dhbw.ravensburg.verteiltesysteme.rpc.SamplingMessageGrpcService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

@Slf4j
public class Main {

    //TODO; implement Apache CLI https://stackoverflow.com/a/367714/876724
    public static void main(String[] args) throws InterruptedException {
        ManagedChannelBuilder<?> managedChannelBuilder = ManagedChannelBuilder.forAddress("127.0.0.1", 8080).usePlaintext();
        ManagedChannel managedChannel = managedChannelBuilder.build();
        SamplingMessageGrpc.SamplingMessageStub samplingMessageStub = SamplingMessageGrpc.newStub(managedChannel);
        SamplingMessageGrpc.SamplingMessageBlockingStub samplingMessageBlockingStub = SamplingMessageGrpc.newBlockingStub(managedChannel);

        Options options = new Options();

        Option name = new Option("n", "name", true, "message name");
        name.setRequired(true);
        options.addOption(name);

        Option content = new Option("c", "content", true, "message content");
        content.setRequired(true);
        options.addOption(content);

        Option lifetime = new Option("d", "duration", true, "duration of the message");
        lifetime.setRequired(true);
        options.addOption(lifetime);


        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLine commandLine;

        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            helpFormatter.printHelp("utility-name", options);

            System.exit(1);
            return;
        }

        final String name_value = commandLine.getOptionValue("name");
        final String content_value = commandLine.getOptionValue("content");
        final long lifetime_value = Long.parseLong(commandLine.getOptionValue("duration"));

        SamplingMessageGrpcService.CreateSamplingMessageRequest createSamplingMessageRequest =
                SamplingMessageGrpcService.CreateSamplingMessageRequest.newBuilder()
                        .setMessageName(name_value)
                        .setLifetimeInSec(lifetime_value)
                        .build();

        SamplingMessageGrpcService.WriteSamplingMessageRequest writeSamplingMessageRequest =
                SamplingMessageGrpcService.WriteSamplingMessageRequest.newBuilder()
                        .setMessageName(name_value)
                        .setMessageContent(content_value)
                        .build();

        SamplingMessageGrpcService.ReadSamplingMessageRequest readSamplingMessageRequest =
                SamplingMessageGrpcService.ReadSamplingMessageRequest.newBuilder()
                        .setMessageName(name_value)
                        .build();


        //SamplingMessageGrpcService.WriteSamplingMessageRequest writeSamplingMessageRequest = SamplingMessageGrpcService.WriteSamplingMessageRequest.newBuilder().setMessageContent("BLA").setMessageName("BLA").build();
        /**
         samplingMessageStub.writeSamplingMessage(writeSamplingMessageRequest, new StreamObserver<SamplingMessageGrpcService.WriteSamplingMessageResponse>() {
        @Override public void onNext(SamplingMessageGrpcService.WriteSamplingMessageResponse writeSamplingMessageResponse) {
        log.info(writeSamplingMessageResponse.toString());
        }

        @Override public void onError(Throwable throwable) {

        }

        @Override public void onCompleted() {
        log.info("DONE writeSamplingMessage");
        }
        });
         try {
         managedChannel.awaitTermination(10, TimeUnit.SECONDS);
         } catch (InterruptedException e) {
         e.printStackTrace();
         }**/
        /*System.out.println(System.currentTimeMillis());
        for(int i = 0; i < 10; ++i){
            samplingMessageStub.createSamplingMessage(createSamplingMessageRequest, new StreamObserver<SamplingMessageGrpcService.CreateSamplingMessageResponse>() {
                @Override public void onNext(SamplingMessageGrpcService.CreateSamplingMessageResponse createSamplingMessageResponse) {
                    log.info(createSamplingMessageResponse.getStatusCode().name());
                    System.out.println(System.currentTimeMillis());
                }

                @Override public void onError(Throwable throwable) {
                    log.error(throwable.getMessage());
                }

                @Override public void onCompleted() {
                }
            });
        }
        Thread.sleep(200000);
        try {
            managedChannel.awaitTermination(100, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        managedChannel.shutdown();
        System.exit(0);*/

        SamplingMessageGrpcService.CreateSamplingMessageResponse createSamplingMessageResponse = samplingMessageBlockingStub.createSamplingMessage(createSamplingMessageRequest);
        log.info("createSamplingMessageResponse Status Code: " + createSamplingMessageResponse.getStatusCode().name());

        SamplingMessageGrpcService.WriteSamplingMessageResponse writeSamplingMessageResponse = samplingMessageBlockingStub.writeSamplingMessage(writeSamplingMessageRequest);
        log.info("writeSamplingMessageResponse Status Code: " + writeSamplingMessageResponse.getStatusCode().name());


        SamplingMessageGrpcService.ReadSamplingMessageResponse readSamplingMessageResponse = samplingMessageBlockingStub.readSamplingMessage(readSamplingMessageRequest);
        log.info("readSamplingMessageResponse Status Code: " + readSamplingMessageResponse.getStatusCode().name());
        log.info("readSamplingMessageResponse Content: " + readSamplingMessageResponse.getMessageContent());
        log.info("readSamplingMessageResponse Valid: " + readSamplingMessageResponse.getMessageIsValid());


        SamplingMessageGrpcService.CreateSamplingMessageResponse createDuplicateSamplingMessageResponse = samplingMessageBlockingStub.createSamplingMessage(createSamplingMessageRequest);
        log.info("createSamplingMessageResponse Status Code: " + createDuplicateSamplingMessageResponse.getStatusCode().name());

        Thread.sleep(lifetime_value * 1000);

        SamplingMessageGrpcService.ReadSamplingMessageResponse readSamplingMessageResponse3 = samplingMessageBlockingStub.readSamplingMessage(readSamplingMessageRequest);
        log.info("readSamplingMessageResponse Status Code: " + readSamplingMessageResponse3.getStatusCode().name());
        log.info("readSamplingMessageResponse Content: " + readSamplingMessageResponse3.getMessageContent());
        log.info("readSamplingMessageResponse Valid: " + readSamplingMessageResponse3.getMessageIsValid());

        managedChannel.shutdown();
    }
}
