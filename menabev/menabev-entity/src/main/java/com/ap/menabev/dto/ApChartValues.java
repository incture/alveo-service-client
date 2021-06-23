package com.ap.menabev.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Data
@Getter
@Setter
@ToString
public class ApChartValues {
	
	private String label;
	private String statusCode;
	private String StatusText;
	private int count;

}
