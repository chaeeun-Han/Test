package com.example.myapp.lecture.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
public class LectureFile {
    Long fileId;
    Long lectureId;
    Long memberId;
    String fileUrl;
}
