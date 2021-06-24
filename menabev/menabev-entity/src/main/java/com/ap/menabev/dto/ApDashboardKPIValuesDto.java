package com.ap.menabev.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
public class ApDashboardKPIValuesDto {
	
	private String label;
	private String icon;
//	private String StatusText;
	private int count;

}
