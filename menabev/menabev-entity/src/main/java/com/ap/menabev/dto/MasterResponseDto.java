package com.ap.menabev.dto;




import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MasterResponseDto {
	private List<DashBoardDetailsDto> dashBoardDetailsDtoList;
	private ResponseDto response;
	private List<InvoiceHeaderDto> invoiceHeaderDtos;
	private String sapInvNum;
	private int filterCount;

}
