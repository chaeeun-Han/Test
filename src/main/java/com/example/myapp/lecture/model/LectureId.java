package com.example.myapp.lecture.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LectureId {
    Long memberId;
    Long lectureId;
}
