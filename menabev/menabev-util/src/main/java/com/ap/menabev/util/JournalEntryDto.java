package com.ap.menabev.util;

import java.util.List;

import lombok.Data;


@Data
public class JournalEntryDto {
	private String OriginalReferenceDocumentType;
    private List<String>  OriginalReferenceDocument;
    private List<String> OriginalReferenceDocumentLogicalSystem;
    private String BusinessTransactionType;
    private String AccountingDocumentType;
    private String DocumentReferenceID;
    private String CreatedByUser;
    private String CompanyCode;
    private String DocumentDate;
    private String PostingDate;
    
    private List<ItemDto> Item;
    private CreditorItemDto CreditorItem;
    private ProductTaxItemDto ProductTaxItem;

}
