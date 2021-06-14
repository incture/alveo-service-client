package com.ap.menabev.abbyy;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ap.menabev.dto.InvoiceHeaderDetailsDto;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.InvoiceItemDto;
import com.ap.menabev.util.ItemDto;
import com.ap.menabev.util.ServiceUtil;

public class ABBYYJSONConverter {
	public static InvoiceHeaderDto abbyyJSONOutputToInvoiceObject(JSONObject jsonObject) throws Exception{
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
								if ("Position".equalsIgnoreCase(itemName) && invoiceItem.has("Value") && !ServiceUtil.isEmpty(invoiceItem.get("Value")))
									itemDto.setExtItemId(invoiceItem.getString("Value"));
								else if ("ArticleNumber".equalsIgnoreCase(itemName) && invoiceItem.has("Value") && !ServiceUtil.isEmpty(invoiceItem.get("Value")))
									itemDto.setArticleNum(invoiceItem.getString("Value"));
								else if ("OrderNumber".equalsIgnoreCase(itemName) && invoiceItem.has("Value") && !ServiceUtil.isEmpty(invoiceItem.get("Value")))
									itemDto.setRefDocNum(new Long((invoiceItem.getString("Value")==""?null:invoiceItem.getString("Value"))==null?0:Long.valueOf(invoiceItem.getString("Value"))));
								else if ("MaterialNumber".equalsIgnoreCase(itemName) && invoiceItem.has("Value") && !ServiceUtil.isEmpty(invoiceItem.get("Value")))
									itemDto.setPoMaterialNum(invoiceItem.getString("Value"));
								else if ("Description".equalsIgnoreCase(itemName) && invoiceItem.has("Value") && !ServiceUtil.isEmpty(invoiceItem.get("Value")))
									itemDto.setItemText(invoiceItem.getString("Value"));
								else if ("Quantity".equalsIgnoreCase(itemName) && invoiceItem.has("Value") && !ServiceUtil.isEmpty(invoiceItem.get("Value")))
									itemDto.setInvQty(String.valueOf(invoiceItem.getInt("Value")));
								else if ("UnitPrice".equalsIgnoreCase(itemName) && invoiceItem.has("Value") && !ServiceUtil.isEmpty(invoiceItem.get("Value")))
									itemDto.setPricingUnit(invoiceItem.getInt("Value"));
								else if ("DiscountPercentage".equalsIgnoreCase(itemName) && invoiceItem.has("Value") && !ServiceUtil.isEmpty(invoiceItem.get("Value")))
									itemDto.setDisAmt(String.valueOf(invoiceItem.getInt("Value")));
								else if ("DiscountValue".equalsIgnoreCase(itemName) && invoiceItem.has("Value") && !ServiceUtil.isEmpty(invoiceItem.get("Value")))
									itemDto.setDisPer(String.valueOf(invoiceItem.getInt("Value")));
								else if ("TotalPriceNetto".equalsIgnoreCase(itemName) && invoiceItem.has("Value") && !ServiceUtil.isEmpty(invoiceItem.get("Value")))
									itemDto.setPrice(String.valueOf(invoiceItem.getInt("Value")));
								else if ("VATPercentage".equalsIgnoreCase(itemName) && invoiceItem.has("Value") && !ServiceUtil.isEmpty(invoiceItem.get("Value")))
									itemDto.setTaxPer(invoiceItem.getInt("Value"));
								else if ("VATValue".equalsIgnoreCase(itemName) && invoiceItem.has("Value") && !ServiceUtil.isEmpty(invoiceItem.get("Value")))
									itemDto.setTaxAmt(invoiceItem.getInt("Value"));

							}

							itemsDtoList.add(itemDto);
						}
					} else if ("InvoiceNumber".equalsIgnoreCase(name)) {
						headerDto.setExtInvNum(headerObj.getString("Value"));
					}
					// else if("InvoiceDate".equalsIgnoreCase(name)){
					// headerDto.setInvoiceDate(headerObj.getString("Value"));
					// }
					else if ("Total".equalsIgnoreCase(name)) {
						// headerDto.setInvoiceTotal(String.valueOf(headerObj.getInt("Value")));
					} else if ("Currency".equalsIgnoreCase(name)) {
						headerDto.setCurrency(headerObj.getString("Value"));
					} else if ("DeliveryNote".equalsIgnoreCase(name)) {
						headerDto.setDeliveryNote(headerObj.getString("Value"));
					}

					else if ("VendorId".equalsIgnoreCase(name)) {
						headerDto.setVendorId(headerObj.getString("Value"));
					} else if ("Name".equalsIgnoreCase(name)) {
						headerDto.setVendorName(headerObj.getString("Value"));
					} else if ("VATID".equalsIgnoreCase(name)) {
						headerDto.setVatRegNum(headerObj.getString("Value"));
					} else if ("BankAccount".equalsIgnoreCase(name)) {
						headerDto.setAccountNumber(headerObj.getString("Value"));
					}

					else if ("TotalNetAmount".equalsIgnoreCase(name)) {
						headerDto.setAmountBeforeTax(String.valueOf(headerObj.getInt("Value")));
					}

				}
				headerDto.setInvoiceItems(itemsDtoList);
				return headerDto;
			

		

		
	}

}
