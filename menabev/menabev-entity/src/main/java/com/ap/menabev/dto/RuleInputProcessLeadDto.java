package com.ap.menabev.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RuleInputProcessLeadDto implements RuleInputDto {
	private String vendorId;
	private String compCode;
	private String processLeadCheck;
	
	@Override
	public String toRuleInputString(String rulesServiceId) {
		return "{ \"RuleServiceId\" : \"" + rulesServiceId + "\", \"Vocabulary\" : [ {"
				+ "\"ReturnOrderRuleInputDto\" : { \"vendorId\":" + "\"" + this.getVendorId() + "\""
				+ ",\"compCode\":" + "\"" + this.getCompCode() + "\"" + ",\"processLeadCheck\":" + "\""
				+ this.getProcessLeadCheck() + "\"" + "} } ] }";

	}

}
