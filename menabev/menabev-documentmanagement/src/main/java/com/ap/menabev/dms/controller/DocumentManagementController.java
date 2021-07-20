package com.ap.menabev.dms.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ap.menabev.dms.dto.DmsGetResponseDto;
import com.ap.menabev.dms.dto.DmsResponseDto;
import com.ap.menabev.dms.dto.ResponseDto;
import com.ap.menabev.dms.service.DocumentManagementService;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;

@RestController
@RequestMapping("/document")
public class DocumentManagementController {
	@Autowired(required = true)
	DocumentManagementService documentManagementService;

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	DmsResponseDto uploadDocument(@RequestParam("file") MultipartFile multipartFile,
			@RequestParam("requestId") String requestId) throws IOException {
		try {
			if (!ServiceUtil.isEmpty(multipartFile)) {
				File file = ServiceUtil.multipartToFile(multipartFile);
				System.err.println(file.getName());
				return documentManagementService.uploadDocument(file, requestId);
			} else {
				ResponseDto response = new ResponseDto(ApplicationConstants.FAILURE, ApplicationConstants.CODE_FAILURE,
						"File Not found",null);
				DmsResponseDto dmsResponse = new DmsResponseDto();
				dmsResponse.setResponse(response);
				return dmsResponse;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@GetMapping(value = "/download/{fileId}")
	@ResponseBody
	DmsGetResponseDto download(@PathVariable String fileId) {
		return documentManagementService.downloadDocument(fileId);
	}
	
	@GetMapping(value = "/downloadByFolderId/folderName={folderName}/fileName={fileName}")
	@ResponseBody
	List<DmsGetResponseDto> downloadByFolder(@PathVariable(name = "folderName", required = true) String folderName ,@PathVariable(name = "fileName", required = false) String fileName ) throws IOException {
		if(!ServiceUtil.isEmpty(folderName) && !ServiceUtil.isEmpty(fileName)){
			System.out.println("FolderName = " + folderName +" FIleName "+ fileName);
		}
		
		return documentManagementService.getDocumentByFolderName(folderName, fileName);
	}

	@DeleteMapping(value = "/delete/{fileId}")
	ResponseDto delete(@PathVariable String fileId) {
		return documentManagementService.deleteDocument(fileId);
	}

	/*@GetMapping(value = "/extractXml")
	DashBoardDetailsDto extraxtXml(@RequestParam("file") MultipartFile multipartFile)
			throws IOException, ParserConfigurationException, SAXException {
		if (!ServiceUtil.isEmpty(multipartFile)) {
			File file = ServiceUtil.multipartToFile(multipartFile);
			return documentManagementService.extraxtXml(file);
		} else {
			return new DashBoardDetailsDto();
		}

	}*/
	
	@GetMapping(value = "/dmsGet")
	String getTest(){
		return documentManagementService.getTest();
	}
	
	@GetMapping(value= "/getToken")
	String getToken(){
		return documentManagementService.getToken();
	}

}
