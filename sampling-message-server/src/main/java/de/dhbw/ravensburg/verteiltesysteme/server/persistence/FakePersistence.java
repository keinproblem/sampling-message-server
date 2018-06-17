package de.dhbw.ravensburg.verteiltesysteme.server.persistence;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Fake persistence class
 * Mocking a partially synchronized persistence key-value storage by being nothing else than a simple {@link ConcurrentHashMap}
 * All the concurrency magic happens here - no need for explicit synchronization or locking.
 *
 * @param <K> Key generic type
 * @param <V> Value generic type
 */
public class FakePersistence<K, V> extends ConcurrentHashMap<K, V> {
}
