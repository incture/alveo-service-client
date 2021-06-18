package com.ap.menabev.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.PurchaseDocumentHeaderDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.entity.PurchaseDocumentHeaderDo;
import com.ap.menabev.invoice.InvoiceHeaderRepository;
import com.ap.menabev.invoice.InvoiceItemRepository;
import com.ap.menabev.invoice.PurchaseDocumentHeaderRepository;
import com.ap.menabev.service.PurchaseDocumentHeaderService;
import com.ap.menabev.util.ApplicationConstants;

@Service
public class PurchaseDocumentHeaderServiceImpl implements PurchaseDocumentHeaderService {

	private static final Logger logger = LoggerFactory.getLogger(PurchaseDocumentHeaderServiceImpl.class);

	@Autowired
	PurchaseDocumentHeaderRepository poHeaderRepository;
	

	@Autowired
	InvoiceHeaderRepository invoiceHeaderRepository;

	@Autowired
	InvoiceItemRepository invoiceItemRepository;


	@Override
	public ResponseDto save(PurchaseDocumentHeaderDto dto) {
		ResponseDto response = new ResponseDto();
		ModelMapper mapper = new ModelMapper();
		try {
			poHeaderRepository.save(mapper.map(dto, PurchaseDocumentHeaderDo.class));
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
	public List<PurchaseDocumentHeaderDto> getAll() {
		List<PurchaseDocumentHeaderDto> list = new ArrayList<>();
		ModelMapper mapper = new ModelMapper();
		try {
			List<PurchaseDocumentHeaderDo> entityList = poHeaderRepository.findAll();
			for (PurchaseDocumentHeaderDo entity : entityList) {
				list.add(mapper.map(entity, PurchaseDocumentHeaderDto.class));
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return list;
		}
	}

	@Override
	public ResponseDto delete(Integer id) {
		ResponseDto response = new ResponseDto();
		try {
			poHeaderRepository.deleteById(id);
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


	
}
