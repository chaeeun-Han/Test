package com.example.myapp.lecture.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.example.myapp.lecture.model.Lecture;
import com.example.myapp.lecture.model.LectureFile;
import com.example.myapp.lecture.model.LectureId;
import com.example.myapp.lecture.model.LectureList;
import com.example.myapp.lecture.model.LectureReminderDto;
import com.example.myapp.lecture.model.LectureRevenueDto;
import com.example.myapp.lecture.model.LectureScheduled;
import com.example.myapp.zoom.model.ZoomMeetingResponse;

@Repository
@Mapper
public interface ILectureRepository {
    List<Lecture> getAllLectures();

    List<Lecture> getRankByDeadDateLectures();

    List<Lecture> getRankByLikeLectures();

    Lecture getLectureDetail(Long lectureId);

    boolean checkLikeLectures(Long memberId, Long lectureId);

    void insertLikeLectures(Long memberId, Long lectureId);

    void deleteLikeLectures(Long memberId, Long lectureId);

    void updateLikeLectures(Long memberId, Long lectureId, boolean exist);

    int lectureFileUpload(LectureFile lectureFile);

    void setRefundStatus(LectureId lectureId);

    Long registerLectures(Lecture lecture);

    void updateLectures(Lecture lecture);

    void deleteLectures(Long lectureId, Long memberId);

    Long getMemberIdById(String memberId);

    Long getLectureMaxId();

    void forceDeleteLecture(Long lectureId);

    void deleteAllLectures();

    List<LectureFile> getLectureFiles(Long lectureId);

    void insertLectureTags(Map<String, Object> params);

    void deleteLectureTags(Map<String, Object> params);

    List<Long> getExistingTags(Long lectureId);

    void buyLecture(Map<String, Long> params);

    Boolean checkCanRefund(Map<String, Long> params);

    void payRefund(Map<String, Long> params);

    Boolean checkBeforeBuyLecture(Map<String, Long> params);

    List<LectureRevenueDto> getSalesForMember(Long memberId);

    void insertPayLog(Map<String, Long> params);

    List<LectureScheduled> findTodayLectures();

    List<LectureReminderDto> getLecturesStartingIn30Minutes();

    void insertLectureListByExcel(LectureList lectureList);
    
    void updateMeetingInfo(@Param("list") List<ZoomMeetingResponse> responses);
}
