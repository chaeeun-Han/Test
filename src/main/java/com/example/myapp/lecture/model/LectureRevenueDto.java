package com.example.myapp.lecture.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LectureRevenueDto {
	private final String month; // start_date의 월
	private final Long lectureId;
	private final String title;
	private final String status;
	private final int price; // 수익 (수수료 10% 차감)
}
