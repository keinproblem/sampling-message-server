package persistence;

import model.SamplingMessage;

import java.util.Optional;

public class DataAccessObject {
    private FakePersistence<String, SamplingMessage> fakePersistence;

    public Optional<SamplingMessage> getSamplingMessage(final String messageName) {
        return Optional.of(fakePersistence.get(messageName));
    }

    public void putSamplingMessage(final String messageName, final SamplingMessage samplingMessage) {
        fakePersistence.put(messageName, samplingMessage);
    }

    public boolean samplingMessageExists(final String messageName) {
        return fakePersistence.containsKey(messageName);
    }

    public boolean deleteSamplingMessage(final String messageName) {
        return fakePersistence.remove(messageName) != null;
    }
}
