package com.ap.menabev.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TriggerWorkflowContext implements WorkflowContextDto{
	
	private String requestId;
	private boolean manualNonPo;
	private boolean isNonPo;
	private String accountantUser;
	private String accountantGroup;
	private String processLead;
	private String processLeadGroup;
	private String accountantAction;
	private String processLeadAction;
	private String remediationUser;
	private String remediationUserAction;
	private String invoiceType;
	private String invoiceStatus;
	private String invoiceStatusText;
	private String extInvNumb;
	
	
	
	@Override
	public String toRuleInputString(String definitionId) {
		
		return  "{\"definitionId\":\"triggerresolutionworkflow.triggerresolutionworkflow\",\"context\":{\"requestId\":\"" + this.getRequestId()
				+ "\",\"manualNonPo\":\"" + this.isManualNonPo() + "\",\"isNonPo\":\"" + this.isNonPo()
				+ "\",\"accountantUser\":\"" + this.getAccountantUser() + "\",\"accountantGroup\":\""
				+ this.getAccountantGroup() + "\",\"processLead\":\"" + this.getProcessLead() + "\",\"processLeadGroup\":\"" + this.getProcessLeadGroup()
				+ "\",\"accountantAction\":\"" + this.getAccountantAction()
				+ "\",\"processLeadAction\":\"" + this.getProcessLeadAction()
				+ "\",\"remediationUser\":\"" + this.getRemediationUser()
				+ "\",\"remediationUserAction\":\"" + this.getRemediationUserAction()
				+ "\",\"invoiceType\":\"" + this.getInvoiceType()
				+ "\",\"invoiceStatus\":\"" + this.getInvoiceStatus()
				+ "\",\"invoiceStatusText\":\"" + this.getInvoiceStatusText()
				+ "\",\"extInvNumb\":\"" + this.getExtInvNumb()
				+ "\"}}";
	}
	

}
