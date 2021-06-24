package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Data
@Getter
@Setter
@ToString
public class ApDashboardKPIDto {
	
		private String rcvdOnFrom;
		private String rcvdOnTo;
		private String chartName;
		private int total;
		private List<ApDashboardKPIValuesDto> charts;


}
