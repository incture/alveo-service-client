package com.ap.menabev.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.service.EmailServices;
import com.ap.menabev.util.ServiceUtil;

@RestController
@RequestMapping(value = "/uploadInvoice")
public class UploadInvoice {
	@Autowired
	EmailServices emailServices;

	@PostMapping
	public ResponseDto uploadInvoice(@RequestParam("file") MultipartFile file, @RequestParam("body") String body) {
		ResponseDto responseDto = new ResponseDto();
		try {

			File fileToUpload = ServiceUtil.multipartToFile(file);

			if (fileToUpload.exists()) {
				String response = emailServices.sendmailTOCSU(body, fileToUpload);
				if (response.equalsIgnoreCase("200")) {
					responseDto.setMessage("Uploaded Successfully and sent to the scanning team");
					responseDto.setCode("200");
					responseDto.setStatus("Success");
				} else {
					responseDto.setMessage(response);
					responseDto.setCode("500");
					responseDto.setStatus("Error");
				}
				return responseDto;
			} else {
				responseDto.setMessage("File does not exists");
				responseDto.setCode("400");
				responseDto.setStatus("Failed");
				return responseDto;

			}
		} catch (Exception e) {
			responseDto.setMessage(e.getLocalizedMessage());
			responseDto.setCode("500");
			responseDto.setStatus("Error");
			// TODO: handle exception
			return responseDto;
		}
	}
}
