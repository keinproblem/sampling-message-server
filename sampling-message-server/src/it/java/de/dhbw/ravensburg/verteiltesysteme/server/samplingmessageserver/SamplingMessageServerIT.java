package de.dhbw.ravensburg.verteiltesysteme.server.samplingmessageserver;

import de.dhbw.ravensburg.verteiltesysteme.de.dhbw.ravensburg.verteiltesysteme.rpc.SamplingMessageGrpc;
import de.dhbw.ravensburg.verteiltesysteme.de.dhbw.ravensburg.verteiltesysteme.rpc.SamplingMessageGrpcService;
import de.dhbw.ravensburg.verteiltesysteme.server.ServiceEndpoint;
import de.dhbw.ravensburg.verteiltesysteme.server.service.ServiceConfig;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class SamplingMessageServerIT {

    private final Integer testingPort = 8888;
    ServiceEndpoint serviceEndpoint;
    ServiceConfig serviceConfig = new ServiceConfig(ServiceConfig.DEFAULT_MAXIMUM_SAMPLING_MESSAGE_NAME_SIZE, ServiceConfig.DEFAULT_MAXIMUM_SAMPLING_MESSAGE_CONTENT_SIZE, ServiceConfig.DEFAULT_MAXIMUM_SAMPLING_MESSAGE_COUNT, testingPort);
    SamplingMessageGrpc.SamplingMessageBlockingStub samplingMessageBlockingStub;

    private static SamplingMessageGrpcService.CreateSamplingMessageRequest createSamplingMessageRequest(String name, long duration) {
        return SamplingMessageGrpcService.CreateSamplingMessageRequest
                .newBuilder()
                .setMessageName(name)
                .setLifetimeInSec(duration)
                .build();
    }

    private static SamplingMessageGrpcService.WriteSamplingMessageRequest writeSamplingMessageRequest(String name, String content) {
        return SamplingMessageGrpcService.WriteSamplingMessageRequest
                .newBuilder()
                .setMessageName(name)
                .setMessageContent(content)
                .build();
    }

    private static SamplingMessageGrpcService.DeleteSamplingMessageRequest deleteSamplingMessageRequest(String name) {
        return SamplingMessageGrpcService.DeleteSamplingMessageRequest
                .newBuilder()
                .setMessageName(name)
                .build();
    }

    private static SamplingMessageGrpcService.ReadSamplingMessageRequest readSamplingMessageRequest(String name) {
        return SamplingMessageGrpcService.ReadSamplingMessageRequest
                .newBuilder()
                .setMessageName(name)
                .build();
    }

    private static SamplingMessageGrpcService.ClearSamplingMessageRequest clearSamplingMessageRequest(String name) {
        return SamplingMessageGrpcService.ClearSamplingMessageRequest
                .newBuilder()
                .setMessageName(name)
                .build();
    }

    private static SamplingMessageGrpcService.GetSamplingMessageStatusRequest getSamplingMessageStatusRequest(String name) {
        return SamplingMessageGrpcService.GetSamplingMessageStatusRequest
                .newBuilder()
                .setMessageName(name)
                .build();
    }

    /*@Test
    public void writeNewMessageWithMaxValueLifetime() {
        final String msgName = "Test01";
        final String msgContent = "content01";
        SamplingMessageGrpcService.CreateSamplingMessageRequest createSamplingMessageRequest = createSamplingMessageRequest(msgName, Long.MAX_VALUE);
        SamplingMessageGrpcService.CreateSamplingMessageResponse createSamplingMessageResponse = this.samplingMessageBlockingStub.createSamplingMessage(createSamplingMessageRequest);
        Assert.assertEquals(createSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);

        SamplingMessageGrpcService.WriteSamplingMessageRequest writeSamplingMessageRequest = writeSamplingMessageRequest(msgName, msgContent);
        SamplingMessageGrpcService.WriteSamplingMessageResponse writeSamplingMessageResponse = this.samplingMessageBlockingStub.writeSamplingMessage(writeSamplingMessageRequest);
        Assert.assertEquals(createSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);

        SamplingMessageGrpcService.ReadSamplingMessageRequest readSamplingMessageRequest = readSamplingMessageRequest(msgName);
        SamplingMessageGrpcService.ReadSamplingMessageResponse readSamplingMessageResponse = this.samplingMessageBlockingStub.readSamplingMessage(readSamplingMessageRequest);
        Assert.assertEquals(readSamplingMessageResponse.getMessageContent(), msgContent);
    }*/

    @BeforeMethod
    public void beforeMethod() {
        this.serviceEndpoint = new ServiceEndpoint(serviceConfig);
        this.serviceEndpoint.init();

        ManagedChannelBuilder<?> managedChannelBuilder = ManagedChannelBuilder.forAddress("127.0.0.1", testingPort).usePlaintext();
        ManagedChannel managedChannel = managedChannelBuilder.build();
        samplingMessageBlockingStub = SamplingMessageGrpc.newBlockingStub(managedChannel);
    }

    @AfterMethod
    public void afterMethod() {
        this.serviceEndpoint.shutdown();
    }

    @Test
    public void createSameMessageTwice() {
        SamplingMessageGrpcService.CreateSamplingMessageRequest createSamplingMessageRequest = createSamplingMessageRequest("TEST01", 120);
        SamplingMessageGrpcService.CreateSamplingMessageResponse createSamplingMessageResponse = this.samplingMessageBlockingStub.createSamplingMessage(createSamplingMessageRequest);
        Assert.assertEquals(createSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);
        SamplingMessageGrpcService.CreateSamplingMessageResponse createSamplingMessageResponse2 = this.samplingMessageBlockingStub.createSamplingMessage(createSamplingMessageRequest);
        Assert.assertEquals(createSamplingMessageResponse2.getStatusCode(), SamplingMessageGrpcService.StatusCode.CONFLICT);

    }

    @Test
    public void deleteExistingMessage() {
        SamplingMessageGrpcService.CreateSamplingMessageRequest createSamplingMessageRequest = createSamplingMessageRequest("TEST01", 120);
        SamplingMessageGrpcService.CreateSamplingMessageResponse createSamplingMessageResponse = this.samplingMessageBlockingStub.createSamplingMessage(createSamplingMessageRequest);
        Assert.assertEquals(createSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);

        SamplingMessageGrpcService.DeleteSamplingMessageRequest deleteSamplingMessageRequest = deleteSamplingMessageRequest("TEST01");
        SamplingMessageGrpcService.DeleteSamplingMessageResponse deleteSamplingMessageResponse = this.samplingMessageBlockingStub.deleteSamplingMessage(deleteSamplingMessageRequest);
        Assert.assertEquals(deleteSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);
    }

    @Test
    public void deleteNonExistingMessage() {
        SamplingMessageGrpcService.DeleteSamplingMessageRequest deleteSamplingMessageRequest = deleteSamplingMessageRequest("TEST01");
        SamplingMessageGrpcService.DeleteSamplingMessageResponse deleteSamplingMessageResponse = this.samplingMessageBlockingStub.deleteSamplingMessage(deleteSamplingMessageRequest);
        Assert.assertEquals(deleteSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.NOT_FOUND);
    }

    @Test
    public void writeNewMessage() {
        final String msgName = "Test01";
        final String msgContent = "content01";
        SamplingMessageGrpcService.CreateSamplingMessageRequest createSamplingMessageRequest = createSamplingMessageRequest(msgName, 120);
        SamplingMessageGrpcService.CreateSamplingMessageResponse createSamplingMessageResponse = this.samplingMessageBlockingStub.createSamplingMessage(createSamplingMessageRequest);
        Assert.assertEquals(createSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);

        SamplingMessageGrpcService.WriteSamplingMessageRequest writeSamplingMessageRequest = writeSamplingMessageRequest(msgName, msgContent);
        SamplingMessageGrpcService.WriteSamplingMessageResponse writeSamplingMessageResponse = this.samplingMessageBlockingStub.writeSamplingMessage(writeSamplingMessageRequest);
        Assert.assertEquals(createSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);

        SamplingMessageGrpcService.ReadSamplingMessageRequest readSamplingMessageRequest = readSamplingMessageRequest(msgName);
        SamplingMessageGrpcService.ReadSamplingMessageResponse readSamplingMessageResponse = this.samplingMessageBlockingStub.readSamplingMessage(readSamplingMessageRequest);
        Assert.assertEquals(readSamplingMessageResponse.getMessageContent(), msgContent);
    }

    @Test
    public void clearNewMessage() {
        final String msgName = "Test01";
        final String msgContent = "content01";
        SamplingMessageGrpcService.CreateSamplingMessageRequest createSamplingMessageRequest = createSamplingMessageRequest(msgName, 120);
        SamplingMessageGrpcService.CreateSamplingMessageResponse createSamplingMessageResponse = this.samplingMessageBlockingStub.createSamplingMessage(createSamplingMessageRequest);
        Assert.assertEquals(createSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);

        SamplingMessageGrpcService.WriteSamplingMessageRequest writeSamplingMessageRequest = writeSamplingMessageRequest(msgName, msgContent);
        SamplingMessageGrpcService.WriteSamplingMessageResponse writeSamplingMessageResponse = this.samplingMessageBlockingStub.writeSamplingMessage(writeSamplingMessageRequest);
        Assert.assertEquals(writeSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);

        SamplingMessageGrpcService.ReadSamplingMessageRequest readSamplingMessageRequest = readSamplingMessageRequest(msgName);
        SamplingMessageGrpcService.ReadSamplingMessageResponse readSamplingMessageResponse = this.samplingMessageBlockingStub.readSamplingMessage(readSamplingMessageRequest);
        Assert.assertEquals(readSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);
        Assert.assertEquals(readSamplingMessageResponse.getMessageContent(), msgContent);
        Assert.assertTrue(readSamplingMessageResponse.getMessageIsValid());

        SamplingMessageGrpcService.ClearSamplingMessageRequest clearSamplingMessageRequest = clearSamplingMessageRequest(msgName);
        SamplingMessageGrpcService.ClearSamplingMessageResponse clearSamplingMessageResponse = this.samplingMessageBlockingStub.clearSamplingMessage(clearSamplingMessageRequest);
        Assert.assertEquals(clearSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);

        SamplingMessageGrpcService.ReadSamplingMessageResponse readSamplingMessageResponse2 = this.samplingMessageBlockingStub.readSamplingMessage(readSamplingMessageRequest);
        Assert.assertEquals(readSamplingMessageResponse2.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);
        Assert.assertTrue(readSamplingMessageResponse2.getMessageContent().isEmpty());
        Assert.assertFalse(readSamplingMessageResponse2.getMessageIsValid());
    }

    @Test
    public void getNewMessageStatus() {
        final String msgName = "Test01";
        final String msgContent = "content01";
        SamplingMessageGrpcService.CreateSamplingMessageRequest createSamplingMessageRequest = createSamplingMessageRequest(msgName, 120);
        SamplingMessageGrpcService.CreateSamplingMessageResponse createSamplingMessageResponse = this.samplingMessageBlockingStub.createSamplingMessage(createSamplingMessageRequest);
        Assert.assertEquals(createSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);

        SamplingMessageGrpcService.GetSamplingMessageStatusRequest getSamplingMessageStatusRequest = getSamplingMessageStatusRequest(msgName);
        SamplingMessageGrpcService.GetSamplingMessageStatusResponse getSamplingMessageStatusResponse = this.samplingMessageBlockingStub.getSamplingMessageStatus(getSamplingMessageStatusRequest);
        Assert.assertTrue(getSamplingMessageStatusResponse.getMessageIsEmpty());
        Assert.assertTrue(getSamplingMessageStatusResponse.getMessageIsValid());

        SamplingMessageGrpcService.WriteSamplingMessageRequest writeSamplingMessageRequest = writeSamplingMessageRequest(msgName, msgContent);
        SamplingMessageGrpcService.WriteSamplingMessageResponse writeSamplingMessageResponse = this.samplingMessageBlockingStub.writeSamplingMessage(writeSamplingMessageRequest);
        Assert.assertEquals(writeSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);

        SamplingMessageGrpcService.GetSamplingMessageStatusRequest getSamplingMessageStatusRequest2 = getSamplingMessageStatusRequest(msgName);
        SamplingMessageGrpcService.GetSamplingMessageStatusResponse getSamplingMessageStatusResponse2 = this.samplingMessageBlockingStub.getSamplingMessageStatus(getSamplingMessageStatusRequest2);
        Assert.assertFalse(getSamplingMessageStatusResponse2.getMessageIsEmpty());
        Assert.assertTrue(getSamplingMessageStatusResponse2.getMessageIsValid());

        SamplingMessageGrpcService.ReadSamplingMessageRequest readSamplingMessageRequest = readSamplingMessageRequest(msgName);
        SamplingMessageGrpcService.ReadSamplingMessageResponse readSamplingMessageResponse = this.samplingMessageBlockingStub.readSamplingMessage(readSamplingMessageRequest);
        Assert.assertEquals(readSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);
        Assert.assertEquals(readSamplingMessageResponse.getMessageContent(), msgContent);
        Assert.assertTrue(readSamplingMessageResponse.getMessageIsValid());

        SamplingMessageGrpcService.ClearSamplingMessageRequest clearSamplingMessageRequest = clearSamplingMessageRequest(msgName);
        SamplingMessageGrpcService.ClearSamplingMessageResponse clearSamplingMessageResponse = this.samplingMessageBlockingStub.clearSamplingMessage(clearSamplingMessageRequest);
        Assert.assertEquals(clearSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);

        SamplingMessageGrpcService.GetSamplingMessageStatusRequest getSamplingMessageStatusRequest3 = getSamplingMessageStatusRequest(msgName);
        SamplingMessageGrpcService.GetSamplingMessageStatusResponse getSamplingMessageStatusResponse3 = this.samplingMessageBlockingStub.getSamplingMessageStatus(getSamplingMessageStatusRequest3);
        Assert.assertEquals(getSamplingMessageStatusResponse3.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);
        Assert.assertTrue(getSamplingMessageStatusResponse3.getMessageIsEmpty());
        Assert.assertFalse(getSamplingMessageStatusResponse3.getMessageIsValid());

        SamplingMessageGrpcService.DeleteSamplingMessageRequest deleteSamplingMessageRequest = deleteSamplingMessageRequest(msgName);
        SamplingMessageGrpcService.DeleteSamplingMessageResponse deleteSamplingMessageResponse = this.samplingMessageBlockingStub.deleteSamplingMessage(deleteSamplingMessageRequest);
        Assert.assertEquals(deleteSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);


        SamplingMessageGrpcService.GetSamplingMessageStatusRequest getSamplingMessageStatusRequest4 = getSamplingMessageStatusRequest(msgName);
        SamplingMessageGrpcService.GetSamplingMessageStatusResponse getSamplingMessageStatusResponse4 = this.samplingMessageBlockingStub.getSamplingMessageStatus(getSamplingMessageStatusRequest4);
        Assert.assertEquals(getSamplingMessageStatusResponse4.getStatusCode(), SamplingMessageGrpcService.StatusCode.NOT_FOUND);
    }


}
