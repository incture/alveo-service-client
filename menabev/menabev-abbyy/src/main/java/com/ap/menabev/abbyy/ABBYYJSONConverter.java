package com.ap.menabev.abbyy;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ap.menabev.dto.InvoiceHeaderDetailsDto;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.InvoiceItemDto;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ItemDto;
import com.ap.menabev.util.ServiceUtil;

public class ABBYYJSONConverter {
	public static InvoiceHeaderDto abbyyJSONOutputToInvoiceObject(JSONObject jsonObject) throws Exception {
		InvoiceHeaderDto headerDto = new InvoiceHeaderDto();
		List<InvoiceItemDto> itemsDtoList = new ArrayList<InvoiceItemDto>();
		JSONArray documentsArray = jsonObject.getJSONArray("Documents");
		JSONObject documents = documentsArray.getJSONObject(0);
		JSONObject documentData = documents.getJSONObject("DocumentData");
		JSONArray fieldsArray = documentData.getJSONArray("Fields");
		for (int i = 0; i < fieldsArray.length(); i++) {
			JSONObject headerObj = fieldsArray.getJSONObject(i);
			String name = headerObj.getString("Name");
			String valueText = null;
			Double valueDouble = null;
			Integer valueNumber = null;

			if ("LineItems".equalsIgnoreCase(name)) {
				JSONArray itemsArray = headerObj.getJSONArray("Items");
				for (int j = 0; j < itemsArray.length(); j++) {
					InvoiceItemDto itemDto = new InvoiceItemDto();
					JSONObject itemObjJson = itemsArray.getJSONObject(j);
					// System.err.println(itemObjJson.toString());
					JSONArray itemFieldsArray = itemObjJson.getJSONArray("Fields");
					for (int k = 0; k < itemFieldsArray.length(); k++) {
						JSONObject invoiceItem = itemFieldsArray.getJSONObject(k);
						System.out.println(invoiceItem.toString());
						String itemName = invoiceItem.getString("Name");
						if ("Position".equalsIgnoreCase(itemName) && invoiceItem.has("Value")
								&& !ServiceUtil.isEmpty(invoiceItem.get("Value")))
							itemDto.setExtItemId(invoiceItem.getString("Value"));
						else if ("ArticleNumber".equalsIgnoreCase(itemName) && invoiceItem.has("Value")
								&& !ServiceUtil.isEmpty(invoiceItem.get("Value")))
							itemDto.setArticleNum(invoiceItem.getString("Value"));
						else if ("OrderNumber".equalsIgnoreCase(itemName) && invoiceItem.has("Value")
								&& !ServiceUtil.isEmpty(invoiceItem.get("Value")))
							itemDto.setRefDocNum(new Long((invoiceItem.getString("Value") == "" ? null
									: invoiceItem.getString("Value")) == null ? 0
											: Long.valueOf(invoiceItem.getString("Value"))));
						else if ("MaterialNumber".equalsIgnoreCase(itemName) && invoiceItem.has("Value")
								&& !ServiceUtil.isEmpty(invoiceItem.get("Value")))
							itemDto.setCustomerItemId(invoiceItem.getString("Value"));
						else if ("Description".equalsIgnoreCase(itemName) && invoiceItem.has("Value")
								&& !ServiceUtil.isEmpty(invoiceItem.get("Value")))
							itemDto.setItemText(invoiceItem.getString("Value"));
						else if ("Quantity".equalsIgnoreCase(itemName) && invoiceItem.has("Value")) {
							if (!ServiceUtil.isEmpty(invoiceItem.get("Value")))
								itemDto.setInvQty(invoiceItem.getDouble("Value"));
							else
								itemDto.setInvQty(0.0);
						} else if ("UnitOfMeasurement".equalsIgnoreCase(itemName) && invoiceItem.has("Value")
								&& !ServiceUtil.isEmpty(invoiceItem.get("Value"))) {
							itemDto.setUom(invoiceItem.getString("Value"));
						} else if ("UnitPrice".equalsIgnoreCase(itemName) && invoiceItem.has("Value")) {
							if (!ServiceUtil.isEmpty(invoiceItem.get("Value")))
								itemDto.setUnitPrice(Double.valueOf(invoiceItem.getDouble("Value")));
							else
								itemDto.setUnitPrice(0.0);
						} else if ("UnitPriceDenominator".equalsIgnoreCase(itemName) && invoiceItem.has("Value")) {
							if (!ServiceUtil.isEmpty(invoiceItem.get("Value")))
								itemDto.setPricingUnit(invoiceItem.getInt("Value"));
							else
								itemDto.setPricingUnit(1);
						} else if ("DiscountPercentage".equalsIgnoreCase(itemName) && invoiceItem.has("Value")) {
							if (!ServiceUtil.isEmpty(invoiceItem.get("Value")))
								itemDto.setDisPerentage(invoiceItem.getDouble("Value"));
							else
								itemDto.setDisPerentage(0.0);
						} else if ("DiscountValue".equalsIgnoreCase(itemName) && invoiceItem.has("Value")) {
							if (!ServiceUtil.isEmpty(invoiceItem.get("Value")))
								itemDto.setDiscountValue(invoiceItem.getDouble("Value"));
							else
								itemDto.setDiscountValue(0.0);
						} else if ("TotalPriceNetto".equalsIgnoreCase(itemName) && invoiceItem.has("Value")) {
							if (!ServiceUtil.isEmpty(invoiceItem.get("Value")))
								itemDto.setGrossPrice(invoiceItem.getDouble("Value"));
							else
								itemDto.setGrossPrice(0.0);
						} else if ("VATPercentage".equalsIgnoreCase(itemName) && invoiceItem.has("Value")) {
							if (!ServiceUtil.isEmpty(invoiceItem.get("Value")))
								itemDto.setTaxPercentage(invoiceItem.getDouble("Value"));
							else
								itemDto.setTaxPercentage(0.0);
						}

						else if ("VATValue".equalsIgnoreCase(itemName) && invoiceItem.has("Value")) {
							if (!ServiceUtil.isEmpty(invoiceItem.get("Value"))) {

								itemDto.setTaxValue(invoiceItem.getDouble("Value"));
							} else
								itemDto.setTaxValue(0.0);
						}

						else if ("TotalPriceBruTo".equalsIgnoreCase(itemName) && invoiceItem.has("Value")
								&& !ServiceUtil.isEmpty(invoiceItem.get("Value")))
							itemDto.setNetWorth(invoiceItem.getDouble("Value"));

						// default value
						itemDto.setItemStatusText("Item Mismatch");
						itemDto.setItemStatusCode(ApplicationConstants.ITEM_MISMATCH);
						itemDto.setIsDeleted(Boolean.FALSE);
						itemDto.setIsAccAssigned(Boolean.FALSE);
						itemDto.setIsThreewayMatched(Boolean.FALSE);
						itemDto.setIsSelected(Boolean.FALSE);
						itemDto.setIsTwowayMatched(Boolean.FALSE);
						

					}

					itemsDtoList.add(itemDto);
				}
			} else if ("InvoiceNumber".equalsIgnoreCase(name)) {
				// headerDto.setExtInvNum(headerObj.getString("Value"));
				headerDto.setInvoice_ref_number(headerObj.getString("Value"));
			} else if ("InvoiceDate".equalsIgnoreCase(name)) {
				String invDateString = headerObj.getString("Value");//
				headerDto.setInvoiceDate(ServiceUtil.getStringToEpoch(invDateString));
			} else if ("InvoiceTotal".equalsIgnoreCase(name) || "InvoiceTotal".equalsIgnoreCase(name)) {
				if (!ServiceUtil.isEmpty(headerObj.getString("Value")))
					headerDto.setInvoiceTotal(new Double(headerObj.getString("Value").replace(",", "")));
				else
					headerDto.setInvoiceTotal(new Double(0.0));
			} else if ("Currency".equalsIgnoreCase(name)) {
				if (!ServiceUtil.isEmpty(headerObj.getString("Value")))
					headerDto.setCurrency((headerObj.getString("Value")));
				else
					headerDto.setCurrency("SAR");
			} else if ("DeliveryNote".equalsIgnoreCase(name)) {
				headerDto.setDeliveryNote(headerObj.getString("Value"));
			}

			else if ("VendorId".equalsIgnoreCase(name)) {
				headerDto.setVendorId(headerObj.getString("Value"));
			} else if ("Name".equalsIgnoreCase(name)) {
				headerDto.setVendorName(headerObj.getString("Value"));
			} else if ("Street".equalsIgnoreCase(name)) {
				headerDto.setStreet(headerObj.getString("Value"));
			} else if ("ZIP".equalsIgnoreCase(name)) {
				headerDto.setZipCode(headerObj.getString("Value"));
			} else if ("Country".equalsIgnoreCase(name)) {
				headerDto.setCountryCode(headerObj.getString("Value"));
			} else if ("City".equalsIgnoreCase(name)) {
				headerDto.setCity(headerObj.getString("Value"));
			}

			else if ("VATID".equalsIgnoreCase(name) || "VATRegistrationNum".equalsIgnoreCase(name)) {
				headerDto.setVatRegNum(headerObj.getString("Value"));
			} else if ("BankAccount".equalsIgnoreCase(name)) {
				headerDto.setAccountNumber(headerObj.getString("Value"));
			}

			else if ("AmountBeforeTax".equalsIgnoreCase(name)) {
				if (!ServiceUtil.isEmpty(headerObj.getString("Value")))
					headerDto.setAmountBeforeTax(Double.valueOf(headerObj.getString("Value").replace(",", "")));
				else
					headerDto.setAmountBeforeTax(new Double(0.0));
			} else if ("PurchaseOrder".equalsIgnoreCase(name)) {
				headerDto.setRefpurchaseDoc(headerObj.getString("Value"));
			} else if ("TransactionType".equalsIgnoreCase(name)) {
				headerDto.setTransactionType(headerObj.getString("Value"));
			} else if ("ABBYY_Batch_Name".equalsIgnoreCase(name)) {
				headerDto.setOcrBatchId(headerObj.getString("Value"));
			} else if ("InvoiceType".equalsIgnoreCase(name)) {
				headerDto.setInvoiceType(headerObj.getString("Value"));
			} else if ("TaxPercentage".equalsIgnoreCase(name)) {
				if (!ServiceUtil.isEmpty(headerObj.getString("Value")))
					headerDto.setTaxPercentage(new Double(headerObj.getString("Value").replace(",", "")));
				else
					headerDto.setTaxPercentage(new Double(0.0));
			}

			else if ("TaxValue".equalsIgnoreCase(name)) {
				if (!ServiceUtil.isEmpty(headerObj.getString("Value")))
					headerDto.setTaxAmount(new Double(headerObj.getString("Value").replace(",", "")));
				else
					headerDto.setTaxAmount(new Double(0.0));
			} else if ("FreightValue".equalsIgnoreCase(name)) {
				if (!ServiceUtil.isEmpty(headerObj.getString("Value")))
					headerDto.setShippingCost(new Double(headerObj.getString("Value").replace(",", "")));
				else
					headerDto.setShippingCost(new Double(0.0));
			} else if ("Surcharge".equalsIgnoreCase(name)) {
				if (!ServiceUtil.isEmpty(headerObj.getString("Value")))
					headerDto.setSurcharge(new Double(headerObj.getString("Value").replace(",", "")));
				else
					headerDto.setSurcharge(new Double(0.0));
			} else if ("BUId".equalsIgnoreCase(name)) {
				if (!ServiceUtil.isEmpty(headerObj.getString("Value")))
					headerDto.setCompCode(headerObj.getString("Value"));
				else
					headerDto.setCompCode("1010");
			}

			headerDto.setIsRejected(Boolean.FALSE);
			headerDto.setChannelType("MAIL PDF");
			headerDto.setSysSusgestedTaxAmount(0.0);
			headerDto.setDiscount(0.0);
			headerDto.setInvoiceGross(0.0);
			headerDto.setUnplannedCost(0.0);
			headerDto.setPlannedCost(0.0);
			headerDto.setInvoiceStatus(ApplicationConstants.NEW_INVOICE);
			headerDto.setInvoiceStatusText("NEW");
		}
		headerDto.setInvoiceItems(itemsDtoList);
		return headerDto;

	}

	public static void main(String[] args) {
		System.out.println(new Double("8,511.00".replace(",", "")));
	}

}
