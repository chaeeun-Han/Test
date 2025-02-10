package com.example.myapp.zoom.model;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ZoomDate {
	private Long lectureListId;
	private LocalDate date;
	private LocalTime startTime;
	private LocalTime endTime;
}