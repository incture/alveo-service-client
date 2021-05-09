package com.ap.menabev.serviceimpl;



import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.NonPoTemplateItemsDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.entity.NonPoTemplateItemsDo;
import com.ap.menabev.invoice.NonPoTemplateItemsRepository;
import com.ap.menabev.service.NonPoTemplateItemsService;
import com.ap.menabev.util.ApplicationConstants;

@Service
public class NonPoTemplateItemsServiceImpl implements NonPoTemplateItemsService {

	@Autowired
	NonPoTemplateItemsRepository nonPoTemplateItemsRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public ResponseDto saveNonPoTemplateItems(NonPoTemplateItemsDto dto) {
		// TODO Auto-gene
		ModelMapper mapper = new ModelMapper();

		NonPoTemplateItemsDo nonPoTemplateItemsDo = new NonPoTemplateItemsDo();

		Integer item = nonPoTemplateItemsRepository.countOfNonPoTemplateItems(dto.getTemplateId());

		nonPoTemplateItemsDo.setCostCenter(dto.getCostCenter());
		nonPoTemplateItemsDo.setGlAccount(dto.getGlAccount());
		nonPoTemplateItemsDo.setInternalOrderId(dto.getInternalOrderId());
		item = item + 1;
		nonPoTemplateItemsDo.setItemId(item.toString());
		nonPoTemplateItemsDo.setTemplateId(dto.getTemplateId());
		nonPoTemplateItemsDo.setIsNonPo(true);
		//nonPoTemplateItemsDo.setAmount(dto.getAmount());
		nonPoTemplateItemsDo.setCompanyCode(dto.getCompanyCode());
		nonPoTemplateItemsDo.setCrDbIndicator(dto.getCrDbIndicator());
		nonPoTemplateItemsDo.setMaterialDescription(dto.getMaterialDescription());
		nonPoTemplateItemsDo.setProfitCenter(dto.getProfitCenter());
		nonPoTemplateItemsDo.setItemText(dto.getItemText());
		 nonPoTemplateItemsDo.setAssetNo(dto.getAssetNo());
		 nonPoTemplateItemsDo.setSubNumber(dto.getSubNumber());
		 nonPoTemplateItemsDo.setWbsElement(dto.getWbsElement());
		ResponseDto response = new ResponseDto();
		if (nonPoTemplateItemsRepository.fetchNonPoTemplateItemsDo(dto.getTemplateId(),
				nonPoTemplateItemsDo.getItemId()) == null) {
			try {

				nonPoTemplateItemsRepository.save(nonPoTemplateItemsDo);
				response.setCode(ApplicationConstants.CODE_SUCCESS);
				response.setStatus(ApplicationConstants.SUCCESS);
				response.setMessage("TemplateId :" + nonPoTemplateItemsDo.getTemplateId() + " ItemId: "
						+ nonPoTemplateItemsDo.getItemId() + " " + ApplicationConstants.CREATED_SUCCESS);
				return response;
			} catch (Exception e) {
				response.setCode(ApplicationConstants.CODE_FAILURE);
				response.setStatus(ApplicationConstants.FAILURE);
				response.setMessage("TemplateId :" + nonPoTemplateItemsDo.getTemplateId() + " ItemId:"
						+ nonPoTemplateItemsDo.getItemId() + " " + ApplicationConstants.CREATE_FAILURE);
				return response;
			}
		} else {
			// nonPoTemplateItemsRepository.save(nonPoTemplateItemsDo);
			// response.setCode(ApplicationConstants.CODE_SUCCESS);
			// response.setStatus(ApplicationConstants.UPDATE_SUCCESS);
			// response.setMessage(
			// "TemplateId :" + dto.getTemplateId() + " ,ItemId:" +
			// nonPoTemplateItemsDo.getItemId() + " Already exists");

			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage("TemplateId :" + nonPoTemplateItemsDo.getTemplateId() + " ItemId:"
					+ nonPoTemplateItemsDo.getItemId() + " " + ApplicationConstants.CREATE_FAILURE);
			return response;

		}
	}

