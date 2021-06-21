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
		var oVisibilityModel = oController.oVisibilityModel;
		var sUrl = "/menabevdev/invoiceHeader/getInvoiceByReqId/" + requestId;
		var oServiceModel = new sap.ui.model.json.JSONModel();
		var busy = new sap.m.BusyDialog();
		var oHeader = {
			"Content-Type": "application/scim+json"
		};
		busy.open();
		oServiceModel.loadData(sUrl, "", true, "GET", false, false, oHeader);
		oServiceModel.attachRequestCompleted(function (oEvent) {
			busy.close();
			var oData = oEvent.getSource().getData();
			oPOModel.setProperty("/", oData);
			if (oData.invoicePdfId) {
				oVisibilityModel.setProperty("/openPdfBtnVisible", true);
			} else {
				oVisibilityModel.setProperty("/openPdfBtnVisible", false);
			}

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

	onVendorNameChange: function (oEvent, oController) {
		var oPOModel = oController.oPOModel;
		var sVendorName = oEvent.getParameter("selectedItem").getProperty("text"),
			sVendorId = oEvent.getParameter("selectedItem").getProperty("additionalText");
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

	onSubmit: function (oEvent, oController, MandatoryFileds, type) {
		var oPOModel = oController.oPOModel;
		var manLength = MandatoryFileds.length;
		var data, count = 0;
		for (var i = 0; i < manLength; i++) {
			data = oPOModel.getProperty("/" + MandatoryFileds[i]);
			if (data) {
				oPOModel.setProperty("/NonPO/" + MandatoryFileds[i] + "State", "None");
			} else {
				count += 1;
				oPOModel.setProperty("/NonPO/" + MandatoryFileds[i] + "State", "Error");
			}
		}
		oPOModel.refresh();
		if (count) {
			// message = resourceBundle.getText("MANDATORYFIELDERROR");
			var message = "Please fill all Mandatory fields";
			sap.m.MessageToast.show(message);
		}
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

	hdrInvAmtCalu: function (oEvent, oController) {
		var oPOModel = oController.oPOModel,
			oMandatoryModel = oController.oMandatoryModel,
			invoiceAmt = oEvent.getParameter("value");
		if (!invoiceAmt) {
			oMandatoryModel.setProperty("/NonPO/invoiceAmountState", "Error");
		} else {
			oMandatoryModel.setProperty("/NonPO/invoiceAmountState", "None");
			invoiceAmt = (parseFloat(invoiceAmt)).toFixed(2);
			oPOModel.setProperty("/invoiceAmount", invoiceAmt);
		}
		this.calculateBalance();
	},

	calculateBalance: function (oEvent, oController) {
		var oPOModel = oController.oPOModel,
			that = this,
			invAmt = oPOModel.getProperty("/invoiceTotal"),
			grossAmount = oPOModel.getProperty("/grossAmount");
		var balance = that.nanValCheck(invAmt) - that.nanValCheck(grossAmount);
		balance = that.nanValCheck(balance).toFixed(2);
		oPOModel.setProperty("/balance", balance);
		oPOModel.refresh();
		this.onHeaderChange(oEvent, oController, "isInvoiceAmountChanged");
	},
	nanValCheck: function (value) {
		if (!value || isNaN(value)) {
			return 0;
		} else if (Number(value) === 0 || parseFloat(value) === -0) {
			return 0;
		} else if (!isNaN(value)) {
			return parseFloat(value);
		}
		return value;
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
	},

	onNonPoSave: function (oEvent, oController, status) {
		var oPOModel = oController.oPOModel;
		var oSaveData = oPOModel.getData();
		oSaveData.invoiceHeaderDto.taskOwner = this.oUserDetailModel.getProperty("/loggedInUserMail");
		oSaveData.invoiceHeaderDto.docStatus = "Draft";
		var sUrl = "/menabevdev/invoiceHeader/accountantSave",
			sMethod = "POST";
		this.saveSubmitServiceCall(oController, oSaveData, sMethod, sUrl, "SAVE");
	},

	//Called on click of Submit Button.
	onNonPoSubmit: function (oEvent, oController, MandatoryFileds) {
		var oPOModel = oController.oPOModel;
		var oSubmitData = oPOModel.getData();
		var bCostAllocation = false;
		//Header Mandatory feilds validation

		var manLength = MandatoryFileds.length;
		var data, count = 0;
		for (var i = 0; i < manLength; i++) {
			data = oPOModel.getProperty("/" + MandatoryFileds[i]);
			if (data) {
				oPOModel.setProperty("/NonPO/" + MandatoryFileds[i] + "State", "None");
			} else {
				count += 1;
				oPOModel.setProperty("/NonPO/" + MandatoryFileds[i] + "State", "Error");
			}
		}
		oPOModel.refresh();
		if (count) {
			// message = resourceBundle.getText("MANDATORYFIELDERROR");
			var message = "Please fill all Mandatory fields";
			sap.m.MessageToast.show(message);
			return;
		} else {
			if (oSubmitData.costAllocation.length > 0) {
				bCostAllocation = true;
			} else {
				bCostAllocation = false;
				sap.m.MessageBox.error("Please Enter Cost Allocation Details!");
			}
		}

		if (!count && bCostAllocation) {
			//COST ALLOCATION VALIDATION START 
			var costAllocation = $.extend(true, [], oPOModel.getProperty("/costAllocation"));
			var bflag = true;
			for (var i = 0; i < costAllocation.length; i++) {
				var bValidate = false;
				if (!costAllocation[i].glAccount || costAllocation[i].glError === "Error") {
					bValidate = true;
					costAllocation[i].glError = "Error";
				}
				if (!costAllocation[i].netValue || costAllocation[i].amountError === "Error") {
					bValidate = true;
					costAllocation[i].amountError = "Error";
				}
				if (!costAllocation[i].costCenter || costAllocation[i].costCenterError === "Error") {
					bValidate = true;
					costAllocation[i].costCenterError = "Error";
				}
				if (!costAllocation[i].itemText || costAllocation[i].itemTextError === "Error") {
					bValidate = true;
					costAllocation[i].itemTextError = "Error";
				}
				if (bValidate) {
					bflag = false;
					continue;
				}
			}
			if (!bflag) {
				oPOModel.setProperty("/costAllocation", costAllocation);
				var sMsg = "Please Enter Required Fields G/L Account,Amount,Cost Center & Text!";
				sap.m.MessageBox.error(sMsg);
				return;
			} else {
				//COST ALLOCATION VALIDATION END
				oSubmitData.invoiceHeaderDto.taskOwner = this.oUserDetailModel.getProperty("/loggedInUserMail");
				oSubmitData.invoiceHeaderDto.docStatus = "Created";
				var balance = oSubmitData.invoiceHeaderDto.balance;
				if (this.nanValCheck(balance) !== 0) {
					sap.m.MessageBox.error("Balance is not 0");
					return;
				}
				var totalTax = oPOModel.getProperty("/taxAmount");
				var userInputTaxAmount = oPOModel.getProperty("/taxValue");
				var taxDifference = this.nanValCheck(userInputTaxAmount) - this.nanValCheck(totalTax);
				if (this.nanValCheck(taxDifference) !== 0) {
					sap.m.MessageBox.error("Tax MisMatched");
					return;
				}

				var sUrl = "/menabevdev/invoiceHeader/accountantSubmit",
					sMethod = "POST";
				this.saveSubmitServiceCall(oSubmitData, sMethod, sUrl);
			}
		}

	},

	//function saveSubmitServiceCall is triggered on SUBMIT or SAVE
	saveSubmitServiceCall: function (oController, oData, sMethod, sUrl, save) {
		var that = this;
		var oPOModel = oController.oPOModel;
		var busy = new sap.m.BusyDialog();

		var oServiceModel = new sap.ui.model.json.JSONModel();
		var busy = new sap.m.BusyDialog();
		var oHeader = {
			"Content-Type": "application/scim+json"
		};
		oServiceModel.loadData(sUrl, JSON.stringify(oData), true, sMethod, false, false, oHeader);
		oServiceModel.attachRequestCompleted(function (oEvent) {
			busy.close();
			var oData = oEvent.getSource().getData();
			var ReqId = oData.requestId;
			oPOModel.setProperty("/requestId", ReqId);
			var message;
			if (oData.status === 200) {
				sap.m.MessageBox.success(message, {
					actions: [sap.m.MessageBox.Action.OK],
					onClose: function (sAction) {
						if (!save) {
							that.router.navTo("Inbox");
						}
					}
				});
			} else {
				sap.m.MessageBox.information(message, {
					actions: [sap.m.MessageBox.Action.OK]
				});
			}
		});
		oServiceModel.attachRequestFailed(function (oEvent) {
			busy.close();
			var oData = oEvent.getSource().getData();
			var errorMsg = "";
			if (oData.status === 504) {
				errorMsg = "Request timed-out. Please refresh your page";
				// this.errorMsg(errorMsg);
			} else {
				// errorMsg = data;
				// this.errorMsg(errorMsg);
			}
		});
	},


};