package com.ap.menabev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.ConfigurationCockpitDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.service.ConfigurationCockpitService;


@RestController
@RequestMapping("/configurationCockpit")
public class ConfigurationCockpitController 
{
	@Autowired 
	ConfigurationCockpitService  configurationcockpitservice;
	
	@PostMapping
	public ResponseDto saveOrUpdate(@RequestBody ConfigurationCockpitDto dto) {
		return  configurationcockpitservice.save(dto);
		
	}
	
	
	
	@GetMapping
	public ConfigurationCockpitDto get() {
		return configurationcockpitservice.get("CURRENT");
	}
	
	@GetMapping("/{version}")
	public  ConfigurationCockpitDto get(@PathVariable String version) {
		return configurationcockpitservice.get(version);
	}
	@GetMapping("/{version}/{year}")
	public  ConfigurationCockpitDto get(@PathVariable String version,@PathVariable String year) {
		return configurationcockpitservice.get(version);
	}
	@GetMapping("/hi")
	public String Hey()
	{
		return "hey!!";
		
	}
	

	@GetMapping("/getDistinctVersions")
	public List<String> getDistinctVersions() {
		return configurationcockpitservice.getDistinctVersions();
	}

}
