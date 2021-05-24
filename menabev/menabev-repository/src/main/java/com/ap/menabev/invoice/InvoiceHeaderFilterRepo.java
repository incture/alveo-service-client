package com.ap.menabev.invoice;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.InvoiceHeaderDo;

@Repository
public interface InvoiceHeaderFilterRepo {
	
	
	 List<InvoiceHeaderDo> getFilterDetails(String query);

}
