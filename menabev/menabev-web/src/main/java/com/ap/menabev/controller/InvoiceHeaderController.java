package com.ap.menabev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.DashBoardDetailsDto;
import com.ap.menabev.dto.HeaderCheckDto;
import com.ap.menabev.dto.InboxDto;
import com.ap.menabev.dto.InvoiceHeaderDashBoardDto;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.MasterResponseDto;
import com.ap.menabev.dto.StatusCountDto;
import com.ap.menabev.entity.InvoiceHeaderDo;
import com.ap.menabev.invoice.InvoiceHeaderRepository;
import com.ap.menabev.service.InvoiceHeaderService;
import com.ap.menabev.dto.ResponseDto;

@RestController
@RequestMapping("/invoiceHeader")
public class InvoiceHeaderController {

	@Autowired
	InvoiceHeaderService headerService;
	@Autowired
	InvoiceHeaderRepository repo;

	@PostMapping("/saveOrUpdate")
	public ResponseDto saveOrUpdate(@RequestBody InvoiceHeaderDto dto) {
		return headerService.saveOrUpdate(dto);
	}
	

	@GetMapping("/getAll")
	public List<InvoiceHeaderDto> getAll() {
		return headerService.getAll();
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

	@GetMapping("/eInvoice/{pageNo}/{limit}")
	public InboxDto getEinvoiceByPageNo(@PathVariable  int pageNo,
			@PathVariable  int limit) {
		return headerService.findEinvoicePaginated(pageNo, limit);
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
	


}
