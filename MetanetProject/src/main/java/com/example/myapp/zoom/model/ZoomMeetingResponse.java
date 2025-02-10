package com.example.myapp.zoom.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ZoomMeetingResponse {
	
	private Long lectureListId;
	
	@JsonProperty("id")
	private String meetingId; //줌 회의 id
	
	@JsonProperty("join_url")
	private String link;
}