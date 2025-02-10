package com.example.myapp.account.dao;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.boot.json.JsonWriter.Members;
import org.springframework.stereotype.Repository;

import com.example.myapp.account.model.AccountLecture;
import com.example.myapp.account.model.AttendLecture;
import com.example.myapp.account.model.DueToLecture;
import com.example.myapp.account.model.IngLecture;

@Repository
@Mapper
public interface IAccountRepository {
	List<AccountLecture> getLecture(Long memberUID);	

	void insertCategory(Long memberUID, String tagName);

	void updateProfile(Long memberUID, String fileUrl);

	List<Members> getMyPage(Long memberUID);

	List<AttendLecture> getAttendLecture(String memberUID);

	List<AttendLecture> getProgressLecture(String memberUID);	

	List<String> getLectureIdById(String memberUID);
	
	String getLectureTitleById(String lectureId);

	int getAllLectureCount(String lectureId);

	List<String> getLectureListId(String lectureId);

	int getCountAttendLecture(String lecture_list_id, String memberUID);

	String getLectureDetailTile(String lecture_list_id);

	int getAllAttendTime(String lecture_list_id);

	Integer getPlayTime(String lecture_list_id, String memberUID);

	List<DueToLecture> getDueToLecture(String memberUID, String formattedNow);

	List<IngLecture> getIngLectures(double attendPercent, String memberUID, String formattedNow, String lectureListId);
	
	List<String> getLectureListIdFalse(String lectureId);

	int getAllStudent(String lecutre_list_id);

	int getNoAttendStudent(String lecutre_list_id);

	List<String> getLectureIdByTeacherId(String memberUID);

	List<String> getLectureListIdTrue(String lecture_id);
}
