package com.example.myapp.account.model;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class AdminLectureDashBoard {
	
	private List<DueToLecture> dutoLecture;
	private List<IngLecture> ingLecture;
	private List<EndLecture> endLecture;
	
	 
}
