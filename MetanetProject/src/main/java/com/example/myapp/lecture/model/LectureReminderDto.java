package com.example.myapp.lecture.model;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LectureReminderDto {
	private final String email;
	private final String title;
	private final String startTime;
	private final String link;
	
	private final String attendId;
	private final Long meetingId;
	private final Long teacherId;
}
