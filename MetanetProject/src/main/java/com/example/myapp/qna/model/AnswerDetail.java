package com.example.myapp.qna.model;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class AnswerDetail {
	private Long answerId;
	private String content;
	private Timestamp date;
	private String profile;
	private String writerId;
}
