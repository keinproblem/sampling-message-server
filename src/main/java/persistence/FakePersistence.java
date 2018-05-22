package persistence;

import java.util.concurrent.ConcurrentHashMap;

class FakePersistence<K, V> extends ConcurrentHashMap<K, V> {
}
