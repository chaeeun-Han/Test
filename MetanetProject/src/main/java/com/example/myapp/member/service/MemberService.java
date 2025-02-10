package com.example.myapp.member.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.example.myapp.common.response.ResponseCode;
import com.example.myapp.common.response.ResponseDto;
import com.example.myapp.common.response.ResponseMessage;
import com.example.myapp.excel.model.MemberForExcel;
import com.example.myapp.jwt.JwtTokenProvider;
import com.example.myapp.jwt.model.JwtToken;
import com.example.myapp.jwt.model.RefreshToken;
import com.example.myapp.jwt.service.RedisTokenService;
import com.example.myapp.member.dao.IMemberRepository;
import com.example.myapp.member.model.Member;
import com.example.myapp.member.model.MemberResponse;
import com.example.myapp.util.RedisUtil;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements IMemberService {

	private final JavaMailSender javaMailSender;
	private final RedisUtil redisUtil;
	private static final String senderEmail = "neighclova@gmail.com";

	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final JwtTokenProvider jwtTokenProvider;

	@Autowired
	private final RedisTokenService redisTokenService;

	@Autowired
	IMemberRepository memberRepository;

	@Autowired
	JwtTokenProvider jwtProvider;

	@Override
	public void insertMember(Member member) {
		memberRepository.insertMember(member);
	}

	// id 값으로 member 객체 여부 확인
	@Override
	public Optional<Member> findById(String id) {
		return memberRepository.findById(id);
	}

	private String createCode() {
		int leftLimit = 48; // number '0'
		int rightLimit = 122; // alphabet 'z'
		int targetStringLength = 6;
		Random random = new Random();

		return random.ints(leftLimit, rightLimit + 1).filter(i -> (i <= 57 || i >= 65) && (i <= 90 | i >= 97))
				.limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
	}

	// 이메일 내용 초기화
	private String setContext(Object data, String templateName) {
		Context context = new Context();
		TemplateEngine templateEngine = new TemplateEngine();
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

		if ("lecture/lecture_schedule_mail".equals(templateName) || "lecture/lecture_reminder".equals(templateName)) {
			context.setVariable("lectures", data);
		} else if ("member/mail".equals(templateName)) {
			context.setVariable("code", data);
		}

		templateResolver.setPrefix("templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCacheable(false);

		templateEngine.setTemplateResolver(templateResolver);

		return templateEngine.process(templateName, context);
	}

	// 이메일 폼 생성
	private MimeMessage createEmailForm(String type, String email, Object data) throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		message.addRecipients(MimeMessage.RecipientType.TO, email);
		if (type.equals("join")) {
			message.setSubject("[Metanet] 회원가입 이메일 인증번호입니다.");
		} else if (type.equals("password")) {
			message.setSubject("[Metanet] 비밀번호 재발급 인증번호입니다.");
		} else if (type.equals("password")) {
			message.setSubject("[Metanet] 이메일 수정 인증번호입니다.");
		}

		message.setFrom(senderEmail);

		String subject;
		String templateName;
		Object templateData;

		// 이메일 종류에 따른 템플릿 및 데이터 설정
		if ("join".equals(type) || "password".equals(type)) {
			subject = "join".equals(type) ? "[Metanet] 회원가입 이메일 인증번호입니다." : "[Metanet] 비밀번호 재발급 인증번호입니다.";
			templateName = "member/mail";
			String authCode = createCode();
			templateData = authCode;

			// 인증 코드 Redis 저장
			if (data instanceof String) {
				redisUtil.setDataExpire(email, (String) authCode, 60 * 30L);
			}

		} else if ("lecture_schedule".equals(type)) {
			subject = "[Metanet] 강의 일정 안내";
			templateName = "lecture/lecture_schedule_mail";
			templateData = data;

		} else if ("lecture_reminder".equals(type)) {
			subject = "[Metanet] 강의 30분 전 안내 및 zoom 링크 제공";
			templateName = "lecture/lecture_reminder";
			templateData = data;
		} else {
			throw new MessagingException("올바른 이메일 타입이 아닙니다.");
		}

		message.setSubject(subject);
		message.setText(setContext(templateData, templateName), "utf-8", "html");

		return message;
	}

	@Override
	public ResponseEntity<ResponseDto> sendEmail(String type, String email) throws MessagingException {
		return sendEmail(type, email, null); // 기본 데이터 없이 호출
	}

	@Override
	public ResponseEntity<ResponseDto> sendEmail(String type, String email, Object data) throws MessagingException {
		try {
			if (redisUtil.existData(email)) {
				redisUtil.deleteData(email);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseDto.redisError();
		}

		try {
			MimeMessage emailForm = createEmailForm(type, email, data);
			javaMailSender.send(emailForm);
		} catch (MessagingException e) {
			e.printStackTrace();
			return ResponseDto.mailSendFail();
		}
		return ResponseEntity.ok(new ResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS));
	}

	@Override
	public ResponseEntity<ResponseDto> verifyEmailCode(String email, String code) {
		try {
			String codeFoundByEmail = redisUtil.getData(email);

			// 이메일 코드가 없을 경우
			if (codeFoundByEmail == null) {
				return ResponseDto.notExistEmail();
			}

			// 코드가 일치하지 않는 경우
			if (!codeFoundByEmail.equals(code)) {
				return ResponseDto.certificateFail();
			}

			// 코드가 일치하는 경우
			ResponseDto responseBody = new ResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
			return ResponseEntity.ok(responseBody);

		} catch (Exception e) {
			return ResponseDto.serverError();
		}
	}

	@Override
	public JwtToken loginService(Member member) {
		// 1. ID + password 를 기반으로 Authentication 객체 생성
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				member.getId(), member.getPassword());

		// 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		// 3. 인증 정보를 기반으로 JWT 토큰 생성
		JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

		// 4. 리프레시 토큰 redis에 저장
		String refreshToken = jwtToken.getRefreshToken(); // 리프레시 토큰
		String userId = member.getId(); // 사용자 ID
		RefreshToken refreshTokenObj = new RefreshToken(refreshToken, userId);

		// 5. Redis에 리프레시 토큰 저장
		redisTokenService.saveRefreshToken(userId, refreshTokenObj);

		return jwtToken;
	}

	@Override
	public boolean findByEmail(String email) {
		// 메일이 없는 경우 오류
		if (memberRepository.findByEmail(email) != 1) {
			return false;
		}
		return true;
	}

	@Override
	public void resetPw(String email, String password) {

		memberRepository.setNewPw(email, password);
	}

	@Override
	public String checkRefreshToken(String refreshToken) {

		// refresh token 복호화
		String docoderesult = jwtProvider.decodeRefreshToken(refreshToken);
		String result = "";

		if (docoderesult.equals("expire")) {
			result = "expire";
		} else if (docoderesult.equals("invalid signature")) {
			result = "invalid signature";
		} else if (docoderesult.equals("invalid token")) {
			result = "invalid token";
		} else if (docoderesult.equals("error")) {
			result = "error";
		} else {
			// 레디스와 비교
			boolean tokenexists = redisTokenService.existsRefreshToken(docoderesult);
			if (tokenexists == true) {

				JwtToken newTokens = jwtProvider.generateTokenWithUserId(docoderesult);

				JSONObject jsonResponse = new JSONObject();
				jsonResponse.put("accessToken", newTokens.getAccessToken());
				jsonResponse.put("refreshToken", newTokens.getRefreshToken());
				result = jsonResponse.toString();

			} else if (tokenexists == false) {
				result = "not redis";
			}
		}

		return result;
	}

	@Override
	public boolean revokeRefreshToken(String refreshToken) {
		try {

			String docoderesult = jwtProvider.decodeRefreshToken(refreshToken);
			redisTokenService.existsRefreshToken(docoderesult);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean deleteMemberByToken(String refreshToken) {
		try {
			// refresh token 디코드
			String docoderesult = jwtProvider.decodeRefreshToken(refreshToken);
			memberRepository.deleteMember(docoderesult);
			redisTokenService.deleteRefreshToken(docoderesult);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean checkRefreshTokenValidity(String refreshToken) {
		// refresh token 디코드
		String docoderesult = jwtProvider.decodeRefreshToken(refreshToken);
		// 회원 ID에 따른 토큰 존재하는지 확인 후 결과 값 반환
		return redisTokenService.existsRefreshToken(docoderesult);

	}

	@Override
	public ResponseEntity<ResponseDto> resetEmail(String user, String email) {
		Long memberUID = memberRepository.getMemberIdById(user);

		try {
			memberRepository.resetEmail(email, memberUID);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseDto.databaseError();
		}

		ResponseDto responseBody = new ResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
		return ResponseEntity.ok(responseBody);

	}

	@Override
	public List<MemberForExcel> getMembersByLecture(Long lectureId) {
		return memberRepository.getMembersByLecture(lectureId);
	}
}