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

import com.ap.menabev.dto.InvoiceItemDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.service.InvoiceItemService;
@RestController
@RequestMapping("/invoiceItem")
public class InvoiceItemController {

	@Autowired
	InvoiceItemService itemService;

	@PostMapping("/save")
	public ResponseDto save(@RequestBody InvoiceItemDto dto) {
		return itemService.save(dto);
	}
	

	


	@GetMapping("/getAll")
	public List<InvoiceItemDto> getAll() {
		return itemService.getAll();
	}

	@DeleteMapping("/delete/{id}")
	public ResponseDto delete(@PathVariable String id) {
		return itemService.delete(id);
	}

	@GetMapping(params = { "limit", "offset" })
	public List<InvoiceItemDto> findAllByLimit(@RequestParam(name = "limit", required = false) int limit,
			@RequestParam(name = "offset", required = false) int offset) {
		return itemService.findAllByLimit(limit, offset);
	}



	// @GetMapping("/getUser")
	// public static String getUser(@RequestBody HttpServletRequest request){
	//
	// String user = request.getUserPrincipal().getName();
	//
	// return user;
	// }

}
