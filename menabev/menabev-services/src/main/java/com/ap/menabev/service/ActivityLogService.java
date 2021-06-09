package com.ap.menabev.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.ap.menabev.dto.ActivityLogDto;

public interface ActivityLogService {
	
	List<ActivityLogDto>  getLogs(String requestId);
	ResponseEntity<?> save(ActivityLogDto dto);

	

}
