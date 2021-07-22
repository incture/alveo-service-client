package com.ap.menabev.dto;

import java.util.List;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardFilterOutputDto {

	private List<InvoiceHeaderDto> queryList;
	private List<InvoiceHeaderDto> recentPayentList;

	private List<InvoiceHeaderDto> recentAgingList;
	private List<InvoiceHeaderDto> overDueAgingReportList;
	private List<InvoiceHeaderDto> vendorDeatailsCount;

}
