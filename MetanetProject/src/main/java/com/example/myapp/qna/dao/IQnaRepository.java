package com.example.myapp.qna.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.example.myapp.qna.model.Answer;
import com.example.myapp.qna.model.AnswerDetail;
import com.example.myapp.qna.model.AnswerUpdateRequest;
import com.example.myapp.qna.model.Question;
import com.example.myapp.qna.model.QuestionDetail;
import com.example.myapp.qna.model.QuestionSummary;

@Repository
@Mapper
public interface IQnaRepository {
	@Options(useGeneratedKeys = true, keyProperty = "question.questionId")
	void insertQuestion(@Param("lectureId") Long lectureId, @Param("writerId") Long memberUID, @Param("question") Question question);
	void insertQuestionImage(@Param("questionId") Long questionId, @Param("memberId") Long memberUID, @Param("lectureId") Long lectureId, @Param("url") String imageUrl);
	void insertAnswer(@Param("lectureId") Long lectureId, @Param("questionId") Long questionId, @Param("writerId") Long memberUID, @Param("answer") Answer answer);
	List<QuestionSummary> getQuestionSummaries(Long lectureId);
	Long getMemberIdByQuestionId(Long questionId);
	QuestionDetail getQuestionDetail(Long questionId);
	List<AnswerDetail> getAnswerDetails(Long questionId);
	Question getQuestionByQuestionId(Long QuestionId);
	void updateQuestion(@Param("questionId") Long questionId, @Param("title") String title, @Param("content") String content);
	void deleteQuestionImages(Long questionId);
	void deleteQuestion(Long questionId);
	void updateAnswer(@Param("answerId") Long answerId, @Param("answerUpdateRequest") AnswerUpdateRequest answerUpdateRequest);
	Long getMemberIdByAnswerId(Long answerId);
	void deleteAnswer(Long answerId);
	String getQuestionTitle(Long questionId);
}
