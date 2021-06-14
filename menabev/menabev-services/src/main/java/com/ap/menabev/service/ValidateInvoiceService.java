package com.ap.menabev.service;

import com.ap.menabev.dto.InvoiceHeaderCheckDto;
import com.ap.menabev.dto.VendorCheckDto;

public interface ValidateInvoiceService {

	InvoiceHeaderCheckDto invoiceHeaderCheck(InvoiceHeaderCheckDto invoiceHeaderCheckDto);
	VendorCheckDto vendorCheck(VendorCheckDto vendorCheckDto);
	
}
