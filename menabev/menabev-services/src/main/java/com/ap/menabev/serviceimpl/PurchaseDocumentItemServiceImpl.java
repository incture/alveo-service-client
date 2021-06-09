package com.ap.menabev.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.PurchaseDocumentItemDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.entity.PurchaseDocumentItemDo;
import com.ap.menabev.invoice.PurchaseDocumentItemRespository;
import com.ap.menabev.service.PurchaseDocumentItemService;
import com.ap.menabev.util.ApplicationConstants;
@Service
public class PurchaseDocumentItemServiceImpl implements PurchaseDocumentItemService{
	
	private static final Logger logger = LoggerFactory.getLogger(PurchaseDocumentItemServiceImpl.class);
	@Autowired
	PurchaseDocumentItemRespository repository;
	@Override
	public ResponseDto save(PurchaseDocumentItemDto dto) {
		ResponseDto response = new ResponseDto();
		ModelMapper mapper = new ModelMapper();
		try {
			repository.save(mapper.map(dto, PurchaseDocumentItemDo.class));
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage(ApplicationConstants.CREATED_SUCCESS);
			return response;
		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.CREATE_FAILURE);
			return response;
		}
	}

	@Override
	public List<PurchaseDocumentItemDto> getAll() {
		List<PurchaseDocumentItemDto> list = new ArrayList<>();
		ModelMapper mapper = new ModelMapper();
		try {
			List<PurchaseDocumentItemDo> entityList = repository.findAll();
			for (PurchaseDocumentItemDo purchaseDocumentItemDo : entityList) {
				list.add(mapper.map(purchaseDocumentItemDo, PurchaseDocumentItemDto.class));
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return list;
		}
		
	}

	@Override
	public ResponseDto delete(String id) {
		ResponseDto response = new ResponseDto();
		try {
			repository.deleteById(id);
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage(ApplicationConstants.DELETE_SUCCESS);
			return response;
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.DELETE_FAILURE);
			return response;
		}
	}

	@Override
	public List<PurchaseDocumentItemDo> getPurchaseDocumentItem(String refDocNum) {
		// TODO Auto-generated method stub
		return  repository.getPurchaseDocumentItem(refDocNum);
	}

}
