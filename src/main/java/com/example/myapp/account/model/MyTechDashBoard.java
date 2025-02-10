package com.example.myapp.account.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
// 내학습률 대시보드
public class MyTechDashBoard {
	private List<AttendLecture> attendLecture;
	private List<ProgressLecture> progreesLecture;    
}
