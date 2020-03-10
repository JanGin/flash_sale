package me.chan.service;

import java.util.concurrent.TimeUnit;

public interface StringRedisService {

    void set(String key, String value, long expire, TimeUnit timeUnit);

    String get(String key);

    boolean delete(String key);

    Long incr(String key);

    Long decr(String key);
}
