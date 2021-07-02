package com.ap.menabev.service;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.TrackInvoiceInputDto;

public interface TrackInvoiceService {

	public ResponseEntity<?> fetchTrackInvoice(TrackInvoiceInputDto trackInvoiceInputDto);

	public ResponseEntity<?> downloadExcel(TrackInvoiceInputDto dto) throws IOException; 
}
