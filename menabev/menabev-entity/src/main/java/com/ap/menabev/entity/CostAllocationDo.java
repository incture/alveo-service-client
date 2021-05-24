package com.ap.menabev.entity;



import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;



import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "COST_ALLOCATION")
public @Data class CostAllocationDo {

	@Id
	/*@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CostDistribution")
	@GenericGenerator(name = "CostDistribution", strategy = "com.incture.ap.sequences.CostDistributionSequenceGenerator", parameters = {
			@Parameter(name = InvoiceHeaderSequenceGenerator.INCREMENT_PARAM, value = "1"),			@Parameter(name = InvoiceHeaderSequenceGenerator.VALUE_PREFIX_PARAMETER, value = "CD-"),
//			@Parameter(name = InvoiceHeaderSequenceGenerator.NUMBER_FORMAT_PARAMETER, value = "%06d"),
//			@Parameter(name = InvoiceHeaderSequenceGenerator.SEQUENCE_PARAM, value = "Cost_Distribution_SEQ") })
*/	@Column(name = "COST_ALLOCATION_ID")
	private String costAllocationId = UUID.randomUUID().toString();
	
	@Column(name = "REQUEST_ID", nullable = false)
    private String requestId; // (FK)
	
	@Column(name = "ITEM_ID", nullable = false)
	private Integer itemId; // (FK)
	
	@Column(name = "SERIAL_NO")
	private Integer serialNo;
	
	@Column(name = "DELETE_IND")
	private Boolean deleteInd;
	
	@Column(name = "QUANTITY")
	private String quantity;
	
	@Column(name = "DISTR_PERC")
	private String distrPerc;
	
	@Column(name = "NET_VALUE")
	private String netValue;
	
	@Column(name = "GL_ACCOUNT")
	private String glAccount;
	
	@Column(name = "COST_CENTER")
	private String costCenter;
	
	@Column(name = "ASSET_NO")
	private String assetNo;
	
	@Column(name = "SUB_NUMBER")
	private String subNumber;
	
	@Column(name = "INTERNAL_ORDER_ID")
	private String internalOrderId;
	
	@Column(name = "PROFIT_CENTER")
	private String profitCenter;
	
	@Column(name = "WBS_ELEMENT")
	private String wbsElement;
	
	@Column(name = "CR_DB_INDICATOR")
	private String crDbIndicator;
	
	@Column(name="ITEM_TEXT")
	private String itemText;
	
	@Column(name = "TAX_C0DE")
	private String taxCode;
}
