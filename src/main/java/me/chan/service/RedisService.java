package me.chan.service;

import java.util.concurrent.TimeUnit;

public interface RedisService {

    void set(String key, Object value, long expirationTime, TimeUnit timeUnit);

    Object get(String key);

    Boolean containsKey(String key);

    Long incr(String key);

    Long incr(String key, Long delta);

    Long decr(String key);

    Long decr(String key, Long delta);

}
