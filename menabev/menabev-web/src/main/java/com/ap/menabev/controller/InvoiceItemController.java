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
import com.ap.menabev.dto.InvoiceItemDto;
import com.ap.menabev.dto.ManualMatchingDto;
import com.ap.menabev.service.InvoiceHeaderService;
import com.ap.menabev.service.InvoiceItemService;
import com.ap.menabev.dto.ResponseDto;
@RestController
@RequestMapping("/invoiceItem")
public class InvoiceItemController {

	@Autowired
	InvoiceItemService itemService;

	@PostMapping("/save")
	public ResponseDto save(@RequestBody InvoiceItemDto dto) {
		return itemService.save(dto);
	}
	

	@PostMapping("/manualMatch")
	public DashBoardDetailsDto manualMatch(@RequestBody ManualMatchingDto manualMatchingDto) {
		return itemService.manualMatch(manualMatchingDto);
	}

	@PostMapping("/unMatchItems")
	public DashBoardDetailsDto unMatch(@RequestBody DashBoardDetailsDto dashBoardDetailsDto) {
		return itemService.unMatch(dashBoardDetailsDto);
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

	@PostMapping("/grnCalculations")
	public DashBoardDetailsDto grnCalculations(@RequestBody DashBoardDetailsDto dashBoardDetailsDto) {
		// return itemService.grnCalculations(dashBoardDetailsDto);
		return null;

	}

	// @GetMapping("/getUser")
	// public static String getUser(@RequestBody HttpServletRequest request){
	//
	// String user = request.getUserPrincipal().getName();
	//
	// return user;
	// }

}
