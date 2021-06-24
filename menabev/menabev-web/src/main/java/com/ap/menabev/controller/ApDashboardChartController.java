package com.ap.menabev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.ApChartUIDto;
import com.ap.menabev.service.ApDashboardChartServices;

@RestController
@RequestMapping("/apDashboardCharts")
public class ApDashboardChartController {
	
	@Autowired
	ApDashboardChartServices services;
	
	@GetMapping("/test")
	public String test(){
		return "Welcome to Menaben";
	}

	
//	@GetMapping("/getDashboardChartDetails")
	@PostMapping("/getDashboardChartDetails")
	public ResponseEntity<?> getDashboardChartDetailsBetween(@RequestBody ApChartUIDto dto){
		return services.getDashboardChartDetailsBetween(dto);
	}

	
	@PostMapping("/getKPIDetails")
	public ResponseEntity<?> getKPIDetails(@RequestBody ApChartUIDto dto){
		return services.getKPIDetails(dto);
	}
}
