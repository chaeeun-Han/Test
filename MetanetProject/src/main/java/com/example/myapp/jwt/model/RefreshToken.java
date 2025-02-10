package com.example.myapp.jwt.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RedisHash(value = "refreshToken")
public class RefreshToken implements Serializable{

	
    private String refreshToken;
    
    @Id
    private String userId;
    
    private static final long serialVersionUID = 1L; // 직렬화 ID 추가
    
    public RefreshToken(String refreshToken, String userId) {
        this.refreshToken = refreshToken;
        this.userId = userId;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
