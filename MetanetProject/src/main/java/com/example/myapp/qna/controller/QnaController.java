package com.example.myapp.qna.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.myapp.common.response.ResponseDto;
import com.example.myapp.qna.model.Answer;
import com.example.myapp.qna.model.AnswerUpdateRequest;
import com.example.myapp.qna.model.Question;
import com.example.myapp.qna.model.QuestionUpdateRequest;
import com.example.myapp.qna.service.IQnaService;
import com.example.myapp.util.GetAuthenUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/lectures")
public class QnaController {

	@Autowired
	private IQnaService qnaService;
	
	//질문 등록
	@PostMapping(value = "/{lectureId}/questions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ResponseDto> registerQuestion(@PathVariable("lectureId") Long lectureId, @RequestPart("question") Question question, @RequestPart(value = "images", required = false) List<MultipartFile> images) {
		String user =  GetAuthenUser.getAuthenUser();
        if (user == null) {
            return ResponseDto.noAuthentication();
        }
		
		if (images != null) {
			question.setImages(images);
		}
		ResponseEntity<ResponseDto> response = qnaService.registerQuestion(lectureId, user, question);
		return response;
	}
	
	//질문 목록 조회
	@GetMapping("/{lectureId}/questions")
	public ResponseEntity<ResponseDto> getQuestionSummaries(@PathVariable("lectureId") Long lectureId) {
		ResponseEntity<ResponseDto> response = qnaService.getQuestionSummaries(lectureId);
		return response;
	}
	
	//질문, 답변 내용 조회
	@GetMapping("/{lectureId}/questions/{questionId}")
	public ResponseEntity<ResponseDto> getQuestionDetails(@PathVariable("questionId") Long questionId) {
		ResponseEntity<ResponseDto> response = qnaService.getQuestionDetails(questionId);
		return response;
	}
	
	//질문 수정
	@PutMapping(value = "/{lectureId}/questions/{questionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ResponseDto> updateQuestion(@PathVariable("lectureId") Long lectureId, @PathVariable("questionId") Long questionId, @RequestPart("questionUpdateRequest") QuestionUpdateRequest questionUpdateRequest, @RequestPart(value = "images", required = false) List<MultipartFile> images) {
		String user =  GetAuthenUser.getAuthenUser();
        if (user == null) {
            return ResponseDto.noAuthentication();
        }
        
        if (images != null) {
        	questionUpdateRequest.setImages(images);
		}
		ResponseEntity<ResponseDto> response = qnaService.updateQuestion(lectureId, questionId, user, questionUpdateRequest);
		return response;
	}
	
	//질문 삭제
	@DeleteMapping("/{lectureId}/questions/{questionId}")
	public ResponseEntity<ResponseDto> deleteQuestion(@PathVariable("questionId") Long questionId) {
		String user =  GetAuthenUser.getAuthenUser();
        if (user == null) {
            return ResponseDto.noAuthentication();
        }
        
        ResponseEntity<ResponseDto> response = qnaService.deleteQuestion(user, questionId);
		return response;
	}
	
	//답변 등록
	@PostMapping("/{lectureId}/questions/{questionId}/answers")
	public ResponseEntity<ResponseDto> registerAnswer(@PathVariable("lectureId") Long lectureId, @PathVariable("questionId") Long questionId, @RequestBody Answer answer) {
		String user =  GetAuthenUser.getAuthenUser();
        if (user == null) {
            return ResponseDto.noAuthentication();
        }
		
		ResponseEntity<ResponseDto> response = qnaService.registerAnswer(lectureId, questionId, user, answer);
		return response;
	}
	
	//답변 수정
	@PutMapping("/{lectureId}/questions/{questionId}/answers/{answerId}")
	public ResponseEntity<ResponseDto> updateAnswer(@PathVariable("answerId") Long answerId, @RequestBody AnswerUpdateRequest answerUpdateRequest) {
		String user =  GetAuthenUser.getAuthenUser();
        if (user == null) {
            return ResponseDto.noAuthentication();
        }

		ResponseEntity<ResponseDto> response = qnaService.updateAnswer(answerId, user, answerUpdateRequest);
		return response;
	}
	
	//답변 삭제
	@DeleteMapping("/{lectureId}/questions/{questionId}/answers/{answerId}")
	public ResponseEntity<ResponseDto> deleteAnswer(@PathVariable("answerId") Long answerId) {
		String user =  GetAuthenUser.getAuthenUser();
        if (user == null) {
            return ResponseDto.noAuthentication();
        }
        
        ResponseEntity<ResponseDto> response = qnaService.deleteAnswer(answerId, user);
        return response;
	}
}