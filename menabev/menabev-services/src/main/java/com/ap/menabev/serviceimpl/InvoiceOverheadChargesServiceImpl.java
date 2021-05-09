package com.ap.menabev.serviceimpl;


import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.InvoiceOverheadChargesDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.service.InvoiceOverheadChargesService;
import com.ap.menabev.util.ApplicationConstants;

@Service
public class InvoiceOverheadChargesServiceImpl implements InvoiceOverheadChargesService
{
	private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

	@Override
	public ResponseDto save(InvoiceOverheadChargesDto dto) {
		// TODO Auto-generated method stub
		return null;
	}
	


	
}
