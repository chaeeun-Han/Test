package com.example.myapp.account.controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.myapp.account.service.IAccountService;
import com.example.myapp.common.response.ResponseCode;
import com.example.myapp.common.response.ResponseDto;
import com.example.myapp.common.response.ResponseMessage;
import com.example.myapp.lecture.dao.ILectureRepository;
import com.example.myapp.lecture.model.LectureRevenueDto;
import com.example.myapp.lecture.model.MonthlySalesDto;
import com.example.myapp.lecture.model.MonthlySalesResponse;
import com.example.myapp.member.dao.IMemberRepository;
import com.example.myapp.util.GetAuthenUser;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {
	
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ILectureRepository lectureRepository;
    
    @Autowired
    private IMemberRepository memberRepository;
    
	@Autowired
	IAccountService accountService;
	
   // 월별 매출 조회 (Redis에서 가져옴) - 한채은
    @GetMapping("/revenue")
    public ResponseEntity<ResponseDto> getMonthlyRevenue(@RequestParam("month") String selectedMonth) {
    	// jwt token 인증
    	String memberId =  GetAuthenUser.getAuthenUser();

        // 인증되지 않은 경우는 바로 처리
         if (memberId == null) {
             return ResponseDto.noAuthentication();
         }
         
        LocalDate today = LocalDate.now();
        Long memberUID = null;
        
        try {
        	memberUID = memberRepository.getMemberIdById(memberId);
        }catch(Exception e) {
        	e.printStackTrace();
        	return ResponseDto.databaseError();
        }

        if (memberUID != null) {
        	// Redis에서 해당 월의 매출 데이터 조회
            String cacheKey = "monthly_revenue:" + today + memberUID;
            String jsonData = redisTemplate.opsForValue().get(cacheKey);

            if (jsonData == null) {
                // Redis에 데이터가 없으면 DB에서 조회
                List<LectureRevenueDto> monthlySales = lectureRepository.getSalesForMember(memberUID);
                
             // totalData의 내용 출력 (디버깅용)
                monthlySales.forEach(data -> {
                    System.out.println("Month: " + data.getMonth());
                    System.out.println("Price: " + data.getPrice());
                });

                // DB에서 조회된 데이터를 Redis에 저장
                try {
                    jsonData = objectMapper.writeValueAsString(monthlySales);
                    redisTemplate.opsForValue().set(cacheKey, jsonData, 24, TimeUnit.HOURS); // 캐시 저장
                } catch (Exception e) {
                    throw new RuntimeException("데이터 변환 오류", e);
                }
            }

            // Redis에서 가져온 JSON 데이터를 파싱하여 Response로 반환
            try {
                List<LectureRevenueDto> totalData = objectMapper.readValue(jsonData,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, LectureRevenueDto.class));
                
         
                
                // 출력형식으로 변환
                // totalData에서 month별로 묶고, 각 month별 totalRevenue를 계산
                Map<String, Integer> monthlyRevenueMap = totalData.stream()
                        .collect(Collectors.groupingBy(
                                LectureRevenueDto::getMonth,
                                Collectors.summingInt(LectureRevenueDto::getPrice) // 해당 월의 전체 매출 합산
                        ));

                // 매출 데이터를 List<MonthlySalesDto> 형식으로 변환
                List<MonthlySalesDto> monthlySales = monthlyRevenueMap.entrySet().stream()
                        .map(entry -> new MonthlySalesDto(entry.getKey(), entry.getValue()))
                        .sorted(Comparator.comparing(MonthlySalesDto::getMonth).reversed()) // 최신순 정렬
                        .collect(Collectors.toList());

                // totalData에서 selectedMonth에 해당하는 강의만 필터링하여 상세 정보 구성
                List<LectureRevenueDto> lectureDetails = totalData.stream()
                        .filter(data -> data.getMonth().equals(selectedMonth)) // 선택한 월과 일치하는 데이터만
                        .collect(Collectors.toList());
                
        		MonthlySalesResponse resultData = new MonthlySalesResponse(monthlySales, selectedMonth, lectureDetails);
                ResponseDto responseBody = new ResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS, resultData);
                return ResponseEntity.ok(responseBody);
            } catch (Exception e) {
            	e.printStackTrace();
                throw new RuntimeException("데이터 변환 오류", e);
            }
        } else {
        	return ResponseDto.notExistUser();
        }
        
    }

	//수강 강의 목록 조회 - 신영서
	@GetMapping("/lecture")
	public ResponseEntity<ResponseDto> getMyLecture() {
		String user = GetAuthenUser.getAuthenUser();
		// 인증되지 않은 경우는 바로 처리
		if (user == null) {
			return ResponseDto.noAuthentication();
		}

		return accountService.getLecture(user);
	}

	// 내 관심 분야 등록 - 신영서
	@PostMapping("/category")
	public ResponseEntity<ResponseDto> insertCategory(@RequestParam String tags) {
		String user = GetAuthenUser.getAuthenUser();
		
		if (user == null) {
			return ResponseDto.noAuthentication();
		}		
		return accountService.insertCategory(tags, user);
	}
	
	// 프로필 수정 - 신영서
	@PutMapping("/update")
	public ResponseEntity<ResponseDto> updateProfile(@RequestParam("files") MultipartFile files){
		String user = GetAuthenUser.getAuthenUser();
		
		if (user == null) {
			return ResponseDto.noAuthentication();
		}
		
		return accountService.updateProfile(user, files);
	}
	
	
	// 프로필 조회 - 신영서	
	@GetMapping
	public ResponseEntity<ResponseDto> getMypage(){	
		
		String user = GetAuthenUser.getAuthenUser();
		
		if (user == null) {
			return ResponseDto.noAuthentication();
		}
						
		return accountService.getMyPage(user);
	}
	
	
	//대시보드 조회 - 내학습률 -모두
//	@GetMapping("/myTech")
//	public ResponseEntity<ResponseDto> getMytech(){
//		
//		String user = GetAuthenUser.getAuthenUser();
//		
//		if (user == null) {
//			return ResponseDto.noAuthentication();
//		}
//		
//		
//		return accountService.getMyTech(user);
//	}
//	
//	
//	
//	//대시보드 조회 - 강의 -Teacher, Admin
//	@GetMapping("/lecture-dashboard")
//	public ResponseEntity<ResponseDto> getLectureDashBoard(){
//		
//		String user = GetAuthenUser.getAuthenUser();
//		
//		if (user == null) {
//			return ResponseDto.noAuthentication();
//		}
//		
//		
//		return accountService.getLectureDashBoard(user);
//	}
	
	
	
	//결제 내역 조회 - 모두 
	 
	
	
	
	
}	
