package com.ap.menabev.serviceimpl;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.HeaderMessageDto;
import com.ap.menabev.dto.InvoiceHeaderObjectInputDto;
import com.ap.menabev.dto.InvoiceHeaderObjectOutputDto;
import com.ap.menabev.service.DuplicateCheckService;
import com.ap.menabev.util.ServiceUtil;

@Service
public class DuplicatecheckServiceImpl implements DuplicateCheckService {

	@Autowired
    EntityManager entityManager;
	
	@Override
	public InvoiceHeaderObjectOutputDto duplicateCheck(InvoiceHeaderObjectInputDto dto) {
		ModelMapper mapper = new ModelMapper();
		InvoiceHeaderObjectOutputDto returnDto = new InvoiceHeaderObjectOutputDto();
		String requestId = "";
		String baseQuery = "Select requestId from InvoiceHeaderDo";
		String query = createStringForQuery(dto);
		String finalQuery = baseQuery;
		System.out.println(query);
		if(!ServiceUtil.isEmpty(query)){
			 int index= query.lastIndexOf(" ");
		     String sb = query.substring(0, index-3);  
			finalQuery = finalQuery + " where " + sb;
		}
		System.out.println("Final Query ::::: "+finalQuery);
		
		Query queryForOutput = entityManager.createQuery(finalQuery);
		 List<String> dos = queryForOutput.getResultList();
	        if(ServiceUtil.isEmpty(dos)){
	        	returnDto = mapper.map(dto, InvoiceHeaderObjectOutputDto.class);
	        	returnDto.setIsDuplicate(false);
	        }else{
	        	for(String itr : dos){
	        		System.out.println("Here Java 49");
	        		requestId = requestId + itr + ",";
	        		
		        	
	        	}
	        	StringBuilder sb  = new StringBuilder(requestId);
	        	sb.deleteCharAt(requestId.length()-1);  
	        	HeaderMessageDto message  = new HeaderMessageDto();
	        	returnDto = mapper.map(dto, InvoiceHeaderObjectOutputDto.class);
	        	returnDto.setIsDuplicate(true);
	        	message.setMessageId(20);
	        	message.setMsgClass("DuplicateCheck");
	        	message.setMessageNumber(sb.toString());
	        	message.setMessageText("Duplicate invoices found -<"+sb+">");
	        	message.setMessageType("E");
	        	returnDto.setMessages(message);
	        	
	        }
		
		return returnDto;
	}
	
	private String createStringForQuery(InvoiceHeaderObjectInputDto dto) {
		String query = "" ;
		if(!ServiceUtil.isEmpty(dto.getRequestId())){
			query = query + "requestId like '"+dto.getRequestId()+"' and ";
		}
		if(!ServiceUtil.isEmpty(dto.getVendorId())){
			query = query + "vendorId like '"+dto.getVendorId()+"' and ";
		}
		if(!ServiceUtil.isEmpty(dto.getInvoiceStatus())){
			query = query + "lifecycleStatus like '"+dto.getInvoiceStatus()+"' and ";
		}
		if(!ServiceUtil.isEmpty(dto.getInvoiceReference())){
			query = query + "extInvNum like '"+dto.getInvoiceReference()+"' and ";
		}
		if(!ServiceUtil.isEmpty(dto.getInvoiceDate())){
			query = query + "invoiceDate like '"+dto.getInvoiceDate()+"' and ";
		}
		if(!ServiceUtil.isEmpty(dto.getInvoiceAmount())){
			query = query + "grossAmount like '"+dto.getInvoiceAmount()+"' and ";
		}
		if(!ServiceUtil.isEmpty(dto.getCompanyCode())){
			query = query + "compCode like '"+dto.getCompanyCode()+"' and ";
		}
		return query;
	}

}
