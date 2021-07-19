package com.ap.menabev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.ActivityLogDto;
import com.ap.menabev.service.ActivityLogService;

@RestController
@RequestMapping("/activityLog")
public class ActivityLogController {

	@Autowired
	ActivityLogService activityLogService;
	
	@GetMapping("/getByRequestId/{requestId}")
	List<ActivityLogDto> getByRequestId(@PathVariable String requestId){
		return activityLogService.getLogs(requestId);
	}
}
