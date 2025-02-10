package com.example.myapp.lecture.model;

import java.util.List;

import lombok.Data;

@Data
public class DeleteLectureRequest {
	private List<Long> lectureIds;
}
