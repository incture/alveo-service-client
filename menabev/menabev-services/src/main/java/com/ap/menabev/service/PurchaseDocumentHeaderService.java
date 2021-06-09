package com.ap.menabev.service;

import java.util.List;

import com.ap.menabev.dto.PurchaseDocumentHeaderDto;
import com.ap.menabev.dto.ResponseDto;
public interface PurchaseDocumentHeaderService {
	ResponseDto save(PurchaseDocumentHeaderDto dto);
	List<PurchaseDocumentHeaderDto> getAll();
	ResponseDto delete(Integer id);
//	PurchaseDocumentDetailsDto getAllPurchaseDocHeaderByDocNumber(String documentNumber,String requestId);
//	String getVendorId(String documentNumber);
//	POHeaderItemDetailsDto getAllPurchaseDetailsByRequestId(String requestId);
//	DeletePoResponseDto deletePoDetails(String requestId, Long poNum);
//	AddPOResponseDto addPO(DashBoardDetailsDto dashBoardDetailsDto);
}
