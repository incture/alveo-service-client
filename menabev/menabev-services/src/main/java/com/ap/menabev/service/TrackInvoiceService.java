package com.ap.menabev.service;

import org.springframework.http.ResponseEntity;

import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.TrackInvoiceInputDto;

public interface TrackInvoiceService {

	public ResponseEntity<?> fetchTrackInvoice(TrackInvoiceInputDto trackInvoiceInputDto);

}
