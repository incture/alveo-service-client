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
 * <p>Java class for JournalEntryCreateRequestJournalEntryDebtorItemPaymentDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="JournalEntryCreateRequestJournalEntryDebtorItemPaymentDetails"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PaymentMethod" type="{http://sap.com/xi/SAPSCORE/SFIN}PaymentMethod" minOccurs="0"/&gt;
 *         &lt;element name="PaymentMethodSupplement" type="{http://sap.com/xi/SAPSCORE/SFIN}PaymentMethodSupplement" minOccurs="0"/&gt;
 *         &lt;element name="SEPAMandate" type="{http://sap.com/xi/SAPSCORE/SFIN}SEPAMandate" minOccurs="0"/&gt;
 *         &lt;element name="PaymentReference" type="{http://sap.com/xi/SAPSCORE/SFIN}PaymentReferenceID" minOccurs="0"/&gt;
 *         &lt;element name="PaymentBlockingReason" type="{http://sap.com/xi/SAPSCORE/SFIN}PaymentBlockingReasonCode" minOccurs="0"/&gt;
 *         &lt;element name="PaymentServiceProvider" type="{http://sap.com/xi/SAPSCORE/SFIN}PaymentServiceProvider" minOccurs="0"/&gt;
 *         &lt;element name="PaymentByPaymentServicePrvdr" type="{http://sap.com/xi/SAPSCORE/SFIN}PaymentByPaymentServicePrvdr" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JournalEntryCreateRequestJournalEntryDebtorItemPaymentDetails", propOrder = {
    "paymentMethod",
    "paymentMethodSupplement",
    "sepaMandate",
    "paymentReference",
    "paymentBlockingReason",
    "paymentServiceProvider",
    "paymentByPaymentServicePrvdr"
})
public class JournalEntryCreateRequestJournalEntryDebtorItemPaymentDetails {

    @XmlElement(name = "PaymentMethod")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String paymentMethod;
    @XmlElement(name = "PaymentMethodSupplement")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String paymentMethodSupplement;
    @XmlElement(name = "SEPAMandate")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String sepaMandate;
    @XmlElement(name = "PaymentReference")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String paymentReference;
    @XmlElement(name = "PaymentBlockingReason")
    protected PaymentBlockingReasonCode paymentBlockingReason;
    @XmlElement(name = "PaymentServiceProvider")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String paymentServiceProvider;
    @XmlElement(name = "PaymentByPaymentServicePrvdr")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String paymentByPaymentServicePrvdr;

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
     * Gets the value of the sepaMandate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSEPAMandate() {
        return sepaMandate;
    }

    /**
     * Sets the value of the sepaMandate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSEPAMandate(String value) {
        this.sepaMandate = value;
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
     * Gets the value of the paymentServiceProvider property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentServiceProvider() {
        return paymentServiceProvider;
    }

    /**
     * Sets the value of the paymentServiceProvider property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentServiceProvider(String value) {
        this.paymentServiceProvider = value;
    }

    /**
     * Gets the value of the paymentByPaymentServicePrvdr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentByPaymentServicePrvdr() {
        return paymentByPaymentServicePrvdr;
    }

    /**
     * Sets the value of the paymentByPaymentServicePrvdr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentByPaymentServicePrvdr(String value) {
        this.paymentByPaymentServicePrvdr = value;
    }

}
