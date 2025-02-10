package com.example.myapp.member.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder; // Lombok의 Builder 어노테이션
import lombok.Getter; // Lombok의 Getter 어노테이션
import lombok.Setter; // Lombok의 Setter 어노테이션
import lombok.ToString; // Lombok의 ToString 어노테이션

@Getter @Setter
@ToString
public class Member implements UserDetails{
	
	private String profile;
    private String id;
	private String password;
	private String phone;
	private String email;	
	private Date birth;
	private String name;
    private String role;
    private String bank;
    private int deleted;   
    
    
    
   
    private List<String> roles = new ArrayList<>();
    
    // 사용자가 가지고 있는 권한들을 반환하는 메서드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    // 계정이 만료되었는지 확인하는 메서드
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠겨있는지 확인하는 메서드
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //사용자 인증 정보가 만료되었는지 확인하는 메서드
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 로그인 가능한지 확인하는 메서드
    @Override
    public boolean isEnabled() {
        return true;
    }
   
    // 사용자 ID 반환
    @Override
    public String getUsername() {
    	return this.id;
    }
}