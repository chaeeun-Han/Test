package com.example.myapp.member.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myapp.common.response.ResponseDto;
import com.example.myapp.jwt.model.JwtToken;
import com.example.myapp.member.model.Member;
import com.example.myapp.member.service.IMemberService;
import com.example.myapp.util.GetAuthenUser;
import com.example.myapp.util.RegexUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthRestController {

	@Autowired
	IMemberService memberService;

	@Autowired
	PasswordEncoder passwordEncoder;

	private final RegexUtil regexUtil = new RegexUtil();

	// 회원가입 - 신영서
	@PostMapping("/join")
	public ResponseEntity<ResponseDto> join(@RequestBody Member member) {

		try {
			// 전화번호 검증

			if (member.getPhone() != null && !regexUtil.telNumber(member.getPhone())) {
				return ResponseEntity.badRequest().body(new ResponseDto("REGEX_ERROR", "Phone value not match Regex"));
			}

			// 이메일 검증
			if (member.getEmail() != null && !regexUtil.checkEmail(member.getEmail())) {
				return ResponseEntity.badRequest().body(new ResponseDto("REGEX_ERROR", "Email value not match Regex"));
			}

			// 비밀번호 검증
			if (member.getPassword() != null && !regexUtil.checkPassword(member.getPassword())) {
				return ResponseEntity.badRequest()
						.body(new ResponseDto("REGEX_ERROR", "Password value not match Regex"));
			}

			// 역할이 teacher일 경우 은행 계좌 정보 검증
			if (member.getRole().equals("teacher")) {
				if (member.getBank().isEmpty()) {
					return ResponseDto.nullInputValue(); // 은행 계좌 입력 값 없음
				}
			}

			// 비밀번호 암호화
			String encodedPw = passwordEncoder.encode(member.getPassword());
			member.setPassword(encodedPw);

			// 회원 정보 저장
			memberService.insertMember(member);

		} catch (DuplicateKeyException e) {
			member.setId(null);
			return ResponseDto.duplicatedId(); // ID 중복 오류
		}

		return ResponseDto.success(); // 회원가입 성공
	}

	// 로그인 - 신영서
	@PostMapping("/login")
	public ResponseEntity<Void> login(@RequestBody Member member) {
		JwtToken jwtToken;

		jwtToken = memberService.loginService(member);

		HttpHeaders headers = new HttpHeaders();

		// Access Token을 Authorization 헤더에 추가
		headers.add("Authorization", "Bearer " + jwtToken.getAccessToken());

		// Refresh Token을 HttpOnly 쿠키에 추가
		ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", jwtToken.getRefreshToken())
				.httpOnly(true) // JavaScript에서 접근할 수 없게 함
				.secure(true) // HTTPS 연결에서만 쿠키를 전송
				.sameSite("Strict") // CSRF 방지
				.path("/") // 쿠키 경로 설정
				.build();

		// 응답 헤더에 쿠키 추가
		headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

		return ResponseEntity.ok().headers(headers) // 헤더에 Authorization과 쿠키를 포함
				.build();
	}

	// 비밀번호 재설정 - 신영서
	@PostMapping("/password")
	public ResponseEntity<ResponseDto> resetPw(@RequestBody Member member) {

		// 비밀번호 검증
		if (member.getPassword() != null && !regexUtil.checkPassword(member.getPassword())) {
			return ResponseEntity.badRequest().body(new ResponseDto("REGEX_ERROR", "Password value not match Regex"));
		}

		// 비밀번호 암호화
		String encodedPw = passwordEncoder.encode(member.getPassword());
		member.setPassword(encodedPw);

		memberService.resetPw(member.getEmail(), member.getPassword());

		return ResponseDto.success();
	}

	// 리프레쉬 토큰 재발급 - 신영서
	@PostMapping("/re-access-token")
	public ResponseEntity<ResponseDto> reIssueAccessToken(HttpServletRequest req) {

		Cookie[] cookies = req.getCookies();

		String refreshToken = null;
		String returnToken = null;

		HttpHeaders headers = new HttpHeaders();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("refreshToken".equals(cookie.getName())) {
					refreshToken = cookie.getValue();

					returnToken = memberService.checkRefreshToken(refreshToken);

					if (returnToken.equals("expire")) {
						return ResponseEntity.badRequest()
								.body(new ResponseDto("EXPIRE_TOKEN", "Refresh Token is expire"));
					} else if (returnToken.equals("invalid signature")) {
						return ResponseEntity.badRequest()
								.body(new ResponseDto("INVALID_SIGNATURE", "Signature is invalid"));
					} else if (returnToken.equals("invalid token")) {
						return ResponseEntity.badRequest()
								.body(new ResponseDto("INVALID_TOKEN", "Refresh Token is invalid"));
					} else if (returnToken.equals("error")) {
						return ResponseEntity.badRequest().body(new ResponseDto("ERROR", "Refresh Token is expire"));
					} else if (returnToken.equals("not redis")) {
						return ResponseEntity.badRequest()
								.body(new ResponseDto("NOT_EXISTS_REDIS", "Id is not exists in redis"));
					} else {
						JSONObject jsonObject = new JSONObject(returnToken);

						String accessToken = jsonObject.getString("accessToken"); // "name" 필드의 값
						refreshToken = jsonObject.getString("refreshToken"); // "age" 필드의 값

						headers.add("Authorization", "Bearer " + accessToken);

						// Refresh Token을 HttpOnly 쿠키에 추가
						ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
								.httpOnly(true) // JavaScript에서 접근할 수 없게 함
								.secure(true) // HTTPS 연결에서만 쿠키를 전송
								.sameSite("Strict") // CSRF 방지
								.path("/") // 쿠키 경로 설정
								.build();

						// 응답 헤더에 쿠키 추가
						headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

					}
				}
			}
		}

		// 쿠키에 refresh token이 없을 경우
		if (refreshToken == null) {
			return ResponseEntity.badRequest().body(new ResponseDto("AUTHORIZATION_FAIL", "refresh token이 없음"));
		}

		return ResponseEntity.ok().headers(headers) // 헤더에 Authorization과 쿠키를 포함
				.build();

	}

	// 로그아웃 - 신영서
	@PostMapping("/logout")
	public ResponseEntity<ResponseDto> logout(HttpServletRequest request, HttpServletResponse response) {

		Cookie[] cookies = request.getCookies();

		String refreshToken = null;

		// 쿠키에 refreshToken이 있을 경우 처리
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("refreshToken".equals(cookie.getName())) {
					refreshToken = cookie.getValue();

					boolean isTokenRevoked = memberService.revokeRefreshToken(refreshToken);
					if (!isTokenRevoked) {
						return ResponseEntity.badRequest()
								.body(new ResponseDto("TOKEN_ERROR", "Failed to revoke refresh token"));
					}

					// refreshToken 쿠키 삭제 처리
					cookie.setMaxAge(0);
					cookie.setPath("/");
					cookie.setHttpOnly(true);
					cookie.setSecure(true);

					// 클라이언트에 삭제된 쿠키 전달
					response.addCookie(cookie);
				}
			}
		}

		// 리프레시 토큰이 없을 경우
		if (refreshToken == null) {
			return ResponseEntity.badRequest().body(new ResponseDto("AUTHORIZATION_FAIL", "No refresh token found"));
		}

		// 로그아웃 성공 응답
		return ResponseEntity.ok().body(new ResponseDto("SUCCESS", "Successfully logged out"));
	}

	// 회원 삭제 - 신영서
	@DeleteMapping("/delete")
	public ResponseEntity<ResponseDto> deleteMember(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();

		String refreshToken = null;

		// 쿠키에 refreshToken이 있을 경우 처리
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("refreshToken".equals(cookie.getName())) {
					refreshToken = cookie.getValue();

					// 리프레시 토큰 유효성 검사 (블랙리스트 체크 또는 Redis에서 유효한 토큰인지 확인)
					boolean isValid = memberService.checkRefreshTokenValidity(refreshToken);
					if (!isValid) {
						return ResponseEntity.badRequest()
								.body(new ResponseDto("INVALID_TOKEN", "Refresh token is invalid or expired"));
					}

					// 리프레시 토큰 쿠키 삭제 처리
					cookie.setMaxAge(0);
					cookie.setPath("/");
					cookie.setHttpOnly(true);
					cookie.setSecure(true);

					// 클라이언트에 삭제된 쿠키 전달
					response.addCookie(cookie);
				}
			}
		}

		// 리프레시 토큰이 없을 경우
		if (refreshToken == null) {
			return ResponseEntity.badRequest().body(new ResponseDto("AUTHORIZATION_FAIL", "No refresh token found"));
		}

		// 회원 삭제 처리
		boolean isDeleted = memberService.deleteMemberByToken(refreshToken);
		if (!isDeleted) {
			return ResponseEntity.badRequest().body(new ResponseDto("DELETE_FAILED", "Failed to delete member"));
		}

		// 회원 삭제 성공 응답
		return ResponseEntity.ok().body(new ResponseDto("SUCCESS", "Member successfully deleted"));
	}

	// 이메일 재설정 - 신영서
	@PostMapping("/email")
	public ResponseEntity<ResponseDto> resetEmail(@RequestBody Member member) {
		String user = GetAuthenUser.getAuthenUser();

		if (user == null) {
			return ResponseDto.noAuthentication();
		}
		// 이메일 검증
		if (member.getEmail() != null && !regexUtil.checkEmail(member.getEmail())) {
			return ResponseEntity.badRequest().body(new ResponseDto("REGEX_ERROR", "Email value not match Regex"));
		}

		return memberService.resetEmail(user, member.getEmail());		
	}
}