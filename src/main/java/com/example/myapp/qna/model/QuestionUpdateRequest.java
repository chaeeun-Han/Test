package com.example.myapp.qna.model;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class QuestionUpdateRequest {
	private String title;
    private String content;
    private List<MultipartFile> images;
}
