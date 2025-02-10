package com.example.myapp.lecture.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LectureList {
    private Long lecture_list_id;
    private Long lecture_id;
    private Long member_id;
    private String title;
    private String description;
    private String start_time;
    private String end_time;
}
