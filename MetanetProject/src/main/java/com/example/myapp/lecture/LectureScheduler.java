package com.example.myapp.lecture;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.myapp.lecture.dao.ILectureRepository;
import com.example.myapp.lecture.model.LectureReminderDto;
import com.example.myapp.lecture.model.LectureScheduled;
import com.example.myapp.member.service.IMemberService;
import com.example.myapp.zoom.service.ZoomService;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LectureScheduler {

	@Autowired
	ZoomService zoomService;
	
	@Autowired
    ILectureRepository lectureRepository;
	
	@Autowired
    IMemberService emailService;
    
	@Scheduled(cron = "0 00 00 * * *")// 자정에 강의 일정 보내기
	public void sendDailyLectureSchedule() {
	    List<LectureScheduled> schedules = lectureRepository.findTodayLectures();

	    // 비동기 이메일 전송
	    for (LectureScheduled scheduled : schedules) {
	        try {
	        	log.info(scheduled.getEmail() + " and " + scheduled.getSchedule());

	        	emailService.sendEmail("lecture_schedule", scheduled.getEmail(), scheduled.getSchedule());
	        } catch (MessagingException e) {
	            e.printStackTrace();
	        }
	    }
	}
	

	@Scheduled(cron = "0 */5 * * * ?") // 5분마다 실행
	public void sendLectureReminderEmails() {    
	    List<LectureReminderDto> lectures = lectureRepository.getLecturesStartingIn30Minutes();

	    // 같은 이메일 주소(email)로 여러 번 보내지 않도록 Set 사용
	    Set<String> sentEmails = new HashSet<>();

	    for (LectureReminderDto lecture : lectures) {
	        if (!sentEmails.contains(lecture.getEmail())) {  // 이미 보냈으면 스킵
	            try {
	                emailService.sendEmail("lecture_reminder", lecture.getEmail(), lecture);
	                sentEmails.add(lecture.getEmail());  // 발송한 이메일 추가
	            } catch (MessagingException e) {
	                e.printStackTrace();
	            }
	        }
	    }

	    // 강의별 참석자 그룹화
	    Map<Long, List<LectureReminderDto>> groupedLectures = lectures.stream()
	        .collect(Collectors.groupingBy(LectureReminderDto::getMeetingId));

	    for (Map.Entry<Long, List<LectureReminderDto>> entry : groupedLectures.entrySet()) {
	        Long meetingId = entry.getKey();
	        List<LectureReminderDto> participants = entry.getValue();

	        // 참석자 중복 등록 방지: Set을 사용하여 이메일 중복을 제거
	        Set<String> registeredEmails = new HashSet<>();

	        for (LectureReminderDto participant : participants) {
	            String attendId = participant.getAttendId();
	            
	            // 이미 등록된 이메일이면 스킵
	            if (!registeredEmails.contains(attendId)) {
	                // 이메일 등록되지 않은 경우만 등록
	                if (!zoomService.isAlreadyRegistered(meetingId, attendId, participant.getTeacherId())) {
	                    zoomService.registerZoomParticipant(meetingId, participant, participant.getTeacherId());
	                    
	                    registeredEmails.add(attendId);  // 이메일 등록 리스트에 추가
	                }
	            }
	        }
	    }
	}
}

