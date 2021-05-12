package com.ap.menabev.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ap.menabev.dto.AllocationDto;
import com.ap.menabev.dto.NonPoTemplateDto;
import com.ap.menabev.dto.NonPoTemplateHIDto;
import com.ap.menabev.dto.NonPoTemplateItemsDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.entity.NonPoTemplateDo;
import com.ap.menabev.entity.NonPoTemplateItemsDo;
import com.ap.menabev.invoice.NonPoTemplateItemsRepository;
import com.ap.menabev.invoice.NonPoTemplateRepository;
import com.ap.menabev.service.NonPoTemplateItemsService;
import com.ap.menabev.service.NonPoTemplateService;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;

@Service
public class NonPoTemplateServiceImpl implements NonPoTemplateService {

	@Autowired
	NonPoTemplateRepository nonPoTemplateRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	NonPoTemplateItemsRepository nonPoTemplateItemsRepository;
	

	@Autowired
	NonPoTemplateItemsService nonPoTemplateItemsService;

	private static final Logger logger = LoggerFactory.getLogger(NonPoTemplateServiceImpl.class);

	@Override
	public ResponseDto saveNonPoTemplate(NonPoTemplateHIDto dto) {
		// TODO Auto-generated method stub
		ResponseDto response = new ResponseDto();
		try {
			String duplicateTemplate = nonPoTemplateRepository.getByName(dto.getNonPoTemplate().getTemplateName());
			if(ServiceUtil.isEmpty(duplicateTemplate)){
				dto.getNonPoTemplate().setCreatedAt(new Date());
				ModelMapper mapper = new ModelMapper();
				dto.getNonPoTemplate().setTemplateId(getTemplateId());
				NonPoTemplateDo re = nonPoTemplateRepository
						.save(mapper.map(dto.getNonPoTemplate(), NonPoTemplateDo.class));
				logger.error("Printing");
				if (!ServiceUtil.isEmpty(re)) {
					for (int i = 0; i < dto.getNonPoTemplateItems().size(); i++) {
						dto.getNonPoTemplateItems().get(i).setTemplateId(dto.getNonPoTemplate().getTemplateId());
						nonPoTemplateItemsService.saveNonPoTemplateItems(dto.getNonPoTemplateItems().get(i));
					}
				}
				response.setCode(ApplicationConstants.CODE_SUCCESS);
				response.setStatus(ApplicationConstants.SUCCESS);
				response.setMessage(dto.getNonPoTemplate().getTemplateName() + " " + ApplicationConstants.CREATED_SUCCESS);
				return response;
			}else{
				response.setCode(ApplicationConstants.CODE_FAILURE);
				response.setStatus(ApplicationConstants.FAILURE);
				response.setMessage(dto.getNonPoTemplate().getTemplateName() + " already exists.");
				return response;
			}
		} catch (Exception e) {
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(dto.getNonPoTemplate().getTemplateName() + " " + ApplicationConstants.CREATE_FAILURE);
			return response;
		}
	}

	public String getTemplateId() {
		
		String tempId = nonPoTemplateRepository.fetchTemplateId();
		return "TEMP-" + String.format("%06d", Integer.parseInt(tempId));
	}

