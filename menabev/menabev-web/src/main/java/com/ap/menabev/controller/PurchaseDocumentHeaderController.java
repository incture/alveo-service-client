package com.ap.menabev.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.AddPoInputDto;
import com.ap.menabev.dto.AddPoOutputDto;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.PurchaseDocumentHeaderDto;
import com.ap.menabev.service.PurchaseDocumentHeaderService;
import com.ap.menabev.dto.ResponseDto;

@RestController
@RequestMapping("/purchaseDocumentHeader")
public class PurchaseDocumentHeaderController {
	@Autowired
	PurchaseDocumentHeaderService documentHeaderService;
	
	@PostMapping("/save")
	public ResponseDto save(PurchaseDocumentHeaderDto dto){
		return documentHeaderService.save(dto);
	}
	
	@PostMapping("/saveOrUpdate")
	public List<ResponseDto> saveOrUpdate(@RequestBody List<PurchaseDocumentHeaderDto> dto){
		return documentHeaderService.saveOrUpdate(dto);
	}
	
	@GetMapping("/getAll")
	public List<PurchaseDocumentHeaderDto> getAll(){
		return documentHeaderService.getAll();
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseDto delete(@PathVariable Integer id){
		return documentHeaderService.delete(id);
	}
	
	@PostMapping("/savePo")
	public AddPoOutputDto savePo(@RequestBody AddPoInputDto dto) throws URISyntaxException, IOException, ParseException{
		return documentHeaderService.savePo(dto);
	}
	
	@PostMapping("/getReferencePoApi")
	public List<PurchaseDocumentHeaderDto> referencePoApi(@RequestBody AddPoInputDto dto){
		return documentHeaderService.referencePoApi(dto);
	}
	
	@PostMapping("/autoPostApi")
	public InvoiceHeaderDto autoPostApi(@RequestBody InvoiceHeaderDto dto) throws URISyntaxException, IOException, ParseException{
		return documentHeaderService.autoPostApi(dto);
	}
	
	@PostMapping("/refreshPo")
	public AddPoOutputDto refreshPoApi(@RequestBody AddPoInputDto dto) throws URISyntaxException, IOException, ParseException{
		return documentHeaderService.refreshPoApi(dto);
	}
	@PostMapping("/deletePo")
	public AddPoOutputDto deletePo(@RequestBody AddPoInputDto dto) throws URISyntaxException, IOException, ParseException{
		return documentHeaderService.deletePo(dto);
	}
	
}
