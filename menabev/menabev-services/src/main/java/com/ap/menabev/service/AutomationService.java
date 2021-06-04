package com.ap.menabev.service;

import java.util.List;

import org.json.JSONObject;

import com.ap.menabev.dto.ResponseDto;

public interface AutomationService {
	public List<JSONObject> downloadFilesFromSFTPABBYYServer();
	public ResponseDto extractInvoiceFromEmail() ;
	public ResponseDto extractInvoiceFromSharedEmailBox();
}
