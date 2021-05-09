package com.ap.menabev.service;



import java.util.List;

import com.ap.menabev.dto.CostAllocationDto;
import com.ap.menabev.dto.ResponseDto;



public interface CostAllocationService {

	ResponseDto saveCostAllocationDetails(CostAllocationDto dto);

	List<CostAllocationDto> get();

	ResponseDto delete(Integer id);

}
