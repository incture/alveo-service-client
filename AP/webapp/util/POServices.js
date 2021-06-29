jQuery.sap.declare("com.menabev.AP.util.POServices");
com.menabev.AP.util.POServices = {
	onTransactionChange: function (oEvent, oController) {
		var oPOModel = oController.oPOModel;
		oController.errorHandlerselect(oEvent);
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
		// oController.errorHandlerInput(oEvent);
		var oPOModel = oController.oPOModel;
		var sVendorId = oEvent.getParameter("selectedItem").getProperty("text"),
			sVendorName = oEvent.getParameter("selectedItem").getProperty("additionalText");
		oPOModel.setProperty("/vendorName", sVendorName);
		oPOModel.setProperty("/vendorId", sVendorId);
		oPOModel.refresh();
		this.onHeaderChange(oEvent, oController, "isVendoIdChanged");
	},

	onVendorNameChange: function (oEvent, oController) {
		// oController.errorHandlerInput(oEvent);
		var oPOModel = oController.oPOModel;
		var sVendorName = oEvent.getParameter("selectedItem").getProperty("text"),
			sVendorId = oEvent.getParameter("selectedItem").getProperty("additionalText");
		oPOModel.setProperty("/vendorName", sVendorName);
		oPOModel.setProperty("/vendorId", sVendorId);
		oPOModel.refresh();
		this.onHeaderChange(oEvent, oController, "isVendoIdChanged");
	},

	vendorNameSelected: function (oEvent, oController) {
		// oController.errorHandlerInput(oEvent);
		var oPOModel = oController.oPOModel;
		var sVendorId = oEvent.getParameter("selectedItem").getProperty("additionalText"),
			sVendorName = oEvent.getParameter("selectedItem").getProperty("text");
		oPOModel.setProperty("/vendorName", sVendorName);
		oPOModel.setProperty("/vendorId", sVendorId);
		oPOModel.refresh();
		this.onHeaderChange(oEvent, oController, "isVendoIdChanged");
	},

	onCompanyCodeChange: function (oEvent, oController) {
		oController.errorHandlerInput(oEvent);
		this.onHeaderChange(oEvent, oController, "isCompanyCodeChanged");
	},

	onInvRefChange: function (oEvent, oController) {
		oController.errorHandlerInput(oEvent);
		this.onHeaderChange(oEvent, oController, "isInvoiceRefChanged");
	},

	onCurrencyChange: function (oEvent, oController) {
		oController.errorHandlerInput(oEvent);
		this.onHeaderChange(oEvent, oController, "isCurrencyChanged");
	},

	onInvDateChange: function (oEvent, oController) {
		oController.errorHandlerInput(oEvent);
		var oPOModel = oController.oPOModel;
		var date = oEvent.getSource().getValue();
		if (!date) {
			oEvent.getSource().setValue(null);
			return;
		}
		var dateVal = new Date(date);
		var day = dateVal.getDate();
		if (!day) {
			sap.m.MessageToast.show("Please enter valid date");
			oEvent.getSource().setValue(null);
			return;
		} else {
			date = dateVal.getTime();
		}
		oPOModel.setProperty("/invoiceDate", date);
		this.onHeaderChange(oEvent, oController, "isInvoiceDateChanged");
	},

	onBaseLineDateChange: function (oEvent, oController) {
		oController.errorHandlerInput(oEvent);
		var oPOModel = oController.oPOModel;
		var date = oEvent.getSource().getValue();
		if (!date) {
			oEvent.getSource().setValue(null);
			return;
		}
		var dateVal = new Date(date);
		var day = dateVal.getDate();
		if (!day) {
			sap.m.MessageToast.show("Please enter valid date");
			oEvent.getSource().setValue(null);
			return;
		} else {
			date = dateVal.getTime();
		}
		oPOModel.setProperty("/baseLineDate", date);
		this.onHeaderChange(oEvent, oController, "isBaselineDateChanged");
	},
	onPostingDateChange: function (oEvent, oController) {
		oController.errorHandlerInput(oEvent);
		var oPOModel = oController.oPOModel;
		var date = oEvent.getSource().getValue();
		if (!date) {
			oEvent.getSource().setValue(null);
			return;
		}
		var dateVal = new Date(date);
		var day = dateVal.getDate();
		if (!day) {
			sap.m.MessageToast.show("Please enter valid date");
			oEvent.getSource().setValue(null);
			return;
		} else {
			date = dateVal.getTime();
		}
		oPOModel.setProperty("/postingDate", date);
	},
	onDueDateChange: function (oEvent, oController) {
		oController.errorHandlerInput(oEvent);
		var oPOModel = oController.oPOModel;
		var date = oEvent.getSource().getValue();
		if (!date) {
			oEvent.getSource().setValue(null);
			return;
		}
		var dateVal = new Date(date);
		var day = dateVal.getDate();
		if (!day) {
			sap.m.MessageToast.show("Please enter valid date");
			oEvent.getSource().setValue(null);
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
		// oController.errorHandlerselect(oEvent);
		this.onHeaderChange(oEvent, oController, "isPaymentTermsChanged");
	},

	onPaymentMethodChange: function (oEvent, oController) {
		// oController.errorHandlerselect(oEvent);
	},
	onPaymentBlockChange: function (oEvent, oController) {
		// oController.errorHandlerselect(oEvent);
	},

	onInvTypeChange: function (oEvent, oController) {
		// oController.errorHandlerselect(oEvent);
		this.onHeaderChange(oEvent, oController, "isInvoiceTypeChanged");
	},

	onTaxCodeChange: function (oEvent, oController) {
		oController.errorHandlerselect(oEvent);
		this.onHeaderChange(oEvent, oController, "isTaxCodeChanged");
		this.onChangeHeaderTax(oEvent, oController);
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
			oPOModel.setProperty("/changeIndicator", {});
		}
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
			"companyCode": oPOModel.getProperty("/compCode"),
			"invoiceReference": oPOModel.getProperty("/extInvNum"),
			"invoiceAmount": oPOModel.getProperty("/invoiceTotal"),
			"grossAmount": oPOModel.getProperty("/grossAmount"),
			"taxAmount": oPOModel.getProperty("/taxAmount"),
			"balanceAmount": oPOModel.getProperty("/balanceAmount"),
			"taxCode": oPOModel.getProperty("/taxCode"),
			"currency": oPOModel.getProperty("/currency"),
			"invoiceDate": oPOModel.getProperty("/invoiceDate"),
			"postingDate": oPOModel.getProperty("/postingDate"),
			"baselineDate": oPOModel.getProperty("/baseLineDate"),
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
			oPOModel.setProperty("/vendorId", oData.vendorID);
			oPOModel.setProperty("/compCode", oData.companyCode);
			oPOModel.setProperty("/extInvNum", oData.invoiceReference);
			oPOModel.setProperty("/invoiceTotal", oData.invoiceAmount);
			oPOModel.setProperty("/grossAmount", oData.grossAmount);
			oPOModel.setProperty("/taxAmount", oData.taxAmount);
			oPOModel.setProperty("/balanceAmount", oData.balanceAmount);
			oPOModel.setProperty("/taxCode", oData.taxCode);
			oPOModel.setProperty("/currency", oData.currency);
			oPOModel.setProperty("/invoiceDate", oData.invoiceDate);
			oPOModel.setProperty("/postingDate", oData.postingDate);
			oPOModel.setProperty("/baseLineDate", oData.baselineDate);
			oPOModel.setProperty("/dueDate", oData.dueDate);
			oPOModel.setProperty("/paymentTerms", oData.paymentTerms);
			oPOModel.setProperty("/invoiceStatus", oData.invoiceStatus);
			oPOModel.setProperty("/invoiceType", oData.invoiceType);
			if (oData.messages && oData.messages.messageType === "E") {
				sap.m.MessageBox.success(oData.messages.messageText, {
					actions: [sap.m.MessageBox.Action.OK]
				});
			}
		});
	},

	onNonPoSave: function (oEvent, oController, status) {
		var oPOModel = oController.oPOModel;
		var oSaveData = oPOModel.getData();
		var sUrl = "/menabevdev/invoiceHeader/accountantSave",
			sMethod = "POST";
		this.saveSubmitServiceCall(oController, oSaveData, sMethod, sUrl, "SAVE", "Draft");
	},

	//Called on click of Submit Button.
	onNonPoSubmit: function (oEvent, oController, MandatoryFileds, actionCode) {
		var oPOModel = oController.oPOModel;
		var oMandatoryModel = oController.oMandatoryModel;
		var oSubmitData = oPOModel.getData();
		var bCostAllocation = false;
		//Header Mandatory feilds validation

		var manLength = MandatoryFileds.length;
		var data, count = 0;
		for (var i = 0; i < manLength; i++) {
			data = oPOModel.getProperty("/" + MandatoryFileds[i]);
			if (data) {
				oMandatoryModel.setProperty("/NonPO/" + MandatoryFileds[i] + "State", "None");
			} else {
				count += 1;
				oMandatoryModel.setProperty("/NonPO/" + MandatoryFileds[i] + "State", "Error");
			}
		}
		oMandatoryModel.refresh();
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
				if (!costAllocation[i].glAccount) {
					bValidate = true;
					costAllocation[i].glError = "Error";
				}
				if (!costAllocation[i].netValue) {
					bValidate = true;
					costAllocation[i].amountError = "Error";
				}
				if (!costAllocation[i].costCenter) {
					bValidate = true;
					costAllocation[i].costCenterError = "Error";
				}
				if (!costAllocation[i].itemText) {
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
				oSubmitData.taskOwner = oController.oUserDetailModel.getProperty("/loggedInUserMail");
				oSubmitData.docStatus = "Created";
				var balanceAmount = oSubmitData.balanceAmount;
				if (this.nanValCheck(balanceAmount) !== 0) {
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
				if (oSubmitData.invoiceType == "PO") {
					var sUrl = "/menabevdev/invoiceHeader/accountant/invoiceSubmit",
						sMethod = "POST";
					this.onAccSubmit(oController, oSubmitData, sMethod, sUrl, actionCode, "openFrag");
				} else {
					var sUrl = "/menabevdev/invoiceHeader/accountantSubmit",
						sMethod = "POST";
					this.saveSubmitServiceCall(oController, oSubmitData, sMethod, sUrl);
				}

			}
		}

	},

	//function saveSubmitServiceCall is triggered on SUBMIT or SAVE
	saveSubmitServiceCall: function (oController, oData, sMethod, sUrl, save, draft) {
		var that = this;
		var oPOModel = oController.oPOModel;
		var oServiceModel = new sap.ui.model.json.JSONModel();
		var busy = new sap.m.BusyDialog();
		var oHeader = {
			"Content-Type": "application/scim+json"
		};
		if (!oData.invoiceTotal) {
			oData.invoiceTotal = 0;
		}
		var oPayload = {
			"invoiceHeaderDto": oData
		};
		oPayload.invoiceHeaderDto.taskOwner = oController.oUserDetailModel.getProperty("/loggedInUserMail");
		if (draft) {
			oPayload.invoiceHeaderDto.docStatus = "Draft";
		}
		busy.open();
		oServiceModel.loadData(sUrl, JSON.stringify(oPayload), true, sMethod, false, false, oHeader);
		oServiceModel.attachRequestCompleted(function (oEvent) {
			busy.close();
			var oData = oEvent.getSource().getData();
			var message = oData.responseStatus;
			if (oData.status === 200) {
				var ReqId = oData.invoiceHeaderDto.requestId;
				oPOModel.setProperty("/", oData.invoiceHeaderDto);
				sap.m.MessageBox.success(message, {
					actions: [sap.m.MessageBox.Action.OK],
					onClose: function (sAction) {
						if (!save) {
							oController.oRouter.navTo("Inbox");
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

	//onChange Header level tax
	onChangeHeaderTax: function (oEvent, oController) {
		var taxDetails = oEvent.getParameters().selectedItem.getBindingContext("oDropDownModel").getObject();
		var oPOModel = oController.oPOModel,
			taxCode = taxDetails.TxCode,
			taxPercentage = taxDetails.TaxRate;
		oPOModel.setProperty("/taxPercentage", taxPercentage);
		oPOModel.setProperty("/taxConditionType", taxDetails.TaxConditionType);
		if (oPOModel.getProperty("/costAllocation")) {
			var length = oPOModel.getProperty("/costAllocation").length;
			for (var i = 0; i < length; i++) {
				oPOModel.setProperty("/costAllocation/" + i + "/taxCode", taxCode);
				oPOModel.setProperty("/costAllocation/" + i + "/taxPer", taxPercentage);
				oPOModel.setProperty("/costAllocation/" + i + "/taxConditionType", taxDetails.TaxConditionType);
			}
		}
		if (taxDetails) {
			oController.oMandatoryModel.setProperty("/NonPO/taxCodeState", "None");
		} else {
			oController.oMandatoryModel.setProperty("/NonPO/taxCodeState", "Error");
		}
		this.costAllocationTaxCalc(oEvent, oController);
		sap.m.MessageToast.show("Tax Code\r" + taxCode + "\r is applied to all Cost Allocation line items");
	},

	//function costAllocationTaxCalc is used to calculate tax in cost allocation table
	costAllocationTaxCalc: function (oEvent, oController) {
		var oPOModel = oController.oPOModel,
			that = this;
		var totalTax = 0,
			totalBaseRate = 0;
		if (oPOModel.getProperty("/costAllocation")) {
			var length = oPOModel.getProperty("/costAllocation").length;
			for (var i = 0; i < length; i++) {
				//Tax Calculations
				var taxPer = oPOModel.getProperty("/costAllocation/" + i + "/taxPer"),
					netValue = oPOModel.getProperty("/costAllocation/" + i + "/netValue");

				var baseRate = that.nanValCheck(netValue) / (1 + (that.nanValCheck(taxPer) / 100));
				var taxValue = (that.nanValCheck(baseRate) * that.nanValCheck(taxPer)) / 100;
				totalTax += Number(taxValue.toFixed(2));
				oPOModel.setProperty("/costAllocation/" + i + "/taxValue", that.nanValCheck(taxValue).toFixed(2));
				oPOModel.setProperty("/costAllocation/" + i + "/baseRate", that.nanValCheck(baseRate).toFixed(2));

				//End of Tax Calulations
				if (baseRate && oPOModel.getProperty("/costAllocation/" + i +
						"/crDbIndicator") === "H") {
					totalBaseRate += that.nanValCheck(oPOModel.getProperty("/costAllocation/" + i + "/baseRate"));

				} else if (baseRate && oPOModel.getProperty("/costAllocation/" + i +
						"/crDbIndicator") === "S") {
					totalBaseRate -= that.nanValCheck(oPOModel.getProperty("/costAllocation/" + i + "/baseRate"));
				}
			}
			totalBaseRate = that.nanValCheck(totalBaseRate).toFixed(2);
			totalTax = this.nanValCheck(totalTax).toFixed(2);
			oPOModel.setProperty("/totalBaseRate", totalBaseRate);
			oPOModel.setProperty("/taxAmount", totalTax);
			this.calculateGrossAmount(oEvent, oController);
			this.calculateBalance(oEvent, oController);
			oPOModel.refresh();
		}
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
			oPOModel.setProperty("/invoiceTotal", invoiceAmt);
		}
		this.calculateBalance(oEvent, oController);
	},

	calculateBalance: function (oEvent, oController) {
		var oPOModel = oController.oPOModel,
			that = this,
			invAmt = oPOModel.getProperty("/invoiceTotal"),
			grossAmount = oPOModel.getProperty("/grossAmount");
		var balanceAmount = that.nanValCheck(invAmt) - that.nanValCheck(grossAmount);
		balanceAmount = that.nanValCheck(balanceAmount).toFixed(2);
		oPOModel.setProperty("/balanceAmount", balanceAmount);
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

	calculateGrossAmount: function (oEvent, oController) {
		var oPOModel = oController.oPOModel,
			that = this,
			totalBaseRate = oPOModel.getProperty("/totalBaseRate"),
			userInputTaxAmount = oPOModel.getProperty("/taxValue");
		var grossAmount = that.nanValCheck(totalBaseRate) + that.nanValCheck(userInputTaxAmount);
		grossAmount = that.nanValCheck(grossAmount).toFixed(2);
		oPOModel.setProperty("/grossAmount", grossAmount);
		oPOModel.refresh();
	},

	calSysSuggTaxAmt: function (oEvent, oController) {
		var oPOModel = oController.oPOModel;
		var itemDetails = oPOModel.getProperty("/invoiceItems");
		var length = itemDetails.length;
		var sysSuggestTax = 0,
			taxPercentage, basePrice, lineTax;
		for (var i = 0; i < length; i++) {
			basePrice = itemDetails[i].basePrice;
			taxPercentage = itemDetails[i].taxPercentage;
			lineTax = this.nanValCheck(basePrice) * (this.nanValCheck(taxPercentage) / 100);
			sysSuggestTax += lineTax;
		}
		oPOModel.setProperty("/taxAmount", sysSuggestTax);
	},
	calculateLineItemNet: function (oEvent, oController) {
		var oPOModel = oController.oPOModel;
		var itemDetails = oPOModel.getProperty("/invoiceItems");
		var length = itemDetails.length,
			grossPrice;
		var sysSuggestTax = 0,
			taxPer, basePrice, lineTax;
		for (var i = 0; i < length; i++) {
			var grossPrice = itemDetails[i].grossPrice;
			lineTax = this.nanValCheck(grossPrice) * (this.nanValCheck(taxPer) / 100);
			sysSuggestTax += lineTax;
		}
		oPOModel.setProperty("/taxAmount", sysSuggestTax);
	},

	onItemTaxCodeChange: function (oEvent, oController) {
		var oPOModel = oController.oPOModel;
		var selectedItem = oEvent.getParameters().selectedItem.getBindingContext("oDropDownModel").getObject();
		var oContextObj = oEvent.getSource().getBindingContext("oDropDownModel").getObject();
		var taxCode = selectedItem.TxCode;
		var taxPercentage = selectedItem.TaxRate;
		var sPath = oEvent.getSource().getBindingContext("oDropDownModel").getPath();
		oPOModel.setProperty(sPath + "/taxCode", taxCode);
		oPOModel.setProperty(sPath + "/taxPercentage", taxPercentage);
	},

	// Item level calculation
	onInvQtyChange: function (oEvent, oController) {
		var oPOModel = oController.oPOModel;
		var oContextObj = oEvent.getSource().getBindingContext("oUserDetailModel").getObject();
		var Quantity = oContextObj.invQty,
			unitPrice = oContextObj.unitPrice,
			taxPer = oContextObj.taxPercentage;
		this.calSysSuggTaxAmt();
		this.calculateBalance();
		this.calculateGrossAmount();
	},

	onAccSubmit: function (oController, oData, sMethod, sUrl, actionCode, method) {
		var that = this;
		var oPOModel = oController.oPOModel;
		var oServiceModel = new sap.ui.model.json.JSONModel();
		var busy = new sap.m.BusyDialog();
		var oHeader = {
			"Content-Type": "application/scim+json"
		};
		if (!oData.invoiceTotal) {
			oData.invoiceTotal = 0;
		}
		var oPayload = {
			"invoice": oData,
			"requestId": oData.requestId,
			"taskId": oData.taskId,
			"actionCode": actionCode
		};
		busy.open();
		oServiceModel.loadData(sUrl, JSON.stringify(oPayload), true, sMethod, false, false, oHeader);
		oServiceModel.attachRequestCompleted(function (oEvent) {
			busy.close();
			var oData = oEvent.getSource().getData();
			var message = oData.responseStatus;
			if (oData.status === 200) {
				var ReqId = oData.invoiceHeaderDto.requestId;
				oPOModel.setProperty("/", oData.invoiceHeaderDto);
				if (actionCode === "ASR") {
					oController.onSubmitForRemediationFrag();
				} else if (actionCode === "ASA") {
					oController.onSubmitForApprovalFrag();
				}
				// sap.m.MessageBox.success(message, {
				// 	actions: [sap.m.MessageBox.Action.OK],
				// 	onClose: function (sAction) {
				// 		oController.oRouter.navTo("Inbox");
				// 	}
				// });
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
	}

};