	@Override
	public List<NonPoTemplateItemsDto> get() {
		// TODO Auto-generated method stub
		List<NonPoTemplateItemsDto> list = new ArrayList<>();

		ModelMapper modelMapper = new ModelMapper();
		try {
			List<NonPoTemplateItemsDo> doList = nonPoTemplateItemsRepository.findAll();
			for (NonPoTemplateItemsDo NonPoTemplateItemsDo : doList) {
				list.add(modelMapper.map(NonPoTemplateItemsDo, NonPoTemplateItemsDto.class));
			}
		} catch (Exception e) {
			System.err.println("Exception : " + e.getMessage());
		}
		return list;
	}

	@Override
	public ResponseDto delete(NonPoTemplateItemsDto dto) {
		// TODO Auto-generated method stub
		ResponseDto response = new ResponseDto();

		NonPoTemplateItemsDo nonPoTemplateItemsDo = new NonPoTemplateItemsDo();

		nonPoTemplateItemsDo.setCostCenter(dto.getCostCenter());
		nonPoTemplateItemsDo.setGlAccount(dto.getGlAccount());
		nonPoTemplateItemsDo.setInternalOrderId(dto.getInternalOrderId());
		nonPoTemplateItemsDo.setItemId(dto.getItemId());
		nonPoTemplateItemsDo.setTemplateId(dto.getTemplateId());
		nonPoTemplateItemsDo.setIsNonPo(true);
		//nonPoTemplateItemsDo.setAmount(dto.getAmount());
		nonPoTemplateItemsDo.setCompanyCode(dto.getCompanyCode());
		nonPoTemplateItemsDo.setCrDbIndicator(dto.getCrDbIndicator());
		nonPoTemplateItemsDo.setMaterialDescription(dto.getMaterialDescription());
		nonPoTemplateItemsDo.setProfitCenter(dto.getProfitCenter());
		nonPoTemplateItemsDo.setItemText(dto.getItemText());
		 nonPoTemplateItemsDo.setAssetNo(dto.getAssetNo());
		 nonPoTemplateItemsDo.setSubNumber(dto.getSubNumber());
		 nonPoTemplateItemsDo.setWbsElement(dto.getWbsElement());
		
		try {

			nonPoTemplateItemsRepository.delete(nonPoTemplateItemsDo);
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
	public ResponseDto updateNonPoTemplateItems(NonPoTemplateItemsDto dto) {
		// TODO Auto-generated method stub
		ResponseDto response = new ResponseDto();

		NonPoTemplateItemsDo nonPoTemplateItemsDo = nonPoTemplateItemsRepository
				.fetchNonPoTemplateItemsDo(dto.getTemplateId(), dto.getItemId());
		nonPoTemplateItemsDo.setCostCenter(dto.getCostCenter());
		nonPoTemplateItemsDo.setGlAccount(dto.getGlAccount());
		nonPoTemplateItemsDo.setInternalOrderId(dto.getInternalOrderId());
		nonPoTemplateItemsDo.setIsNonPo(true);
		//nonPoTemplateItemsDo.setAmount(dto.getAmount());
		nonPoTemplateItemsDo.setCompanyCode(dto.getCompanyCode());
		nonPoTemplateItemsDo.setCrDbIndicator(dto.getCrDbIndicator());
		nonPoTemplateItemsDo.setMaterialDescription(dto.getMaterialDescription());
		nonPoTemplateItemsDo.setProfitCenter(dto.getProfitCenter());
		nonPoTemplateItemsDo.setItemText(dto.getItemText());
		 nonPoTemplateItemsDo.setAssetNo(dto.getAssetNo());
		 nonPoTemplateItemsDo.setSubNumber(dto.getSubNumber());
		 nonPoTemplateItemsDo.setWbsElement(dto.getWbsElement());

		try {

			nonPoTemplateItemsRepository.save(nonPoTemplateItemsDo);
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage("TemplateId :" + dto.getTemplateId() + " ItemId: " + dto.getItemId() + " "
					+ ApplicationConstants.UPDATE_SUCCESS);
			return response;
		} catch (Exception e) {
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage("TemplateId :" + dto.getTemplateId() + " ItemId: " + dto.getItemId() + " "
					+ ApplicationConstants.UPDATE_FAILURE);
			return response;
		}
	}

}
