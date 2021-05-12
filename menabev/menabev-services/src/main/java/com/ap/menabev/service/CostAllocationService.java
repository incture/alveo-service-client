package com.ap.menabev.service;



import java.util.List;

import com.ap.menabev.dto.AllocationDto;
import com.ap.menabev.dto.AllocationForTemplateDto;
import com.ap.menabev.dto.CostAllocationDto;
import com.ap.menabev.dto.ResponseDto;



public interface CostAllocationService {

	ResponseDto saveCostAllocationDetails(CostAllocationDto dto);

	List<CostAllocationDto> get();

	ResponseDto delete(Integer id);

	List<AllocationForTemplateDto> getCostAllocationForTemplate(List<AllocationDto> allocateTemp);

}
