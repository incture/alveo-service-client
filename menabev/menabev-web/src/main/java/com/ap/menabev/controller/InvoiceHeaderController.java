package com.ap.menabev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.ClaimAndReleaseDto;
import com.ap.menabev.dto.CreateInvoiceHeaderDto;
import com.ap.menabev.dto.DashBoardDetailsDto;
import com.ap.menabev.dto.FilterHeaderDto;
import com.ap.menabev.dto.HeaderCheckDto;
import com.ap.menabev.dto.InboxDto;
import com.ap.menabev.dto.InvoiceHeaderDashBoardDto;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.MasterResponseDto;
import com.ap.menabev.dto.StatusCountDto;
import com.ap.menabev.entity.InvoiceHeaderDo;
import com.ap.menabev.invoice.InvoiceHeaderRepository;
import com.ap.menabev.service.InvoiceHeaderService;
import com.ap.menabev.service.SequenceGeneratorService;
import com.ap.menabev.util.MenabevApplicationConstant;
import com.ap.menabev.dto.ResponseDto;

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
	public ResponseEntity<?> getByInvoiceRequestIdCorrection(@PathVariable String requestId) {
		return headerService.getInvoiceDetailChanged(requestId);
	}


	@DeleteMapping("/delete/{id}")
	public ResponseDto delete(@PathVariable Integer id) {
		return headerService.delete(id);
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
	


}
