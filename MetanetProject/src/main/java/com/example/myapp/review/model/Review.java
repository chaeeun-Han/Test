package com.example.myapp.review.model;

import java.sql.Date;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Review {
    private Long reviewId;
    private String profile;
    private Long lectureId;
    private Long memberId;
    private String content;
    private Date reviewDate;
    private Long answerId;
    private int deleted;
}
