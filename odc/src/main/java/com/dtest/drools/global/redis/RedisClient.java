package com.dtest.drools.global.redis;

import com.dtest.drools.global.apipayload.code.status.ErrorStatus;
import com.dtest.drools.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisClient {
    private final RedisTemplate<String, Object> redisTemplate; // key-value 형식으로 저장

    // 데이터 저장
    public void setValue(String key, Object value, Long timeout) {
        try {
            ValueOperations<String, Object> values = redisTemplate.opsForValue();
            values.set(key, value, Duration.ofMillis(timeout));
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.REDIS_NOT_FOUND);
        }
    }

    // 데이터 조회
    public String getValue(String key) {
        try {
            ValueOperations<String, Object> values = redisTemplate.opsForValue();
            if (values.get(key) == null) {
                return "";
            }
            return values.get(key).toString();
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.REDIS_NOT_FOUND);
        }
    }

    // 데이터 삭제
    public void deleteValue(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.REDIS_NOT_FOUND);
        }
    }

    // 데이터 유무 확인
    public boolean checkExistsValue(String key) {
        try {
            return redisTemplate.hasKey(key); // 캐시에 key가 존재하는 지 확인
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.REDIS_NOT_FOUND);
        }
    }
}
