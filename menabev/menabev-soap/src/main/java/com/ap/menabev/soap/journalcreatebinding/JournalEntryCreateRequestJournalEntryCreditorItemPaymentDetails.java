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
 * <p>Java class for JournalEntryCreateRequestJournalEntryCreditorItemPaymentDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="JournalEntryCreateRequestJournalEntryCreditorItemPaymentDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PaymentMethod" type="{http://sap.com/xi/SAPSCORE/SFIN}PaymentMethod" minOccurs="0"/&gt;
 *         &lt;element name="PaymentMethodSupplement" type="{http://sap.com/xi/SAPSCORE/SFIN}PaymentMethodSupplement" minOccurs="0"/&gt;
 *         &lt;element name="PaymentReference" type="{http://sap.com/xi/SAPSCORE/SFIN}PaymentReferenceID" minOccurs="0"/&gt;
 *         &lt;element name="PaymentBlockingReason" type="{http://sap.com/xi/SAPSCORE/SFIN}PaymentBlockingReasonCode" minOccurs="0"/&gt;
 *         &lt;element name="AlternativePayee" type="{http://sap.com/xi/SAPSCORE/SFIN}BusinessPartnerInternalID" minOccurs="0"/&gt;
 *         &lt;element name="PayeeBankAccount" type="{http://sap.com/xi/SAPSCORE/SFIN}JournalEntryCreateRequestJournalEntryCreditorItemPayeeBankAccount" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JournalEntryCreateRequestJournalEntryCreditorItemPaymentDetails", propOrder = {
    "paymentMethod",
    "paymentMethodSupplement",
    "paymentReference",
    "paymentBlockingReason",
    "alternativePayee",
    "payeeBankAccount"
})
public class JournalEntryCreateRequestJournalEntryCreditorItemPaymentDetails {

    @XmlElement(name = "PaymentMethod")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String paymentMethod;
    @XmlElement(name = "PaymentMethodSupplement")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String paymentMethodSupplement;
    @XmlElement(name = "PaymentReference")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String paymentReference;
    @XmlElement(name = "PaymentBlockingReason")
    protected PaymentBlockingReasonCode paymentBlockingReason;
    @XmlElement(name = "AlternativePayee")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String alternativePayee;
    @XmlElement(name = "PayeeBankAccount")
    protected JournalEntryCreateRequestJournalEntryCreditorItemPayeeBankAccount payeeBankAccount;

    /**
     * Gets the value of the paymentMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the value of the paymentMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentMethod(String value) {
        this.paymentMethod = value;
    }

    /**
     * Gets the value of the paymentMethodSupplement property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentMethodSupplement() {
        return paymentMethodSupplement;
    }

    /**
     * Sets the value of the paymentMethodSupplement property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentMethodSupplement(String value) {
        this.paymentMethodSupplement = value;
    }

    /**
     * Gets the value of the paymentReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentReference() {
        return paymentReference;
    }

    /**
     * Sets the value of the paymentReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentReference(String value) {
        this.paymentReference = value;
    }

    /**
     * Gets the value of the paymentBlockingReason property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentBlockingReasonCode }
     *     
     */
    public PaymentBlockingReasonCode getPaymentBlockingReason() {
        return paymentBlockingReason;
    }

    /**
     * Sets the value of the paymentBlockingReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentBlockingReasonCode }
     *     
     */
    public void setPaymentBlockingReason(PaymentBlockingReasonCode value) {
        this.paymentBlockingReason = value;
    }

    /**
     * Gets the value of the alternativePayee property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlternativePayee() {
        return alternativePayee;
    }

    /**
     * Sets the value of the alternativePayee property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlternativePayee(String value) {
        this.alternativePayee = value;
    }

    /**
     * Gets the value of the payeeBankAccount property.
     * 
     * @return
     *     possible object is
     *     {@link JournalEntryCreateRequestJournalEntryCreditorItemPayeeBankAccount }
     *     
     */
    public JournalEntryCreateRequestJournalEntryCreditorItemPayeeBankAccount getPayeeBankAccount() {
        return payeeBankAccount;
    }

    /**
     * Sets the value of the payeeBankAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link JournalEntryCreateRequestJournalEntryCreditorItemPayeeBankAccount }
     *     
     */
    public void setPayeeBankAccount(JournalEntryCreateRequestJournalEntryCreditorItemPayeeBankAccount value) {
        this.payeeBankAccount = value;
    }

}
