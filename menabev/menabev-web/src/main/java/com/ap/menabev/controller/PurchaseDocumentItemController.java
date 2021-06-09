package com.ap.menabev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.PurchaseDocumentItemDto;
import com.ap.menabev.service.PurchaseDocumentItemService;
import com.ap.menabev.dto.ResponseDto;
@RestController
@RequestMapping("/purchaseDocumentItem")
public class PurchaseDocumentItemController {
	
	@Autowired
	PurchaseDocumentItemService documentItemService;
	
	@PostMapping("/save")
	public ResponseDto save(PurchaseDocumentItemDto dto) {
		return documentItemService.save(dto);
	}

	@GetMapping("/getAll")
	public List<PurchaseDocumentItemDto> getAll() {
		return documentItemService.getAll();
	}

	@DeleteMapping("/delete/{id}")
	public ResponseDto delete(@PathVariable String id) {
		return documentItemService.delete(id);
	}

}
