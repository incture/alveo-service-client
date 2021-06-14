package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChangeIndicator {
	private Boolean isHeaderChanged;
	private Boolean isVendoIdChanged;
	private Boolean isCompanyCodeChanged;
	private Boolean isInvoiceRefChanged;
	private Boolean isCurrencyChanged;
	private Boolean isInvoiceDateChanged;
	private Boolean isBaselineDateChanged;
	private Boolean isPaymentTermsChanged;
	private Boolean isInvoiceTypeChanged;
	private Boolean isTaxCodeChanged;
	private Boolean isTaxAmountChanged;
	private Boolean isInvoiceAmountChanged;
}
