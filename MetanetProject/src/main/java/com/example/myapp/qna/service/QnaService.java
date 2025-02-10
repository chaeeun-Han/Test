package com.example.myapp.qna.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myapp.common.response.ResponseCode;
import com.example.myapp.common.response.ResponseDto;
import com.example.myapp.common.response.ResponseMessage;
import com.example.myapp.lecture.dao.ILectureRepository;
import com.example.myapp.lecture.model.Lecture;
import com.example.myapp.member.dao.IMemberRepository;
import com.example.myapp.qna.dao.IQnaRepository;
import com.example.myapp.qna.model.Answer;
import com.example.myapp.qna.model.AnswerDetail;
import com.example.myapp.qna.model.AnswerUpdateRequest;
import com.example.myapp.qna.model.Notification;
import com.example.myapp.qna.model.Question;
import com.example.myapp.qna.model.QuestionDetail;
import com.example.myapp.qna.model.QuestionSummary;
import com.example.myapp.qna.model.QuestionUpdateRequest;
import com.example.myapp.util.S3FileUploader;

@Service
public class QnaService implements IQnaService {
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	private IMemberRepository memberRepository;
	
	@Autowired
	private IQnaRepository qnaRepository;
	
	@Autowired
	private S3FileUploader s3FileUploader;
	
	@Autowired
	private ILectureRepository lectureRepository;

	//질문 등록 - 소진
	@Override
	@Transactional
	public ResponseEntity<ResponseDto> registerQuestion(Long lectureId, String memberId, Question question) {
		//질문 DB에 저장
		try {
			question.setDate(new Timestamp(new Date().getTime()));
			Long memberUID = memberRepository.getMemberIdById(memberId);
			List<String> imageUrls = new ArrayList<>();
		    if (question.getImages() != null && !question.getImages().isEmpty()) {
		    	imageUrls = s3FileUploader.uploadFiles(question.getImages(), "questions", "questionImage", memberUID);
		    }
		    qnaRepository.insertQuestion(lectureId, memberUID, question);
		    for (String imageUrl : imageUrls) {
		    	qnaRepository.insertQuestionImage(question.getQuestionId(), memberUID, lectureId, imageUrl);
		    }
		    
		} catch (Exception exception){
			exception.printStackTrace();
			return ResponseDto.databaseError();
		}
		
		//알림 생성
		Lecture lecture = lectureRepository.getLectureDetail(lectureId);
        String receiverUsername = memberRepository.getIdByMemberId(lecture.getMemberId());

        Notification notification = new Notification();
        notification.setMessage("['" + lecture.getTitle() + "' 강의에 질문이 등록되었습니다]  " + question.getTitle());
        notification.setTimestamp(LocalDateTime.now());

        //자기 강의에 자기가 질문 등록하면 알림X
        if (!receiverUsername.equals(memberId)) {
            messagingTemplate.convertAndSendToUser(receiverUsername, "/queue/notifications", notification);
        }

		return ResponseDto.success();
	}
	
