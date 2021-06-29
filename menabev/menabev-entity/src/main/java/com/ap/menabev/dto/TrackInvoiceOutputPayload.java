package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;

@Data
public class TrackInvoiceOutputPayload {
	private String type;
    private List<InvoiceHeaderDto> payload;
    private String message;

}
