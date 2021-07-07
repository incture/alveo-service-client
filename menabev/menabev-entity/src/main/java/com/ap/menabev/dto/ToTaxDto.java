package com.ap.menabev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ToTaxDto {
	@JsonProperty("InvoiceReferenceNumber")
	private String invoiceReferenceNumber;
	@JsonProperty("TaxCode")
	private String taxCode;
	@JsonProperty("TaxAmount")
	private String taxAmount;
	@JsonProperty("TaxBaseAmount")
	private String taxBaseAmount;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((taxCode == null) ? 0 : taxCode.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ToTaxDto other = (ToTaxDto) obj;
		if (taxCode == null) {
			if (other.taxCode != null)
				return false;
		} else if (!taxCode.equals(other.taxCode))
			return false;
		return true;
	}
	

}