	//질문 목록 조회 - 소진
	@Override
	public ResponseEntity<ResponseDto> getQuestionSummaries(Long lectureId) {
		List<QuestionSummary> questions = null;
		try {
			questions = qnaRepository.getQuestionSummaries(lectureId);
		} catch (Exception exception) {
			exception.printStackTrace();
			return ResponseDto.databaseError();
		}
		ResponseDto responseBody = new ResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS, questions);
		return ResponseEntity.ok(responseBody);
	}

	//답변(댓글) 등록 - 소진
	@Override
	public ResponseEntity<ResponseDto> registerAnswer(Long lectureId, Long questionId, String memberId, Answer answer) {
		//답변 DB 저장
		try {
			answer.setDate(new Timestamp(new Date().getTime()));
			Long memberUID = memberRepository.getMemberIdById(memberId);
			qnaRepository.insertAnswer(lectureId, questionId, memberUID, answer);
		} catch (Exception exception){
			exception.printStackTrace();
			return ResponseDto.databaseError();
		}

		//알림 생성
		Long questionMemberUID = qnaRepository.getMemberIdByQuestionId(questionId);
		String receiverUsername = memberRepository.getIdByMemberId(questionMemberUID);
		String questionTitle = qnaRepository.getQuestionTitle(questionId);
		Notification notification = new Notification();
		notification.setMessage("['" + questionTitle + "' 질문에 " + memberId
				+ "님이 답변을 등록했습니다] " + answer.getContent());
		notification.setTimestamp(LocalDateTime.now());
		
		//자기가 쓴 글에 자기가 답변 등록하면 알림 전송X
		if (!receiverUsername.equals(memberId)) {
			//학생에게 알림 전송
			messagingTemplate.convertAndSendToUser(receiverUsername, "/queue/notifications", notification);
		}
		return ResponseDto.success();
	}

	//질문 수정 - 소진
	@Override
	@Transactional
	public ResponseEntity<ResponseDto> updateQuestion(Long lectureId, Long questionId, String memberId, QuestionUpdateRequest questionUpdateRequest) {
		try {
			Long memberUID = qnaRepository.getMemberIdByQuestionId(questionId);
			Long writerUID = memberRepository.getMemberIdById(memberId);
			if (memberUID != writerUID) {
				return ResponseDto.noPermission();
			}
			qnaRepository.updateQuestion(questionId, questionUpdateRequest.getTitle(), questionUpdateRequest.getContent());
			List<String> imageUrls = new ArrayList<>();
		    if (questionUpdateRequest.getImages() != null && !questionUpdateRequest.getImages().isEmpty()) {
		    	qnaRepository.deleteQuestionImages(questionId);
		    	imageUrls = s3FileUploader.uploadFiles(questionUpdateRequest.getImages(), "questions", "questionImage", memberUID);
		    }
		    for (String imageUrl : imageUrls) {
		    	qnaRepository.insertQuestionImage(questionId, memberUID, lectureId, imageUrl);
		    }
		    
		} catch (Exception exception){
			exception.printStackTrace();
			return ResponseDto.databaseError();
		}
		
		return ResponseDto.success();
	}

	//QnA 상세 내용 조회 - 소진
	@Override
	public ResponseEntity<ResponseDto> getQuestionDetails(Long questionId) {
		QuestionDetail questionDetail = null;
		List<AnswerDetail> answerDetails = null;
		try {
			questionDetail = qnaRepository.getQuestionDetail(questionId);
			answerDetails = qnaRepository.getAnswerDetails(questionId);
			questionDetail.setAnswerDetails(answerDetails);
		} catch (Exception exception){
			exception.printStackTrace();
			return ResponseDto.databaseError();
		}
		ResponseDto responseBody = new ResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS, questionDetail);
		return ResponseEntity.ok(responseBody);
	}

	//질문 삭제 - 소진
	@Override
	public ResponseEntity<ResponseDto> deleteQuestion(String memberId, Long questionId) {
		try {
			Long memberUID = qnaRepository.getMemberIdByQuestionId(questionId);
			Long writerUID = memberRepository.getMemberIdById(memberId);
			if (memberUID != writerUID) {
				return ResponseDto.noPermission();
			}
			
			qnaRepository.deleteQuestion(questionId);
		} catch (Exception exception){
			exception.printStackTrace();
			return ResponseDto.databaseError();
		}
		return ResponseDto.success();
	}

	//답변 수정 - 소진
	@Override
	public ResponseEntity<ResponseDto> updateAnswer(Long answerId, String memberId,
			AnswerUpdateRequest answerUpdateRequest) {
		try {
			Long memberUID = qnaRepository.getMemberIdByAnswerId(answerId);
			Long writerUID = memberRepository.getMemberIdById(memberId);
			if (memberUID != writerUID) {
				return ResponseDto.noPermission();
			}
			
			qnaRepository.updateAnswer(answerId, answerUpdateRequest);
		} catch (Exception exception){
			exception.printStackTrace();
			return ResponseDto.databaseError();
		}
		return ResponseDto.success();
	}

	//답변 삭제 - 소진
	@Override
	public ResponseEntity<ResponseDto> deleteAnswer(Long answerId, String memberId) {
		try {
			Long memberUID = qnaRepository.getMemberIdByAnswerId(answerId);
			Long writerUID = memberRepository.getMemberIdById(memberId);
			if (memberUID != writerUID) {
				return ResponseDto.noPermission();
			}
			
			qnaRepository.deleteAnswer(answerId);
		} catch (Exception exception){
			exception.printStackTrace();
			return ResponseDto.databaseError();
		}
		return ResponseDto.success();
	}

	

}
