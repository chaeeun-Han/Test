package com.example.myapp.lecture.model;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
public class Lecture {
    private Long lectureId;
    private Long memberId;
    private String title;
    private String profile;
    private String profileUrl;
    private String description;
    private String descriptionPic;
    private String descriptionPicUrl;
    private String category;
    private Integer price;
    private Boolean status;
    private String link;
    private Integer likes;
    private Integer limitStudent;
    private Integer enrolledStudents;
    private Integer leftSpace;
    private LocalDateTime deadlineTime;
    private LocalDateTime lecturesDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean deleted;
    private String tags;
}
