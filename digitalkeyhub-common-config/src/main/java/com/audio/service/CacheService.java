package com.audio.service;

public interface CacheService {
    void save(String key, Object value);

    Object get(String key);

    void delete(String key);

    default boolean contains(String key) {
        return get(key) != null;
    }
}
