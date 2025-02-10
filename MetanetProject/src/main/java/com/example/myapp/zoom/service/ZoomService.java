package com.example.myapp.zoom.service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.myapp.common.response.ResponseCode;
import com.example.myapp.common.response.ResponseDto;
import com.example.myapp.common.response.ResponseMessage;
import com.example.myapp.lecture.dao.ILectureRepository;
import com.example.myapp.lecture.model.LectureReminderDto;
import com.example.myapp.member.dao.IMemberRepository;
import com.example.myapp.zoom.dao.IZoomRepository;
import com.example.myapp.zoom.model.ZoomDate;
import com.example.myapp.zoom.model.ZoomMeetingObject;
import com.example.myapp.zoom.model.ZoomMeetingRequest;
import com.example.myapp.zoom.model.ZoomMeetingResponse;
import com.example.myapp.zoom.model.ZoomTokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ZoomService {

    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Autowired
    ILectureRepository lectureRepositiory;
    
    @Autowired
    IMemberRepository memberRepository;
    
    @Autowired
    IZoomRepository zoomRepository;

    @Value("${zoom.CLIENT_ID}")
    private String CLIENT_ID;

    @Value("${zoom.CLIENT_SECRET}")
    private String CLIENT_SECRET;

    @Value("${zoom.REDIRECT_URI}")
    private String REDIRECT_URI;

    public ZoomService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }


     // Zoom Access Token 요청
    public ZoomTokenResponse requestZoomAccessToken(String code, String memberId) {
        String url = "https://zoom.us/oauth/token";
        Long memberUID = memberRepository.getMemberIdById(memberId);

        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + encodedCredentials);

        String requestBody = "code=" + code +
                "&redirect_uri=" + REDIRECT_URI +
                "&grant_type=authorization_code";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);


        try {
            ZoomTokenResponse tokenResponse = objectMapper.readValue(response.getBody(), ZoomTokenResponse.class);

            redisTemplate.opsForValue().set("zoom_accessToken:" + memberUID, tokenResponse.getAccessToken(), 1, TimeUnit.HOURS);
            redisTemplate.opsForValue().set("zoom_refreshToken:" + memberUID, tokenResponse.getRefreshToken(), 30, TimeUnit.DAYS);

            return tokenResponse;

        } catch (JsonMappingException e) {
        	e.printStackTrace();
            throw new RuntimeException("JSON 매핑 오류", e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 처리 오류", e);
        }
    }


     // Zoom Refresh Token을 이용하여 Access Token 갱신
    public ZoomTokenResponse refreshZoomToken(Long memberUID) {
        String url = "https://zoom.us/oauth/token";

        String refreshToken = redisTemplate.opsForValue().get("zoom_refreshToken:" + memberUID);
        if (refreshToken == null) {
            throw new RuntimeException("저장된 Refresh Token이 없습니다.");
        }

        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + encodedCredentials);

        String requestBody = "grant_type=refresh_token&refresh_token=" + refreshToken;

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        try {
            ZoomTokenResponse tokenResponse = objectMapper.readValue(response.getBody(), ZoomTokenResponse.class);

            redisTemplate.opsForValue().set("zoom_accessToken:" + memberUID, tokenResponse.getAccessToken(), 1, TimeUnit.HOURS);
            redisTemplate.opsForValue().set("zoom_refreshToken:" + memberUID, tokenResponse.getRefreshToken(), 30, TimeUnit.DAYS);

            return tokenResponse;

        } catch (JsonMappingException e) {
            throw new RuntimeException("JSON 매핑 오류", e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 처리 오류", e);
        }
    }
    
    public void registerZoomParticipant(Long meetingId, LectureReminderDto participant, Long memberUID) {
    	String ZOOM_API_URL = "https://api.zoom.us/v2/meetings/{meetingId}/registrants";
    	
		String accessToken = redisTemplate.opsForValue().get("zoom_accessToken:" + memberUID);
        if (accessToken == null) {   
        	try {
        		ZoomTokenResponse newToken = refreshZoomToken(memberUID);
                accessToken = newToken.getAccessToken();
        	} catch(RuntimeException e) {
        		e.printStackTrace();
        		return ;
        	}
        	
        }
        log.info("*******at*******" + accessToken);
		
				
		String refreshToken = redisTemplate.opsForValue().get("zoom_refreshToken:" + memberUID);
        if (refreshToken == null) {
            throw new RuntimeException("저장된 Refresh Token이 없습니다.");
        }
        
        String apiUrl = ZOOM_API_URL.replace("{meetingId}", String.valueOf(meetingId));

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", participant.getAttendId());
        requestBody.put("first_name", participant.getAttendId()); // attendId를 first_name으로 사용 (적절히 변경 필요)
        requestBody.put("last_name", "ko"); 
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ Zoom 참가자 등록 성공: " + participant.getAttendId());
            } else {
                System.err.println("❌ Zoom 참가자 등록 실패: " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("🚨 Zoom API 요청 중 오류 발생: " + e.getMessage());
        }
    }
    
    //줌 회의실 생성 - 소진
  	public ResponseEntity<ResponseDto> createMeeting(Long memberUID, ZoomMeetingRequest zoomMeetingRequest) {
  		String email = memberRepository.getAttendIdById(memberUID);
  		String url = "https://api.zoom.us/v2/users/" + email + "/meetings";
  		List<ZoomMeetingResponse> zoomResponses = new ArrayList<>();
  		RestTemplate restTemplate = new RestTemplate();
  		HttpHeaders headers = new HttpHeaders();
  		
  		String accessToken = redisTemplate.opsForValue().get("zoom_accessToken:" + memberUID);
  		if (accessToken == null) {   
        	try {
        		ZoomTokenResponse newToken = refreshZoomToken(memberUID);
                accessToken = newToken.getAccessToken();
        	} catch(RuntimeException e) {
        		e.printStackTrace();
        		return ResponseDto.noAuthentication();
        	}
        	
  		}
  		
  		headers.add("Authorization", "Bearer " + accessToken);
  	    headers.add("Content-Type", "application/json");
  		
  		//줌 회의실 생성 - topic, startTime, duration, hostEmail 설정
  		for (ZoomDate zoomDate : zoomMeetingRequest.getZoomDates()) {
  			//회의 설정
  			ZoomMeetingObject zoomMeetingObject = new ZoomMeetingObject();
  			zoomMeetingObject.setTopic(zoomDate.getLectureListId().toString());
  			String startTime = convertToLocalDateTime(zoomDate.getDate(), zoomDate.getStartTime()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
  			int duration = (int) ChronoUnit.MINUTES.between(convertToLocalDateTime(zoomDate.getDate(), zoomDate.getStartTime()), convertToLocalDateTime(zoomDate.getDate(), zoomDate.getEndTime()));
  			zoomMeetingObject.setStartTime(startTime);
  			zoomMeetingObject.setDuration(duration);
  			zoomMeetingObject.setHostEmail(email);
  			
  			try {
  				HttpEntity<ZoomMeetingObject> requestEntity = new HttpEntity<>(zoomMeetingObject, headers);
  			    ResponseEntity<ZoomMeetingResponse> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ZoomMeetingResponse.class);
  			    log.info(response.getBody().getLink());
  			    if (response.getStatusCode() == HttpStatus.CREATED) {
  			    	ZoomMeetingResponse zoomMeetingResponse = response.getBody();
  			    	zoomMeetingResponse.setLectureListId(zoomDate.getLectureListId());
                      zoomResponses.add(zoomMeetingResponse);
  			    	log.info(zoomMeetingResponse.toString());
  			    }
  			} catch (HttpClientErrorException  e) {
  				String errorMessage = "Zoom API 응답 파싱 실패";
  	            int errorCode = -1;

  	            try {
  	                Map<String, Object> errorResponse = objectMapper.readValue(
  	                    e.getResponseBodyAsString(), new TypeReference<Map<String, Object>>() {}
  	                );

  	                errorCode = (int) errorResponse.getOrDefault("code", -1);
  	                errorMessage = (String) errorResponse.getOrDefault("message", "알 수 없는 오류 발생");

  	            } catch (Exception jsonException) {
  	                System.err.println("JSON 파싱 오류: " + jsonException.getMessage());
  	            }

  	            HttpStatusCode status = e.getStatusCode();

  	            if (status == HttpStatus.BAD_REQUEST) {
  	                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
  	                        .body(new ResponseDto(ResponseCode.ZOOM_BAD_REQUEST, ResponseMessage.ZOOM_BAD_REQUEST, errorMessage));
  	            } else if (status == HttpStatus.NOT_FOUND) {
  	                return ResponseEntity.status(HttpStatus.NOT_FOUND)
  	                        .body(new ResponseDto(ResponseCode.ZOOM_NOT_FOUND, ResponseMessage.ZOOM_NOT_FOUND, errorMessage));
  	            } else if (status == HttpStatus.TOO_MANY_REQUESTS) {
  	                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
  	                        .body(new ResponseDto(ResponseCode.ZOOM_TOO_MANY_REQUESTS, ResponseMessage.ZOOM_TOO_MANY_REQUESTS, errorMessage));
  	            } else {
  	            	e.printStackTrace();
  	                return ResponseDto.serverError();
  	            }
  	            
  			}
  			
  			try {
  				lectureRepositiory.updateMeetingInfo(zoomResponses);
  			} catch (Exception e) {
  				e.printStackTrace();
  				return ResponseDto.databaseError();
  			}

  		}
  		return ResponseDto.success();
  	}
  	
  	public ResponseEntity<ResponseDto> registerAttendance(String topic, String email, String leaveTime) {
  		try {
  			zoomRepository.upsertStudentAttendance(topic, email, leaveTime);
  		} catch(Exception e) {
  			e.printStackTrace();
  			return ResponseDto.databaseError();
  		}	
		return ResponseDto.success();
  	}
  	
  	
  	
  	public static LocalDateTime convertToLocalDateTime(LocalDate date, LocalTime time) {
  		return LocalDateTime.of(date, time);
  	}
    
    public Long getMemberUID(String memberId) {
        return memberRepository.getMemberIdById(memberId);
    }
    
    public boolean isAlreadyRegistered(Long meetingId, String email, Long memberUID) {
        String url = "https://api.zoom.us/v2/meetings/" + meetingId + "/registrants";
        
        String accessToken = redisTemplate.opsForValue().get("zoom_accessToken:" + memberUID);
        if (accessToken == null) {   
        	try {
        		ZoomTokenResponse newToken = refreshZoomToken(memberUID);
                accessToken = newToken.getAccessToken();
        	} catch(RuntimeException e) {
        		e.printStackTrace();
        		return false;
        	}
        	
        }
        log.info("*******at*******" + accessToken);
		
				
		String refreshToken = redisTemplate.opsForValue().get("zoom_refreshToken:" + memberUID);
        if (refreshToken == null) {
            throw new RuntimeException("저장된 Refresh Token이 없습니다.");
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // 이미 등록된 이메일인지 확인
        return response.getBody().contains(email);
    }


}