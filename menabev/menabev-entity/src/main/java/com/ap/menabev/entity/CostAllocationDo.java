package com.ap.menabev.entity;



import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "COST_ALLOCATION")
@IdClass(CostAllocationPkDo.class)
public @Data class CostAllocationDo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "COST_ALLOCATION_ID")
	private String costAllocationId;
	@Id
	@Column(name = "REQUEST_ID", nullable = false)
    private String requestId; 
	@Id
	@Column(name = "ITEM_ID", nullable = false)
	private String itemId; 
	@Column(name = "ITEM_TEXT",length = 50)
	private String itemText;
	@Column(name = "SUB_NUM" ,length = 10)
	private String subNum;
	@Column(name = "DELETE_IND")
	private Boolean deleteInd;
	@Column(name = "QUANTITY")
	private Double quantity;
	@Column(name = "QUANTITY_UNIT", length = 5)
	private String quantityUnit;
	@Column(name = "DISTR_PERC")
	private Double distrPerc;
	@Column(name = "ORDER_ID",length = 5)
	private String orderId;
	@Column(name = "NET_VALUE")
	private Double netValue;
	@Column(name = "GL_ACCOUNT",length = 10)
	private String glAccount;
	@Column(name = "GL_ACCOUNT_TEXT",length = 10)
	private String glAccountText;
	@Column(name = "COST_CENTER",length = 50)
	private String costCenter;
	@Column(name = "COST_CENTER_TEXT",length = 50)
	private String costCenterText;
	@Column(name = "ASSET_NO",length = 10)
	private String assetNo;
	@Column(name = "SUB_NUMBER",length = 10)
	private String subNumber;
	@Column(name = "PROFIT_CENTER",length = 10)
	private String profitCenter;
	@Column(name = "WBS_ELEMENT",length = 10)
	private String wbsElement;
	@Column(name = "CR_DB_INDICATOR",length = 2)
	private String crDbIndicator;
	@Column(name = "TAX_C0DE",length = 50)
	private String taxCode;
	@Column(name = "ACCOUNT_NUM",length = 50)
	private String accountNum;
	@Column(name = "LINE_TEXT",length = 50)
	private String lineText;
	@Column(name ="TAX_VALUE",length = 50)
	private String taxValue;
	@Column(name ="TAX_PER",length = 50)
	private String taxPer;
	@Column(name ="BASE_RATE",length = 50)
	private String baseRate;
	@Column(name ="MATERIAL_DESC",length = 50)
	private String materialDesc;
	@Column(name ="CURRENCY",length = 50)
	private String currency;
	@Column(name ="CONDITION_TYPE",length = 50)
	private String conditionType;
	
}
