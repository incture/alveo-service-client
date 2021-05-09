package com.ap.menabev.serviceimpl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ap.menabev.dto.AttachmentDto;
import com.ap.menabev.dto.AttamentMasterDto;
import com.ap.menabev.dto.DashBoardDetailsDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.entity.AttachmentDo;
import com.ap.menabev.invoice.AttachmentRepository;
import com.ap.menabev.service.AttachmentService;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;
//import com.incture.ap.util.CreatePdf;

@Service
public class AttachmentServiceImpl implements AttachmentService {

	private static final Logger logger = LoggerFactory.getLogger(AttachmentServiceImpl.class);

	@Autowired
	AttachmentRepository attachmentRepository;

	@PersistenceContext
	private EntityManager entityManager;

	
	
	
	@Override
	public AttamentMasterDto saveOrUpdateAllAttachments(List<AttachmentDto> dtoList, String type) {
		AttamentMasterDto attamentMasterDto = new AttamentMasterDto();
		ResponseDto response = new ResponseDto();
		List<AttachmentDo> doList = new ArrayList<>();
		List<AttachmentDto> attamentDtos = new ArrayList<>();
		ModelMapper mapper = new ModelMapper();
		try {

			for (AttachmentDto dto : dtoList) {
				AttachmentDo attachedDo = new AttachmentDo();

				logger.error("Attachment id = " + dto.getAttachmentId());
				attachedDo = entityManager.find(AttachmentDo.class, dto.getAttachmentId());
				if (ServiceUtil.isEmpty(attachedDo)) {
					dto.setCreatedAt(ServiceUtil.getEpocTime());
					
					attachedDo = attachmentRepository.save(mapper.map(dto, AttachmentDo.class));
				} else {
					attachedDo = updateAttachmentDo(attachedDo, dto);
					
					attachedDo = attachmentRepository.save(attachedDo);
				}

				doList.add(attachedDo);
			}

			for (AttachmentDo attachmentDo : doList) {
				attamentDtos.add(mapper.map(attachmentDo, AttachmentDto.class));
			}

			if (!ServiceUtil.isEmpty(attamentDtos)) {
				response.setCode(ApplicationConstants.CODE_SUCCESS);
				response.setStatus(ApplicationConstants.SUCCESS);
				response.setMessage(ApplicationConstants.CREATED_SUCCESS);
				attamentMasterDto.setAttachmentList(attamentDtos);
			}
		} catch (Exception e) {
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.CREATE_FAILURE);
			logger.error(
					"[ApAutomation][AttachmentServiceImpl][saveOrUpdateAllAttachments][Exception] = " + e.getMessage());
			e.printStackTrace();

		}

		attamentMasterDto.setResponse(response);
		return attamentMasterDto;
	}

	public AttachmentDo updateAttachmentDo(AttachmentDo attachedDo, AttachmentDto dto) {

		try {

			
				attachedDo.setFileBase64(dto.getFileBase64());

			
				attachedDo.setFileName(dto.getFileName());

			
				attachedDo.setFileType(dto.getFileType());

			
				attachedDo.setUpdatedBy(dto.getUpdatedBy());

			attachedDo.setUpdatedAt(ServiceUtil.getEpocTime());

		} catch (Exception e) {
			logger.error("[ApAutomation][AttachmentServiceImpl][updateAttachmentDo][Exception] = " + e.getMessage());
			e.printStackTrace();
		}

		return attachedDo;
	}

	@Override
	public AttamentMasterDto getAllAttachments() {
		AttamentMasterDto attamentMasterDto = new AttamentMasterDto();
		List<AttachmentDto> list = new ArrayList<>();
		ResponseDto response = new ResponseDto();
		ModelMapper modelMapper = new ModelMapper();
		try {
			List<AttachmentDo> doList = attachmentRepository.findAll();
			for (AttachmentDo AttachmentDo : doList) {
				list.add(modelMapper.map(AttachmentDo, AttachmentDto.class));
			}

			if (!ServiceUtil.isEmpty(list)) {
				response.setCode(ApplicationConstants.CODE_SUCCESS);
				response.setStatus(ApplicationConstants.SUCCESS);
				response.setMessage(ApplicationConstants.FETCHED_SUCCESS);
				attamentMasterDto.setResponse(response);
				attamentMasterDto.setAttachmentList(list);
			}
		} catch (Exception e) {
			logger.error("[ApAutomation][AttachmentServiceImpl][getAllAttachments][Exception] = " + e.getMessage());
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.FETCHED_FAILURE);
			attamentMasterDto.setResponse(response);
		}

		return attamentMasterDto;
	}

	@Override
	public ResponseDto deleteAttachment(String attachmentId) {
		ResponseDto response = new ResponseDto();
		try {
			logger.error("attachmentId =" + attachmentId);
			int deleteCount = attachmentRepository.deleteByAttachmentId(attachmentId);
			logger.error("deleteCount =" + deleteCount);
			if (deleteCount > 0) {
				response.setCode(ApplicationConstants.CODE_SUCCESS);
				response.setStatus(ApplicationConstants.SUCCESS);
				response.setMessage(ApplicationConstants.DELETE_SUCCESS);
			} else {
				response.setCode(ApplicationConstants.CODE_FAILURE);
				response.setStatus(ApplicationConstants.FAILURE);
				response.setMessage("No record Found for given id");
			}
		} catch (Exception e) {
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.DELETE_FAILURE);
			logger.error("[ApAutomation][AttachmentServiceImpl][deleteAttachment][Exception] = " + e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public AttamentMasterDto getAllAttachmentsByReqId(String requestId) {
		AttamentMasterDto dto = new AttamentMasterDto();
		ResponseDto response = new ResponseDto();
		response.setCode(ApplicationConstants.CODE_SUCCESS);
		response.setMessage(ApplicationConstants.FETCHED_SUCCESS);
		response.setStatus(ApplicationConstants.SUCCESS);
		ModelMapper mapper = new ModelMapper();
		List<AttachmentDto> attachmenDtotList = new ArrayList<>();
		try {
			List<AttachmentDo> attachmentList = attachmentRepository.getAllAttachmentsForRequestId(requestId);
			for (AttachmentDo attachmentDo : attachmentList) {
				AttachmentDto attachmentDto = mapper.map(attachmentDo, AttachmentDto.class);
				attachmenDtotList.add(attachmentDto);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setMessage(ApplicationConstants.FETCHED_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
		}
		dto.setAttachmentList(attachmenDtotList);
		dto.setResponse(response);
		return dto;
	}
	@Override
	public ResponseEntity<?> downloadPdf(String requestId) {
		return null;
	}
	
//	public void createPdf(){
//	     final String uri = "https://ap-automation.cfapps.eu10.hana.ondemand.com/invoiceHeader?requestId=APA-000291";
//	     RestTemplate restTemplate = new RestTemplate();
//	     DashBoardDetailsDto result = restTemplate.getForObject(uri, DashBoardDetailsDto.class);      
//	     System.out.println(result.getBillTo()); 
//	     
//		
//	}
	
//	public static void main(String[] args) {
//		AttachmentServiceImpl obj = new AttachmentServiceImpl();
//		obj.createPdf();
//	}
	
	


}
