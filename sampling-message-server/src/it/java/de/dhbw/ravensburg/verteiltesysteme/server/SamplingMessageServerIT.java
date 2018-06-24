package de.dhbw.ravensburg.verteiltesysteme.server;

import de.dhbw.ravensburg.verteiltesysteme.de.dhbw.ravensburg.verteiltesysteme.rpc.SamplingMessageGrpc;
import de.dhbw.ravensburg.verteiltesysteme.de.dhbw.ravensburg.verteiltesysteme.rpc.SamplingMessageGrpcService;
import de.dhbw.ravensburg.verteiltesysteme.server.service.ServiceConfig;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

@Slf4j
public class SamplingMessageServerIT {

    final Integer testingPort = 8888;
    ServiceEndpoint serviceEndpoint;
    final int maxSamplingMessageNameLength = 20;
    final int maxSamplingMessageContentLength = 20;
    final int maxSamplingMessageCount = 20;
    ServiceConfig serviceConfig = new ServiceConfig((long) maxSamplingMessageNameLength, (long) maxSamplingMessageContentLength, (long) maxSamplingMessageCount, testingPort);
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

    /*
        Start new server instance for each test method
     */
    @BeforeMethod
    public void beforeMethod() {
        this.serviceEndpoint = new ServiceEndpoint(serviceConfig);
        this.serviceEndpoint.init();

        ManagedChannelBuilder<?> managedChannelBuilder = ManagedChannelBuilder.forAddress("127.0.0.1", testingPort).usePlaintext();
        ManagedChannel managedChannel = managedChannelBuilder.build();
        samplingMessageBlockingStub = SamplingMessageGrpc.newBlockingStub(managedChannel);
    }

    /*
        Kill server after test method
     */
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


    @Test
    public void getStatusOfNonExistingMessage() {
        final String msgName = "iDontExist";
        SamplingMessageGrpcService.GetSamplingMessageStatusRequest getSamplingMessageStatusRequest = getSamplingMessageStatusRequest(msgName);
        SamplingMessageGrpcService.GetSamplingMessageStatusResponse getSamplingMessageStatusResponse = this.samplingMessageBlockingStub.getSamplingMessageStatus(getSamplingMessageStatusRequest);
        Assert.assertEquals(getSamplingMessageStatusResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.NOT_FOUND);
        Assert.assertFalse(getSamplingMessageStatusResponse.getMessageIsEmpty());
        Assert.assertFalse(getSamplingMessageStatusResponse.getMessageIsValid());
    }

    @Test
    public void readNonExistingMessage() {
        final String msgName = "iDontExist";
        SamplingMessageGrpcService.ReadSamplingMessageRequest readSamplingMessageRequest = readSamplingMessageRequest(msgName);
        SamplingMessageGrpcService.ReadSamplingMessageResponse readSamplingMessageResponse = this.samplingMessageBlockingStub.readSamplingMessage(readSamplingMessageRequest);
        Assert.assertEquals(readSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.NOT_FOUND);
        Assert.assertFalse(readSamplingMessageResponse.getMessageIsValid());
    }

    @Test
    public void clearNonExistingMessage() {
        final String msgName = "iDontExist";
        SamplingMessageGrpcService.ClearSamplingMessageRequest clearSamplingMessageRequest = clearSamplingMessageRequest(msgName);
        SamplingMessageGrpcService.ClearSamplingMessageResponse clearSamplingMessageResponse = this.samplingMessageBlockingStub.clearSamplingMessage(clearSamplingMessageRequest);
        Assert.assertEquals(clearSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.NOT_FOUND);
    }

    @Test
    public void writeNonExistingMessage() {
        final String msgName = "iDontExist";
        final String msgContent = "random";
        SamplingMessageGrpcService.WriteSamplingMessageRequest writeSamplingMessageRequest = writeSamplingMessageRequest(msgName, msgContent);
        SamplingMessageGrpcService.WriteSamplingMessageResponse writeSamplingMessageResponse = this.samplingMessageBlockingStub.writeSamplingMessage(writeSamplingMessageRequest);
        Assert.assertEquals(writeSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.NOT_FOUND);
    }

