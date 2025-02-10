package com.example.myapp.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myapp.admin.service.IAdminService;
import com.example.myapp.common.response.ResponseDto;
import com.example.myapp.lecture.model.DeleteLectureRequest;
import com.example.myapp.member.model.DeleteMemberRequest;
import com.example.myapp.util.GetAuthenUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	IAdminService adminService;
	
	// 회원 전체 조회
	@GetMapping("/accounts")
	ResponseEntity<ResponseDto> getAllMembers() {
        String user =  GetAuthenUser.getAuthenUser();
        // 인증되지 않은 경우는 바로 처리
        if (user == null) {
            return ResponseDto.noAuthentication();
        }
        
		ResponseEntity<ResponseDto> response = adminService.getAllMembers();
		return response;
	}
	
	// 회원 삭제
	@DeleteMapping("/accounts")
	ResponseEntity<ResponseDto> deleteMembers(@RequestBody DeleteMemberRequest memberIds) {
		String memberId = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
        try {
            if (authentication != null) {
                // 현재 인증된 사용자 정보
                memberId = authentication.getName();
            }

            if (memberId == null)
                return ResponseDto.noAuthentication();
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return ResponseDto.databaseError();
        }
        
		ResponseEntity<ResponseDto> response = adminService.deleteMembers(memberIds);
		return response;
	}
	
	// 전체 회원 삭제
	@DeleteMapping("/accounts/all")
	ResponseEntity<ResponseDto> deleteAllMembers() {
		String memberId = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
        try {
            if (authentication != null) {
                // 현재 인증된 사용자 정보
                memberId = authentication.getName();
            }

            if (memberId == null)
                return ResponseDto.noAuthentication();
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return ResponseDto.databaseError();
        }
        
		ResponseEntity<ResponseDto> response = adminService.deleteAllMembers();
		return response;
	}
	
	// 강의 삭제
	@DeleteMapping("/lectures")
	ResponseEntity<ResponseDto> deleteLectures(@RequestBody DeleteLectureRequest lectureIds) {
		String memberId = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
        try {
            if (authentication != null) {
                // 현재 인증된 사용자 정보
                memberId = authentication.getName();
            }

            if (memberId == null)
                return ResponseDto.noAuthentication();
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return ResponseDto.databaseError();
        }
        
		ResponseEntity<ResponseDto> response = adminService.deleteLectures(lectureIds);
		return response;
	}
	
	// 전체 강의 삭제
	@DeleteMapping("/lectures/all")
	ResponseEntity<ResponseDto> deleteAllLectures() {
		String memberId = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
        try {
            if (authentication != null) {
                // 현재 인증된 사용자 정보
                memberId = authentication.getName();
            }

            if (memberId == null)
                return ResponseDto.noAuthentication();
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return ResponseDto.databaseError();
        }
        
		ResponseEntity<ResponseDto> response = adminService.deleteAllLectures();
		return response;
	}
}
