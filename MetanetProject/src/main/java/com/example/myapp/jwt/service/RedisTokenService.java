package com.example.myapp.jwt.service;

import com.example.myapp.jwt.model.RefreshToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisTokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.max-age.refresh}")
    private long refreshTokenTtl ; 

    public RedisTokenService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 리프레시 토큰 저장 시 TTL 동적 설정
    // refresh token의 기간을 지정할 때에는 밀리초 단위를 사용하기 때문에 
    // 레디스와 동일한 기간으로 설정해야하기 때문에 / 1000을 적용
    public void saveRefreshToken(String tokenKey, RefreshToken refreshToken) {

    	// 기존의 값이 있을 경우 삭제 후 저장
    	if(redisTemplate.hasKey(tokenKey)) {
    		redisTemplate.delete(tokenKey);
    	}
        redisTemplate.opsForValue().set(tokenKey, refreshToken, refreshTokenTtl / 1000, TimeUnit.SECONDS);
    }

    // 리프레시 토큰 가져오기
    public RefreshToken getRefreshToken(String tokenKey) {
        return (RefreshToken) redisTemplate.opsForValue().get(tokenKey);
    }

    // 리프레시 토큰 존재 여부 확인
    public boolean existsRefreshToken(String tokenKey) {
        // Redis에서 해당 키에 대한 값이 있는지 확인
        return redisTemplate.hasKey(tokenKey); // true: 키가 존재, false: 키가 없음
    }
    
    // 레디스에 있는 값 삭제
    public void deleteRefreshToken(String userid) {  
    	redisTemplate.delete(userid);
    }
    
}
