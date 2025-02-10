package com.example.myapp.admin.service;

import org.springframework.http.ResponseEntity;

import com.example.myapp.common.response.ResponseDto;
import com.example.myapp.lecture.model.DeleteLectureRequest;
import com.example.myapp.member.model.DeleteMemberRequest;

public interface IAdminService {

	ResponseEntity<ResponseDto> getAllMembers();
	ResponseEntity<ResponseDto> deleteMembers(DeleteMemberRequest memberIds);
	ResponseEntity<ResponseDto> deleteAllMembers();
	ResponseEntity<ResponseDto> deleteLectures(DeleteLectureRequest lectureIds);
	ResponseEntity<ResponseDto> deleteAllLectures();
	
}
