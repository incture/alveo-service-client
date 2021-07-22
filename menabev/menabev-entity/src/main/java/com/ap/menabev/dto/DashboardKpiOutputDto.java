package com.ap.menabev.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class DashboardKpiOutputDto {
	private Long rcvdOnFrom;
	private Long rcvdOnTo;
	private Integer total;
	private List<DashboardOutputDtoValues> values;
	private String chartName;
}
