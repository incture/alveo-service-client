package com.ap.menabev.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.CommentDto;
import com.ap.menabev.dto.CommentsMasterResponseDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.entity.CommentDo;
import com.ap.menabev.invoice.CommentRepository;
import com.ap.menabev.service.CommentService;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;


@Service
public class CommentServiceImpl implements CommentService {

	private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);
	
	@Autowired
	CommentRepository commentRepository;

	@Override
	public ResponseDto saveComment(CommentDto dto) {
		ResponseDto response = new ResponseDto();
		try {
			ModelMapper mapper = new ModelMapper();
			commentRepository.save(mapper.map(dto, CommentDo.class));
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
	

	@Override
	public List<CommentDto> get() {
		List<CommentDto> list = new ArrayList<>();

		ModelMapper modelMapper = new ModelMapper();
		try {
			List<CommentDo> doList = commentRepository.findAll();
			for (CommentDo CommentDo : doList) {
				list.add(modelMapper.map(CommentDo, CommentDto.class));
			}
		} catch (Exception e) {
			System.err.println("Exception :" + e.getMessage());
		}
		return list;
	}

	@Override
	public ResponseDto delete(String id) {
		ResponseDto response = new ResponseDto();
		try {
			logger.error("attachmentId =" + id);
			int deleteCount = commentRepository.deleteByCommentId(id);
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
			logger.error("[ApAutomation][CommentServiceImpl][delete][Exception] = " + e.getMessage());
			e.printStackTrace();
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.DELETE_FAILURE);
		}
		return response;
	}


	@Override
	public CommentsMasterResponseDto getCommentsByRequestIdAndUser(String requestId) {
		CommentsMasterResponseDto dto = new CommentsMasterResponseDto();
		ResponseDto response = new ResponseDto();
		ModelMapper mapper = new ModelMapper();
		List<CommentDto> commentDtotList = new ArrayList<>();
		try {
			List<CommentDo> commentDoList = commentRepository.getAllCommentsForRequestId(requestId);
			for (CommentDo commentDo : commentDoList) {
				CommentDto commentDto = mapper.map(commentDo, CommentDto.class);
				commentDtotList.add(commentDto);
			}
			
			if (!ServiceUtil.isEmpty(commentDtotList)) {
				response.setCode(ApplicationConstants.CODE_SUCCESS);
				response.setStatus(ApplicationConstants.SUCCESS);
				response.setMessage(ApplicationConstants.FETCHED_SUCCESS);
				dto.setCommentDtos(commentDtotList);
			} else {
				response.setCode(ApplicationConstants.CODE_FAILURE);
				response.setStatus(ApplicationConstants.FAILURE);
				response.setMessage("No record Found for given RequestId and user");
			}
		} catch (Exception e) {
			logger.error("[ApAutomation][CommentServiceImpl][getCommentsByRequestIdAndUser][Exception] = " + e.getMessage());
			e.printStackTrace();
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setMessage(ApplicationConstants.FETCHED_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
		}
		dto.setResponse(response);
		return dto;
	}


	public CommentDo updateCommentDo(CommentDo commentDo, CommentDto dto) {
		try {

			
				commentDo.setComment(dto.getComment());

			
				commentDo.setUpdatedBy(dto.getUpdatedBy());

			commentDo.setUpdatedAt(ServiceUtil.getEpocTime());

		} catch (Exception e) {
			logger.error("[ApAutomation][CommentServiceImpl][updateCommentDo][Exception] = " + e.getMessage());
			e.printStackTrace();
		}

		return commentDo;
	}



}
