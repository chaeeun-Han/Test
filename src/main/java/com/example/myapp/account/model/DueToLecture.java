package com.example.myapp.account.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 진행 예정 강의
public class DueToLecture {
	 private String title;
	 private Date start_time;
	 private Date end_time;	 	 
}
