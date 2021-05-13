package com.ap.menabev.service;

import java.util.List;

import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.VendorDetailsDto;

public interface VendorDetailsService 
{
	ResponseDto saveVendorDetailsCrud(VendorDetailsDto dto);

	List<VendorDetailsDto> get();

	ResponseDto delete(Integer id);

	

}
