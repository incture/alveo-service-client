package com.ap.menabev.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.ap.menabev.dto.NonPoTemplateItemsPKId;

import java.math.BigDecimal;

import lombok.Data;

@Entity
@Table(name = "NON_PO_TEMPLATE_ITEMS")
@IdClass(NonPoTemplateItemsPKId.class)

public @Data class NonPoTemplateItemsDo implements Serializable {

	// @EmbeddedId
	// NonPoTemplateItemsPKId nonPoTemplateItemsPKId;
	@Id
	@Column(name = "ITEM_ID")
	private String itemId;
	@Id
	@Column(name = "TEMPLATE_ID")
	private String templateId;

	@Column(name = "GL_ACCOUNT")
	private String glAccount;

	@Column(name = "COST_CENTER")
	private String costCenter;

	@Column(name = "INTERNAL_ORDER_Id")
	private String internalOrderId;

	@Column(name = "ASSET_NO")
	private String assetNo;
	
	@Column(name = "SUB_NUMBER")
	private String subNumber;
	
	@Column(name = "WBS_ELEMENT")
	private String wbsElement;
	
	@Column(name="MATERIAL_DESCRIPTION")
	private String materialDescription;
	
	@Column(name = "CR_DB_INDICATOR")
	private String crDbIndicator;
	
	@Column(name="PROFIT_CENTER")
	private String profitCenter;
	
	@Column(name="ITEM_TEXT")
	private String itemText;
	
	@Column(name="COMPANY_CODE")
	private String companyCode;
	
	@Column(name="isNonPo")
	private Boolean isNonPo;
	
	@Column(name="ACCOUNT_NO")
    private String accountNo;
	
	@Column(name="ALLOCATION_PERCENTAGE")
	private String allocationPercent;
}

