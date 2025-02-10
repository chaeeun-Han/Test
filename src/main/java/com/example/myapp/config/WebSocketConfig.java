package com.example.myapp.config;

import java.security.Principal;
import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.example.myapp.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	private final JwtTokenProvider jwtTokenProvider;
	
	@Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    public Principal determineUser(
                            ServerHttpRequest request,
                            WebSocketHandler wsHandler,
                            Map<String, Object> attributes) {

                        String authHeader = request.getHeaders().getFirst("Authorization");
                        if (authHeader != null && authHeader.startsWith("Bearer ")) {
                            String token = authHeader.substring(7);
                            if (jwtTokenProvider.validateToken(token)) {
                                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                                return authentication;
                            }
                        }
                        return null;
                    }
                })
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트의 구독 경로
        registry.enableSimpleBroker("/queue", "/topic");
        registry.setApplicationDestinationPrefixes("/app");
        // 사용자별 메시지 전송을 위한 prefix (convertAndSendToUser()가 내부적으로 사용)
        registry.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
        	@Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader("Authorization");

                    if (token == null) {
                        System.out.println("### STOMP CONNECT 요청에 Authorization 헤더가 없음");
                        throw new RuntimeException("STOMP 연결에 인증 정보가 없습니다.");
                    }

                    if (token.startsWith("Bearer ")) {
                        token = token.substring(7);
                    }

                    if (!jwtTokenProvider.validateToken(token)) {
                        System.out.println("### 유효하지 않은 JWT 토큰: " + token);
                        throw new RuntimeException("유효하지 않은 JWT 토큰입니다.");
                    }

                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    String username = authentication.getName();
                    
                    //Principal 설정 - Spring Security의 Authentication 객체 사용
                    accessor.setUser(authentication);

                    System.out.println("### STOMP 연결 인증 성공: " + username);
                }

                return message;
            }
        });
    }
}
