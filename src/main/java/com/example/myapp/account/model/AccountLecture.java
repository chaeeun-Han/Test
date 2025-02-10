package com.example.myapp.account.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccountLecture {
	
	private String category;
	private String title;
	private String profile;	
	//lectures의 강의 종료일 추가해야함
	//attends의 수료가능여부 추가해야함
}
