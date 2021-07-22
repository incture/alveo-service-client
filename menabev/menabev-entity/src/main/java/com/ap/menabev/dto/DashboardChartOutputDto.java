package com.ap.menabev.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class DashboardChartOutputDto {
	private Long requestCreatedAt;
	private Long requestUpdatedAt;
	private Integer totalAgingReportCount;
	private Integer totalExceptioReportCount;
	
	private List<DashboardOutputChartDtos> chart;
}
