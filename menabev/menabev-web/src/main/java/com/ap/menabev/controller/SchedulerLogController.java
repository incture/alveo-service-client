package com.ap.menabev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.SchedulerLogDto;
import com.ap.menabev.service.SchedulerLogService;
import com.ap.menabev.dto.ResponseDto;

@RestController
@RequestMapping("/schedulerLog")
public class SchedulerLogController {
	
	@Autowired
	SchedulerLogService schedulerLogService;
	
	@GetMapping("/getAll")
	public List<SchedulerLogDto> getAll()
	{
		return schedulerLogService.getAll();
		
	}
	
	@PostMapping("/saveOrUpdate")
	public ResponseDto saveOrUpdate(@RequestBody SchedulerLogDto dto){
		return schedulerLogService.saveOrUpdate(dto);
	}
	
	

}