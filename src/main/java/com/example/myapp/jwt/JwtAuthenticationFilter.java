package com.example.myapp.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/*\
 * 
 * 커스텀 필터로 UserPasswordAuthenticationFilter 이전에 실행
 * 클라이언트로부터 들어오는 요청에서 JWT 토큰을 처리하고 유효한 토큰인 경우
 * 해당 토큰의 인증 정보를 SecurityConfig에 저장하여 인증된 요청을 처리하도록 한다.
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
private final JwtTokenProvider jwtTokenProvider;
   private final AntPathMatcher pathMatcher = new AntPathMatcher();
   public final static List<String> ACCEPTED_URL_LIST = List.of("/auth/join", "/auth/login", "/auth/password",
           "/auth/re-access-token", "/auth/delete","/email/send", "/email/verify", "/email/mail-password",
           "/ws/**", "/lectures/all", "/zoom/auth", "/zoom/oauth2/callback", "/error", "/favicon.ico");

   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
           throws IOException, ServletException {
	   HttpServletRequest httpRequest = (HttpServletRequest) request;
	   
       // 1. Request Header에서 JWT 토큰 추출
       String token = resolveToken(httpRequest);

       // 2. 허용된 URL일 경우 JWT 토큰 검증을 건너뛰기
       String requestURI = httpRequest.getRequestURI();      
       List<String> acceptedUrls = ACCEPTED_URL_LIST;
       String method = httpRequest.getMethod();


       boolean isAcceptedUrl = false; 

       // 허용된 URL 목록에 대해 순차적으로 확인
       for (String acceptedUrl : acceptedUrls) {               
           if (pathMatcher.match(acceptedUrl, requestURI)) {
               isAcceptedUrl = true; 
               break;  
           }
       }
       
       if (!isAcceptedUrl && "GET".equalsIgnoreCase(method)) {
           if (pathMatcher.match("lectures/**", requestURI) ||  // 전체 lectures 경로 허용
               pathMatcher.match("lectures/*/reviews", requestURI) ||
               pathMatcher.match("/lectures/*/questions", requestURI) ||
               pathMatcher.match("/lectures/*/questions/*", requestURI)) {
               isAcceptedUrl = true;
           }
       }

       if (!isAcceptedUrl) {  // 허용된 URL이 아닐 경우에만 토큰 검증
           if (token == null) {
               throw new JwtException("토큰이 빈 값입니다.");
           }

           // JWT 토큰 검증
           if (jwtTokenProvider.validateToken(token)) {
               Authentication authentication = jwtTokenProvider.getAuthentication(token);
               // SecurityContext에 인증 정보 저장
               SecurityContextHolder.getContext().setAuthentication(authentication);
           } else {
               throw new JwtException("유효하지 않은 토큰입니다.");
           }
       }

       // 3. 필터 체인 진행
       chain.doFilter(request, response);
   }


   // Request Header에서 토큰 정보 추출
   private String resolveToken(HttpServletRequest request) {
      String bearerToken = request.getHeader("Authorization");
      if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
         return bearerToken.substring(7);
      }
      return null;
   }
}