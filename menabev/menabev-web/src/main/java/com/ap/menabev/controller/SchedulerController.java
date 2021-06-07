package com.ap.menabev.controller;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.service.AutomationService;

@RestController
@RequestMapping(value = "/scheduler")
public class SchedulerController {
	@Autowired
	AutomationService automationService;
	@GetMapping
	public List<JSONObject> getJSONFromAbbyy(){
		return automationService.downloadFilesFromSFTPABBYYServer();
	}
}
