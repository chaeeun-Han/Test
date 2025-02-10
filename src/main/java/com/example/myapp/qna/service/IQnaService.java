package com.example.myapp.qna.service;

import org.springframework.http.ResponseEntity;

import com.example.myapp.common.response.ResponseDto;
import com.example.myapp.qna.model.Answer;
import com.example.myapp.qna.model.AnswerUpdateRequest;
import com.example.myapp.qna.model.Question;
import com.example.myapp.qna.model.QuestionUpdateRequest;

public interface IQnaService {
	ResponseEntity<ResponseDto> registerQuestion(Long lectureId, String memberId, Question question);
	ResponseEntity<ResponseDto> registerAnswer(Long lectureId, Long questionId, String memberId, Answer answer);
	ResponseEntity<ResponseDto> getQuestionSummaries(Long lectureId);
	ResponseEntity<ResponseDto> updateQuestion(Long lectureId, Long questionId, String memberId, QuestionUpdateRequest questionUpdateRequest);
	ResponseEntity<ResponseDto> getQuestionDetails(Long questionId);
	ResponseEntity<ResponseDto> deleteQuestion(String memberId, Long questionId);
	ResponseEntity<ResponseDto> updateAnswer(Long answerId, String user, AnswerUpdateRequest answerUpdateRequest);
	ResponseEntity<ResponseDto> deleteAnswer(Long answerId, String user);
}
