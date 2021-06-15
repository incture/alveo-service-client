package com.ap.menabev.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.ClaimAndReleaseDto;
import com.ap.menabev.dto.CreateInvoiceHeaderDto;
import com.ap.menabev.dto.DashBoardDetailsDto;
import com.ap.menabev.dto.FilterHeaderDto;
import com.ap.menabev.dto.HeaderCheckDto;
import com.ap.menabev.dto.InboxCountReponseDto;
import com.ap.menabev.dto.InboxDto;
import com.ap.menabev.dto.InboxResponseOutputDto;
import com.ap.menabev.dto.InvoiceHeaderDashBoardDto;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.MasterResponseDto;
import com.ap.menabev.dto.PurchaseOrderRemediationInput;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.StatusCountDto;
import com.ap.menabev.entity.InvoiceHeaderDo;
import com.ap.menabev.invoice.InvoiceHeaderRepository;
import com.ap.menabev.service.InvoiceHeaderService;
import com.ap.menabev.service.SequenceGeneratorService;
import com.ap.menabev.serviceimpl.FilterMultipleHeaderSearchDto;
import com.ap.menabev.util.MenabevApplicationConstant;

@RestController
@RequestMapping("/invoiceHeader")
public class InvoiceHeaderController {

	@Autowired
	InvoiceHeaderService headerService;
	@Autowired
	InvoiceHeaderRepository repo;
	@Autowired
	private SequenceGeneratorService seqService;


	@PostMapping("/saveOrUpdate")
	public ResponseDto saveOrUpdate(@RequestBody InvoiceHeaderDto dto) {
		return headerService.saveOrUpdate(dto);
	}
	

	@GetMapping("/getAll")
	public List<InvoiceHeaderDto> getAll() {
		return headerService.getAll();
	}
	@GetMapping("/getInvoiceByRequestId/{requestId}")
	public ResponseEntity<?> getByInvoiceRequestId(@PathVariable String requestId) {
		return headerService.getInvoiceDetails(requestId);
	}
	
	@GetMapping("/getInvoiceByReqId/{requestId}")
	@ResponseBody
	public ResponseEntity<?> getByInvoiceRequestIdCorrection(@PathVariable String requestId) {
		return headerService.getInvoiceDetailChanged(requestId);
	}
	@GetMapping("/getInvoiceByReqId/{requestId}/item")
	public ResponseEntity<?> getByInvoiceRequestIdItem(@PathVariable String requestId) {
		return headerService.getInvoiceItemDetail(requestId);
	}
	@GetMapping("/getInvoiceByReqId/{requestId}/costAllocation")
	public ResponseEntity<?> getByInvoiceRequestIdCostAllocation(@PathVariable String requestId) {
		return headerService.getCostAllocationDetail(requestId);
	}
	@GetMapping("/getInvoiceByReqId/{requestId}/invoiceItemAcctAssignment")
	public ResponseEntity<?> getByInvoiceRequestIdInvoiceItemAcct(@PathVariable String requestId) {
		return headerService.getInvoiceAcctAssinment(requestId);
	}

	@GetMapping("/getInvoiceByReqId/{requestId}/attachment")
	public ResponseEntity<?> getByInvoiceRequestIdAttachment(@PathVariable String requestId) {
		return headerService.getInvoiceAttachment(requestId);
	}
	
	@GetMapping("/getInvoiceByReqId/{requestId}/comment")
	public ResponseEntity<?> getByInvoiceRequestIdComment(@PathVariable String requestId) {
		return headerService.getInvoiceComments(requestId);
	}


	@DeleteMapping("/delete")
	public ResponseEntity<?> delete(@RequestBody List<String> requestId) {
		return headerService.deleteDraft(requestId);
	}

	@GetMapping(params = { "pageNo", "limit" })
	public InboxDto getByPageNo(@RequestParam(name = "pageNo", required = true) int pageNo,
			@RequestParam(name = "limit", required = true) int limit) {
		return headerService.findPaginated(pageNo, limit);
	}
	
	@GetMapping("/drafts/{pageNo}/{limit}")
	public InboxDto getDraftByPageNo(@PathVariable  int pageNo,
			@PathVariable  int limit) {
		return headerService.findDraftPaginated(pageNo, limit);
	}
	
