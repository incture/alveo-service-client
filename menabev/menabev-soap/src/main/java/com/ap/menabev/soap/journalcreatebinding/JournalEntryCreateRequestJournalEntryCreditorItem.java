//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.05.21 at 01:12:35 PM IST 
//


package com.ap.menabev.soap.journalcreatebinding;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for JournalEntryCreateRequestJournalEntryCreditorItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="JournalEntryCreateRequestJournalEntryCreditorItem"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ReferenceDocumentItem" type="{http://sap.com/xi/SAPSCORE/SFIN}BusinessTransactionDocumentItemID"/&gt;
 *         &lt;element name="Creditor" type="{http://sap.com/xi/SAPSCORE/SFIN}BusinessPartnerInternalID"/&gt;
 *         &lt;element name="AmountInTransactionCurrency" type="{http://sap.com/xi/SAPSCORE/SFIN}Amount"/&gt;
 *         &lt;element name="AmountInCompanyCodeCurrency" type="{http://sap.com/xi/SAPSCORE/SFIN}Amount" minOccurs="0"/&gt;
 *         &lt;element name="AmountInGroupCurrency" type="{http://sap.com/xi/SAPSCORE/SFIN}Amount" minOccurs="0"/&gt;
 *         &lt;element name="DebitCreditCode" type="{http://sap.com/xi/SAPSCORE/SFIN}DebitCreditCode" minOccurs="0"/&gt;
 *         &lt;element name="DocumentItemText" type="{http://sap.com/xi/SAPSCORE/SFIN}DocumentItemText" minOccurs="0"/&gt;
 *         &lt;element name="AssignmentReference" type="{http://sap.com/xi/SAPSCORE/SFIN}AssignmentReference" minOccurs="0"/&gt;
 *         &lt;element name="Reference1IDByBusinessPartner" type="{http://sap.com/xi/SAPSCORE/SFIN}ReferenceKey" minOccurs="0"/&gt;
 *         &lt;element name="Reference2IDByBusinessPartner" type="{http://sap.com/xi/SAPSCORE/SFIN}ReferenceKey" minOccurs="0"/&gt;
 *         &lt;element name="Reference3IDByBusinessPartner" type="{http://sap.com/xi/SAPSCORE/SFIN}ReferenceKey_3" minOccurs="0"/&gt;
 *         &lt;element name="CashDiscountTerms" type="{http://sap.com/xi/SAPSCORE/SFIN}JournalEntryCreateRequestJournalEntryCashDiscountTerms" minOccurs="0"/&gt;
 *         &lt;element name="PaymentDetails" type="{http://sap.com/xi/SAPSCORE/SFIN}JournalEntryCreateRequestJournalEntryCreditorItemPaymentDetails" minOccurs="0"/&gt;
 *         &lt;element name="StateCentralBankPaymentReason" type="{http://sap.com/xi/SAPSCORE/SFIN}StateCentralBankPaymentReason" minOccurs="0"/&gt;
 *         &lt;element name="VATRegistration" type="{http://sap.com/xi/SAPSCORE/SFIN}VATRegistration" minOccurs="0"/&gt;
 *         &lt;element name="BranchAccount" type="{http://sap.com/xi/SAPSCORE/SFIN}BusinessPartnerInternalID" minOccurs="0"/&gt;
 *         &lt;element name="BusinessPlace" type="{http://sap.com/xi/SAPSCORE/SFIN}BusinessPlace_GFN" minOccurs="0"/&gt;
 *         &lt;element name="HouseBank" type="{http://sap.com/xi/SAPSCORE/SFIN}HouseBank_GFN" minOccurs="0"/&gt;
 *         &lt;element name="HouseBankAccount" type="{http://sap.com/xi/SAPSCORE/SFIN}HouseBankAccountID" minOccurs="0"/&gt;
 *         &lt;element name="DownPaymentTerms" type="{http://sap.com/xi/SAPSCORE/SFIN}JournalEntryCreateRequestJournalEntryDownPaymentCreditor" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JournalEntryCreateRequestJournalEntryCreditorItem", propOrder = {
    "referenceDocumentItem",
    "creditor",
    "amountInTransactionCurrency",
    "amountInCompanyCodeCurrency",
    "amountInGroupCurrency",
    "debitCreditCode",
    "documentItemText",
    "assignmentReference",
    "reference1IDByBusinessPartner",
    "reference2IDByBusinessPartner",
    "reference3IDByBusinessPartner",
    "cashDiscountTerms",
    "paymentDetails",
    "stateCentralBankPaymentReason",
    "vatRegistration",
    "branchAccount",
    "businessPlace",
    "houseBank",
    "houseBankAccount",
    "downPaymentTerms"
})
public class JournalEntryCreateRequestJournalEntryCreditorItem {

