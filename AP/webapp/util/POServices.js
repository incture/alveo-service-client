jQuery.sap.declare("com.menabev.AP.util.POServices");
com.menabev.AP.util.POServices = {
	onTransactionChange: function (oEvent, oController) {
		var oPOModel = oController.oPOModel;
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
			"vendorID": "v2",
			"companyCode": oPOModel.companyCode,
			"invoiceReference": oPOModel.invoiceRefNum,
			"invoiceAmount": oPOModel.invoiceAmount,
			"grossAmount": oPOModel.grossAmount,
			"taxAmount": oPOModel.taxAmount,
			"balanceAmount": oPOModel.balanceAmount,
			"taxCode": oPOModel.headerTaxcode,
			"currency": oPOModel.currency,
			"invoiceDate": oPOModel.documentDate,
			"postingDate": oPOModel.postingDate,
			"baselineDate": oPOModel.baselineDate,
			"dueDate": oPOModel.dueDate,
			"paymentTerms": oPOModel.paymentTerms,
			"invoiceStatus": oPOModel.invoiceStatus,
			"invoiceType": oPOModel.invoiceType,
			"discountedDueDate1": null,
			"discountedDueDate2": null,
			"systemSuggestedtaxAmount": oPOModel.systemSuggestedtaxAmount,
			"changeIndicator": changeIndicator
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