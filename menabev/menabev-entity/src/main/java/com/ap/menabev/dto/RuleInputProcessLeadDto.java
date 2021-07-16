package com.ap.menabev.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RuleInputProcessLeadDto implements RuleInputDto {
	private String vendorId;
	private String compCode;
	
	@Override
	public String toRuleInputString(String rulesServiceId) {
		return "{ \"RuleServiceId\" : \"" + rulesServiceId + "\", \"Vocabulary\" : [ {"
				+ "\"AcctOrProcessLeadDeterminationNode\" : { \"VendorId\":" + "\"" + this.getVendorId() + "\""
				+ ",\"CompanyCode\":" + "\"" + this.getCompCode() + "\"" + "} } ] }";

	}

}
