package com.example.backend.common.util;

import com.example.backend.dto.response.project.ProjectResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate stringRedisTemplate;

    private final RedisTemplate<String, Object> redisTemplate;

    private final RedisTemplate<String, List<ProjectResponseDto>> rankingRedisTemplate;

    public String getData(String key) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public void setData(String key, String value, Long expiredTime){
        stringRedisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.MILLISECONDS);
    }

    public void deleteData(String key) {
        stringRedisTemplate.delete(key);
    }

    public void incrementProjectRankingScore(Long projectId) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.incrementScore("project:ranking", projectId, 1);
    }

    public List<Long> getProjectRankingIdList() {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        Set<Object> rankingIds = Objects.requireNonNull(zSetOperations.reverseRange("project:ranking", 0, 9), "프로젝트 랭킹 id들이 비어있음");
        return rankingIds.stream().map(id -> ((Integer)id).longValue()).collect(Collectors.toList());
    }

    public void projectCacheSave(List<ProjectResponseDto> projectResponseDtoList) {
        rankingRedisTemplate.opsForValue().set("project:ranking:list", projectResponseDtoList, 7, TimeUnit.DAYS);
    }

    public List<ProjectResponseDto> getProjectCacheList() {
        return rankingRedisTemplate.opsForValue().get("project:ranking:list");
    }

    public void deleteZSetData(String key) {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.removeRange(key, 0, -1);
    }

}