	@Override
	public List<NonPoTemplateHIDto> get() {
		// TODO Auto-generated method stub
		List<NonPoTemplateHIDto> list = new ArrayList<NonPoTemplateHIDto>();

		ModelMapper modelMapper = new ModelMapper();
		try {

			List<NonPoTemplateDo> doList = nonPoTemplateRepository.fetchAll();
			NonPoTemplateHIDto nonPoTemplateHIDto;

			for (NonPoTemplateDo nonPoTemplateDo : doList) {
				NonPoTemplateDto nonPoTemplateDto = new NonPoTemplateDto();
				nonPoTemplateDto = modelMapper.map(nonPoTemplateDo, NonPoTemplateDto.class);
				List<NonPoTemplateItemsDo> nonPoTemplateItemsDoList = nonPoTemplateItemsRepository
						.fetchNonPoTemplateItemsDo(nonPoTemplateDto.getTemplateId());
				logger.error("[ApAutomation][NonPoTemplateServiceImpl][FetchNonPoData][Output] = "
						+ nonPoTemplateItemsDoList.toString());

				List<NonPoTemplateItemsDto> nonPoTemplateItemsDtoList = new ArrayList<NonPoTemplateItemsDto>();
				for (NonPoTemplateItemsDo nonPoTemplateItemsDo : nonPoTemplateItemsDoList) {

					NonPoTemplateItemsDto nonPoTemplateItemsDto = new NonPoTemplateItemsDto();
					nonPoTemplateItemsDto = modelMapper.map(nonPoTemplateItemsDo, NonPoTemplateItemsDto.class);

					// nonPoTemplateItemsDtoList.add(modelMapper.map(nonPoTemplateItemsDo,
					// NonPoTemplateItemsDto.class));
					nonPoTemplateItemsDtoList.add(nonPoTemplateItemsDto);
				}

				nonPoTemplateHIDto = new NonPoTemplateHIDto(nonPoTemplateDto, nonPoTemplateItemsDtoList);
				list.add(nonPoTemplateHIDto);
				// nonPoTemplateItemsDtoList.clear();

			}
		} catch (Exception e) {
			System.err.println("Exception :" + e.getMessage());
		}
		return list;
	}

	@Override
	public List<NonPoTemplateHIDto> get(int limit, int offset) {
		// TODO Auto-generated method stub
		List<NonPoTemplateHIDto> list = new ArrayList<NonPoTemplateHIDto>();
		if (offset != 0) {
			offset = offset - 1;
		}
		ModelMapper modelMapper = new ModelMapper();
		try {

			// List<NonPoTemplateDo> doList =
			// nonPoTemplateRepository.fetchAll(limit, offset);
			List<NonPoTemplateDo> doList = nonPoTemplateRepository.fetchAll();
			NonPoTemplateHIDto nonPoTemplateHIDto;

			for (NonPoTemplateDo nonPoTemplateDo : doList) {
				NonPoTemplateDto nonPoTemplateDto = new NonPoTemplateDto();
				nonPoTemplateDto = modelMapper.map(nonPoTemplateDo, NonPoTemplateDto.class);
				List<NonPoTemplateItemsDo> nonPoTemplateItemsDoList = nonPoTemplateItemsRepository
						.fetchNonPoTemplateItemsDo(nonPoTemplateDto.getTemplateId());
				logger.error("[ApAutomation][NonPoTemplateServiceImpl][FetchNonPoData][Output] = "
						+ nonPoTemplateItemsDoList.toString());

				List<NonPoTemplateItemsDto> nonPoTemplateItemsDtoList = new ArrayList<NonPoTemplateItemsDto>();
				for (NonPoTemplateItemsDo nonPoTemplateItemsDo : nonPoTemplateItemsDoList) {

					NonPoTemplateItemsDto nonPoTemplateItemsDto = new NonPoTemplateItemsDto();
					nonPoTemplateItemsDto = modelMapper.map(nonPoTemplateItemsDo, NonPoTemplateItemsDto.class);

					// nonPoTemplateItemsDtoList.add(modelMapper.map(nonPoTemplateItemsDo,
					// NonPoTemplateItemsDto.class));
					nonPoTemplateItemsDtoList.add(nonPoTemplateItemsDto);
				}

				nonPoTemplateHIDto = new NonPoTemplateHIDto(nonPoTemplateDto, nonPoTemplateItemsDtoList);
				list.add(nonPoTemplateHIDto);
				// nonPoTemplateItemsDtoList.clear();

			}
		} catch (Exception e) {
			System.err.println("Exception :" + e.getMessage());
		}
		return list;
	}

