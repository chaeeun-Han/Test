package com.example.myapp.aop;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface IAopRepository {
	void insertLog(Log log);
	Log getLogById();
	List<Log> getAllLogs();
}
