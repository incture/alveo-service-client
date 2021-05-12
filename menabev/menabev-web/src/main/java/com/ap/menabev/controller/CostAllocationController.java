package com.ap.menabev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.AllocationDto;
import com.ap.menabev.dto.AllocationForTemplateDto;
import com.ap.menabev.dto.CostAllocationDto;
import com.ap.menabev.service.CostAllocationService;
import com.ap.menabev.dto.ResponseDto;

@RestController
@RequestMapping("/costAllocation")
public class CostAllocationController {

	@Autowired
	CostAllocationService costAllocationService;
	
	@PostMapping("/save")
	public ResponseDto save(@RequestBody CostAllocationDto dto) {
		return costAllocationService.saveCostAllocationDetails(dto);
	}

	@GetMapping("/getAll")
	public List<CostAllocationDto> get() {
		return costAllocationService.get();
	}
	@DeleteMapping("/delete/{id}")
	public ResponseDto delete(@PathVariable Integer id){
		return costAllocationService.delete(id);
		
	}
	
	@GetMapping("/getCostAllocationForTemplate")
	public List<AllocationForTemplateDto> getCostAllocationForTemplate(@RequestBody List<AllocationDto> allocateTemp){
		
		return costAllocationService.getCostAllocationForTemplate(allocateTemp);
	}
}
