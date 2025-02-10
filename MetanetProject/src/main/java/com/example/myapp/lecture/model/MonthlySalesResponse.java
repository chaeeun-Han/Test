package com.example.myapp.lecture.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlySalesResponse {
	private final List<MonthlySalesDto> monthlySales;
	private final String selectedMonth;
	private final List<LectureRevenueDto> lectureDetails;
}
