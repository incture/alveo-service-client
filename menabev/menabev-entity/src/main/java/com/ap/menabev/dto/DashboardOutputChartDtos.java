package com.ap.menabev.dto;

import java.util.List;


import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class DashboardOutputChartDtos {
	private String chartName;
	private String label;
	private List<DashboardOutputDtoValues> values;
	private List<InvoiceHeaderDto> recentPayments;
	private List<InvoiceHeaderDto> agingPayments;
	
}
