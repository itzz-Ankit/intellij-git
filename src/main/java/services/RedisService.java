package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> T get(String key, Class<T> entityClass) {
        Object value;
        try {
            value = redisTemplate.opsForValue().get(key);
        } catch (RuntimeException e) {
            log.error("Unable to connect to Redis while reading key {}", key, e);
            return null;
        }

        if (value == null) {
            return null;
        }

        try {
            return objectMapper.readValue(value.toString(), entityClass);
        } catch (Exception e) {
            log.error("Exception while reading from Redis", e);
            return null;
        }
    }

    public void set(String key, Object value, Long ttl) {
        try {
            redisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(value),
                    ttl,
                    TimeUnit.SECONDS
            );
        } catch (Exception e) {
            log.error("Exception while writing to Redis", e);
        }
    }
}
