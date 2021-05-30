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
	@Column(name = "COST_ALLOCATION_ID")
	private String costAllocationId;
	
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
	
	@Column(name = "QUANTITY_UNIT")
	private String quantityUnit;
	@Column(name = "DISTR_PERC")
	private String distrPerc;
	
	@Column(name = "ORDER-ID")
	private String orderId;
	
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
	
	@Column(name = "ACCOUNT_NUM")
	private String accountNum;
	
	@Column(name = "LINE_TEXT")
	private String lineText;
	
	@Column(name ="TAX_VALUE")
	private String taxValue;
	@Column(name ="TAX_PER")
	private String taxPer;
	@Column(name ="TAX_RATE")
	private String taxRate;
	
}
