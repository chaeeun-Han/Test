package com.example.myapp.zoom.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ZoomMeetingSettings {
	@JsonProperty("host_video")
	private boolean hostVideo = true;
	
	@JsonProperty("participant_video")
	private boolean participantVideo = true;
	
	@JsonProperty("join_before_host")
	private boolean joinBeforeHost = true;
	
	@JsonProperty("mute_upon_entry")
	private boolean muteUponEntry = true;
	
	@JsonProperty("auto_recording")
	private String autoRecording = "none";
	
	@JsonProperty("waiting_room")
	private boolean waitingRoom = false;
	
	@JsonProperty("approval_type")
	private int approvalType = 1;
	
}