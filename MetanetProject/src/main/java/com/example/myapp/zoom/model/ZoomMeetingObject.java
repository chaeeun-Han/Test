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
public class ZoomMeetingObject {
	@JsonProperty("topic")
	private String topic;
	
	@JsonProperty("type")
	private int type = 2;
	
	//사용자한테 받음
	@JsonProperty("start_time")
	private String startTime; //"2022-03-25T07:32:55Z"
	
	@JsonProperty("duration")
	private int duration;
	
	@JsonProperty("timezone")
	private String timezone = "Asia/Seoul";
	
	@JsonProperty("host_email")
	private String hostEmail;

	@JsonProperty("settings")
	private ZoomMeetingSettings settings = new ZoomMeetingSettings();
}