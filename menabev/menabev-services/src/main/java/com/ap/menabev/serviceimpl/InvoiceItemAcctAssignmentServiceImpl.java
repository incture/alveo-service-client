package com.ap.menabev.serviceimpl;



import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ap.menabev.dto.InvoiceItemAcctAssignmentDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.entity.InvoiceItemAcctAssignmentDo;
import com.ap.menabev.invoice.InvoiceItemAcctAssignmentRepository;
import com.ap.menabev.service.InvoiceItemAcctAssignmentService;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;


@Service
public class InvoiceItemAcctAssignmentServiceImpl implements InvoiceItemAcctAssignmentService {

	@Autowired
	InvoiceItemAcctAssignmentRepository invoiceItemAcctAssignmentRepository;

	@PersistenceContext
	private EntityManager entityManager;

	private static final Logger logger = LoggerFactory.getLogger(InvoiceItemAcctAssignmentServiceImpl.class);

	@Override
	public ResponseDto saveInvoiceItemAcctAssignment(List<InvoiceItemAcctAssignmentDto> dtoList) {
		ResponseDto response = new ResponseDto();
		ModelMapper mapper = new ModelMapper();

		try {

			/*for (InvoiceItemAcctAssignmentDto dto : dtoList) {
				InvoiceItemAcctAssignmentDo aDo = entityManager.find(InvoiceItemAcctAssignmentDo.class,
						dto.getAccountAssgnGuid());
				if (ServiceUtil.isEmpty(aDo)) {
					logger.error("In empty id block");
					System.err.println("IN empty Id block");
					dto.setSerialNo(getSerialNo(dto.getRequestId(), dto.getItemId()));
					System.err.println("SerialNo " + dto.getSerialNo());
					dto.setIsDeleted(Boolean.FALSE);
					if (dto.getCreatedOn() == null)
						dto.setCreatedOn(System.currentTimeMillis());
					dto.setQuantity(String.format("%.3f", stringToDouble(dto.getQuantity())));
					dto.setNetValue(String.format("%.3f", stringToDouble(dto.getNetValue())));
					invoiceItemAcctAssignmentRepository.save(mapper.map(dto, InvoiceItemAcctAssignmentDo.class));

				} else {
					dto.setQuantity(String.format("%.3f", stringToDouble(dto.getQuantity())));
					dto.setNetValue(String.format("%.3f", stringToDouble(dto.getNetValue())));
					invoiceItemAcctAssignmentRepository.save(mapper.map(dto, InvoiceItemAcctAssignmentDo.class));

				}

			}
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage("Account Assigned successfully for " + dtoList.get(0).getRequestId());
*/
		} catch (Exception e) {
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage("Failed to assign account  for " + dtoList.get(0).getRequestId());

		}
		return response;

	}

	@Override
	public ResponseDto save(InvoiceItemAcctAssignmentDto dto) {
		ResponseDto response = new ResponseDto();
		ModelMapper mapper = new ModelMapper();
		try {
			invoiceItemAcctAssignmentRepository.save(mapper.map(dto, InvoiceItemAcctAssignmentDo.class));
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

	private static Double stringToDouble(String in) {
		if (ServiceUtil.isEmpty(in)) {
			in = "0";
		}

		if (in.matches("-?\\d+(\\.\\d+)?"))
			return new Double(in);
		else {
			return Double.valueOf(0.0);
		}
	}
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String getSerialNo(String requestId, String itemId) {
		// TODO Auto-generated method stub
		String serialNo = "";
		try {
			serialNo = invoiceItemAcctAssignmentRepository.getSerialNo(requestId, itemId);

			if (ServiceUtil.isEmpty(serialNo)) {
				return "0001";
			}
			logger.error("[ApAutomation][InvoiceItemAcctAssignmentServiceImpl][getSerialNo][serialNo] = " + serialNo);
			System.err.println("serialNo " + serialNo);
			return String.format("%04d", Integer.parseInt(serialNo) + 1);
		} catch (Exception e) {
			logger.error(
					"[ApAutomation][InvoiceItemAcctAssignmentServiceImpl][getSerialNo][Exception] = " + e.getMessage());
		}
		return serialNo;
	}

	@Override
	public List<InvoiceItemAcctAssignmentDto> get() {
		List<InvoiceItemAcctAssignmentDto> list = new ArrayList<>();

		ModelMapper modelMapper = new ModelMapper();
		try {
			List<InvoiceItemAcctAssignmentDo> doList = invoiceItemAcctAssignmentRepository.findAll();
			for (InvoiceItemAcctAssignmentDo InvoiceItemAcctAssignmentDo : doList) {
				list.add(modelMapper.map(InvoiceItemAcctAssignmentDo, InvoiceItemAcctAssignmentDto.class));
			}
		} catch (Exception e) {
			System.err.println("Exception :" + e.getMessage());
		}
		return list;
	}

	@Override
	public List<InvoiceItemAcctAssignmentDto> get(String requestId, String itemId) {
		List<InvoiceItemAcctAssignmentDto> list = new ArrayList<>();

		ModelMapper modelMapper = new ModelMapper();
		try {
			List<InvoiceItemAcctAssignmentDo> doList = invoiceItemAcctAssignmentRepository.get(requestId, itemId);
			for (InvoiceItemAcctAssignmentDo InvoiceItemAcctAssignmentDo : doList) {
				list.add(modelMapper.map(InvoiceItemAcctAssignmentDo, InvoiceItemAcctAssignmentDto.class));
			}
		} catch (Exception e) {
			System.err.println("Exception :" + e.getMessage());
		}
		return list;
	}

}
