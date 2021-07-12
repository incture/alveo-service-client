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
@IdClass(InvoiceItemAcctAssignmentPkDo.class)
@Table(name = "INVOICE_ITEM_ACCOUNT_ASSIGNMENT")
public @Data class InvoiceItemAcctAssignmentDo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="INV_ACC_ASS_ID")
	private String accountAssgnGuid;
	@Id
	@Column(name="REQUEST_ID", nullable = false)
	private String requestId;
	@Id
	@Column(name = "ITEM_ID", nullable = false)
	private String itemId; // invoiceItemId
	@Id
	@Column(name="SERIAL_NO",nullable=false)
	private String serialNo;
	@Column(name="IS_DELETED")
	private Boolean isDeleted;
	@Column(name = "IS_PLANNED")
	private Boolean  isPlanned;
	@Column(name = "QUANTITY")
	private Double qty;
	@Column(name = "QUANTITY_UNIT",length = 10)
	private String qtyUnit;
	@Column(name="DIST_PERC")
	private Double distPerc;
	@Column(name = "NET_VALUE")
	private Double netValue;
	@Column(name = "GL_ACCOUNT",length = 10)
	private String glAccount;
	@Column(name = "COST_CENTER")
	private String costCenter;
	@Column(name = "COST_CENTER_TEXT")
	private String costCenterText;
	@Column(name="TAX_VALUE")
	private String  taxValue;
	@Column(name = "TAX-PERC")
     private String taxPercentage;
	@Column(name="SUB_NUMBER",length =10)
	private String subNumber;
	@Column(name="ORDER_ID",length = 15)
	private String orderId;
	@Column(name="PROFIT_CTR",length=15)
	private String profitCtr;
    @Column(name="WBS_ELEMENT",length=14)
    private String wbsElement;
    @Column(name = "CR_DB_INDICATOR")
    private String crDbIndicator;
    @Column(name="CO_AREA",length = 4)
	private String coArea;
    @Column(name = "PRICING_UNIT")
    private Integer pricingUnit;

}

