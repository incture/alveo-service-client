package com.ap.menabev.service;



import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.soap.SOAPException;

import org.apache.http.client.ClientProtocolException;
import org.springframework.http.ResponseEntity;

import com.ap.menabev.dto.AcountOrProcessLeadDetermination;
import com.ap.menabev.dto.ActivityLogDto;
import com.ap.menabev.dto.ClaimAndReleaseDto;
import com.ap.menabev.dto.CreateInvoiceHeaderDto;
import com.ap.menabev.dto.HeaderCheckDto;
import com.ap.menabev.dto.InvoiceHeaderDashBoardDto;
import com.ap.menabev.dto.InvoiceHeaderDetailsDto;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.InvoiceSubmitDto;
import com.ap.menabev.dto.PurchaseDocumentHeaderDto;
import com.ap.menabev.dto.PurchaseOrderRemediationInput;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.StatusCountDto;
import com.ap.menabev.dto.WorkflowContextDto;
import com.ap.menabev.entity.InvoiceHeaderDo;
import com.ap.menabev.serviceimpl.FilterMultipleHeaderSearchDto;



public interface InvoiceHeaderService {

	ResponseDto saveOrUpdate(InvoiceHeaderDto dto);
	List<InvoiceHeaderDto> getAll();
	List<InvoiceHeaderDto> filterByKeys(InvoiceHeaderDto dto);
	String getVendorId(String requestId);
	ResponseDto reasonForRejection(InvoiceHeaderDto dto);
	InvoiceHeaderDashBoardDto headerCheck(HeaderCheckDto headerCheckDto);
	ResponseDto updateHeader(InvoiceHeaderDto dto);
	ResponseEntity<?> save(CreateInvoiceHeaderDto invoiceDto);
	ResponseEntity<?> submitForNonPo(CreateInvoiceHeaderDto invoiceDto);
	ResponseDto delete(String id);
	ResponseEntity<?> getInvoiceItemDetail(String requestId);
	ResponseEntity<?> getCostAllocationDetail(String requestId);
	ResponseEntity<?> getInvoiceAcctAssinment(String requestId);
	ResponseEntity<?> getInvoiceAttachment(String requestId);
	ResponseEntity<?> getInvoiceComments(String requestId);
	ResponseEntity<?> getActivityLog(String requestId);
	ResponseEntity<?> deleteDraft(List<String> requestId);
    ResponseEntity<?> claimTaskOfUser(ClaimAndReleaseDto dto);
	ResponseEntity<?>  getInboxTaskWithMultipleSearch(FilterMultipleHeaderSearchDto filterDto);
    ResponseEntity<?> getRemediationUserDetails(List<PurchaseDocumentHeaderDto> dtoList,String userListNeeded) throws URISyntaxException, IOException;
	ResponseEntity<?> getInboxUserTask(FilterMultipleHeaderSearchDto filterDto);
	ResponseEntity<?> getInvoiceDetail(String requestId);
	ResponseEntity<?> accountantInvoiceSubmit(InvoiceSubmitDto invoiceSubmit) throws URISyntaxException, IOException;
	InvoiceHeaderDto saveAPI(InvoiceHeaderDto dto);
	ResponseEntity<?> odataGetRemediationDetailsForBuyerAndGrnWithCreatedByAndPurchGrp(
			List<PurchaseDocumentHeaderDto> dtoList, String userListNeeded, String entitySet, String param)
			throws URISyntaxException, IOException;
	ResponseEntity<?> odataGetRemediationDetailsForGrnByPurchReqAndPurchReqItem(List<String> purchaseReqList,
			List<String> purchaseReqItemList, String userListNeeded, String entitySet)
			throws URISyntaxException, IOException;
	ResponseEntity<?> accountantSubmitOkApi(InvoiceSubmitDto invoiceSubmitOk);
	ActivityLogDto createActivityLogForPoOrNonPo(InvoiceHeaderDto invoiceHeader);
	List<ActivityLogDto> createActivityLogForSubmit(InvoiceSubmitDto invoiceSubmitOk, String actionCode,
			String actionCodeText);
	ResponseEntity<?> processLeadSubmit(InvoiceSubmitDto invoiceSubmit);
	ResponseEntity<?> triggerRuleService(AcountOrProcessLeadDetermination determination)
			throws ClientProtocolException, IOException, URISyntaxException;
	ResponseEntity<?> triggerWorkflow(WorkflowContextDto dto, String definitionId);
	ResponseEntity<?> postOdataCall() throws IOException, URISyntaxException;
	ResponseEntity<?> buyerSubmit(InvoiceSubmitDto invoiceSubmit);
	ResponseEntity<?> getSupplierEmailAddress(String vendorId) throws URISyntaxException, IOException;
	ResponseEntity<?> NonPoProcessLeadSubmit(InvoiceSubmitDto invoiceSubmit) throws IOException, URISyntaxException, JAXBException, SOAPException, DatatypeConfigurationException;	

}
