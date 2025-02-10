package com.example.myapp.account.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 출석률
public class AttendLecture {
	private String lectureTitle; 
	private double attendPercent;
}
