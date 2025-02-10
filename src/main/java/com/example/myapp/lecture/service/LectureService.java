package com.example.myapp.lecture.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.myapp.lecture.dao.ILectureRepository;
import com.example.myapp.lecture.model.Lecture;
import com.example.myapp.lecture.model.LectureFile;
import com.example.myapp.lecture.model.LectureId;
import com.example.myapp.lecture.model.LectureList;

@Service
public class LectureService implements ILectureService {

    @Autowired
    ILectureRepository lectureDao;

    @Override
    public Map<String, List<Lecture>> getAllLectures() {

        Map<String, List<Lecture>> lectures = new HashMap<String, List<Lecture>>();

        lectures.put("getAll", lectureDao.getAllLectures());
        lectures.put("getRankByDeadDate", lectureDao.getRankByDeadDateLectures());
        lectures.put("getRankByLike", lectureDao.getRankByLikeLectures());
        return lectures;
    }

    @Override
    public int lectureFileUpload(LectureFile lectureFile) {
        return lectureDao.lectureFileUpload(lectureFile);
    }

    @Override
    public void setRefundStatus(LectureId lectureId) {
        lectureDao.setRefundStatus(lectureId);
    }

    @Override
    public Long registerLectures(Lecture lecture) {
        return lectureDao.registerLectures(lecture);

    }

    @Override
    public void updateLectures(Lecture lecture) {
        lectureDao.updateLectures(lecture);
    }

    @Override
    public void deleteLectures(Long lectureId, Long memberId) {
        lectureDao.deleteLectures(lectureId, memberId);
    }

    @Override
    public Long getMemberIdById(String memberId) {
        return lectureDao.getMemberIdById(memberId);
    }

    @Override
    public Lecture getLectureDetail(Long lectureId) {
        return lectureDao.getLectureDetail(lectureId);
    }

    @Override
    public boolean checkLikeLectures(Long memberId, Long lectureId) {
        return lectureDao.checkLikeLectures(memberId, lectureId);
    }

    @Override
    public void insertLikeLectures(Long memberId, Long lectureId) {
        lectureDao.insertLikeLectures(memberId, lectureId);
    }

    @Override
    public void deleteLikeLectures(Long memberId, Long lectureId) {
        lectureDao.deleteLikeLectures(memberId, lectureId);
    }

    @Override
    public void updateLikeLectures(Long memberId, Long lectureId, boolean exist) {
        lectureDao.updateLikeLectures(memberId, lectureId, exist);
    }

    @Override
    public Long getLectureMaxId() {
        return lectureDao.getLectureMaxId();
    }

    @Override
    public List<LectureFile> getLectureFiles(Long lectureId) {
        return lectureDao.getLectureFiles(lectureId);
    }

    @Override
    public void insertLectureTags(Map<String, Object> params) {
        lectureDao.insertLectureTags(params);
    }

    @Override
    public void deleteLectureTags(Map<String, Object> params) {
        lectureDao.deleteLectureTags(params);
    }

    @Override
    public List<Long> getExistingTags(Long lectureId) {
        return lectureDao.getExistingTags(lectureId);
    }

    @Override
    public void updateLectureTags(Long lectureId, String tags) {
        List<Long> existTags = lectureDao.getExistingTags(lectureId);

        List<Long> insertedTags = Arrays.stream(tags.replace(" ", "").split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        // 지워야 할 태그 목록록
        List<Long> tagsToDelete = existTags.stream()
                .filter(tag -> !insertedTags.contains(tag))
                .collect(Collectors.toList());

        // 입력해야 할 태그 목록
        List<Long> tagsToInsert = insertedTags.stream()
                .filter(tag -> !existTags.contains(tag))
                .collect(Collectors.toList());

        if (!tagsToDelete.isEmpty()) {
            Map<String, Object> params = new HashMap<>();
            params.put("lectureId", lectureId);
            params.put("tagIds", tagsToDelete);
            lectureDao.deleteLectureTags(params);
        }

        if (!tagsToInsert.isEmpty()) {
            Map<String, Object> params = new HashMap<>();
            params.put("lectureId", lectureId);
            params.put("tagIds", tagsToInsert);
            lectureDao.insertLectureTags(params);
        }
    }

    @Override
    public void buyLecture(Map<String, Long> params) {
        lectureDao.buyLecture(params);
        lectureDao.insertPayLog(params);
    }

    @Override
    public Boolean checkBeforeBuyLecture(Map<String, Long> params) {
        return lectureDao.checkBeforeBuyLecture(params);
    }

    @Override
    public Boolean checkCanRefund(Map<String, Long> params) {
        return lectureDao.checkCanRefund(params);
    }

    @Override
    public void payRefund(Map<String, Long> params) {
        lectureDao.payRefund(params);
    }

    @Override
    public void insertLectureListByExcel(LectureList lectureList) {
        lectureDao.insertLectureListByExcel(lectureList);
    }

}
