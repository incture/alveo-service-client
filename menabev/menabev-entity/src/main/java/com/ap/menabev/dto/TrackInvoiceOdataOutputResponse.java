package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;
@Data
public class TrackInvoiceOdataOutputResponse {
	    private String type;
	    private List<OdataTrackInvoiceObject> users;
	    private String message;
}
