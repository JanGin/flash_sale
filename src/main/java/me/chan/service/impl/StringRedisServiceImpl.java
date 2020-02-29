package me.chan.service.impl;

import me.chan.service.StringRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service("stringRedisService")
public class StringRedisServiceImpl implements StringRedisService {

    @Autowired
    private StringRedisTemplate stringTemplate;

    @Override
    public void set(String key, String value, long expire, TimeUnit timeUnit) {
        stringTemplate.opsForValue().set(key, value, expire, timeUnit);
    }

    @Override
    public String get(String key) {

        return stringTemplate.opsForValue().get(key);
    }
}
