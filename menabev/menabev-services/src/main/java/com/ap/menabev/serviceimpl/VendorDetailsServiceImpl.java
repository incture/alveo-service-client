package com.ap.menabev.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.VendorDetailsDto;
import com.ap.menabev.entity.VendorDetailsDo;
import com.ap.menabev.invoice.VendorDetailsRepository;
import com.ap.menabev.service.VendorDetailsService;
import com.ap.menabev.util.ApplicationConstants;

@Service
public class VendorDetailsServiceImpl implements VendorDetailsService
{

	@Autowired
	VendorDetailsRepository vendorDetailsRepository;
	
     public ResponseDto saveVendorDetailsCrud(VendorDetailsDto dto) {
		ResponseDto response = new ResponseDto();
		try {
			
			System.out.println("hi"+dto);
			ModelMapper mapper = new ModelMapper();
			vendorDetailsRepository.save(mapper.map(dto, VendorDetailsDo.class));
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage(ApplicationConstants.CREATED_SUCCESS);
			return response;
		} catch (Exception e) {
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.CREATE_FAILURE);
			return response;
		}

	}
	


	public List<VendorDetailsDto> get() {
		List<VendorDetailsDto> list = new ArrayList<>();

		ModelMapper modelMapper = new ModelMapper();
		try {
			List<VendorDetailsDo> doList = vendorDetailsRepository.findAll();
			for (VendorDetailsDo VendordetailsDo : doList) {
				list.add(modelMapper.map(VendordetailsDo, VendorDetailsDto.class));
			}
		} catch (Exception e) {
			System.err.println("Exception :" + e.getMessage());
		}
		return list;
	}

	public ResponseDto delete(String id) {
		ResponseDto response = new ResponseDto();
		try {
			vendorDetailsRepository.deleteById(id);
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage(ApplicationConstants.DELETE_SUCCESS);
		} catch (Exception e) {
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.DELETE_FAILURE);
		}
		return response;
	}



	@Override
	public ResponseDto delete(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

}



