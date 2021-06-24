package com.ap.menabev.service;

import org.springframework.http.ResponseEntity;

import com.ap.menabev.dto.ApChartUIDto;

public interface ApDashboardChartServices {
	ResponseEntity<?> getDashboardChartDetailsBetween(ApChartUIDto dto);
	ResponseEntity<?> getKPIDetails(ApChartUIDto dto);

}
