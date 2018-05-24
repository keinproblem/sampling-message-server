package de.dhbw.ravensburg.verteiltesysteme.persistence;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Fake persistence class
 * Mocking a partially synchronized persistence key-value storage by being nothing else than a simple java.util.concurrent.ConcurrentHashMap
 *
 * @param <K> Key generic type
 * @param <V> Value generic type
 */
public class FakePersistence<K, V> extends ConcurrentHashMap<K, V> {
}
