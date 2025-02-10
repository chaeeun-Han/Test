package com.example.myapp.qna.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Notification {
	private String message;
	private LocalDateTime timestamp;
}