	@GetMapping("/nondrafts/{pageNo}/{limit}")
	public InboxDto getNonDraftByPageNo(@PathVariable  int pageNo,
			@PathVariable  int limit) {
		return headerService.findNonDraftPaginated(pageNo, limit);
	}

	@PostMapping("/inbox")
	public ResponseEntity<?> getinboxTaskByPageNo(@RequestBody FilterHeaderDto dto) {
		return headerService.getInboxTask(dto);
	}
	
	
	@PostMapping("/inboxMultiple")
	public ResponseEntity<?> getinboxTaskByPageNo(@RequestBody FilterMultipleHeaderSearchDto dto) throws InterruptedException, ExecutionException {
		
		
		return headerService.getInboxTaskWithMultipleSearch(dto);
	}
	
	@PostMapping("/claimOrRelease")
	public ResponseEntity<?> claimAndRelease(@RequestBody ClaimAndReleaseDto dto){
		return headerService.claimTaskOfUser(dto.getTaskID(),dto.getUserId(),dto.isClaim());
	}


	@GetMapping(params = { "requestId" })
	public DashBoardDetailsDto getInvoiceDetailsByRequestId(@RequestParam(name = "requestId") String requestId) {
		return headerService.getDashBoardDetails(requestId);
	}

	@PostMapping("/filter")
	public List<InvoiceHeaderDto> filterGetAll(@RequestBody InvoiceHeaderDto dto) {
		return headerService.filterByKeys(dto);
	}

	@PostMapping("/getForFilter")
	public MasterResponseDto getDetailsForFilter(@RequestBody InvoiceHeaderDto dto) {
		return headerService.getDetailsForFilter(dto);
	}

	@PutMapping("/updateLifeCycleStatus")
	public ResponseDto updateLifeCycleStatus(@RequestBody InvoiceHeaderDto dto) {
		return headerService.reasonForRejection(dto);
	}

	@PostMapping("/headerCheck")
	public InvoiceHeaderDashBoardDto headerCheck(@RequestBody HeaderCheckDto headerCheckDto) {
		return headerService.headerCheck(headerCheckDto);
	}

	@GetMapping("/validateInvoiceDocument")
	public ResponseDto validateInvoiceDocument(@RequestParam("requestId") String requestId) {
		return headerService.validateInvoiceDocument(requestId);
	}
	@GetMapping("/statusCount")
	public List<StatusCountDto> getStatusCount(){
		return headerService.getStatusCount();
	}
	@GetMapping("/getByDates")
	public List<InvoiceHeaderDo> getByDates(@RequestParam("from") String from ,@RequestParam("to") String to){
		return repo.findByInvoiceDateBetween(from, to);
	}
	
	@PostMapping("/getPaymentStatus")
	public ResponseDto odataPaymentStatus(@RequestBody String sapInvoiceNumber){
		return headerService.odataPaymentStatus(sapInvoiceNumber);
	}
	
	@PostMapping("/accountantSubmit")
	public ResponseEntity<?> onAccountantNonPoSubmit(@RequestBody CreateInvoiceHeaderDto create){
		return headerService.submitForNonPo(create);
	}
	
	@PostMapping("/accountantSave")
	public ResponseEntity<?> onAccountantNonPoSave(@RequestBody CreateInvoiceHeaderDto create){
		return headerService.save(create);
		
	}
	
	
	@GetMapping(params = { "sequnceCode" })
	public ResponseDto getCurrentSequnceOfInvoice(@RequestParam(name = "sequnceCode") String sequnceCode){
	ResponseDto response = new ResponseDto();
	String sequencId = 	seqService.getSequenceNoByMappingId(
			MenabevApplicationConstant.INVOICE_SEQUENCE,"INV"
			);
	response.setCode("200");
	response.setMessage(sequencId);
	response.setStatus("Success");
		return response;
		}
	

	@PostMapping("/remediation/Users")
	public ResponseEntity<?> onAccountantNonPoSave(@RequestBody List<PurchaseOrderRemediationInput> create) throws URISyntaxException, IOException{
		return headerService.odataGetRemediationDetailsForBuyerAndGrnWithCreatedByAndPurchGrp(create,"GRN","/PurchOrdDetailsSet?","PurchaseOrderCreator");
	}
	
	
	

}
