package com.example.myapp.member.controller;

import com.example.myapp.common.response.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myapp.member.model.Email;
import com.example.myapp.member.service.IMemberService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

	@Autowired
	IMemberService memberService;

	// 회원가입 인증코드 메일 발송
	@PostMapping("/send")
	public ResponseEntity<ResponseDto> mailSend(@RequestBody Email email) throws MessagingException {
		ResponseEntity<ResponseDto> response = memberService.sendEmail("join", email.getEmail());
		return response;
	}

	// 인증코드 인증
	@PostMapping("/verify")
	public ResponseEntity<ResponseDto> verify(@RequestBody Email email) {
		ResponseEntity<ResponseDto> response = memberService.verifyEmailCode(email.getEmail(), email.getVerifyCode());
		return response;
	}

	// pw 인증번호
	@PostMapping("/mail-password")
	public ResponseEntity<ResponseDto> mailPw(@RequestBody Email email) throws MessagingException {

		if (memberService.findByEmail(email.getEmail()) == false) {
			return ResponseDto.notExistEmail();
		}

		ResponseEntity<ResponseDto> response = memberService.sendEmail("password", email.getEmail());

		return response;
	}

	// 이메일 변경 인증번호
	@PostMapping("/mail-email")
	public ResponseEntity<ResponseDto> mailEmail(@RequestBody Email email) throws MessagingException {

		if (memberService.findByEmail(email.getEmail()) == false) {
			return ResponseDto.notExistEmail();
		}

		ResponseEntity<ResponseDto> response = memberService.sendEmail("email", email.getEmail());

		return response;
	}

	
}