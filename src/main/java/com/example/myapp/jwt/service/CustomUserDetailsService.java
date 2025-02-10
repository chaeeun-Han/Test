package com.example.myapp.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.myapp.member.model.Member;
import com.example.myapp.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        return (UserDetails) memberService.findById(id)
        		 .map(this::createUserDetails)
                 .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
    }
    
    private UserDetails createUserDetails(Member member) {
    	return User.builder()
			.username(member.getId())
			.password(member.getPassword())
            .roles(member.getRole().toString())
            .build();
    }
    
    
}