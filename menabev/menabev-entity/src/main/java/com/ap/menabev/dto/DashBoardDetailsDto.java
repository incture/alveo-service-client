package com.ap.menabev.dto;



import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class DashBoardDetailsDto {
	private InvoiceHeaderDashBoardDto invoiceHeader;
	private List<InvoiceItemDashBoardDto> invoiceItems;
	private List<CostAllocationDto> costAllocation;
	private List<CommentDto> commentDto;
	private String workflowResponse;
	private List<String> errorMsgesOnSAPPosting;
	private List<AttachmentDto> attachments;
	private List<ScreenVariantForCADto> screenVariantForCADtoList;
	
	//E-invoce
	private List<InvoiceOverheadChargesDto> invoiceOverheadCharges;
	private List<InvoiceTaxDetailsDto> invoiceTaxDetails;
	private InvoiceBPDto remitTo;
	private InvoiceBPDto billTo;
	private List<ShippingHandlingCostDto> shippingHandling;
	ResponseDto response;
	private String emaiBody;
}
