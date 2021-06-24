package com.ap.menabev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.TrackInvoiceInputDto;
import com.ap.menabev.service.TrackInvoiceService;

@RestController
@RequestMapping(value = "/trackinvoice")
public class TrackInvoiceController {

	@Autowired
	TrackInvoiceService trackInvoiceService;
	@PostMapping("/fetchTrackInvoice")
	public ResponseEntity<?> fetchTrackInvoice(@RequestBody TrackInvoiceInputDto trackInvoiceInputDto)
	{
		return trackInvoiceService.fetchTrackInvoice(trackInvoiceInputDto);
		
	}
	
}
