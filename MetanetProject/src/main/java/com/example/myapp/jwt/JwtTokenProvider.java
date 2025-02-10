package com.example.myapp.jwt;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.myapp.jwt.model.JwtToken;
import com.example.myapp.member.dao.IMemberRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring Security와 JWT 토큰을 사용하여 인증과 권한 부여하는 클래스 JWT 토큰의 생성, 복호화 검증
 * 
 * 
 */
@Slf4j
@Component
public class JwtTokenProvider {
	private final Key key;

	@Value("${jwt.max-age.access}")
	private long accessExpire;

	@Value("${jwt.max-age.refresh}")
	private long refreshExpire;

	@Value("${jwt.secret}")
	private String secretKey;
	
	@Autowired
	IMemberRepository memberDao;

	// application.yml에서 secret 값 가져와서 key에 저장
	public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	// Member 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
	public JwtToken generateToken(Authentication authentication) {

		// 권한 가져오기
		String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));

		long now = (new Date()).getTime();

		// Access Token 생성
		Date accessTokenExpiresIn = new Date(now + accessExpire);
		String accessToken = Jwts.builder().setSubject(authentication.getName()).claim("auth", authorities)
				.setExpiration(accessTokenExpiresIn).signWith(key, SignatureAlgorithm.HS256).compact();

		// Refresh Token 생성
		String refreshToken = Jwts.builder().setSubject(authentication.getName())
				.setExpiration(new Date(now + refreshExpire)).signWith(key, SignatureAlgorithm.HS256).compact();

		return JwtToken.builder().grantType("Bearer").accessToken(accessToken).refreshToken(refreshToken).build();
	}

	// Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
	public Authentication getAuthentication(String accessToken) {
		// Jwt 토큰 복호화
		Claims claims = parseClaims(accessToken);

		if (claims.get("auth") == null) {
			throw new RuntimeException("권한 정보가 없는 토큰입니다.");
		}

		// 클레임에서 권한 정보 가져오기
		Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
				.map(SimpleGrantedAuthority::new).collect(Collectors.toList());

		// UserDetails 객체를 만들어서 Authentication return
		// UserDetails: interface, User: UserDetails를 구현한 class
		UserDetails principal = new User(claims.getSubject(), "", authorities);
		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	// 토큰 정보를 검증하는 메서드
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			throw new JwtException("Invalid JWT Token");
		} catch (ExpiredJwtException e) {
			throw new JwtException("Expired JWT Token");
		} catch (UnsupportedJwtException e) {
			throw new JwtException("Unsupported JWT Token");
		} catch (IllegalArgumentException e) {
			throw new JwtException("JWT claims string is empty.");
		}
	}

	// accessToken
	private Claims parseClaims(String accessToken) {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

	// Refresh Token에서 id와 expire time 추출하는 메서드
	public String decodeRefreshToken(String refreshToken) {
		try {
			// JWT 복호화하여 Claims 객체 추출
			Claims claims = Jwts.parserBuilder().setSigningKey(secretKey) // 비밀 키 설정
					.build().parseClaimsJws(refreshToken) // JWT 파싱
					.getBody();

			// id와 만료 시간을 추출
			String userId = claims.getSubject();			
			Date expiration = claims.getExpiration(); // 만료 시간 추출

			// 만료 시간이 현재 시간보다 이전이면 토큰이 만료된 것
			if (expiration != null && expiration.before(new Date())) {
				return "expire"; // 만료된 토큰
			}

			return userId; // 유효한 토큰이면 userId 반환
		} catch (SignatureException e) {
			return "invalid signature"; // 서명 검증 실패
		} catch (IllegalArgumentException e) {
			return "invalid token"; // 토큰이 잘못되었거나 파싱 오류
		} catch (Exception e) {
			return "error"; // 기타 예외
		}
	}


	
	public JwtToken generateTokenWithUserId(String userId) {
		long now = (new Date()).getTime();
		
		String role= memberDao.getRoleById(userId);
		// Access Token 생성
		Date accessTokenExpiresIn = new Date(now + accessExpire);
		String accessToken = Jwts.builder().setSubject(userId).claim("auth", role)
				.setExpiration(accessTokenExpiresIn).signWith(key, SignatureAlgorithm.HS256).compact();

		// Refresh Token 생성
		String refreshToken = Jwts.builder().setSubject(userId)
				.setExpiration(new Date(now + refreshExpire)).signWith(key, SignatureAlgorithm.HS256).compact();

		return JwtToken.builder().grantType("Bearer").accessToken(accessToken).refreshToken(refreshToken).build();	    	 	    	
	}

	


}