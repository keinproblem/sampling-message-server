import de.dhbw.ravensburg.verteiltesysteme.server.SamplingMessageGrpc;
import de.dhbw.ravensburg.verteiltesysteme.server.SamplingMessageGrpcService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Client {

    public static void main(String[] args) {
        ManagedChannelBuilder<?> managedChannelBuilder = ManagedChannelBuilder.forAddress("127.0.0.1", 8088).usePlaintext();
        ManagedChannel managedChannel = managedChannelBuilder.build();
        SamplingMessageGrpc.SamplingMessageStub samplingMessageStub = SamplingMessageGrpc.newStub(managedChannel);
        SamplingMessageGrpc.SamplingMessageBlockingStub samplingMessageBlockingStub = SamplingMessageGrpc.newBlockingStub(managedChannel);
        SamplingMessageGrpcService.WriteSamplingMessageRequest writeSamplingMessageRequest = SamplingMessageGrpcService.WriteSamplingMessageRequest.newBuilder().setMessageContent("BLA").setMessageName("BLA").build();
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
        samplingMessageBlockingStub.writeSamplingMessage(writeSamplingMessageRequest);
        managedChannel.shutdown();
    }
}
