package com.ap.menabev.entity;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "INVOICE_ITEM")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@IdClass(InvoiceItemPkDo.class)
public class InvoiceItemDo implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "ID")
	private String guid;
	@Id
	@Column(name = "ITEM_CODE")
	private String itemCode;
	@Id
	@Column(name = "REQUEST_ID", nullable = false)
	private String requestId;
	@Column(name = "EXT_ITEM_ID",length = 10)
	private String extItemId;
	@Column(name = "ITEM_TEXT" , length = 50)
	private String itemText;
	@Column(name = "REF_DOC_CAT",length = 2)
	private String refDocCat;//ref_purchasedoc_category
	@Column(name = "REF_DOC_NUM",length = 10)
	private Long refDocNum;//ref_purchase_num
	@Column(name = "ARTICLE_NUM" ,length = 20)
	private String articleNum;
	@Column(name = "CUSTOMER_ITEM_ID" , length = 20)
	private Integer customerItemId;
	@Column(name = "UPC_CODE",length = 50)
	private String upcCode;
	@Column(name = "INV_QTY")
	private double invQty;
	@Column(name = "UOM",length = 5)
	private String uom;
	@Column(name = "UNIT_PRICE")
	private double unitPrice;
	@Column(name = "CURRENCY",length = 5)
	private String currency;
	@Column(name = "PRICING_UNIT",length = 5)
	private Integer pricingUnit;
	@Column(name = "ORDER_PRICE_UNIT",length = 5)
	private String orderPriceUnit;
	@Column(name = "GROSS_PRICE")
	private double grossPrice;
	@Column(name = "DISCOUNT_VALUE")
	private double discountValue;
	@Column(name = "DISCOUNT_PERCENTAGE")
	private double disPerentage;
	@Column(name = "TAX_CODE", length = 5)
	private String taxCode;
	@Column(name = "TAX_VALUE")
	private double taxValue;
	@Column(name = "TAX_PERCENTAGE")
	private double taxPercentage;
	@Column(name = "NET_WORTH")
	private double netWorth;
	@Column(name = "IS_TWOWAY_MATCHED")
	private Boolean isTwowayMatched;
	@Column(name = "IS_THREEWAY_MATCHED")
	private Boolean isThreewayMatched;
	@Column(name = "MATCH_DOC_NUM",length = 20)
	private Long matchDocNum;
	@Column(name = "MATCH_DOC_ITEM",length = 10)
	private String matchDocItem;
	@Column(name = "MATCH_PARAM",length = 20)
	private String matchParam;
	@Column(name = "MATCH_SERVICE_NUM",length = 10)
	private String matchserviceNumber;
	@Column(name = "MATCH_PACKAGE_NUMBER",length = 10)
	private String matchpackageNumber;
	@Column(name = "MATCH_TYPE")
	private String matchType;// manuall or Auto posting
 	@Column(name="UPDATED_BY",length = 100)
	private String updatedBy;
 	@Column(name="UPDATED_AT")
	private long updatedAt;
 	@Column(name = "isSelected")
	private Boolean isSelected;
 	@Column(name="MATCHED_By")
	private String matchedBy;
    @Column (name="IS_ACC_ASSIGNED")
    private boolean isAccAssigned;
    @Column(name = "ITEM_REQUISATION_NUM",length =10)
    private String itemRequisationNum;
    @Column(name = "REQUISATION_NUM" ,length = 10)
    private String requisationNum;
    @Column(name = "CONTRACT_NUM",length = 10)
    private String contractNum;
    @Column(name = "CONTRACT_ITEM",length = 10)
    private String contractItem;
    @Column(name = "IS_DELETED")
    private boolean isDeleted;
    

    
}