	@Override
	@Transactional
	public ResponseDto delete(List<String> templateId) {
		// TODO Auto-generated method stub
		ResponseDto response = new ResponseDto();
		// NonPoTemplateDo nonPoTemplateDo=new NonPoTemplateDo();
		// nonPoTemplateDo.setTemplateId(templateId);

		try {
			// nonPoTemplateRepository.delete(nonPoTemplateDo);
			Integer delTemplate = nonPoTemplateRepository.deleteNonPoTemplateDo(templateId);
			Integer delTemplateItems = nonPoTemplateItemsRepository.deleteNonPoTemplateItemsDo(templateId);
			if (delTemplate > 0) {
				response.setCode(ApplicationConstants.CODE_SUCCESS);
				response.setStatus(ApplicationConstants.SUCCESS);
				response.setMessage(ApplicationConstants.DELETE_SUCCESS);
			}
		} catch (Exception e) {
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.DELETE_FAILURE + " " + e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseDto updateNonPoTemplate(NonPoTemplateDto dto) {

		// TODO Auto-generated method stub
		ResponseDto response = new ResponseDto();
		NonPoTemplateDo nonPoTemplateDo = nonPoTemplateRepository.fetchNonPoTemplateDo(dto.getTemplateId());
		nonPoTemplateDo.setAccClerkId(dto.getAccClerkId());
		nonPoTemplateDo.setBasecoderId(dto.getBasecoderId());
//		nonPoTemplateDo.setCreatedAt(dto.getCreatedAt());
//		nonPoTemplateDo.setCreatedBy(dto.getCreatedBy());
		nonPoTemplateDo.setUpdatedAt(new Date());
		nonPoTemplateDo.setUpdatedBy(dto.getUpdatedBy());
		nonPoTemplateDo.setVendorId(dto.getVendorId());
		try {

			nonPoTemplateRepository.save(nonPoTemplateDo);
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage(dto.getTemplateName() + " " + ApplicationConstants.UPDATE_SUCCESS);
			return response;
		} catch (Exception e) {
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(dto.getTemplateName() + " " + ApplicationConstants.UPDATE_FAILURE);
			return response;
		}
	}

	@Override
	public ResponseDto updateNonPoTemplateHI(NonPoTemplateHIDto dto) {

		// TODO Auto-generated method stub
		ResponseDto response = new ResponseDto();
		try {

			updateNonPoTemplate(dto.getNonPoTemplate());
			for (NonPoTemplateItemsDto nonPoTemplateItemsDto : dto.getNonPoTemplateItems()) {
				nonPoTemplateItemsService.updateNonPoTemplateItems(nonPoTemplateItemsDto);
			}
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage(dto.getNonPoTemplate().getTemplateName() + " " + ApplicationConstants.UPDATE_SUCCESS);
			return response;
		} catch (Exception e) {
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(dto.getNonPoTemplate().getTemplateName() + " " + ApplicationConstants.UPDATE_FAILURE);
			return response;
		}

	}

	@Override
	public List<NonPoTemplateItemsDto> getNonPoTemplateItems(String templateId) {
		// TODO Auto-generated method stub
		List<NonPoTemplateItemsDo> doList = nonPoTemplateItemsRepository.fetchNonPoTemplateItemsDo(templateId);
		ModelMapper modelMapper = new ModelMapper();
		List<NonPoTemplateItemsDto> list = new ArrayList<>();
		for (NonPoTemplateItemsDo NonPoTemplateItemsDo : doList) {
			list.add(modelMapper.map(NonPoTemplateItemsDo, NonPoTemplateItemsDto.class));
		}
		return list;
	}
	@Override
	public List<AllocationDto> selectNonPoTemplate() {
		// TODO Auto-generated method stub

		List<Object[]>is=nonPoTemplateItemsRepository.selectNonPoTemplate();

		List<AllocationDto> res = new ArrayList<AllocationDto>();
		Object obj1[]=is.get(0);
		String templateId=(String)obj1[0];
		String accNumber=(String)obj1[1];
		String acNumber=accNumber;
		for(int i=1;i<is.size();i++){
			Object iObj1[]=is.get(i);
			String iTemplateId=(String)iObj1[0];
			String iAccNumber=(String)iObj1[1];
			

			if(iTemplateId.equalsIgnoreCase(templateId)&&(!acNumber.equalsIgnoreCase("*"))){
				if(!accNumber.equalsIgnoreCase(iAccNumber)){
					acNumber="*";
				}
			}
			else if(!iTemplateId.equalsIgnoreCase(templateId)){
				String templateName = nonPoTemplateRepository.getById(templateId);
				res.add(new AllocationDto(templateName,templateId,acNumber));
				acNumber=iAccNumber;
				accNumber=iAccNumber;
				templateId=iTemplateId;
			}
		}
		String templateName = nonPoTemplateRepository.getById(templateId);
		res.add(new AllocationDto(templateName,templateId,acNumber));
		
		return res;
	
	}

}
