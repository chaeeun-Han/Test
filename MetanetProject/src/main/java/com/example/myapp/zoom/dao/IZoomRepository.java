package com.example.myapp.zoom.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface IZoomRepository {
	void upsertStudentAttendance(String topic, String email, String leaveTime);
}