    @XmlElement(name = "ReferenceDocumentItem", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String referenceDocumentItem;
    @XmlElement(name = "Creditor", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String creditor;
    @XmlElement(name = "AmountInTransactionCurrency", required = true)
    protected Amount amountInTransactionCurrency;
    @XmlElement(name = "AmountInCompanyCodeCurrency")
    protected Amount amountInCompanyCodeCurrency;
    @XmlElement(name = "AmountInGroupCurrency")
    protected Amount amountInGroupCurrency;
    @XmlElement(name = "DebitCreditCode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String debitCreditCode;
    @XmlElement(name = "DocumentItemText")
    protected String documentItemText;
    @XmlElement(name = "AssignmentReference")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String assignmentReference;
    @XmlElement(name = "Reference1IDByBusinessPartner")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String reference1IDByBusinessPartner;
    @XmlElement(name = "Reference2IDByBusinessPartner")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String reference2IDByBusinessPartner;
    @XmlElement(name = "Reference3IDByBusinessPartner")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String reference3IDByBusinessPartner;
    @XmlElement(name = "CashDiscountTerms")
    protected JournalEntryCreateRequestJournalEntryCashDiscountTerms cashDiscountTerms;
    @XmlElement(name = "PaymentDetails")
    protected JournalEntryCreateRequestJournalEntryCreditorItemPaymentDetails paymentDetails;
    @XmlElement(name = "StateCentralBankPaymentReason")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String stateCentralBankPaymentReason;
    @XmlElement(name = "VATRegistration")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String vatRegistration;
    @XmlElement(name = "BranchAccount")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String branchAccount;
    @XmlElement(name = "BusinessPlace")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String businessPlace;
    @XmlElement(name = "HouseBank")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String houseBank;
    @XmlElement(name = "HouseBankAccount")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String houseBankAccount;
    @XmlElement(name = "DownPaymentTerms")
    protected JournalEntryCreateRequestJournalEntryDownPaymentCreditor downPaymentTerms;

    /**
     * Gets the value of the referenceDocumentItem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferenceDocumentItem() {
        return referenceDocumentItem;
    }

    /**
     * Sets the value of the referenceDocumentItem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferenceDocumentItem(String value) {
        this.referenceDocumentItem = value;
    }

    /**
     * Gets the value of the creditor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditor() {
        return creditor;
    }

    /**
     * Sets the value of the creditor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditor(String value) {
        this.creditor = value;
    }

    /**
     * Gets the value of the amountInTransactionCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link Amount }
     *     
     */
    public Amount getAmountInTransactionCurrency() {
        return amountInTransactionCurrency;
    }

    /**
     * Sets the value of the amountInTransactionCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Amount }
     *     
     */
    public void setAmountInTransactionCurrency(Amount value) {
        this.amountInTransactionCurrency = value;
    }

    /**
     * Gets the value of the amountInCompanyCodeCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link Amount }
     *     
     */
    public Amount getAmountInCompanyCodeCurrency() {
        return amountInCompanyCodeCurrency;
    }

    /**
     * Sets the value of the amountInCompanyCodeCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Amount }
     *     
     */
    public void setAmountInCompanyCodeCurrency(Amount value) {
        this.amountInCompanyCodeCurrency = value;
    }

    /**
     * Gets the value of the amountInGroupCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link Amount }
     *     
     */
    public Amount getAmountInGroupCurrency() {
        return amountInGroupCurrency;
    }

    /**
     * Sets the value of the amountInGroupCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Amount }
     *     
     */
    public void setAmountInGroupCurrency(Amount value) {
        this.amountInGroupCurrency = value;
    }

    /**
     * Gets the value of the debitCreditCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDebitCreditCode() {
        return debitCreditCode;
    }

    /**
     * Sets the value of the debitCreditCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDebitCreditCode(String value) {
        this.debitCreditCode = value;
    }

    /**
     * Gets the value of the documentItemText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentItemText() {
        return documentItemText;
    }

    /**
     * Sets the value of the documentItemText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentItemText(String value) {
        this.documentItemText = value;
    }

    /**
     * Gets the value of the assignmentReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssignmentReference() {
        return assignmentReference;
    }

    /**
     * Sets the value of the assignmentReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssignmentReference(String value) {
        this.assignmentReference = value;
    }

    /**
     * Gets the value of the reference1IDByBusinessPartner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReference1IDByBusinessPartner() {
        return reference1IDByBusinessPartner;
    }

    /**
     * Sets the value of the reference1IDByBusinessPartner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReference1IDByBusinessPartner(String value) {
        this.reference1IDByBusinessPartner = value;
    }

    /**
     * Gets the value of the reference2IDByBusinessPartner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReference2IDByBusinessPartner() {
        return reference2IDByBusinessPartner;
    }

    /**
     * Sets the value of the reference2IDByBusinessPartner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReference2IDByBusinessPartner(String value) {
        this.reference2IDByBusinessPartner = value;
    }

    /**
     * Gets the value of the reference3IDByBusinessPartner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReference3IDByBusinessPartner() {
        return reference3IDByBusinessPartner;
    }

    /**
     * Sets the value of the reference3IDByBusinessPartner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReference3IDByBusinessPartner(String value) {
        this.reference3IDByBusinessPartner = value;
    }

    /**
     * Gets the value of the cashDiscountTerms property.
     * 
     * @return
     *     possible object is
     *     {@link JournalEntryCreateRequestJournalEntryCashDiscountTerms }
     *     
     */
    public JournalEntryCreateRequestJournalEntryCashDiscountTerms getCashDiscountTerms() {
        return cashDiscountTerms;
    }

    /**
     * Sets the value of the cashDiscountTerms property.
     * 
     * @param value
     *     allowed object is
     *     {@link JournalEntryCreateRequestJournalEntryCashDiscountTerms }
     *     
     */
    public void setCashDiscountTerms(JournalEntryCreateRequestJournalEntryCashDiscountTerms value) {
        this.cashDiscountTerms = value;
    }

    /**
     * Gets the value of the paymentDetails property.
     * 
     * @return
     *     possible object is
     *     {@link JournalEntryCreateRequestJournalEntryCreditorItemPaymentDetails }
     *     
     */
    public JournalEntryCreateRequestJournalEntryCreditorItemPaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    /**
     * Sets the value of the paymentDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link JournalEntryCreateRequestJournalEntryCreditorItemPaymentDetails }
     *     
     */
    public void setPaymentDetails(JournalEntryCreateRequestJournalEntryCreditorItemPaymentDetails value) {
        this.paymentDetails = value;
    }

    /**
     * Gets the value of the stateCentralBankPaymentReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStateCentralBankPaymentReason() {
        return stateCentralBankPaymentReason;
    }

    /**
     * Sets the value of the stateCentralBankPaymentReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStateCentralBankPaymentReason(String value) {
        this.stateCentralBankPaymentReason = value;
    }

    /**
     * Gets the value of the vatRegistration property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVATRegistration() {
        return vatRegistration;
    }

    /**
     * Sets the value of the vatRegistration property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVATRegistration(String value) {
        this.vatRegistration = value;
    }

    /**
     * Gets the value of the branchAccount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBranchAccount() {
        return branchAccount;
    }

    /**
     * Sets the value of the branchAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBranchAccount(String value) {
        this.branchAccount = value;
    }

    /**
     * Gets the value of the businessPlace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBusinessPlace() {
        return businessPlace;
    }

    /**
     * Sets the value of the businessPlace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBusinessPlace(String value) {
        this.businessPlace = value;
    }

    /**
     * Gets the value of the houseBank property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHouseBank() {
        return houseBank;
    }

    /**
     * Sets the value of the houseBank property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHouseBank(String value) {
        this.houseBank = value;
    }

    /**
     * Gets the value of the houseBankAccount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHouseBankAccount() {
        return houseBankAccount;
    }

    /**
     * Sets the value of the houseBankAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHouseBankAccount(String value) {
        this.houseBankAccount = value;
    }

    /**
     * Gets the value of the downPaymentTerms property.
     * 
     * @return
     *     possible object is
     *     {@link JournalEntryCreateRequestJournalEntryDownPaymentCreditor }
     *     
     */
    public JournalEntryCreateRequestJournalEntryDownPaymentCreditor getDownPaymentTerms() {
        return downPaymentTerms;
    }

    /**
     * Sets the value of the downPaymentTerms property.
     * 
     * @param value
     *     allowed object is
     *     {@link JournalEntryCreateRequestJournalEntryDownPaymentCreditor }
     *     
     */
    public void setDownPaymentTerms(JournalEntryCreateRequestJournalEntryDownPaymentCreditor value) {
        this.downPaymentTerms = value;
    }

}
