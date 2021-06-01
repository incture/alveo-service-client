package com.ap.menabev.serviceimpl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
			if (ServiceUtil.isEmpty(duplicateTemplate)) {
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
				response.setMessage(
						dto.getNonPoTemplate().getTemplateName() + " " + ApplicationConstants.CREATED_SUCCESS);
				return response;
			} else {
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
	public List<NonPoTemplateHIDto> get(String templateName, String accountNo, Integer limit, Integer offset) {
		// TODO Auto-generated method stub
		Integer count = nonPoTemplateRepository.getCount();
		List<NonPoTemplateHIDto> list = new ArrayList<NonPoTemplateHIDto>();

		ModelMapper modelMapper = new ModelMapper();
		try {

			if (ServiceUtil.isEmpty(templateName) && ServiceUtil.isEmpty(accountNo)) {
				System.out.println("Both Null");
				List<NonPoTemplateDo> doList = new ArrayList<>();
				if (!ServiceUtil.isEmpty(limit) && !ServiceUtil.isEmpty(offset)) {
					doList = nonPoTemplateRepository.fetchAllByLimitOffset(limit, offset);
				} else {
					doList = nonPoTemplateRepository.fetchAll();
				}

				NonPoTemplateHIDto nonPoTemplateHIDto;
				List<Object[]> is = nonPoTemplateItemsRepository.selectNonPoTemplate();
				HashMap<String, String> getAccountNo = new HashMap<>();
				for (Object[] obj : is) {
					getAccountNo.put(String.valueOf(obj[0]), String.valueOf(obj[1]));
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
					nonPoTemplateDto.setAccountNo(getAccountNo.get(nonPoTemplateDto.getTemplateId()));
					nonPoTemplateHIDto = new NonPoTemplateHIDto(nonPoTemplateDto, nonPoTemplateItemsDtoList);
					nonPoTemplateHIDto.setCount(count);
					list.add(nonPoTemplateHIDto);
					// nonPoTemplateItemsDtoList.clear();
				}

			} else if (ServiceUtil.isEmpty(templateName)) {
				System.out.println("templateId is null");
				List<String> getTemplateId = nonPoTemplateItemsRepository.getTemplateId(accountNo);
				System.out.println("List of template Id:::::" + getTemplateId);
				List<NonPoTemplateDo> doList = new ArrayList<>();
				if (!ServiceUtil.isEmpty(limit) && !ServiceUtil.isEmpty(offset)) {
					doList = nonPoTemplateRepository.fetchAllByTemplateIdWithLimitandOffset(getTemplateId, limit,
							offset);
				} else {
					doList = nonPoTemplateRepository.fetchAllByTemplateId(getTemplateId);
				}

				NonPoTemplateHIDto nonPoTemplateHIDto;
				List<Object[]> is = nonPoTemplateItemsRepository.selectNonPoTemplateByTemplateId(getTemplateId);
				HashMap<String, String> getAccountNo = new HashMap<>();
				for (Object[] obj : is) {
					getAccountNo.put(String.valueOf(obj[0]), String.valueOf(obj[1]));
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
					nonPoTemplateDto.setAccountNo(getAccountNo.get(nonPoTemplateDto.getTemplateId()));
					nonPoTemplateHIDto = new NonPoTemplateHIDto(nonPoTemplateDto, nonPoTemplateItemsDtoList);
					nonPoTemplateHIDto.setCount(count);
					list.add(nonPoTemplateHIDto);
					// nonPoTemplateItemsDtoList.clear();
				}

			} else if (ServiceUtil.isEmpty(accountNo)) {
				System.out.println("Account no is null");
				List<String> getTemplateId = new ArrayList<>();
				getTemplateId.add(templateName);
				System.out.println("template Id::::" + getTemplateId);
				String templateIdByTemplateName = nonPoTemplateRepository.templateIdByName(templateName);
				List<String> getTemplateIdByName = new ArrayList<>();
				getTemplateIdByName.add(templateIdByTemplateName);
				List<NonPoTemplateDo> doList = new ArrayList<>();
				if (!ServiceUtil.isEmpty(limit) && !ServiceUtil.isEmpty(offset)) {
					doList = nonPoTemplateRepository
							.fetchAllByTemplateIdWithLimitandOffsetandtemplateName(getTemplateId, limit, offset);
				} else {
					doList = nonPoTemplateRepository
							.fetchAllByTemplateIdWithLimitandOffsetandtemplateName(getTemplateId, 100, 0);
				}
				NonPoTemplateHIDto nonPoTemplateHIDto;
				List<Object[]> is = nonPoTemplateItemsRepository.selectNonPoTemplateByTemplateId(getTemplateIdByName);
				HashMap<String, String> getAccountNo = new HashMap<>();
				for (Object[] obj : is) {
					getAccountNo.put(String.valueOf(obj[0]), String.valueOf(obj[1]));
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
					nonPoTemplateDto.setAccountNo(getAccountNo.get(nonPoTemplateDto.getTemplateId()));
					nonPoTemplateHIDto = new NonPoTemplateHIDto(nonPoTemplateDto, nonPoTemplateItemsDtoList);
					nonPoTemplateHIDto.setCount(count);
					list.add(nonPoTemplateHIDto);
					// nonPoTemplateItemsDtoList.clear();
				}
			} else {

				System.out.println("Both Are Present");
				List<String> getTemplateId = nonPoTemplateItemsRepository.gettemplateIdByAccountNo(templateName,
						accountNo);
				System.out.println("template Id::::" + getTemplateId);
				List<NonPoTemplateDo> doList = new ArrayList<>();
				if (!ServiceUtil.isEmpty(limit) && !ServiceUtil.isEmpty(offset)) {
					doList = nonPoTemplateRepository.fetchAllByTemplateIdWithLimitandOffset(getTemplateId, limit,
							offset);
				} else {
					doList = nonPoTemplateRepository.fetchAllByTemplateId(getTemplateId);
				}
				NonPoTemplateHIDto nonPoTemplateHIDto;
				List<Object[]> is = nonPoTemplateItemsRepository.selectNonPoTemplateByTemplateId(getTemplateId);
				HashMap<String, String> getAccountNo = new HashMap<>();
				for (Object[] obj : is) {
					getAccountNo.put(String.valueOf(obj[0]), String.valueOf(obj[1]));
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
					nonPoTemplateDto.setAccountNo(getAccountNo.get(nonPoTemplateDto.getTemplateId()));
					nonPoTemplateHIDto = new NonPoTemplateHIDto(nonPoTemplateDto, nonPoTemplateItemsDtoList);
					nonPoTemplateHIDto.setCount(count);
					list.add(nonPoTemplateHIDto);
					// nonPoTemplateItemsDtoList.clear();
				}

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
		String checkTemplateNameIsAvaible = nonPoTemplateRepository.checkTemplateNameIsAvaible(dto.getTemplateName(),
				dto.getTemplateId());
		if (!ServiceUtil.isEmpty(checkTemplateNameIsAvaible)) {
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(dto.getTemplateName() + " " + ApplicationConstants.UPDATE_FAILURE);
			return response;
		}

		NonPoTemplateDo nonPoTemplateDo = nonPoTemplateRepository.fetchNonPoTemplateDo(dto.getTemplateId());
		nonPoTemplateDo.setAccClerkId(dto.getAccClerkId());
		nonPoTemplateDo.setBasecoderId(dto.getBasecoderId());
		nonPoTemplateDo.setTemplateName(dto.getTemplateName());
		// nonPoTemplateDo.setCreatedAt(dto.getCreatedAt());
		// nonPoTemplateDo.setCreatedBy(dto.getCreatedBy());
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
			if (getResponse.getCode().equals(ApplicationConstants.CODE_FAILURE)) {
				response.setCode(ApplicationConstants.CODE_FAILURE);
				response.setStatus(ApplicationConstants.FAILURE);
				response.setMessage(dto.getNonPoTemplate().getTemplateName() + " already exists.");
				return response;
			}
			List<String> templateId = new ArrayList<String>();
			templateId.add(dto.getNonPoTemplate().getTemplateId());
			Integer delTemplateItems = nonPoTemplateItemsRepository.deleteNonPoTemplateItemsDo(templateId);
			// for (NonPoTemplateItemsDto nonPoTemplateItemsDto :
			// dto.getNonPoTemplateItems()) {
			// nonPoTemplateItemsService.updateNonPoTemplateItems(nonPoTemplateItemsDto);
			// }
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
			response.setMessage(dto.getNonPoTemplate().getTemplateName() + " " + ApplicationConstants.UPDATE_FAILURE
					+ " due to " + e.getMessage());
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

		List<Object[]> is = nonPoTemplateItemsRepository.selectNonPoTemplate();

		List<AllocationDto> res = new ArrayList<AllocationDto>();
		// Object obj1[]=is.get(0);
		// String templateId=(String)obj1[0];
		// String accNumber=(String)obj1[1];
		// String acNumber=accNumber;
		// for(int i=1;i<is.size();i++){
		// Object iObj1[]=is.get(i);
		// String iTemplateId=(String)iObj1[0];
		// String iAccNumber=(String)iObj1[1];
		//
		//
		// if(iTemplateId.equalsIgnoreCase(templateId)&&(!acNumber.equalsIgnoreCase("*"))){
		// if(!accNumber.equalsIgnoreCase(iAccNumber)){
		// acNumber="*";
		// }
		// }
		// else if(!iTemplateId.equalsIgnoreCase(templateId)){
		// String templateName = nonPoTemplateRepository.getById(templateId);
		// res.add(new AllocationDto(templateName,templateId,acNumber));
		// acNumber=iAccNumber;
		// accNumber=iAccNumber;
		// templateId=iTemplateId;
		// }
		// }
		// String templateName = nonPoTemplateRepository.getById(templateId);
		// res.add(new AllocationDto(templateName,templateId,acNumber));
		for (Object[] obj : is) {
			res.add(new AllocationDto(String.valueOf(obj[2]), String.valueOf(obj[0]), String.valueOf(obj[1])));
		}

		return res;

	}

	@Override
	public List<NonPoTemplateItemsDto> uploadExcel(File file) throws IOException {
		List<NonPoTemplateItemsDto> dto = new ArrayList<>();
		FileInputStream fis = new FileInputStream(file);
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet = wb.getSheetAt(0);
		int i = 0;

		for (Row row : sheet) {
			NonPoTemplateItemsDto newDto = new NonPoTemplateItemsDto();

			for (int cn = 0; cn < row.getLastCellNum(); cn++) {
				// If the cell is missing from the file, generate a blank one
				// (Works by specifying a MissingCellPolicy)
				Cell cell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				// Print the cell for debugging
				System.out.println("CELL: " + cn + " --> " + cell.toString());
				if (i != 0) {
					if (cn == 0) {
						newDto.setGlAccount(cell.toString());
					} else if (cn == 1) {
						switch (cell.getCellType())               
						{  
						case Cell.CELL_TYPE_STRING:    //field that represents string cell type  
							newDto.setAccountNo(String.valueOf(cell.getStringCellValue()));
						break;  
						case Cell.CELL_TYPE_NUMERIC:    //field that represents number cell type  
							System.out.println("Here   "+ cell.getNumericCellValue());
							newDto.setAccountNo(String.valueOf(Integer.valueOf((int) cell.getNumericCellValue())));
						break;  
						default:  
						}
						
					} else if (cn == 2) {
						newDto.setCostCenter((cell.toString()));
					} else if (cn == 3) {
						newDto.setMaterialDescription((cell.toString()));
					} else if (cn == 4) {
						newDto.setCrDbIndicator((cell.toString()));
					} else if (cn == 5) {
						newDto.setItemText((cell.toString()));
					} else if (cn == 6) {
						newDto.setProfitCenter((cell.toString()));
					} else if (cn == 7) {
						switch (cell.getCellType())               
						{  
						case Cell.CELL_TYPE_STRING:    //field that represents string cell type  
							newDto.setCompanyCode(String.valueOf(cell.getStringCellValue()));
						break;  
						case Cell.CELL_TYPE_NUMERIC:    //field that represents number cell type  
							System.out.println("Here   "+ cell.getNumericCellValue());
							newDto.setCompanyCode(String.valueOf(Integer.valueOf((int) cell.getNumericCellValue())));
						break;  
						default:  
						}
					} else if (cn == 8) {
						switch (cell.getCellType())               
						{  
						case Cell.CELL_TYPE_STRING:    //field that represents string cell type  
							newDto.setAllocationPercent(String.valueOf(cell.getStringCellValue()));
						break;  
						case Cell.CELL_TYPE_NUMERIC:    //field that represents number cell type  
							System.out.println("Here   "+ cell.getNumericCellValue());
							newDto.setAllocationPercent(String.valueOf(Integer.valueOf((int) cell.getNumericCellValue())));
						break;  
						default:  
						}
						
					}

				}

			}
			if (i != 0) {
				dto.add(newDto);
			}
			i = i + 1;
		}

		System.out.println(dto);

		return dto;
	}
	
	@Override
	public ResponseDto postNonPoItemsToSAP() throws IOException, URISyntaxException {
//		System.err.println("input "+inputDto.toString());
//		Map<String, Object> destinationInfo = getDestination("https://vhmeasd4ci.hec.menabev.com:44300/sap/bc/srt/xip/sap/journalentrycreaterequestconfi/100/journalcreateservice/journalcreatebinding");
		
//		System.err.println("destinationInfo "+destinationInfo);
		//set Url
		String url = "https://sd4.menabev.com:443"
				+ "/sap/bc/srt/xip/sap/journalentrycreaterequestconfi/100/journalcreateservice/journalcreatebinding";
		// form payload into a string entity 
		
		String  entity  = "POST /StockQuote HTTP/1.1"+
"Host:"+ "https://sd4.menabev.com:443"+
"Content-Type:" +"text/xml;"+ "charset=utf-8"+
"Content-Length: nnnn"
+ ""
+ "<soapenv:Envelopexmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"xmlns:sfin=\"http://sap.com/xi/SAPSCORE/SFIN\"><soapenv:Header/><soapenv:Body><sfin:JournalEntryBulkCreateRequest><MessageHeader><CreationDateTime>2021-05-25T12:37:00.1234567Z</CreationDateTime></MessageHeader><JournalEntryCreateRequest><MessageHeader><CreationDateTime>2021-05-25T12:37:00.1234567Z</CreationDateTime></MessageHeader><JournalEntry><OriginalReferenceDocumentType>BKPFF</OriginalReferenceDocumentType><OriginalReferenceDocument/><OriginalReferenceDocumentLogicalSystem/><BusinessTransactionType>RFBU</BusinessTransactionType><AccountingDocumentType>KR</AccountingDocumentType><DocumentReferenceID>INV12345</DocumentReferenceID><CreatedByUser>SYUVRAJ</CreatedByUser><CompanyCode>1010</CompanyCode><DocumentDate>2021-05-25</DocumentDate><PostingDate>2021-05-25</PostingDate><Item><GLAccount>0005500046</GLAccount><CompanyCode>1010</CompanyCode><AmountInTransactionCurrencycurrencyCode=\"SAR\">100.00</AmountInTransactionCurrency><Tax><TaxCode>I1</TaxCode></Tax><AccountAssignment><CostCenter>0000521001</CostCenter></AccountAssignment></Item><Item><GLAccount>0006021003</GLAccount><CompanyCode>1010</CompanyCode><AmountInTransactionCurrencycurrencyCode=\"SAR\">100.00</AmountInTransactionCurrency><Tax><TaxCode>I1</TaxCode></Tax><AccountAssignment><CostCenter>0000111001</CostCenter></AccountAssignment></Item><CreditorItem><ReferenceDocumentItem>1</ReferenceDocumentItem><Creditor>0001000030</Creditor><AmountInTransactionCurrencycurrencyCode=\"SAR\">-230.00</AmountInTransactionCurrency></CreditorItem><ProductTaxItem><TaxCode>I1</TaxCode><AmountInTransactionCurrencycurrencyCode=\"SAR\">30.00</AmountInTransactionCurrency><TaxBaseAmountInTransCrcycurrencyCode=\"SAR\">200.00</TaxBaseAmountInTransCrcy><ConditionType>MWVS</ConditionType></ProductTaxItem></JournalEntry></JournalEntryCreateRequest></sfin:JournalEntryBulkCreateRequest></soapenv:Body></soapenv:Envelope>";
		// call odata method 
//		stringToDom(entity);
		ResponseEntity<?> responseFromOdata = consumingOdataService(url, entity, "POST", null);
		System.err.println("odata output "+ responseFromOdata);
	 return null;

	}
//	public static void stringToDom(String xmlSource) 
//	        throws IOException {
//	    java.io.FileWriter fw = new java.io.FileWriter("C:/Users/Lakhu D/Desktop/my-file.xml");
//	    fw.write(xmlSource);
//	    fw.close();
//	}
//	
	public static String getJwtTokenForAuthenticationForSapApi() throws URISyntaxException, IOException {
		System.err.println("77 destination");
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost("https://menabevdev.authentication.eu20.hana.ondemand.com");
		httpPost.addHeader("Content-Type", "application/json");
		// Encoding username and password
		String auth = encodeUsernameAndPassword("sb-cloneb41bf10568ca4499840711bb8a0f2de4!b3189|connectivity!b5",
				"cf792fe9-32f6-496c-aeb6-aec065a33512$WhwgyCaocXG__utqLrg1NJjS3mRwCEGW9VxWDTTniK4=");
		httpPost.addHeader("Authorization", auth);
		HttpResponse res = client.execute(httpPost);
		System.err.println( " 92 rest" + res);
		String data = getDataFromStream(res.getEntity().getContent());
		if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
			String jwtToken = new JSONObject(data).getString("access_token");
			System.err.println("jwtProxyToken "+jwtToken);
				return jwtToken;
			}
		return null;
	}
	
	public static String encodeUsernameAndPassword(String username, String password) {
		String encodeUsernamePassword = username + ":" + password;
		String auth = "Basic " + DatatypeConverter.printBase64Binary(encodeUsernamePassword.getBytes());
		return auth;
	}
	
	public static String getDataFromStream(InputStream stream) throws IOException {
		StringBuilder dataBuffer = new StringBuilder();
		BufferedReader inStream = new BufferedReader(new InputStreamReader(stream));
		String data = "";

		while ((data = inStream.readLine()) != null) {
			dataBuffer.append(data);
		}
		inStream.close();
		return dataBuffer.toString();
	}
	
	public static Map<String, Object> getDestination(String destinationName) throws URISyntaxException, IOException {

		HttpClient client = HttpClientBuilder.create().build();

		HttpPost httpPost = new HttpPost("https://sd4.menabev.com:443/sap/bc/srt/xip/sap/journalentrycreaterequestconfi/100/journalcreateservice/journalcreatebinding");
		httpPost.addHeader("Content-Type", "application/json");

		// Encoding username and password
		String auth = encodeUsernameAndPassword("Syuvraj",
				"Incture@12345");
		httpPost.addHeader("Authorization", auth);

		HttpResponse res = client.execute(httpPost);
		System.out.println("RESPONSE :::::::::::::" + res);

		String data = getDataFromStream(res.getEntity().getContent());
		if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
			String jwtToken = new JSONObject(data).getString("access_token");

			HttpGet httpGet = new HttpGet("/sap/bc/srt/xip/sap/journalentrycreaterequestconfi/100/journalcreateservice/journalcreatebinding"
					+ "destination-configuration/v1/destinations/" + destinationName);

			httpGet.addHeader("Content-Type", "application/json");

			httpGet.addHeader("Authorization", "Bearer " + jwtToken);

			HttpResponse response = client.execute(httpGet);
			String dataFromStream = getDataFromStream(response.getEntity().getContent());
			if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {

				JSONObject json = new JSONObject(dataFromStream);
				return json.getJSONObject("destinationConfiguration").toMap();

			}
		}

		return null;
	}
	
	public static ResponseEntity<?> consumingOdataService(String url, String entity, String method,
			Map<String, Object> destinationInfo) throws IOException, URISyntaxException {


		System.err.println("com.incture.utils.HelperClass  + Inside consumingOdataService==================");
		
//		String proxyHost = "connectivityproxy.internal.cf.eu20.hana.ondemand.com";
		String proxyHost = "10.0.4.5";
		System.err.println("proxyHost-- " + "10.0.4.5");
		int proxyPort = 20003;
		Header[] jsonResponse = null;
		String objresult = null;
		
//		JSONObject jsonObj = new JSONObject(System.getenv("VCAP_SERVICES"));
		
//		System.err.println("116 - jsonObj =" + jsonObj);
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort),
			    new UsernamePasswordCredentials( "Syuvraj", "Incture@12345")); 
		
		HttpClientBuilder clientBuilder =  HttpClientBuilder.create();
		
		clientBuilder.setProxy(new HttpHost(proxyHost, proxyPort))
		   .setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy())
		   .setDefaultCredentialsProvider(credsProvider) 
		   .disableCookieManagement();
		
		HttpClient httpClient = clientBuilder.build();
		HttpRequestBase httpRequestBase = null;
		String  jsonMessage=null;
		HttpResponse httpResponse = null;
		StringEntity input = null;
		Header [] json = null;
		JSONObject obj = null;
		 String jwToken = getConectivityProxy();
		 System.out.println("741 jwt Token "+ jwToken);
		if (url != null) {
			if (method.equalsIgnoreCase("GET")) {
				httpRequestBase = new HttpGet(url);
			} else if (method.equalsIgnoreCase("POST")) {
				httpRequestBase = new HttpPost(url);
				try {
					
					System.err.println("entity "+entity);
					input = new StringEntity(entity);
					input.setContentType("text/xml");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				System.err.println("inputEntity "+ input);
//				 String body ="<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://example.com/v1.0/Records\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><SOAP-ENV:Body>"+soapEnvBody+"</SOAP-ENV:Body></SOAP-ENV:Envelope>";
				 StringEntity stringEntity = new StringEntity(entity, "UTF-8");
				 System.out.println(stringEntity);
				 stringEntity.setChunked(true);
				 ((HttpPost) httpRequestBase).setEntity(stringEntity);

			}
//			if (destinationInfo.get("sap-client") != null) {
				httpRequestBase.addHeader("sap-client", "100");
				httpRequestBase.addHeader("Content-Type","text/xml");
//			}
//			httpRequestBase.addHeader("accept", "application/xml");
			/*
			Header[] headers = getAccessToken("https://sd4.menabev.com:443/sap/bc/srt/xip/sap/journalentrycreaterequestconfi/100/journalcreateservice/journalcreatebinding","Syuvraj",
					"Incture@12345",httpClient,  proxyHost, proxyPort,
					"100",jwToken);*/
			String token = null;
			List<String> cookies = new ArrayList<>();
			/*if(headers.length != 0){
			
			for (Header header : headers) {

				if (header.getName().equalsIgnoreCase("x-csrf-token")) {
					token = header.getValue();
					System.err.println("token --- " + token);
				}

				if (header.getName().equalsIgnoreCase("set-cookie")) {
					cookies.add(header.getValue());
				}

			}
			}*/

//			if (destinationInfo.get("User") != null && destinationInfo.get("Password") != null) {
				String encoded = encodeUsernameAndPassword("Syuvraj",
						"Incture@12345");
				httpRequestBase.addHeader("Authorization", encoded);
				httpRequestBase.setHeader("Proxy-Authorization","Bearer " +jwToken);
				httpRequestBase.addHeader("SAP-Connectivity-SCC-Location_ID","incture");
				
//			}
			/*if (token != null) {
				//httpRequestBase.addHeader("X-CSRF-Token", token);
			}
			if (!cookies.isEmpty()) {
				for (String cookie : cookies) {
					String tmp = cookie.split(";", 2)[0];
					httpRequestBase.addHeader("Cookie", tmp);
				}
			}*/
//			if (tenantctx != null) {
//				httpRequestBase.addHeader("SAP-Connectivity-ConsumerAccount",
//						tenantctx.getTenant().getAccount().getId());
//			}
			try {
				
				
				System.err.println("this is requestBase ============" + Arrays.asList(httpRequestBase));
				httpResponse = httpClient.execute(httpRequestBase);
				System.err.println(
						"com.incture.utils.HelperClass ============" + Arrays.asList(httpResponse.getAllHeaders()));
				System.err.println(
						"com.incture.utils.HelperClass ============" + httpResponse);
				System.err.println("STEP 4 com.incture.utils.HelperClass ============StatusCode from odata hit="
						+ httpResponse.getStatusLine().getStatusCode());
				if (httpResponse.getStatusLine().getStatusCode() == 201) {
					json = httpResponse.getAllHeaders();
					for (Header header : json) {
						if (header.getName().equalsIgnoreCase("sap-message")) {
							String message  = header.getValue();
							System.err.println("message --- " + message);
							JSONObject jsonOutput = new JSONObject(message);
							  jsonMessage  = jsonOutput.getString("message");
							System.err.println("jsonMessage "+jsonMessage);
						}
					}
			
				} else {
					String responseFromECC = httpResponse.getEntity().toString();
					
					System.err.println("responseFromEcc"+responseFromECC);
					
					
					return new ResponseEntity<String>("Response from odata call "+httpResponse,HttpStatus.BAD_REQUEST);
				}

				System.err.println("STEP 5 Result from odata hit ============" + json);

			} catch (IOException e) {
				System.err.print("IOException : " + e);
				throw new IOException(
						"Please Check VPN Connection ......." + e.getMessage() + " on " + e.getStackTrace()[4]);
			}

			try {
				
				System.err.println("jsonOutput"+json);

				System.err.println("jsonHeaderResponse"+jsonResponse);
			
				
			} catch (JSONException e) {
				System.err.print("JSONException : check " + e + "JSON Object : " + json);
				
				throw new JSONException(
						"Exception occured during json conversion" + e.getMessage() + " on " + e.getStackTrace()[4]);
			}

		}

		return new ResponseEntity<String>(jsonMessage,HttpStatus.OK);

	}
	
	public static String getConectivityProxy() throws URISyntaxException, IOException {

		System.err.println("77 destination");
		HttpClient client = HttpClientBuilder.create().build();

		HttpPost httpPost = new HttpPost("https://menabevdev.authentication.eu20.hana.ondemand.com/oauth/token?grant_type=client_credentials");
		httpPost.addHeader("Content-Type", "application/json");

		// Encoding username and password
		String auth = encodeUsernameAndPassword("sb-cloneb41bf10568ca4499840711bb8a0f2de4!b3189|connectivity!b5",
				"cf792fe9-32f6-496c-aeb6-aec065a33512$WhwgyCaocXG__utqLrg1NJjS3mRwCEGW9VxWDTTniK4=");
		
		
		httpPost.addHeader("Authorization", auth);

		HttpResponse res = client.execute(httpPost);
		
		System.err.println( " 92 rest" + res);

		String data = getDataFromStream(res.getEntity().getContent());
		if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
			String jwtToken = new JSONObject(data).getString("access_token");
			
			System.err.println("jwtProxyToken "+jwtToken);

			
				return jwtToken;

			}
		
		

		return null;
	}
	
	
	private static Header[] getAccessToken(String url, String username, String password, HttpClient client,
			String proxyHost, int proxyPort, String sapClient,String token)
			throws ClientProtocolException, IOException {

		
  
		HttpGet httpGet = new HttpGet(url);
		
       String userpass = username + ":" + password;
       
       httpGet.setHeader("Proxy-Authorization","Bearer " +token);
        httpGet.setHeader(HttpHeaders.AUTHORIZATION,
                                        "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes()));
		// Encoding username and password
		httpGet.addHeader("X-CSRF-Token", "Fetch");
		httpGet.addHeader("Content-Type", "application/json");
		httpGet.addHeader("sap-client", sapClient);
		httpGet.addHeader("SAP-Connectivity-SCC-Location_ID","incture");


		HttpResponse response = client.execute(httpGet);
		//HttpResponse response = client.execute( httpGet);
		
		System.err.println("313 response "+ response);

		// HttpResponse response = client.execute(httpGet);

		return response.getAllHeaders();

	}
//	public static void main(String[] args) throws IOException, URISyntaxException {
//		NonPoTemplateServiceImpl obj = new NonPoTemplateServiceImpl();
//		File file = new File("C:\\Users\\Lakhu D\\Downloads\\exampleXml.xlsx");
//		obj.uploadExcel(file);
//	}
	

	}