    @Test
    public void deleteNonExistingMessage() {
        SamplingMessageGrpcService.DeleteSamplingMessageRequest deleteSamplingMessageRequest = deleteSamplingMessageRequest("iDontExist");
        SamplingMessageGrpcService.DeleteSamplingMessageResponse deleteSamplingMessageResponse = this.samplingMessageBlockingStub.deleteSamplingMessage(deleteSamplingMessageRequest);
        Assert.assertEquals(deleteSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.NOT_FOUND);
    }


    @Test(timeOut = 20000)
    public void messageTimeoutTest() throws InterruptedException {
        final String msgName = "Test01";
        final String msgContent = "content01";
        final long lifetime = 2;
        SamplingMessageGrpcService.CreateSamplingMessageRequest createSamplingMessageRequest = createSamplingMessageRequest(msgName, lifetime);
        SamplingMessageGrpcService.CreateSamplingMessageResponse createSamplingMessageResponse = this.samplingMessageBlockingStub.createSamplingMessage(createSamplingMessageRequest);
        Assert.assertEquals(createSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);

        Thread.sleep(lifetime * 1000 + 1);

        SamplingMessageGrpcService.GetSamplingMessageStatusRequest getSamplingMessageStatusRequest = getSamplingMessageStatusRequest(msgName);
        SamplingMessageGrpcService.GetSamplingMessageStatusResponse getSamplingMessageStatusResponse = this.samplingMessageBlockingStub.getSamplingMessageStatus(getSamplingMessageStatusRequest);
        Assert.assertFalse(getSamplingMessageStatusResponse.getMessageIsValid());
    }

    @Test
    public void exceedMaxMessageNameLengthAtCreation() {
        final String tooLongMsgName = String.join("", Collections.nCopies(maxSamplingMessageNameLength + 1, "x"));
        SamplingMessageGrpcService.CreateSamplingMessageRequest createSamplingMessageRequest = createSamplingMessageRequest(tooLongMsgName, 120);
        SamplingMessageGrpcService.CreateSamplingMessageResponse createSamplingMessageResponse = this.samplingMessageBlockingStub.createSamplingMessage(createSamplingMessageRequest);
        Assert.assertEquals(createSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.ILLEGAL_MESSAGE_NAME_LENGTH);
    }

    @Test
    public void exceedMaxMessageNameLengthAtGetingStatus() {
        final String tooLongMsgName = String.join("", Collections.nCopies(maxSamplingMessageNameLength + 1, "x"));
        SamplingMessageGrpcService.GetSamplingMessageStatusRequest getSamplingMessageStatusRequest = getSamplingMessageStatusRequest(tooLongMsgName);
        SamplingMessageGrpcService.GetSamplingMessageStatusResponse getSamplingMessageStatusResponse = this.samplingMessageBlockingStub.getSamplingMessageStatus(getSamplingMessageStatusRequest);
        Assert.assertEquals(getSamplingMessageStatusResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.ILLEGAL_MESSAGE_NAME_LENGTH);
        Assert.assertFalse(getSamplingMessageStatusResponse.getMessageIsEmpty());
        Assert.assertFalse(getSamplingMessageStatusResponse.getMessageIsValid());
    }

    @Test
    public void exceedMaxMessageNameLengthAtRading() {
        final String tooLongMsgName = String.join("", Collections.nCopies(maxSamplingMessageNameLength + 1, "x"));
        SamplingMessageGrpcService.ReadSamplingMessageRequest readSamplingMessageRequest = readSamplingMessageRequest(tooLongMsgName);
        SamplingMessageGrpcService.ReadSamplingMessageResponse readSamplingMessageResponse = this.samplingMessageBlockingStub.readSamplingMessage(readSamplingMessageRequest);
        Assert.assertEquals(readSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.ILLEGAL_MESSAGE_NAME_LENGTH);
        Assert.assertFalse(readSamplingMessageResponse.getMessageIsValid());
    }

