package com.ap.menabev.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.ap.menabev.dto.AddPoInputDto;
import com.ap.menabev.dto.AddPoOutputDto;
import com.ap.menabev.dto.InvoiceHeaderDto;
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
	List<ResponseDto> saveOrUpdate(List<PurchaseDocumentHeaderDto> dto);
	AddPoOutputDto savePo(AddPoInputDto poNumbers) throws URISyntaxException, IOException, ParseException;
	List<PurchaseDocumentHeaderDto> referencePoApi(AddPoInputDto dto);
	InvoiceHeaderDto autoPostApi(InvoiceHeaderDto dto) throws URISyntaxException, IOException, ParseException;
	AddPoOutputDto refreshPoApi(AddPoInputDto dto) throws URISyntaxException, IOException, ParseException;
}