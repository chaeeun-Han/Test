package com.example.myapp.review.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myapp.common.response.ResponseCode;
import com.example.myapp.common.response.ResponseDto;
import com.example.myapp.common.response.ResponseMessage;
import com.example.myapp.lecture.service.ILectureService;
import com.example.myapp.review.model.Review;
import com.example.myapp.review.service.IReviewService;
import com.example.myapp.util.GetAuthenUser;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RestController
@RequestMapping("/lecture")
public class ReviewControlloer {

    @Autowired
    IReviewService reviewService;

    @Autowired
    ILectureService lectureService;

    // review 추가 -- 고범준
    @SuppressWarnings({ "rawtypes" })
    @PostMapping("/{lecture_id}/reviews")
    public ResponseEntity<ResponseDto> registerReview(@RequestBody Review review,
            @PathVariable("lecture_id") Long lectureId) {

        String user = GetAuthenUser.getAuthenUser();
        // 인증되지 않은 경우는 바로 처리
        if (user == null) {
            return ResponseDto.noAuthentication();
        }
        Long member_id = lectureService.getMemberIdById(user);

        review.setMemberId(member_id);
        review.setLectureId(lectureId);
        try {
            reviewService.registerReview(review);
        } catch (Exception e) {
            return ResponseDto.databaseError();
        }
        ResponseDto responseBody = new ResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        return ResponseEntity.ok(responseBody);

    }

    // review 답글 추가 -- 고범준
    @SuppressWarnings({ "rawtypes" })
    @PostMapping("/{lecture_id}/reviews/{answer_id}")
    public ResponseEntity<ResponseDto> registerReReview(@RequestBody Review review,
            @PathVariable("lecture_id") Long lectureId,
            @PathVariable("answer_id") Long answerId) {

        String user = GetAuthenUser.getAuthenUser();
        // 인증되지 않은 경우는 바로 처리
        if (user == null) {
            return ResponseDto.noAuthentication();
        }
        Long member_id = lectureService.getMemberIdById(user);

        review.setMemberId(member_id);
        review.setLectureId(lectureId);
        if (answerId == null || answerId.toString().isEmpty()) {
            return ResponseDto.invalidGrant();
        }
        review.setAnswerId(answerId);
        try {
            reviewService.registerReview(review);
        } catch (Exception e) {
            return ResponseDto.databaseError();
        }
        ResponseDto responseBody = new ResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        return ResponseEntity.ok(responseBody);

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("/{lecture_id}/reviews")
    public ResponseEntity<ResponseDto> getReviews(@PathVariable("lecture_id") Long lectureId) {

        List<Review> reviews = new ArrayList<Review>();
        try {
            reviews = reviewService.getReviews(lectureId);
        } catch (Exception e) {
            return ResponseDto.databaseError();
        }
        ResponseDto responseBody = new ResponseDto(ResponseCode.SUCCESS,
                ResponseMessage.SUCCESS, reviews);
        return ResponseEntity.ok(responseBody);

    }

    @SuppressWarnings("rawtypes")
    @PutMapping("/{lecture_id}/reviews/{review_id}")
    public ResponseEntity<ResponseDto> updateReview(@RequestBody Review review,
            @PathVariable("lecture_id") Long lectureId,
            @PathVariable("review_id") Long reviewId) {

        String user = GetAuthenUser.getAuthenUser();
        // 인증되지 않은 경우는 바로 처리
        if (user == null) {
            return ResponseDto.noAuthentication();
        }
        Long member_id = lectureService.getMemberIdById(user);
        review.setMemberId(member_id);
        review.setLectureId(lectureId);
        review.setReviewId(reviewId);
        log.info("Updating review: " + review.toString());

        try {
            reviewService.updateReview(review);
        } catch (Exception e) {
            log.error("Database update error", e);
            return ResponseDto.databaseError();
        }

        return ResponseEntity.ok(new ResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS));
    }

    @SuppressWarnings({ "rawtypes" })
    @DeleteMapping("/{lecture_id}/reviews/{review_id}")
    public ResponseEntity<ResponseDto> deleteReview(@PathVariable("lecture_id") Long lectureId,
            @PathVariable("review_id") Long reviewId) {

        String user = GetAuthenUser.getAuthenUser();
        // 인증되지 않은 경우는 바로 처리
        if (user == null) {
            return ResponseDto.noAuthentication();
        }

        Long memberId = lectureService.getMemberIdById(user);

        Review review = new Review();

        review.setMemberId(memberId);
        review.setLectureId(lectureId);
        review.setReviewId(reviewId);

        try {
            reviewService.deleteReview(review);
        } catch (Exception e) {
            return ResponseDto.databaseError();
        }
        ResponseDto responseBody = new ResponseDto(ResponseCode.SUCCESS,
                ResponseMessage.SUCCESS);
        return ResponseEntity.ok(responseBody);

    }

}