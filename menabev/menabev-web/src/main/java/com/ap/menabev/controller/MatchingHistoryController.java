package com.ap.menabev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.MatchingHistoryDto;
import com.ap.menabev.service.MatchingHistoryService;

@RestController
@RequestMapping("/matchingHistory")
public class MatchingHistoryController {

	
	@Autowired
	MatchingHistoryService matchingHistoryService;
	
	@PostMapping("/saveOrUpdate")
	List<MatchingHistoryDto> saveOrUpdate(@RequestBody List<MatchingHistoryDto> dto){
		return matchingHistoryService.saveOrUpdate(dto);
	}
	
	@PostMapping("/get")
	List<MatchingHistoryDto> get(@RequestBody MatchingHistoryDto dto){
		return matchingHistoryService.get(dto);
	}
}
