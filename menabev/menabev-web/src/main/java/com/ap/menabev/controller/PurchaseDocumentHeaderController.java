package com.ap.menabev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.DashBoardDetailsDto;
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
	
	@GetMapping("/getAll")
	public List<PurchaseDocumentHeaderDto> getAll(){
		return documentHeaderService.getAll();
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseDto delete(@PathVariable Integer id){
		return documentHeaderService.delete(id);
	}
}
