package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
@Data
public class ApCharts {
	private String chartName;
	private String label;
	private List<ApChartValues> values;
	

}
