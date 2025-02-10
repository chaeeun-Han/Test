package com.example.myapp.qna.model;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class Answer {
	private String content;
	private Timestamp date;
}
