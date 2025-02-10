package com.example.myapp.account.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.example.myapp.common.response.ResponseDto;

public interface IAccountService {
	ResponseEntity<ResponseDto> getLecture(String memberId);
	
	ResponseEntity<ResponseDto> insertCategory(String tags, String memberId);

	ResponseEntity<ResponseDto> updateProfile(String user, MultipartFile files);

	ResponseEntity<ResponseDto> getMyPage(String user);

//	ResponseEntity<ResponseDto> getMyTech(String user);
//
//	ResponseEntity<ResponseDto> getLectureDashBoard(String user);
}
