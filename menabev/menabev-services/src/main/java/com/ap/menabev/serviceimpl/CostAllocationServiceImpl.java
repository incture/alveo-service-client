package com.ap.menabev.serviceimpl;



import java.util.ArrayList;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ap.menabev.dto.AllocationDto;
import com.ap.menabev.dto.AllocationForTemplateDto;
import com.ap.menabev.dto.CostAllocationDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.entity.CostAllocationDo;
import com.ap.menabev.entity.NonPoTemplateItemsDo;
import com.ap.menabev.invoice.CostAllocationRepository;
import com.ap.menabev.invoice.NonPoTemplateItemsRepository;
import com.ap.menabev.service.CostAllocationService;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;


@Service
public class CostAllocationServiceImpl implements CostAllocationService {

	@Autowired
	CostAllocationRepository costAllocationRepository;
	
	@Autowired
	NonPoTemplateItemsRepository nonPoTemplateItemsRepository;

	private static final Logger logger = LoggerFactory.getLogger(CostAllocationServiceImpl.class);

	@Override
	public ResponseDto saveCostAllocationDetails(CostAllocationDto dto) {
		ResponseDto response = new ResponseDto();
		try {
			ModelMapper mapper = new ModelMapper();
			CostAllocationDo costAllocationDo = costAllocationRepository
					.save(mapper.map(dto, CostAllocationDo.class));
			if (!ServiceUtil.isEmpty(costAllocationDo)) {
				response.setMessage("Cost Allocation data created Successfully");
				response.setStatus(ApplicationConstants.SUCCESS);
				response.setCode(ApplicationConstants.CODE_SUCCESS);
			}
			return response;
		} catch (Exception e) {

			logger.error(
					"[ApAutomation][PoPlannedCostServiceImpl][saveCostAllocationDetails][Exception] = " + e.getMessage());
			e.printStackTrace();
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.CREATE_FAILURE);
			return response;
			
		}
	}

	@Override
	public List<CostAllocationDto> get() {
		List<CostAllocationDto> list = new ArrayList<>();
		try {
			ModelMapper mapper = new ModelMapper();
			List<CostAllocationDo> entityList = costAllocationRepository.findAll();
			for (CostAllocationDo costAllocationDo : entityList) {
				list.add(mapper.map(costAllocationDo, CostAllocationDto.class));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}

	@Override
	public ResponseDto delete(Integer id) {
		ResponseDto response = new ResponseDto();
		try {
			//costAllocationRepository.deleteById(id);
			response.setMessage("Cost Allocation data deleted Successfully");
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			return response;
		} catch (Exception e) {
			logger.error(
					"[ApAutomation][PoPlannedCostServiceImpl][delete][Exception] = " + e.getMessage());
			e.printStackTrace();
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.DELETE_FAILURE);
			return response;
		}
	}
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String getItemID(String requestId) {
		// TODO Auto-generated method stub
		String serialNo = "";
		try {
			serialNo = costAllocationRepository.getItemID(requestId);

			if (ServiceUtil.isEmpty(serialNo)) {
				return "0001";
			}
			logger.error("[ApAutomation][CostAllocation][getItemId][itemId] = " + serialNo);
			System.err.println("serialNo " + serialNo);
			return String.format("%04d", Integer.parseInt(serialNo) + 1);
		} catch (Exception e) {
			logger.error(
					"[ApAutomation][CostAllocation][getItemId][Exception] = " + e.getMessage());
		}
		return serialNo;
	}
	
	@Override
	public List<AllocationForTemplateDto> getCostAllocationForTemplate(List<AllocationDto> allocateTemp) {
		// TODO Auto-generated method stub
		
		List<AllocationForTemplateDto> allTempDtoList= new ArrayList<AllocationForTemplateDto>();
		ModelMapper m= new ModelMapper();
		for (AllocationDto dto :allocateTemp ){
			AllocationForTemplateDto  allTempDto= new AllocationForTemplateDto();
			List<CostAllocationDto> costAllocationList=new ArrayList<CostAllocationDto>();
			Double amount=ServiceUtil.stringToDouble(dto.getAmount());
			List<NonPoTemplateItemsDo> itemList=nonPoTemplateItemsRepository.fetchNonPoTemplateItemsDo(dto.getTemplateId());
			for(NonPoTemplateItemsDo item:itemList){
				CostAllocationDto costAllocationDto= new CostAllocationDto();
				costAllocationDto=m.map(item,CostAllocationDto.class);
				Double per=ServiceUtil.stringToDouble(item.getAllocationPercent());
				Double netValue=(amount*per)/100;
				costAllocationDto.setNetValue(String.format("%.2f", netValue));
				costAllocationDto.setDistrPerc((item.getAllocationPercent()));
				costAllocationDto.setAccountNum(item.getAccountNo());
				costAllocationDto.setMaterialDesc(item.getMaterialDescription());
				costAllocationList.add(costAllocationDto);
				
				
				
			}
			allTempDto.setAmount(dto.getAmount());
			allTempDto.setTemplateId(dto.getTemplateId());
			allTempDto.setTemplateName(dto.getTemplateName());
			allTempDto.setCostAllocationList(costAllocationList);
			allTempDtoList.add(allTempDto);
		}
		
		
		return allTempDtoList;
	}

}
