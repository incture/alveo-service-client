package com.ap.menabev.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ThreeWayInvoiceItemDto {
	private String guid;
	private String itemCode;
	private String requestId;
	private String itemText;
	private String refDocCat;// ref_purchasedoc_category
	private Long refDocNum;// ref_purchase_num
	private String extItemId;
	private String articleNum;
	private String customerItemId;
	private String upcCode;
	private double invQty;
	private String uom;
	private double unitPrice;
	private String currency;
	private Integer pricingUnit;
	private String orderPriceUnit;
	private double grossPrice;
	private double discountValue;
	private double disPerentage;
	private String taxCode;
	private double taxValue;
	private double taxPercentage;
	private double netWorth;
	private Boolean isTwowayMatched;
	private Boolean isThreewayMatched;
	private Long matchDocNum;
	private String matchDocItem;
	private String matchParam;
	private String matchserviceNumber;
	private String matchpackageNumber;
	private String matchType;// manuall or Auto posting
	private String updatedBy;
	private long updatedAt;
	private Boolean isSelected;
	private String matchedBy;
	private boolean isAccAssigned;
	private String itemRequisationNum;
	private String requisationNum;
	private String contractNum;
	private String contractItem;
	private boolean isDeleted;
	private String setPoMaterialNum;
	private List<InvoiceItemAcctAssignmentDto> invItemAcctDtoList;
	private List<ItemMessageDto> invoiceItemMessages;
	private List<ItemThreeWayMatchPaylod> itemThreeWayMatchPayload;

	// Added by Dipanjan on 21/06/2021 from Menabev AP DB Tables sheet shared by
	// Prashant Kumar

	private String accAssignmentCat;
	private String itemStatusCode;
	private String itemStatusText;
	private Double alvQtyUOM;
	private Double poUnitPriceUOM;
	private Double alvQtyOPU;
	private Double alvQtyOU;
	private Double poUnitPriceOPU;
	private Double poUnitPriceOU;
	private String orderUnit;
	// private String orderPriceUnit;
	private String orderUnitISO;
	private String orderPriceUnitISO;
	private String itemCategory;
	private String accountAssignmentCat;
	private String productType;
	private String poMatNum;
	private String poItemText;
	private Double poQtyOU;
	private Double poQtyOPU;
	private String poTaxCode;
	private Boolean grFlag;
	private Boolean grBsdIv;
	private Boolean srvBsdIv;
	private Boolean ivFlag;
	private Integer convNum1;
	private Integer convDen1;

}
