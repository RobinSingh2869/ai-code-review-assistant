package com.Robin.core_api.Service;

import com.Robin.core_api.Model.Entity.CodeReview;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import java.time.Duration;

@Slf4j
@Service
public class RedisService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    private static int MAX_REQUESTS_PER_HOUR = 10;
    private static Duration RATE_LIMIT_TTL = Duration.ofHours(1);
    private static Duration CACHE_TTL = Duration.ofHours(24);

    public boolean isRateLimited(Long userId){
        String key = "rate_limit:" + userId;
        String countStr = redisTemplate.opsForValue().get(key);
        if(countStr == null){
            redisTemplate.opsForValue().set(key,"1", RATE_LIMIT_TTL);
            return false;
        }
        int count = Integer.parseInt(countStr);
        if(count>=MAX_REQUESTS_PER_HOUR){
            log.warn("Rate Limit exceeded for user: {}", userId);
            return true; //User is limited
        }
        redisTemplate.opsForValue().increment(key);
        return false;
    }
    public CodeReview getCachedReview(String codeHash){
        String key = "review_cache:" + codeHash;
        String cachedJson = redisTemplate.opsForValue().get(key);

        if(cachedJson!=null){
            try{
                log.info("Cache Hit! Returning cached Review for hash: {}", codeHash);
                return objectMapper.readValue(cachedJson, CodeReview.class);
            }catch (Exception e){
                log.error("Failed to deserialize cached review for hash: {}",codeHash,e);
            }
        }
        return null; //cache Miss
    }
    public void cacheReview(String codeHash,CodeReview review){
        String key = "review_cache:" + codeHash;
        try{
            String json = objectMapper.writeValueAsString(review);
            redisTemplate.opsForValue().set(key, json,CACHE_TTL);
            log.info("Successfully cache review for hash:{}", codeHash);
        }catch (Exception e){
            log.error("Failed to serialize review for caching, hash:{}", codeHash, e);
        }
    }
}
