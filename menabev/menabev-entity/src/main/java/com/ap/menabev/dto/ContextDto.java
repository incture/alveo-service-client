package com.ap.menabev.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContextDto implements WorkflowContextDto{
	
	
	 private String invoiceStatusText;
	 private String remediationUser;
	 private String remediationUserType;
	 private String remediationUserAction;
	 private String processLead;
	 private String processLeadGroup;
	 private String processLeadAction;
	 private boolean isNonPo;
	 private boolean manualNonPo;
	 private String requestId;
	 private String accountantAction;
	 private String invoiceType;
	 private String invoice_ref_number;
	 private String invoiceStatus;
	 private String refPurchDocNum;
	 private String vendorId;
	
	@Override
	public String toRuleInputString(String definitionId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public String toUpdateTaskString() {
		
		return  "{\"context\":{\"requestId\":\"" + this.getRequestId()
		+ "\",\"processLead\":\"" + this.getProcessLead() 
		+ "\",\"processLeadGroup\":\"" + this.getProcessLeadGroup()
		+ "\",\"accountantAction\":\"" + this.getAccountantAction()
		+ "\",\"processLeadAction\":\"" + this.getProcessLeadAction()
		+ "\",\"remediationUser\":\"" + this.getRemediationUser()
		+ "\",\"remediationUserAction\":\"" + this.getRemediationUserAction()
		+ "\",\"invoiceType\":\"" + this.getInvoiceType()
		+ "\",\"invoiceStatus\":\"" + this.getInvoiceStatus()
		+ "\",\"invoiceStatusText\":\"" + this.getInvoiceStatusText()
		+ "\",\"invoice_ref_number\":\"" + this.getInvoice_ref_number()
		+ "\",\"refPurchDocNum\":\"" + this.getRefPurchDocNum()
		+ "\",\"vendorId\":\"" + this.getVendorId()
		+ "\",\"remediationUserType\":\"" + this.getRemediationUserType()
		+ "\"},\"status\":\"COMPLETED\",\"processor\":\"\",\"priority\": \"MEDIUM\"}";
	}
	 
	 
	

}
