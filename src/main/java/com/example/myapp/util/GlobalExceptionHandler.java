package com.example.myapp.util;

import java.io.IOException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

	// JWT 오류 시 response로 message 넘기기 위한 메소드 - 신영서
    @ExceptionHandler(JwtException.class)
    public void handleJwtException(JwtException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + ex.getMessage() + "\"}");
    }
}