    @Test
    public void exceedMaxMessageNameLengthAtClearing() {
        final String tooLongMsgName = String.join("", Collections.nCopies(maxSamplingMessageNameLength + 1, "x"));
        SamplingMessageGrpcService.ClearSamplingMessageRequest clearSamplingMessageRequest = clearSamplingMessageRequest(tooLongMsgName);
        SamplingMessageGrpcService.ClearSamplingMessageResponse clearSamplingMessageResponse = this.samplingMessageBlockingStub.clearSamplingMessage(clearSamplingMessageRequest);
        Assert.assertEquals(clearSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.ILLEGAL_MESSAGE_NAME_LENGTH);
    }

    @Test
    public void exceedMaxMessageNameLengthAtWriting() {
        final String tooLongMsgName = String.join("", Collections.nCopies(maxSamplingMessageNameLength + 1, "x"));
        final String msgContent = "random";
        SamplingMessageGrpcService.WriteSamplingMessageRequest writeSamplingMessageRequest = writeSamplingMessageRequest(tooLongMsgName, msgContent);
        SamplingMessageGrpcService.WriteSamplingMessageResponse writeSamplingMessageResponse = this.samplingMessageBlockingStub.writeSamplingMessage(writeSamplingMessageRequest);
        Assert.assertEquals(writeSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.ILLEGAL_MESSAGE_NAME_LENGTH);
    }

    @Test
    public void exceedMaxMessageNameLengthAtDeleting() {
        final String tooLongMsgName = String.join("", Collections.nCopies(maxSamplingMessageNameLength + 1, "x"));
        SamplingMessageGrpcService.DeleteSamplingMessageRequest deleteSamplingMessageRequest = deleteSamplingMessageRequest(tooLongMsgName);
        SamplingMessageGrpcService.DeleteSamplingMessageResponse deleteSamplingMessageResponse = this.samplingMessageBlockingStub.deleteSamplingMessage(deleteSamplingMessageRequest);
        Assert.assertEquals(deleteSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.ILLEGAL_MESSAGE_NAME_LENGTH);
    }


    @Test
    public void exceedMaxMessageContentLength() {
        final String msgName = String.join("", Collections.nCopies(maxSamplingMessageNameLength, "x"));
        final String tooLongMsgContent = String.join("", Collections.nCopies(maxSamplingMessageContentLength + 1, "x"));
        final long lifetime = 2;
        SamplingMessageGrpcService.CreateSamplingMessageRequest createSamplingMessageRequest = createSamplingMessageRequest(msgName, 120);
        SamplingMessageGrpcService.CreateSamplingMessageResponse createSamplingMessageResponse = this.samplingMessageBlockingStub.createSamplingMessage(createSamplingMessageRequest);
        Assert.assertEquals(createSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);

        SamplingMessageGrpcService.WriteSamplingMessageRequest writeSamplingMessageRequest = writeSamplingMessageRequest(msgName, tooLongMsgContent);
        SamplingMessageGrpcService.WriteSamplingMessageResponse writeSamplingMessageResponse = this.samplingMessageBlockingStub.writeSamplingMessage(writeSamplingMessageRequest);
        Assert.assertEquals(writeSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.ILLEGAL_MESSAGE_CONTENT_LENGTH);

    }

    @Test
    public void exceedMaxMessageCount() {
        for (int i = 0; i < maxSamplingMessageCount; ++i) {
            SamplingMessageGrpcService.CreateSamplingMessageRequest createSamplingMessageRequest = createSamplingMessageRequest(String.valueOf(i), 120);
            SamplingMessageGrpcService.CreateSamplingMessageResponse createSamplingMessageResponse = this.samplingMessageBlockingStub.createSamplingMessage(createSamplingMessageRequest);
            Assert.assertEquals(createSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.SUCCESS);
        }
        final String msgName = "killer";
        SamplingMessageGrpcService.CreateSamplingMessageRequest createSamplingMessageRequest = createSamplingMessageRequest(msgName, 120);
        SamplingMessageGrpcService.CreateSamplingMessageResponse createSamplingMessageResponse = this.samplingMessageBlockingStub.createSamplingMessage(createSamplingMessageRequest);
        Assert.assertEquals(createSamplingMessageResponse.getStatusCode(), SamplingMessageGrpcService.StatusCode.MESSAGE_COUNT_EXCEEDED);
    }


}
