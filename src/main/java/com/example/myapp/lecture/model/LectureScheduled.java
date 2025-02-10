package com.example.myapp.lecture.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LectureScheduled {
    private String email;
    private String schedule; // GROUP_CONCAT 결과 저장 필드
}

