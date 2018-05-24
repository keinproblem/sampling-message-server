package de.dhbw.ravensburg.verteiltesysteme;

import de.dhbw.ravensburg.verteiltesysteme.de.dhbw.ravensburg.verteiltesysteme.rpc.SamplingMessageGrpc;
import de.dhbw.ravensburg.verteiltesysteme.de.dhbw.ravensburg.verteiltesysteme.rpc.SamplingMessageGrpcService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Client {

    //TODO; implement Apache CLI https://stackoverflow.com/a/367714/876724
    public static void main(String[] args) throws InterruptedException {
        ManagedChannelBuilder<?> managedChannelBuilder = ManagedChannelBuilder.forAddress("127.0.0.1", 8080).usePlaintext();
        ManagedChannel managedChannel = managedChannelBuilder.build();
        SamplingMessageGrpc.SamplingMessageStub samplingMessageStub = SamplingMessageGrpc.newStub(managedChannel);
        SamplingMessageGrpc.SamplingMessageBlockingStub samplingMessageBlockingStub = SamplingMessageGrpc.newBlockingStub(managedChannel);

        final String name = "NOLI";
        final String content = "bla";
        final long lifetime = 2;

        SamplingMessageGrpcService.CreateSamplingMessageRequest createSamplingMessageRequest =
                SamplingMessageGrpcService.CreateSamplingMessageRequest.newBuilder()
                        .setMessageName(name)
                        .setLifetimeInSec(lifetime)
                        .build();

        SamplingMessageGrpcService.WriteSamplingMessageRequest writeSamplingMessageRequest =
                SamplingMessageGrpcService.WriteSamplingMessageRequest.newBuilder()
                        .setMessageName(name)
                        .setMessageContent(content)
                        .build();

        SamplingMessageGrpcService.ReadSamplingMessageRequest readSamplingMessageRequest =
                SamplingMessageGrpcService.ReadSamplingMessageRequest.newBuilder()
                        .setMessageName(name)
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

        Thread.sleep(lifetime * 1000);

        SamplingMessageGrpcService.ReadSamplingMessageResponse readSamplingMessageResponse3 = samplingMessageBlockingStub.readSamplingMessage(readSamplingMessageRequest);
        log.info("readSamplingMessageResponse Status Code: " + readSamplingMessageResponse3.getStatusCode().name());
        log.info("readSamplingMessageResponse Content: " + readSamplingMessageResponse3.getMessageContent());
        log.info("readSamplingMessageResponse Valid: " + readSamplingMessageResponse3.getMessageIsValid());

        managedChannel.shutdown();
    }
}
