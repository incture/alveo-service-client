package com.ap.menabev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.DashboardInputDto;
import com.ap.menabev.service.DashboardService;


@RestController
@RequestMapping(value="/apDashboardCharts",produces=MediaType.APPLICATION_JSON_VALUE)
public class DashboardController {
	
	@Autowired
	private DashboardService dashboardService;

	@PostMapping(value="/getKPIDetails")
	public ResponseEntity<?> getDashboardKpiData(@RequestBody DashboardInputDto dashboardInputDto)
	{
		return dashboardService.getDashboardKpiData(dashboardInputDto);
	}
	@PostMapping(value="/getDashboardChartDetails")
	public ResponseEntity<?> getDashboardKpiChartData(@RequestBody DashboardInputDto dashboardInputDto)
	{
		return dashboardService.getDashboardKpiChartData(dashboardInputDto);
	}
}
