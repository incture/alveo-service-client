package com.ap.menabev.service;



import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.ap.menabev.dto.CreateInvoiceHeaderDto;
import com.ap.menabev.dto.DashBoardDetailsDto;
import com.ap.menabev.dto.FilterHeaderDto;
import com.ap.menabev.dto.HeaderCheckDto;
import com.ap.menabev.dto.InboxDto;
import com.ap.menabev.dto.InvoiceHeaderDashBoardDto;
import com.ap.menabev.dto.InvoiceHeaderDetailsDto;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.MasterResponseDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.StatusCountDto;
import com.ap.menabev.entity.InvoiceHeaderDo;
import com.ap.menabev.serviceimpl.FilterMultipleHeaderSearchDto;



public interface InvoiceHeaderService {

	ResponseDto saveOrUpdate(InvoiceHeaderDto dto);

	List<InvoiceHeaderDto> getAll();

	ResponseDto delete(Integer id);

	InboxDto findPaginated(int pageNo, int limit);

	InvoiceHeaderDetailsDto getAllInvoiceDetailsOnRequestId(String requestId);

	List<InvoiceHeaderDto> filterByKeys(InvoiceHeaderDto dto);

	MasterResponseDto getDetailsForFilter(InvoiceHeaderDto dto);

	String getVendorId(String requestId);

	DashBoardDetailsDto getDashBoardDetails(String requestId);

	ResponseDto reasonForRejection(InvoiceHeaderDto dto);

	InvoiceHeaderDashBoardDto headerCheck(HeaderCheckDto headerCheckDto);

	ResponseDto validateInvoiceDocument(String requestId);

	ResponseDto updateHeader(InvoiceHeaderDto dto);

	InvoiceHeaderDo updateLifeCycleStatus(String lifeCycleStatus, String requestId, Double balance, Double grossAmount,
			String docStatus, String manualpaymentBlock);

	List<StatusCountDto> getStatusCount();

	int updateInvoiceHeader(InvoiceHeaderDashBoardDto dto);

	Boolean checkDuplicateInvoice(InvoiceHeaderDto dto);

	InboxDto findDraftPaginated(int pageNo, int limit);

	InboxDto findEinvoicePaginated(int pageNo, int limit);

	ResponseDto odataPaymentStatus(String sapInvoiceNumber);

	InboxDto findNonDraftPaginated(int pageNo, int limit);
	
	ResponseEntity<?> save(CreateInvoiceHeaderDto invoiceDto);
	
	ResponseEntity<?> submitForNonPo(CreateInvoiceHeaderDto invoiceDto);

	ResponseEntity<?> getInvoiceDetails(String requestId);

	ResponseEntity<?> getInboxTask(FilterHeaderDto filterDto);

	ResponseEntity<?> claimTaskOfUser(String taskId, String userId, boolean isClaim);

	ResponseDto delete(String id);

	
	ResponseEntity<?> getInvoiceItemDetail(String requestId);
	
	ResponseEntity<?> getCostAllocationDetail(String requestId);
	
	ResponseEntity<?> getInvoiceAcctAssinment(String requestId);
	
	ResponseEntity<?> getInvoiceAttachment(String requestId);
	
	ResponseEntity<?> getInvoiceComments(String requestId);
	
	ResponseEntity<?> getActivityLog(String requestId);
    ResponseEntity<?> getInvoiceDetailChanged(String requestId);

	ResponseEntity<?> deleteDraft(List<String> requestId);

	ResponseEntity<?> getInboxTaskWithMultipleSearch(FilterMultipleHeaderSearchDto filterDto);

}
