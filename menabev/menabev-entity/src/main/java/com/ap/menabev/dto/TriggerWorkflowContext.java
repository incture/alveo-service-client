package com.ap.menabev.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TriggerWorkflowContext implements WorkflowContextDto{
	
	private String requestId;
	private boolean manualNonPo;
	private boolean isNonPo;
	private String invoiceReferenceNumber;
	private String accountantUser;
	private String accountantGroup;
	private String processLead;
	private String processLeadGroup;
	
	
	@Override
	public String toRuleInputString(String definitionId) {
		
		return  "{\"definitionId\":\"triggerresolutionworkflow.triggerresolutionworkflow\",\"context\":{\"requestId\":\"" + this.getRequestId()
				+ "\",\"manualNonPo\":\"" + this.isManualNonPo() + "\",\"isNonPo\":\"" + this.isNonPo()
				+ "\",\"invoiceReferenceNumber\":\"" + this.getInvoiceReferenceNumber() + "\",\"accountantUser\":\"" + this.getAccountantUser() + "\",\"accountantGroup\":\""
				+ this.getAccountantGroup() + "\",\"processLead\":\"" + this.getProcessLead() + "\",\"processLeadGroup\":\"" + this.getProcessLeadGroup()
				+ "\"}}";
	}
	

}
