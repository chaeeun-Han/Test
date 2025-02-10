package com.example.myapp.aop;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Log {
	private Long logId;
	private String requestUrl;
	private String requestMethod;
	private int responseStatus;
	private String clientIp;
	private LocalDateTime requestTime;
	private LocalDateTime responseTime;
	private String serviceName;
}
