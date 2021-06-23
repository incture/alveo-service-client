package com.ap.menabev.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ToHistoryOdata {

	@Getter
	@Setter
	@ToString
	public class Results{
		private __metadata __metadata;

	    private String Number;

	    private String ItemNo;

	    private String AccountAssignmentNumber;

	    private String PurchasingHistoryDocumentType;

	    private String PurchasingHistoryDocumentYear;

	    private String PurchasingHistoryDocument;

	    private String PurchasingHistoryDocumentItem;

	    private String PurchasingHistoryCategory;

	    private String GoodsMovementType;

	    private String PostingDate;

	    private String Quantity;

	    private String PurOrdAmountInCompanyCodeCrcy;

	    private String PurchaseOrderAmount;

	    private String Currency;

	    private String GRIRAcctClrgAmtInCoCodeCrcy;

	    private String GdsRcptBlkdStkQtyInOrdQtyUnit;

	    private String GdsRcptBlkdStkQtyInOrdPrcUnit;

	    private String DebitCreditCode;

	    private String InventoryValuationType;

	    private boolean IsCompletelyDelivered;

	    private String DocumentReferenceID;

	    private String ReferenceDocumentFiscalYear;

	    private String ReferenceDocument;

	    private String ReferenceDocumentItem;

	    private String AccountingDocumentCreationDate;

	    private String InvoiceAmtInCoCodeCrcy;

	    private String InvoiceAmountInFrgnCurrency;

	    private String Material;

	    private String Plant;

	    private String PricingDocument;

	    private String TaxCode;

	    private String QuantityInDeliveryQtyUnit;

	    private String DeliveryQuantityUnit;

	    private String ManufacturerMaterial;

	    private String CompanyCodeCurrency;

	    private String DocumentDate;

	    private String CurrencyIso;

	    private String LocCurrIso;

	    private String DelivUnitIso;

	    private String MaterialExternal;

	    private String MaterialGuid;

	    private String MaterialVersion;

	    private String PurMatExternal;

	    private String PurMatGuid;

	    private String PurMatVersion;

	    private String RefDocNoLong;

	    private String Batch;

	    private String StkSegment;

	    private String MaterialLong;

	    private String PurMatLong;

	    private String StkSegLong;

	    private String MoveReas;

	    private String EntryTime;

	    private String ConfSer;

	    private String RvslOfGoodsReceiptIsAllowed;

	    private String QtyInPurchaseOrderPriceUnit;

	    private String ShipgInstrnSupplierCompliance;

	    private String GRIRAcctClrgAmtInTransacCrcy;

	    private String QuantityInBaseUnit;

	    private String GRIRAcctClrgAmtInOrdTrnsacCrcy;

	    private String InvoiceAmtInPurOrdTransacCrcy;

	    private String VltdGdsRcptBlkdStkQtyInOrdUnit;

	    private String VltdGdsRcptBlkdQtyInOrdPrcUnit;

	    private String IsToBeAcceptedAtOrigin;

	    private String ExchangeRateDifferenceAmount;

	    private String ExchangeRate;

	    private String DeliveryDocument;

	    private String EntryDate;

	    private String DeliveryDocumentItem;

	    private String AccountingDocumentCreationTime;

	    private String SrvPos;

	    private String PackNo;

	    private String IntRow;

	    private String PlnPackNo;

	    private String PlnIntRow;

	    private String ExtRow;
	}
	private List<Results> results;
}
