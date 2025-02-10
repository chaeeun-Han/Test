package com.example.myapp.account.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
// 완료된 강의
public class EndLecture {
	private String detailTitle;
	private Date startTime;
	private Date endTime;
	private double coursePercent;
	private double lectureId;
}
