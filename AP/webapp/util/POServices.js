jQuery.sap.declare("com.menabev.AP.util.POServices");
com.menabev.AP.util.POServices = {
	onTransactionChange: function (oEvent, oController) {
		var oPOModel = oController.oPOModel;
	},

	VendorIdSuggest: function (oEvent, oController) {
		var oDataAPIModel = oController.oDataAPIModel;
		var oDropDownModel = oController.oDropDownModel;
		var value = oEvent.getParameter("suggestValue");
		if (value && value.length > 2) {
			var url = "/A_Supplier?$format=json&$filter=substringof('" + value + "',Supplier) eq true";
			oDataAPIModel.read(url, {
				success: function (oData, header) {
					var data = oData.d.results;
					oDropDownModel.setProperty("/VendorIdSuggest", data);
				},
				error: function (oData) {}
			});
		}
	},

	getPONonPOData: function (oEvent, oController, requestId) {
		var oPOModel = oController.oPOModel;
		var sUrl = "/invoiceHeader/getInvoiceByReqId/" + requestId;
		var oServiceModel = new sap.ui.model.json.JSONModel();
		var busy = new sap.m.BusyDialog();
		var oHeader = {
			"Content-Type": "application/scim+json"
		};
		busy.open();
		oServiceModel.loadData(sUrl, "", true, "GET", false, false, oHeader);
		oServiceModel.attachRequestCompleted(function (oEvent) {

			var oData = oEvent.getSource().getData().invoiceHeaderDto;
			oPOModel.setProperty("/",oData);
			// if (oData.messages.messageType === "E") {
			// 	sap.m.MessageBox.success(oData.messages.messageText, {
			// 		actions: [sap.m.MessageBox.Action.OK]
			// 	});
			// }
		});

	},

	vendorIdSelected: function (oEvent, oController) {
		var oPOModel = oController.oPOModel;
		var sVendorId = oEvent.getParameter("selectedItem").getProperty("text"),
			sVendorName = oEvent.getParameter("selectedItem").getProperty("additionalText");
		oPOModel.setProperty("/vendorName", sVendorName);
		oPOModel.setProperty("/vendorId", sVendorId);
		oPOModel.refresh();
		this.onHeaderChange(oEvent, oController, "isVendoIdChanged");
	},

	vendorNameSelected: function (oEvent, oController) {
		var oPOModel = oController.oPOModel;
		var sVendorId = oEvent.getParameter("selectedItem").getProperty("additionalText"),
			sVendorName = oEvent.getParameter("selectedItem").getProperty("text");
		oPOModel.setProperty("/vendorName", sVendorName);
		oPOModel.setProperty("/vendorId", sVendorId);
		oPOModel.refresh();
		this.onHeaderChange(oEvent, oController, "isVendoIdChanged");
	},

	onCompanyCodeChange: function (oEvent, oController) {
		this.onHeaderChange(oEvent, oController, "isCompanyCodeChanged");
	},
	onInvRefChange: function (oEvent, oController) {
		this.onHeaderChange(oEvent, oController, "isInvoiceRefChanged");
	},

	onCurrencyChange: function (oEvent, oController) {
		this.onHeaderChange(oEvent, oController, "isCurrencyChanged");
	},

	onInvDateChange: function (oEvent, oController) {
		var oPOModel = oController.oPOModel;
		var date = oEvent.getSource().getValue();
		var dateVal = new Date(date);
		var day = dateVal.getDate();
		if (!day) {
			sap.m.MessageToast.show("Please enter valid date");
			oEvent.getSource().setValue("");
			return;
		} else {
			date = dateVal.getTime();
		}
		oPOModel.setProperty("/documentDate", date);
		this.onHeaderChange(oEvent, oController, "isInvoiceDateChanged");
	},

	onBaseLineDateChange: function (oEvent, oController) {
		var oPOModel = oController.oPOModel;
		var date = oEvent.getSource().getValue();
		var dateVal = new Date(date);
		var day = dateVal.getDate();
		if (!day) {
			sap.m.MessageToast.show("Please enter valid date");
			oEvent.getSource().setValue("");
			return;
		} else {
			date = dateVal.getTime();
		}
		oPOModel.setProperty("/baselineDate", date);
		this.onHeaderChange(oEvent, oController, "isBaselineDateChanged");
	},
	onPostingDateChange: function (oEvent, oController) {
		var oPOModel = oController.oPOModel;
		var date = oEvent.getSource().getValue();
		var dateVal = new Date(date);
		var day = dateVal.getDate();
		if (!day) {
			sap.m.MessageToast.show("Please enter valid date");
			oEvent.getSource().setValue("");
			return;
		} else {
			date = dateVal.getTime();
		}
		oPOModel.setProperty("/postingDate", date);
	},
	onDueDateChange: function (oEvent, oController) {
		var oPOModel = oController.oPOModel;
		var date = oEvent.getSource().getValue();
		var dateVal = new Date(date);
		var day = dateVal.getDate();
		if (!day) {
			sap.m.MessageToast.show("Please enter valid date");
			oEvent.getSource().setValue("");
			return;
		} else {
			date = dateVal.getTime();
		}
		oPOModel.setProperty("/dueDate", date);
	},

	onPaymentTermsChange: function (oEvent, oController) {
		this.onHeaderChange(oEvent, oController, "isPaymentTermsChanged");
	},

	onPaymentMethodChange: function (oEvent, oController) {},
	onPaymentBlockChange: function (oEvent, oController) {},

	onInvTypeChange: function (oEvent, oController) {
		this.onHeaderChange(oEvent, oController, "isInvoiceTypeChanged");
	},

	onTaxCodeChange: function (oEvent, oController) {
		this.onHeaderChange(oEvent, oController, "isTaxCodeChanged");
	},

	onTaxAmtChange: function (oEvent, oController) {
		this.onHeaderChange(oEvent, oController, "isTaxAmountChanged");
	},

	onInvAmtChange: function (oEvent, oController) {
		this.onHeaderChange(oEvent, oController, "isInvoiceAmountChanged");
	},

	onHeaderChange: function (oEvent, oController, transaction) {
		var oPOModel = oController.oPOModel;
		var changeIndicator = oPOModel.getProperty("/changeIndicator");
		if (!changeIndicator) {
			changeIndicator = {
				"isHeaderChanged": null,
				"isVendoIdChanged": true,
				"isCompanyCodeChanged": null,
				"isInvoiceRefChanged": null,
				"isCurrencyChanged": null,
				"isInvoiceDateChanged": null,
				"isBaselineDateChanged": null,
				"isPaymentTermsChanged": null,
				"isInvoiceTypeChanged": null,
				"isTaxCodeChanged": null,
				"isTaxAmountChanged": null,
				"isInvoiceAmountChanged": null
			};
		}
		oPOModel.setProperty("/changeIndicator/isHeaderChanged", true);
		oPOModel.setProperty("/changeIndicator/" + transaction, true);
		var oPayload = {
			"requestID": "APA-210609-000001",
			"vendorID": oPOModel.getProperty("/vendorId"),
			"companyCode": oPOModel.getProperty("/companyCode"),
			"invoiceReference": oPOModel.getProperty("/invoiceRefNum"),
			"invoiceAmount": oPOModel.getProperty("/invoiceAmount"),
			"grossAmount": oPOModel.getProperty("/grossAmount"),
			"taxAmount": oPOModel.getProperty("/taxAmount"),
			"balanceAmount": oPOModel.getProperty("/balanceAmount"),
			"taxCode": oPOModel.getProperty("/headerTaxcode"),
			"currency": oPOModel.getProperty("/currency"),
			"invoiceDate": oPOModel.getProperty("/documentDate"),
			"postingDate": oPOModel.getProperty("/postingDate"),
			"baselineDate": oPOModel.getProperty("/baselineDate"),
			"dueDate": oPOModel.getProperty("/dueDate"),
			"paymentTerms": oPOModel.getProperty("/paymentTerms"),
			"invoiceStatus": oPOModel.getProperty("/invoiceStatus"),
			"invoiceType": oPOModel.getProperty("/invoiceType"),
			"discountedDueDate1": null,
			"discountedDueDate2": null,
			"systemSuggestedtaxAmount": oPOModel.getProperty("/systemSuggestedtaxAmount"),
			"changeIndicator": oPOModel.getProperty("/changeIndicator")
		};
		var sUrl = "/menabevdev/validate/header";
		var oServiceModel = new sap.ui.model.json.JSONModel();
		var busy = new sap.m.BusyDialog();
		var oHeader = {
			"Content-Type": "application/scim+json"
		};
		oServiceModel.loadData(sUrl, JSON.stringify(oPayload), true, "POST", false, false, oHeader);
		oServiceModel.attachRequestCompleted(function (oEvent) {
			var oData = oEvent.getSource().getData();
			changeIndicator = oData.changeIndicator;
			oPOModel.setProperty("/changeIndicator", changeIndicator);
			if (oData.messages.messageType === "E") {
				sap.m.MessageBox.success(oData.messages.messageText, {
					actions: [sap.m.MessageBox.Action.OK]
				});
			}
		});
	}
};