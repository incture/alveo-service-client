package com.ap.menabev.dms.service;

import java.io.File;

import com.ap.menabev.dms.dto.DmsGetResponseDto;
import com.ap.menabev.dms.dto.DmsResponseDto;
import com.ap.menabev.dto.ResponseDto;

public interface DocumentManagementService {
	DmsResponseDto uploadDocument(File file,String requestId);
	DmsGetResponseDto downloadDocument(String fileId);
	ResponseDto deleteDocument(String fileId);
	//DashBoardDetailsDto extraxtXml(File file) throws ParserConfigurationException, SAXException, IOException;
	String getTest();
	String getToken();

}
