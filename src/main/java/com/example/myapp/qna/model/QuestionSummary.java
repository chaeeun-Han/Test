package com.example.myapp.qna.model;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class QuestionSummary {
	private Long questionId;
	private String title;
	private String writer;
	private Timestamp date;
	private int answerCount;
}
