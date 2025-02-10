package com.example.myapp.account.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonWriter.Members;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.myapp.account.dao.IAccountRepository;
import com.example.myapp.account.model.AccountLecture;
import com.example.myapp.account.model.AdminLectureDashBoard;
import com.example.myapp.account.model.AttendLecture;
import com.example.myapp.account.model.DueToLecture;
import com.example.myapp.account.model.EndLecture;
import com.example.myapp.account.model.IngLecture;
import com.example.myapp.account.model.MyTechDashBoard;
import com.example.myapp.account.model.ProgressLecture;
import com.example.myapp.common.response.ResponseCode;
import com.example.myapp.common.response.ResponseDto;
import com.example.myapp.common.response.ResponseMessage;
import com.example.myapp.member.dao.IMemberRepository;
import com.example.myapp.util.S3FileUploader;

@Service
public class AccountService implements IAccountService {

	@Autowired
	IAccountRepository accountRepository;

	@Autowired
	IMemberRepository memberRepository;

	@Autowired
	S3FileUploader s3FileUploader;

	@Override
	public ResponseEntity<ResponseDto> getLecture(String memberId) {

		List<AccountLecture> result = null;
		try {
			Long memberUID = memberRepository.getMemberIdById(memberId);
			result = accountRepository.getLecture(memberUID);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseDto.databaseError();
		}
		ResponseDto responseBody = new ResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS, result);
		return ResponseEntity.ok(responseBody);
	}

	@Override
	public ResponseEntity<ResponseDto> insertCategory(String tags, String memberId) {

		Long memberUID = memberRepository.getMemberIdById(memberId);

		String[] categories = tags.split(",");

		try {
			for (String cat : categories) {
				accountRepository.insertCategory(memberUID, cat.trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseDto.databaseError();
		}

		ResponseDto responseBody = new ResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
		return ResponseEntity.ok(responseBody);
	}

	@Override
	public ResponseEntity<ResponseDto> updateProfile(String user, MultipartFile file) {
		Long memberUID = memberRepository.getMemberIdById(user);
		Long memberid = Long.valueOf(memberUID);

		String fileUrl = s3FileUploader.uploadFile(file, "members", "profile", memberid);

		try {
			accountRepository.updateProfile(memberUID, fileUrl);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseDto.databaseError();
		}

		ResponseDto responseBody = new ResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
		return ResponseEntity.ok(responseBody);
	}

	@Override
	public ResponseEntity<ResponseDto> getMyPage(String user) {
		Long memberUID = memberRepository.getMemberIdById(user);
		List<Members> result = null;

		try {
			result = accountRepository.getMyPage(memberUID);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseDto.databaseError();
		}

		ResponseDto responseBody = new ResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS, result);
		return ResponseEntity.ok(responseBody);

	}

	// 학습률 가져오기
//	@SuppressWarnings("null")
//	@Override
//	public ResponseEntity<ResponseDto> getMyTech(String user) {
//		String memberUID = memberRepository.getMemberIdById(user);
//
//		List<String> lectureIds = accountRepository.getLectureIdById(memberUID);
//
//		List<AttendLecture> attendLectures = new ArrayList<>(); // attendLectures 리스트를 초기화
//
//		for (String lectureId : lectureIds) {
//
//			String lectureTitle = accountRepository.getLectureTitleById(lectureId);
//
//			int allLectureCount = accountRepository.getAllLectureCount(lectureId);
//			int attendLectureCount = countAttendLecuture(lectureId, memberUID);
//			double attendPercent = 0.0;
//
//			if (allLectureCount > 0) {
//				attendPercent = (double) attendLectureCount / allLectureCount * 100; // 출석률 계산
//			}
//
//			AttendLecture attendLecture = new AttendLecture();
//			attendLecture.setLectureTitle(lectureTitle);
//			attendLecture.setAttendPercent(attendPercent);
//
//			attendLectures.add(attendLecture);
//		}
//
//		List<ProgressLecture> progressLectures = new ArrayList<>();
//
//		for (String lectureId : lectureIds) {
//
//			List<String> lecture_list_ids = accountRepository.getLectureListId(lectureId);
//
//			for (String lecture_list_id : lecture_list_ids) {
//				int allAttendTime = accountRepository.getAllAttendTime(lecture_list_id);
//
//				String lecture_detail_title = accountRepository.getLectureDetailTile(lecture_list_id);
//				Integer attendLectureCount = accountRepository.getPlayTime(lecture_list_id, memberUID);
//				int attendLectureCountValue = (attendLectureCount != null) ? attendLectureCount : 0; // null일 경우 0으로 처리
//				double progressPercent = 0.0;
//
//				progressPercent = (double) attendLectureCountValue / allAttendTime * 100;
//
//				ProgressLecture progressLecture = new ProgressLecture();
//				progressLecture.setDetailLectureTitle(lecture_detail_title);
//				progressLecture.setProgressPercent(progressPercent);
//
//				progressLectures.add(progressLecture);
//			}
//		}
//
//		MyTechDashBoard result = new MyTechDashBoard(attendLectures, progressLectures);
//
//		ResponseDto responseBody = new ResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS, result);
//		return ResponseEntity.ok(responseBody);
//	}

	private int countAttendLecuture(String lectureId, String memberUID) {
		List<String> lecture_list_ids = accountRepository.getLectureListId(lectureId);

		int countAttendLectureCount = 0;

		for (String lecture_list_id : lecture_list_ids) {

			int countAttendLecture = accountRepository.getCountAttendLecture(lecture_list_id, memberUID);
			countAttendLectureCount += countAttendLecture;
		}

		return countAttendLectureCount;
	}

//	@Override
//	public ResponseEntity<ResponseDto> getLectureDashBoard(String user) {
//		String memberUID = memberRepository.getMemberIdById(user);
//
//		// 진행 예정 강의
//		List<DueToLecture> dueToLectures = new ArrayList<>();
//
//		LocalDateTime now = LocalDateTime.now();
//		String formattedNow = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//		dueToLectures = accountRepository.getDueToLecture(memberUID, formattedNow);
//
//		List<IngLecture> ingLectures = new ArrayList<>();
//		List<String> lecture_ids = accountRepository.getLectureIdByTeacherId(memberUID);
//		List<Object> attendPercentList = calculateAttendPercent(lecture_ids,memberUID);
//		
//		for (Object item : attendPercentList) {
//			if (item instanceof Map) {
//				Map<String, Double> attendData = (Map<String, Double>) item;
//
//				for (Map.Entry<String, Double> entry : attendData.entrySet()) {
//					String lectureListId = entry.getKey();
//					Double attendPercent = entry.getValue();
//
//					ingLectures = accountRepository.getIngLectures(attendPercent, memberUID, formattedNow,
//							lectureListId);
//				}
//			}
//		}

		// 완료된 강의
//		List<EndLecture> endLectures = new ArrayList<>();
//		List<Object> coursePercentList = calculateCoursePercent(lecture_ids, memberUID);
//
//		for (Object item : attendPercentList) {
//			if (item instanceof Map) {
//				Map<String, Double> attendData = (Map<String, Double>) item;
//
//				for (Map.Entry<String, Double> entry : attendData.entrySet()) {
//					String lectureListId = entry.getKey();
//					Double attendPercent = entry.getValue();
//
//					ingLectures = accountRepository.getEndLectures(attendPercent, memberUID, formattedNow,
//							lectureListId);
//				}
//			}
//		}
//
//		AdminLectureDashBoard result = new AdminLectureDashBoard(dueToLectures, ingLectures);
//
//		ResponseDto responseBody = new ResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS, result);
//		return ResponseEntity.ok(responseBody);
//	}

//	private List<Object> calculateAttendPercent(List<String> lecture_ids, String memberUID) {
//		List<Object> result = new ArrayList<>();
//
//		double attendPercent = 0.0;
//
//		
//		for (String lecture_id : lecture_ids) {
//			List<String> lecture_list_ids = accountRepository.getLectureListIdFalse(lecture_id);
//
//			for (String lecture_list_id : lecture_list_ids) {
//				int allStudent = accountRepository.getAllStudent(lecture_list_id);
//				int noAttendStudent = accountRepository.getNoAttendStudent(lecture_list_id);
//
//				if (allStudent != 0) {
//					attendPercent = (noAttendStudent / (double) allStudent) * 100;
//				} else {
//					attendPercent = 0.0; 
//				}
//
//				
//				Map<String, Double> attendData = new HashMap<>();
//				attendData.put(lecture_list_id, attendPercent);
//
//			
//				result.add(attendData);
//			}
//
//		}
//
//		return result;
//	}

//	private List<Object> calculateCoursePercent(List<String> lecture_ids, String memberUID) {
//		List<Object> result = new ArrayList<>();
//		double attendPercent = 0.0;
//	
//		for (String lecture_id : lecture_ids) {
//			List<String> lecture_list_ids = accountRepository.getLectureListIdTrue(lecture_id);
//
//			for (String lecture_list_id : lecture_list_ids) {				
//				
//				int allStudent = accountRepository.getAllStudent(lecture_list_id);
//				int noAttendStudent = accountRepository.getNoAttendStudent(lecture_list_id);				
//
//				if (allStudent != 0) {
//					attendPercent = (noAttendStudent / (double) allStudent) * 100;
//				} else {
//					attendPercent = 0.0; // 또는 원하는 기본값 (예: 0% 또는 -1 등)
//				}
//				
//				if(attendPercent >= 80.0) {
//					
//				}					            
//			}
//
//		}
//
//		return result;
//	}
}
