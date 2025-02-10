package com.example.myapp.account.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
//진행 중인 강의
public class IngLecture {
	private String title;
	private Date start_time;
	private Date end_time;
	private Long lecture_detail_id;
	private double attendPercent;
	
}
