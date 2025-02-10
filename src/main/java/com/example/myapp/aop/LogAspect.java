package com.example.myapp.aop;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {
	
	private final IAopRepository aopRepository;
	
	@Around("execution(* com.example.myapp..*Service.*(..))")
	public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
		
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
        LocalDateTime requestTime = LocalDateTime.now();
        
        HttpServletRequest request = null;
        HttpServletResponse response = null;
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            request = attributes.getRequest();
            response = attributes.getResponse();
        }

        String url = (request == null || "org.springframework.mock.web.MockHttpServletRequest".equals(request.getClass().getName())) ? "N/A" : request.getRequestURL().toString();
        String httpMethod = (request == null || "org.springframework.mock.web.MockHttpServletRequest".equals(request.getClass().getName())) ? "N/A" : request.getMethod();
        String clientIp = (request == null || "org.springframework.mock.web.MockHttpServletRequest".equals(request.getClass().getName())) ? "N/A" : getRemoteAddr(request);
        int status = (request == null || "org.springframework.mock.web.MockHttpServletRequest".equals(request.getClass().getName())) ? 0 : response.getStatus();
        
        log.info("[[[AOP-before log]]]-{}: Request to URL '{}' with HTTP Method '{}' from IP '{}'", methodName, url, httpMethod, clientIp);
        
        Object result;
        LocalDateTime responseTime;
        try {
            result = joinPoint.proceed();
            responseTime = LocalDateTime.now();
            log.info("[[[AOP-after log]]]-{}: Method executed successfully", methodName);
        } catch (Throwable throwable) {
            responseTime = LocalDateTime.now();
            log.error("[[[AOP-exception log]]]-{}: Exception occurred: {}", methodName, throwable.getMessage());
            throw throwable;
        }
        
        Log logEntry = new Log();
        logEntry.setRequestUrl(url);
        logEntry.setRequestMethod(httpMethod);
        logEntry.setClientIp(clientIp);
        logEntry.setRequestTime(requestTime);
        logEntry.setResponseTime(responseTime);
        logEntry.setResponseStatus(status);
        logEntry.setServiceName(className + "." + methodName);

        log.info("Log : {}", logEntry);
        aopRepository.insertLog(logEntry);

        return result;
	}
	
	public static String getRemoteAddr(HttpServletRequest request) {
	    String ip = request.getHeader("X-Forwarded-For");

	    if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
	        // X-Forwarded-For 값이 여러 개일 경우 첫 번째 IP만 사용
	        return ip.split(",")[0].trim();
	    }

	    ip = request.getHeader("Proxy-Client-IP");
	    if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) return ip;

	    ip = request.getHeader("WL-Proxy-Client-IP");
	    if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) return ip;

	    ip = request.getHeader("HTTP_CLIENT_IP");
	    if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) return ip;

	    ip = request.getHeader("HTTP_X_FORWARDED_FOR");
	    if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) return ip;

	    ip = request.getHeader("X-Real-IP");
	    if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) return ip;

	    ip = request.getHeader("X-RealIP");
	    if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) return ip;

	    ip = request.getHeader("REMOTE_ADDR");
	    if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) return ip;

	    ip = request.getRemoteAddr();

	    // IPv6 로컬주소 (::1) → IPv4 (127.0.0.1) 변환
	    if ("0:0:0:0:0:0:0:1".equals(ip)) {
	        ip = "127.0.0.1";
	    }

	    return ip;
	}
}
