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
 * <p>Java class for JournalEntryCreateRequestJournalEntryDownPayment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="JournalEntryCreateRequestJournalEntryDownPayment"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SpecialGLCode" type="{http://sap.com/xi/SAPSCORE/SFIN}SpecialGLCode" minOccurs="0"/&gt;
 *         &lt;element name="SalesOrder" type="{http://sap.com/xi/SAPSCORE/SFIN}SalesOrder" minOccurs="0"/&gt;
 *         &lt;element name="SalesOrderItem" type="{http://sap.com/xi/SAPSCORE/SFIN}SalesOrderItem" minOccurs="0"/&gt;
 *         &lt;element name="TaxItemGroup" type="{http://sap.com/xi/SAPSCORE/SFIN}TaxItemGroupID" minOccurs="0"/&gt;
 *         &lt;element name="TaxCode" type="{http://sap.com/xi/SAPSCORE/SFIN}ProductTaxationCharacteristicsCode" minOccurs="0"/&gt;
 *         &lt;element name="TaxJurisdiction" type="{http://sap.com/xi/SAPSCORE/SFIN}TaxJurisdictionCode" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JournalEntryCreateRequestJournalEntryDownPayment", propOrder = {
    "specialGLCode",
    "salesOrder",
    "salesOrderItem",
    "taxItemGroup",
    "taxCode",
    "taxJurisdiction"
})
public class JournalEntryCreateRequestJournalEntryDownPayment {

    @XmlElement(name = "SpecialGLCode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String specialGLCode;
    @XmlElement(name = "SalesOrder")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String salesOrder;
    @XmlElement(name = "SalesOrderItem")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String salesOrderItem;
    @XmlElement(name = "TaxItemGroup")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String taxItemGroup;
    @XmlElement(name = "TaxCode")
    protected ProductTaxationCharacteristicsCode taxCode;
    @XmlElement(name = "TaxJurisdiction")
    protected TaxJurisdictionCode taxJurisdiction;

    /**
     * Gets the value of the specialGLCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialGLCode() {
        return specialGLCode;
    }

    /**
     * Sets the value of the specialGLCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialGLCode(String value) {
        this.specialGLCode = value;
    }

    /**
     * Gets the value of the salesOrder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSalesOrder() {
        return salesOrder;
    }

    /**
     * Sets the value of the salesOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSalesOrder(String value) {
        this.salesOrder = value;
    }

    /**
     * Gets the value of the salesOrderItem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSalesOrderItem() {
        return salesOrderItem;
    }

    /**
     * Sets the value of the salesOrderItem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSalesOrderItem(String value) {
        this.salesOrderItem = value;
    }

    /**
     * Gets the value of the taxItemGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxItemGroup() {
        return taxItemGroup;
    }

    /**
     * Sets the value of the taxItemGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxItemGroup(String value) {
        this.taxItemGroup = value;
    }

    /**
     * Gets the value of the taxCode property.
     * 
     * @return
     *     possible object is
     *     {@link ProductTaxationCharacteristicsCode }
     *     
     */
    public ProductTaxationCharacteristicsCode getTaxCode() {
        return taxCode;
    }

    /**
     * Sets the value of the taxCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductTaxationCharacteristicsCode }
     *     
     */
    public void setTaxCode(ProductTaxationCharacteristicsCode value) {
        this.taxCode = value;
    }

    /**
     * Gets the value of the taxJurisdiction property.
     * 
     * @return
     *     possible object is
     *     {@link TaxJurisdictionCode }
     *     
     */
    public TaxJurisdictionCode getTaxJurisdiction() {
        return taxJurisdiction;
    }

    /**
     * Sets the value of the taxJurisdiction property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaxJurisdictionCode }
     *     
     */
    public void setTaxJurisdiction(TaxJurisdictionCode value) {
        this.taxJurisdiction = value;
    }

}
