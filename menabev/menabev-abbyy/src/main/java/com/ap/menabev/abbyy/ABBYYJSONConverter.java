package com.ap.menabev.abbyy;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ap.menabev.dto.InvoiceHeaderDetailsDto;

public class ABBYYJSONConverter {
public static InvoiceHeaderDetailsDto abbyyJSONOutputToInvoiceObject(JSONObject jsonObject){
		
		try {
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
						
				if(headerObj.getString("$type").equalsIgnoreCase("number")){
					if(headerObj.get("Value") instanceof Double){
						
					}else{
						
					}
				}else if(headerObj.getString("$type").equalsIgnoreCase("Text")){
					valueText = headerObj.getString("Value");
				}else{
					
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return null;
	}
	
}
