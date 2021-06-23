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
public class ApDashboardCharts {
	
	private String rcvdOnFrom;
	private String rcvdOnTo;
	private int total;
	private List<ApCharts> charts;

}
