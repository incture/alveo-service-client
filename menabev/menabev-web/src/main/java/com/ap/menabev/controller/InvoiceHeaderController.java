package com.ap.menabev.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.ClaimAndReleaseDto;
import com.ap.menabev.dto.CreateInvoiceHeaderDto;
import com.ap.menabev.dto.HeaderCheckDto;
import com.ap.menabev.dto.InvoiceHeaderDashBoardDto;
import com.ap.menabev.dto.InvoiceHeaderDto;
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
	@GetMapping("/getInvoiceByReqId/{requestId}")
	@ResponseBody
	public ResponseEntity<?> getByInvoiceRequestIdCorrection(@PathVariable String requestId) {
		return headerService.getInvoiceDetail(requestId);
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
	@PostMapping("/inbox/tasks")
	public ResponseEntity<?> getinboxTaskNew(@RequestBody FilterMultipleHeaderSearchDto dto) {
		return headerService.getInboxUserTask(dto);
	}
	@PostMapping("/inboxMultiple")
	public ResponseEntity<?> getinboxTaskByPageNo(@RequestBody FilterMultipleHeaderSearchDto dto) throws InterruptedException, ExecutionException {
		return headerService.getInboxTaskWithMultipleSearch(dto);
	}
	@PostMapping("/claimOrRelease")
	public ResponseEntity<?> claimAndRelease(@RequestBody ClaimAndReleaseDto dto){
		return headerService.claimTaskOfUser(dto);
	}
	@PostMapping("/headerCheck")
	public InvoiceHeaderDashBoardDto headerCheck(@RequestBody HeaderCheckDto headerCheckDto) {
		return headerService.headerCheck(headerCheckDto);
		
		
	}
	@GetMapping("/getByDates")
	public List<InvoiceHeaderDo> getByDates(@RequestParam("from") String from ,@RequestParam("to") String to){
		return repo.findByInvoiceDateBetween(from, to);
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
	@PostMapping("/remediationBuyer/Users")
	public ResponseEntity<?> remediationBuyer(@RequestBody List<PurchaseOrderRemediationInput> create) throws URISyntaxException, IOException{
		return headerService.odataGetRemediationDetailsForBuyerAndGrnWithCreatedByAndPurchGrp(create,"BUYER","/PurchOrdDetailsSet?","PurchaseOrderCreator");
	}
	@PostMapping("/remediationGRNWithNoPR/Users")
	public ResponseEntity<?> remediationGrnWithPurchGrp(@RequestBody List<PurchaseOrderRemediationInput> create) throws URISyntaxException, IOException{
		return headerService.odataGetRemediationDetailsForBuyerAndGrnWithCreatedByAndPurchGrp(create,"GRN","/PurchGrpDetailsSet?","PurchasingGroup");
	}
	@PostMapping("/remediation/Users")
	public ResponseEntity<?> remediationBuyerWithPrNumandPrItem(@RequestBody List<PurchaseOrderRemediationInput> create) throws URISyntaxException, IOException{
		return headerService.getRemediationUserDetails(create,"ALL");
	}
	

}
