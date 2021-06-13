package com.ap.menabev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.PoSearchApiDto;
import com.ap.menabev.service.PoSearchApiService;

@RestController
@RequestMapping("/po")
public class PoSearchApiController {

	@Autowired
	PoSearchApiService poSearchApiService;
	
	@PostMapping("/search")
	List<PoSearchApiDto> poSearch(@RequestBody PoSearchApiDto dto){
		return poSearchApiService.poSearch(dto);
	}
	
}
