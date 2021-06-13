package com.ap.menabev.dms.service;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.xml.sax.SAXException;

import com.ap.menabev.dms.dto.DmsGetResponseDto;
import com.ap.menabev.dms.dto.DmsResponseDto;
import com.ap.menabev.dto.DashBoardDetailsDto;
import com.ap.menabev.dto.ResponseDto;

public interface DocumentManagementService {
	DmsResponseDto uploadDocument(File file,String requestId);
	DmsGetResponseDto downloadDocument(String fileId);
	ResponseDto deleteDocument(String fileId);
	DashBoardDetailsDto extraxtXml(File file) throws ParserConfigurationException, SAXException, IOException;
	String getTest();
	String getToken();

}
