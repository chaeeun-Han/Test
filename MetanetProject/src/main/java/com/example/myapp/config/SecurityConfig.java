package com.example.myapp.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.myapp.jwt.JwtAuthenticationFilter;
import com.example.myapp.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// Rest API이기 때문에 csrf 보안 사용 X
		http.csrf((csrfConfig) -> csrfConfig.disable());
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
		// JWT를 사용하기 때문에 세션 사용 비활성
		http.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// 인가 규칙 설정
		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
				.requestMatchers("/auth/**").permitAll()
				.requestMatchers("/email/**").permitAll()
				.requestMatchers("/lectures/all").permitAll()
				.requestMatchers(HttpMethod.GET, "lectures/**").permitAll()
				.requestMatchers(HttpMethod.GET, "lectures/*/reviews").permitAll()
				.requestMatchers("/lectures/likes/**").hasAnyRole("Student", "Teacher", "Admin")
				.requestMatchers(HttpMethod.GET, "/lectures/*/questions").permitAll()
	            .requestMatchers(HttpMethod.GET, "/lectures/*/questions/*").permitAll()
				.requestMatchers("/lectures/**").hasAnyRole("Student", "Teacher", "Admin")
				.requestMatchers("/revenue").hasAnyRole("Teacher", "Admin")
	            .requestMatchers("/admin/**").hasRole("Admin")
				.requestMatchers("/ws/**").permitAll()
	            .requestMatchers("/user/**").permitAll()
	            .requestMatchers("/topic/**", "/queue/**").permitAll()
	            .requestMatchers("/zoom/*/meetings").hasAnyRole("Teacher", "Admin")
	            .requestMatchers("/zoom/*").permitAll()
	            .requestMatchers("/error", "/favicon.ico").permitAll()
				.anyRequest().authenticated() // 모든 요청은 인증이 필요
		);

		// JWT 인증을 위해 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행
		http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

		return http.build(); // 필터 체인 빌드
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); // 비밀번호 암호화에 BCryptPasswordEncoder 사용
	}
	
	@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://127.0.0.1:5500", "http://localhost:8080", "https://bamjun.click:443"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
