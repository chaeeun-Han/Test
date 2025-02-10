package com.example.myapp.qna.model;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class Question {
	private Long questionId;
	private String title;
	private String content;
	private int deleted;
	private Timestamp date;
	private List<MultipartFile> images;
}
