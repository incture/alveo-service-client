package com.ap.menabev.dto;



import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PurchaseDocumentItemDto {

	private String purchaseDocumentItemId = UUID.randomUUID().toString();

	
	private String documentNumber;
	private String documentItem;//Item Id
	private Boolean deletionInd;
	private String shortText;//Description
	private String materialNum;//Material Code
	private String vendMat;
	private String plant;
	private String stgLoc;
	private BigDecimal poQty;
	private String orderUnit;//UOM
	private String orderPriceUnit;
	private BigDecimal convNum1;
	private BigDecimal convDen1;
	private BigDecimal netPrice;//Unit Price 
	private String priceUnit;
	private String taxCode;
	private Boolean noMoreGr;
	private String itemCat;
	private String acctasscat;
	private Boolean grInd;
	private Boolean irInd;
	private Boolean grBasediv;
	private BigDecimal netWeight;
	private BigDecimal netVolume;
	private Integer baseUnit;
    private String upcCode;
    private Boolean isMatched;
    private String deliveredQty;
    private String invoiceQty;
    private String taxPercentage;
    private String totalPrice;//net Price
    private String taxAmount;
    private Date deliveryDate;
    
    //Delivered Qty
    //Invoice Qty
    //Tax Percentage 
    //Total Price
    //Tax Amount 
    //Delivery Date 
}
