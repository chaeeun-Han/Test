package com.example.myapp.qna.model;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

@Data
public class QuestionDetail {
	private Long questionId;
	private String memberId;
	private String lectureTitle;
	private String questionTitle;
	private String questionContent;
	private Timestamp questionDate;
	private List<String> images;
	private List<AnswerDetail> answerDetails;
}
