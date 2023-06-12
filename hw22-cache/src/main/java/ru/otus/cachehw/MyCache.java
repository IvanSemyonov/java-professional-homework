package ru.otus.cachehw;


import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;

public class MyCache<K, V> implements HwCache<K, V> {

    private static final String PUT_ACTION = "PUT";
    private static final String GET_ACTION = "GET";
    private static final String REMOVE_ACTION = "REMOVE";

    private final WeakHashMap<K,V> cache = new WeakHashMap<>();
    private final List<WeakReference<HwListener<K,V>>> listeners = new ArrayList<>();
    private final ReferenceQueue<HwListener<K,V>> queue = new ReferenceQueue<>();

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
        notifyListeners(key, value, PUT_ACTION);
    }

    @Override
    public void remove(K key) {
        V removed = cache.remove(key);
        notifyListeners(key, removed, REMOVE_ACTION);
    }

    @Override
    public V get(K key) {
        V value = cache.get(key);
        notifyListeners(key, value, GET_ACTION);
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(new WeakReference<>(listener, queue));
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(new WeakReference<>(listener, queue));
    }

    private void notifyListeners(K key, V value, String action) {
        listeners.forEach(weakReferenceListener -> {
            HwListener<K, V> listener = weakReferenceListener.get();
            if (Objects.nonNull(listener)) {
                listener.notify(key, value, action);
            }
            else {
                listeners.remove(weakReferenceListener);
            }
        });

    }
}
