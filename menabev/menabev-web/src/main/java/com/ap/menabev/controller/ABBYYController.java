package com.ap.menabev.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ap.menabev.abbyy.ABBYYIntegration;
import com.ap.menabev.util.ServiceUtil;

@RestController
@RequestMapping(value = "/abbyy")
public class ABBYYController {
	@Autowired
	ABBYYIntegration abbyyIntegration;

	@PostMapping
	public String putFileInSFTPServer(@RequestParam("file") MultipartFile file) {
		try {

			File fileToUpload = ServiceUtil.multipartToFile(file);
			return abbyyIntegration.uploadFileUsingJsch(fileToUpload, "/C/ABBYY/Input/");
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
			return e.getMessage();
		}
	}
}
