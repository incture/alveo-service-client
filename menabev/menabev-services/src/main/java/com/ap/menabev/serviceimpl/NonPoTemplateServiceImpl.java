package com.ap.menabev.serviceimpl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
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
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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
			List<Object[]>is=nonPoTemplateItemsRepository.selectNonPoTemplate();
			HashMap<String , String> accountNo = new HashMap<>();
			for(Object[] obj : is){
				accountNo.put(String.valueOf(obj[0]), String.valueOf(obj[1]));
			}

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
                nonPoTemplateDto.setAccountNo(accountNo.get(nonPoTemplateDto.getTemplateId()));
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

		ResponseDto response = new ResponseDto();
		String checkTemplateNameIsAvaible = nonPoTemplateRepository.checkTemplateNameIsAvaible(dto.getTemplateName(),dto.getTemplateId());
		if(!ServiceUtil.isEmpty(checkTemplateNameIsAvaible)){
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(dto.getTemplateName() + " " + ApplicationConstants.UPDATE_FAILURE);
			return response;
		}
		
		NonPoTemplateDo nonPoTemplateDo = nonPoTemplateRepository.fetchNonPoTemplateDo(dto.getTemplateId());
		nonPoTemplateDo.setAccClerkId(dto.getAccClerkId());
		nonPoTemplateDo.setBasecoderId(dto.getBasecoderId());
		nonPoTemplateDo.setTemplateName(dto.getTemplateName());
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

			ResponseDto getResponse = updateNonPoTemplate(dto.getNonPoTemplate());
			if(getResponse.getCode().equals(ApplicationConstants.CODE_FAILURE)){
				response.setCode(ApplicationConstants.CODE_FAILURE);
				response.setStatus(ApplicationConstants.FAILURE);
				response.setMessage(dto.getNonPoTemplate().getTemplateName() + " already exists.");
				return response;
			}
			List<String> templateId = new ArrayList<String>();
			templateId.add(dto.getNonPoTemplate().getTemplateId());
			Integer delTemplateItems = nonPoTemplateItemsRepository.deleteNonPoTemplateItemsDo(templateId);
//			for (NonPoTemplateItemsDto nonPoTemplateItemsDto : dto.getNonPoTemplateItems()) {
//				nonPoTemplateItemsService.updateNonPoTemplateItems(nonPoTemplateItemsDto);
//			}
			for (NonPoTemplateItemsDto item : dto.getNonPoTemplateItems()) {
				item.setTemplateId(dto.getNonPoTemplate().getTemplateId());
				nonPoTemplateItemsService.saveNonPoTemplateItems(item);
			}
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage(dto.getNonPoTemplate().getTemplateName() + " " + ApplicationConstants.UPDATE_SUCCESS);
			return response;
		} catch (Exception e) {
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(dto.getNonPoTemplate().getTemplateName() + " " + ApplicationConstants.UPDATE_FAILURE+ " due to " + e.getMessage());
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
//		Object obj1[]=is.get(0);
//		String templateId=(String)obj1[0];
//		String accNumber=(String)obj1[1];
//		String acNumber=accNumber;
//		for(int i=1;i<is.size();i++){
//			Object iObj1[]=is.get(i);
//			String iTemplateId=(String)iObj1[0];
//			String iAccNumber=(String)iObj1[1];
//			
//
//			if(iTemplateId.equalsIgnoreCase(templateId)&&(!acNumber.equalsIgnoreCase("*"))){
//				if(!accNumber.equalsIgnoreCase(iAccNumber)){
//					acNumber="*";
//				}
//			}
//			else if(!iTemplateId.equalsIgnoreCase(templateId)){
//				String templateName = nonPoTemplateRepository.getById(templateId);
//				res.add(new AllocationDto(templateName,templateId,acNumber));
//				acNumber=iAccNumber;
//				accNumber=iAccNumber;
//				templateId=iTemplateId;
//			}
//		}
//		String templateName = nonPoTemplateRepository.getById(templateId);
//		res.add(new AllocationDto(templateName,templateId,acNumber));
		for(Object[] obj : is){
			res.add(new AllocationDto(String.valueOf(obj[2]), String.valueOf(obj[0]), String.valueOf(obj[1])));
		}
		
		return res;
	
	}

	@Override
	public ResponseDto postNonPoItemsToSAP() throws IOException, URISyntaxException {
	 return null;

	}
	
}