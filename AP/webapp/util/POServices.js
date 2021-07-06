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
		var that = this;
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
			if (oEvent.getParameters().success) {
				var oData = oEvent.getSource().getData();
				oPOModel.setProperty("/", oData);
				if (oData.invoicePdfId) {
					oVisibilityModel.setProperty("/openPdfBtnVisible", true);
				} else {
					oVisibilityModel.setProperty("/openPdfBtnVisible", false);
				}
				var invoiceItems = oPOModel.getProperty("/invoiceItems");
				var arrUniqueInvoiceItems = oController.fnFindUniqueInvoiceItems(invoiceItems);
				oController.getReferencePo(arrUniqueInvoiceItems);
				that.formatUOMList(oData.invoiceItems, oController);
			} else if (oEvent.getParameters().errorobject.statusCode == 401) {
				var message = "Session Lost. Press OK to refresh the page";
				sap.m.MessageBox.information(message, {
					styleClass: "sapUiSizeCompact",
					actions: [sap.m.MessageBox.Action.OK],
					onClose: function (sAction) {
						location.reload(true);
					}
				});
			} else if (oEvent.getParameters().errorobject.statusCode == 400 || oEvent.getParameters().errorobject.statusCode == 404) {
				var message = "Service Unavailable. Please try after sometime";
				sap.m.MessageBox.information(message, {
					styleClass: "sapUiSizeCompact",
					actions: [sap.m.MessageBox.Action.OK]
				});
			} else if (oEvent.getParameters().errorobject.statusCode == 500) {
				var message = "Service Unavailable. Please contact administrator";
				sap.m.MessageBox.information(message, {
					styleClass: "sapUiSizeCompact",
					actions: [sap.m.MessageBox.Action.OK]
				});
			}
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
		this.setChangeInd(oEvent, oController, "headerChange");
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
		this.setChangeInd(oEvent, oController, "headerChange");
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
		this.setChangeInd(oEvent, oController, "headerChange");
	},
	onPaymentBlockChange: function (oEvent, oController) {
		this.setChangeInd(oEvent, oController, "headerChange");
	},

	setChangeInd: function (oEvent, oController, changeInd) {
		var oPOModel = oController.oPOModel;
		var oVisibilityModel = oController.oVisibilityModel;
		var changeIndicator = oPOModel.getProperty("/changeIndicator");
		if (!changeIndicator) {
			oPOModel.setProperty("/changeIndicators", {});
		}
		var headerChangeIndicator = oVisibilityModel.getProperty("/changeIndicator");
		if (!headerChangeIndicator) {
			oVisibilityModel.setProperty("/changeIndicators", {});
		}
		oPOModel.setProperty("/changeIndicators/headerChange", true);
		oVisibilityModel.setProperty("/changeIndicators/" + changeInd, true);
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
		var oVisibilityModel = oController.oVisibilityModel;
		var changeIndicator = oPOModel.getProperty("/changeIndicator");
		if (!changeIndicator) {
			oPOModel.setProperty("/changeIndicators", {});
		}
		var headerChangeIndicator = oVisibilityModel.getProperty("/changeIndicator");
		if (!headerChangeIndicator) {
			oVisibilityModel.setProperty("/changeIndicators", {});
		}
		oPOModel.setProperty("/changeIndicators/headerChange", true);
		oVisibilityModel.setProperty("/changeIndicators/" + transaction, true);
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
			"changeIndicator": oVisibilityModel.getProperty("/changeIndicators")
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
			oVisibilityModel.setProperty("/changeIndicators", changeIndicator);
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
		// oSaveData.request_created_by = 
		var sUrl = "/menabevdev/invoiceHeader/saveAPI",
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
				if (!oSubmitData.invoiceTotal) {
					oSubmitData.invoiceTotal = 0;
				}
				var sMethod = "POST";
				var oPayload = {
					"invoiceHeaderDto": oSubmitData
				};
				var sUrl;
				oPayload.invoiceHeaderDto.taskOwner = oController.oUserDetailModel.getProperty("/loggedInUserMail");
				if (oSubmitData.invoiceType == "PO") {
					sUrl = "/menabevdev/invoiceHeader/accountant/invoiceSubmit";
					oPayload.invoiceHeaderDto.purchaseOrder = jQuery.extend(true, [], oData.purchaseOrder);
					this.onAccSubmit(oController, oPayload, sMethod, sUrl, actionCode);
				} else {
					var sUrl = "/menabevdev/invoiceHeader/accountantSubmit";
					this.saveSubmitServiceCall(oController, oPayload, sMethod, sUrl);
				}

			}
		}

	},
	onPoSubmit: function (oEvent, oController, MandatoryFileds, actionCode, sUrl) {
		var oPOModel = oController.oPOModel;
		var oMandatoryModel = oController.oMandatoryModel;
		var oData = oPOModel.getData();
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
		}
		if (!oData.invoiceTotal) {
			oData.invoiceTotal = 0;
		}
		var status = oData.invoiceStatus;
		if (actionCode == "ASR" && (status < 4 || status > 12)) {
			sap.m.MessageToast.show("Please check invoice status before sending for remidiation");
			return;
		} else if (actionCode === "ASA" && status != 16) {
			sap.m.MessageToast.show("Invoice status should be ready to post to send it for approval");
			return;
		}
		var sMethod = "POST";
		var oPayload = {
			"invoice": oData,
			"requestId": oData.requestId,
			"taskId": oController.taskId,
			"actionCode": actionCode,
			"purchaseOrders": jQuery.extend(true, [], oData.purchaseOrders)
		};
		oPayload.invoice.taskOwner = oController.oUserDetailModel.getProperty("/loggedInUserMail");
		this.onAccSubmit(oController, oPayload, sMethod, sUrl, actionCode);

	},

	//function saveSubmitServiceCall is triggered on SUBMIT or SAVE
	saveSubmitServiceCall: function (oController, oData, sMethod, sUrl, save) {
		var that = this;
		var oPOModel = oController.oPOModel;
		var oServiceModel = new sap.ui.model.json.JSONModel();
		var busy = new sap.m.BusyDialog();
		var oHeader = {
			"Content-Type": "application/scim+json"
		};
		// var oPayload = {
		// 	"invoiceHeaderDto": oData
		// };
		// oPayload.invoiceHeaderDto.taskOwner = oController.oUserDetailModel.getProperty("/loggedInUserMail");
		// if (draft) {
		// 	oPayload.invoiceHeaderDto.docStatus = "Draft";
		// }
		busy.open();
		oServiceModel.loadData(sUrl, JSON.stringify(oData), true, sMethod, false, false, oHeader);
		oServiceModel.attachRequestCompleted(function (oEvent) {
			busy.close();
			var oData = oEvent.getSource().getData();
			var message = oData.responseStatus;
			if (oEvent.getParameters().success) {
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
			} else if (oEvent.getParameters().errorobject.statusCode == 401) {
				var message = "Session Lost. Press OK to refresh the page";
				sap.m.MessageBox.information(message, {
					styleClass: "sapUiSizeCompact",
					actions: [sap.m.MessageBox.Action.OK],
					onClose: function (sAction) {
						location.reload(true);
					}
				});
			} else if (oEvent.getParameters().errorobject.statusCode == 400 || oEvent.getParameters().errorobject.statusCode == 404) {
				var message = "Service Unavailable. Please try after sometime";
				sap.m.MessageBox.information(message, {
					styleClass: "sapUiSizeCompact",
					actions: [sap.m.MessageBox.Action.OK]
				});
			} else if (oEvent.getParameters().errorobject.statusCode == 500) {
				var message = "Service Unavailable. Please contact administrator";
				sap.m.MessageBox.information(message, {
					styleClass: "sapUiSizeCompact",
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
			} else if (oEvent.getParameters().errorobject.statusCode == 401) {
				var message = "Session Lost. Press OK to refresh the page";
				sap.m.MessageBox.information(message, {
					styleClass: "sapUiSizeCompact",
					actions: [sap.m.MessageBox.Action.OK],
					onClose: function (sAction) {
						location.reload(true);
					}
				});
			} else if (oEvent.getParameters().errorobject.statusCode == 400 || oEvent.getParameters().errorobject.statusCode == 404) {
				var message = "Service Unavailable. Please try after sometime";
				sap.m.MessageBox.information(message, {
					styleClass: "sapUiSizeCompact",
					actions: [sap.m.MessageBox.Action.OK]
				});
			} else if (oEvent.getParameters().errorobject.statusCode == 500) {
				var message = "Service Unavailable. Please contact administrator";
				sap.m.MessageBox.information(message, {
					styleClass: "sapUiSizeCompact",
					actions: [sap.m.MessageBox.Action.OK]
				});
			}
		});
	},

	onAccSubmit: function (oController, oData, sMethod, sUrl, actionCode, userList, ok) {
		var that = this;
		var oPOModel = oController.oPOModel;
		var oServiceModel = new sap.ui.model.json.JSONModel();
		var busy = new sap.m.BusyDialog();
		var oHeader = {
			"Content-Type": "application/scim+json"
		};
		busy.open();
		oServiceModel.loadData(sUrl, JSON.stringify(oData), true, sMethod, false, false, oHeader);
		oServiceModel.attachRequestCompleted(function (oEvent) {
			busy.close();
			var oData = oEvent.getSource().getData();
			var message = oData.responseStatus;
			if (oEvent.getParameters().success) {
				var ReqId = oData.invoice.requestId;
				oPOModel.setProperty("/", oData.invoice);
				oPOModel.setProperty("/purchaseOrders", oData.purchaseOrders);
				if (!ok && actionCode === "ASR") {
					oController.onSubmitForRemediationFrag(oData.userList);
					oPOModel.setProperty("/actionCode", "ASR");
					return;
				} else if (!ok && actionCode === "ASA") {
					oController.onSubmitForApprovalFrag(oData.userList);
					oPOModel.setProperty("/actionCode", "ASA");
					return;
				} else if (!ok && actionCode === "AR") {
					oController.onSubmitForApprovalFrag(oData.userList, "Reject Invoice");
					oPOModel.setProperty("/actionCode", "AR");
					return;
				}

			} else if (oEvent.getParameters().errorobject.statusCode == 401) {
				var message = "Session Lost. Press OK to refresh the page";
				sap.m.MessageBox.information(message, {
					styleClass: "sapUiSizeCompact",
					actions: [sap.m.MessageBox.Action.OK],
					onClose: function (sAction) {
						location.reload(true);
					}
				});
			} else if (oEvent.getParameters().errorobject.statusCode == 400 || oEvent.getParameters().errorobject.statusCode == 404) {
				var message = "Service Unavailable. Please try after sometime";
				sap.m.MessageBox.information(message, {
					styleClass: "sapUiSizeCompact",
					actions: [sap.m.MessageBox.Action.OK]
				});
			} else if (oEvent.getParameters().errorobject.statusCode == 500) {
				var message = "Service Unavailable. Please contact administrator";
				sap.m.MessageBox.information(message, {
					styleClass: "sapUiSizeCompact",
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
		this.onHeaderChange(oEvent, oController, "isInvoiceAmountChanged");
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

	onItemSelect: function (oEvent, oController) {
		var oPOModel = oController.oPOModel;
		var selItem = oEvent.getParameters().listItem;
		if (oEvent.getParameters().selected && !oEvent.getParameters().selectAll) {
			var selObj = selItem.getBindingContext("oPOModel").getObject();
			if (!selObj.isTwowayMatched || selObj.itemStatusCode === "5" || selObj.itemStatusCode === "6") {
				oEvent.getSource().setSelectedItem(selItem, false);
			}
		}
		var selectedItems = oEvent.getSource().getSelectedItems();
		var tax = this.calculateTax(selectedItems, oController);
		oPOModel.setProperty("/taxValue", this.nanValCheck(tax.totalVax));
		oPOModel.setProperty("/itemGrossAmount", this.nanValCheck(tax.grossTotal));
		oPOModel.setProperty("/grossAmount", this.nanValCheck(tax.headerGross));
		oPOModel.setProperty("/balanceAmount", this.nanValCheck(tax.bal));
	},

	onChangeInvoiceQtyUom: function (oEvent, oController) {
		var oPOModel = oController.oPOModel;
		var selobj = oEvent.getSource().getSelectedItem().getBindingContext("oPOModel").getObject();
		var sPath = oEvent.getSource().getBindingContext("oPOModel");
		oPOModel.setProperty(sPath + "/alvQtyUOM", selobj.alvQtyOPU);
		oPOModel.setProperty(sPath + "/poUnitPriceUOM", selobj.poUnitPriceUOM);
	},

	formatUOMList: function (itemDetails, oController) {
		var len = itemDetails.length;
		var data, obj1, obj2, UOMList, UOM1, UOM2;
		for (var i = 0; i < len; i++) {
			data = [];
			UOMList = [];
			obj1 = {
				"desc": "Qty",
				"OUdetails": "1" + " " + itemDetails[i].orderPriceUnit,
				"OPUdetails": "1" + " " + itemDetails[i].orderUnit
			};
			obj2 = {
				"desc": "unit Price",
				"OUdetails": itemDetails[i].poUnitPriceOPU + " " + itemDetails[i].currency + "/" + itemDetails[i].orderPriceUnit,
				"OPUdetails": itemDetails[i].poUnitPriceOU + " " + itemDetails[i].currency + "/" + itemDetails[i].orderUnit
			};
			data.push(obj1);
			data.push(obj2);
			itemDetails[i].UOMConversion = data;
			if (itemDetails[i].orderPriceUnit === itemDetails[i].orderUnit) {
				UOM1 = {
					"key": itemDetails[i].orderPriceUnit,
					"value": itemDetails[i].orderPriceUnit,
					"alvQtyOPU": itemDetails[i].alvQtyOPU,
					"poUnitPriceUOM": itemDetails[i].poUnitPriceOPU
				};
				UOMList.push(UOM1);
			} else {
				UOM1 = {
					"key": itemDetails[i].orderPriceUnit,
					"value": itemDetails[i].orderPriceUnit,
					"alvQtyOPU": itemDetails[i].alvQtyOPU,
					"poUnitPriceUOM": itemDetails[i].poUnitPriceOPU
				};
				UOM2 = {
					"key": itemDetails[i].orderUnit,
					"value": itemDetails[i].orderUnit,
					"alvQtyOPU": itemDetails[i].alvQtyOU,
					"poUnitPriceUOM": itemDetails[i].poUnitPriceOU
				};
				UOMList.push(UOM1);
				UOMList.push(UOM2);
			}
			itemDetails[i].UOMList = UOMList;
		}
		oController.oPOModel.setProperty("/invoiceItems", itemDetails);
	},

	onViewUOMDetails: function (oEvent, oController) {
		var oButton = oEvent.getSource();
		if (!this.PopoverNewNotification) {
			oController.PopoverNewNotification = sap.ui.xmlfragment("com.menabev.AP.fragment.UOMConversion", this);
			oController.getView().addDependent(this.PopoverNewNotification, this);
		}
		var data = oEvent.getSource().getBindingContext("oPOModel").getObject().UOMConversion;
		oController.oPOModel.setProperty("/UOMList", data);
		oController.PopoverNewNotification.openBy(oButton);
	},

	calculateTax: function (selItems, oController) {
		var length = selItems.length;
		var unplannedCost = oController.oPOModel.getProperty("/unplannedCost");
		var invoiceTotal = oController.oPOModel.getProperty("/invoiceTotal");
		var tax, gross, totalVax = 0,
			grossTotal = 0;
		for (var i = 0; i < length; i++) {
			tax = selItems[0].getBindingContext("oPOModel").getObject().taxValue;
			totalVax += this.nanValCheck(tax);
			gross = selItems[0].getBindingContext("oPOModel").getObject().grossPrice;
			grossTotal += this.nanValCheck(gross);
		}
		var headerGross = this.nanValCheck(totalVax) + this.nanValCheck(grossTotal) + this.nanValCheck(unplannedCost);
		var bal = this.nanValCheck(invoiceTotal) - this.nanValCheck(headerGross);
		var obj = {
			"tax": totalVax,
			"gross": grossTotal,
			"headerGross": headerGross,
			"bal": bal
		};
		return obj;
	},

	nanValCheck: function (value) {
		if (!value || isNaN(value)) {
			return 0;
		} else if (Number(value) === 0 || parseFloat(value) === -0) {
			return 0;
		} else if (!isNaN(value)) {
			value = Number(value).toFixed(2);
			value = parseFloat(value);
		}
		return value;
	},

	onItemTaxCodeChange: function (oEvent, oController) {
		var oPOModel = oController.oPOModel;
		var selectedItem = oEvent.getParameters().selectedItem.getBindingContext("oDropDownModel").getObject();
		var oContextObj = oEvent.getSource().getBindingContext("oPOModel").getObject();
		var taxCode = selectedItem.TxCode;
		var taxPercentage = selectedItem.TaxRate;
		var sPath = oEvent.getSource().getBindingContext("oPOModel").getPath();
		oPOModel.setProperty(sPath + "/taxCode", taxCode);
		oPOModel.setProperty(sPath + "/taxPercentage", taxPercentage);
		var gross = this.nanValCheck(oContextObj.grossPrice);
		var taxAmount = this.calculateLineItemTax(oContextObj);
		oContextObj.taxValue = taxAmount;
		var netWorth = parseFloat(gross) + parseFloat(taxAmount);
		netWorth = netWorth.toFixed(2);
		oContextObj.netWorth = netWorth;
		oPOModel.setProperty(sPath, oContextObj);
		var selectedItems = oEvent.getSource().getParent().getParent().getSelectedItems();
		var tax = this.calculateTax(selectedItems, oController);
		oPOModel.setProperty("/taxValue", this.nanValCheck(tax.totalVax));
		oPOModel.setProperty("/itemGrossAmount", this.nanValCheck(tax.grossTotal));
		oPOModel.setProperty("/grossAmount", this.nanValCheck(tax.headerGross));
		oPOModel.setProperty("/balanceAmount", this.nanValCheck(tax.bal));
		oPOModel.refresh();
	},

	// Item level calculation
	onInvQtyChange: function (oEvent, oController) {
		this.setChangeInd(oEvent, oController, "itemChange");
		var oPOModel = oController.oPOModel;
		var oContextObj = oEvent.getSource().getBindingContext("oPOModel").getObject();
		var sPath = oEvent.getSource().getBindingContext("oPOModel").getPath();
		var gross = this.calculateGrossAmount(oContextObj);
		oContextObj.grossPrice = gross;
		var taxAmount = this.calculateLineItemTax(oContextObj);
		oContextObj.taxValue = taxAmount;
		var netWorth = parseFloat(gross) + parseFloat(taxAmount);
		netWorth = netWorth.toFixed(2);
		oContextObj.netWorth = netWorth;
		oPOModel.setProperty(sPath, oContextObj);
		var selectedItems = oEvent.getSource().getParent().getParent().getSelectedItems();
		var tax = this.calculateTax(selectedItems, oController);
		oPOModel.setProperty("/taxValue", this.nanValCheck(tax.totalVax));
		oPOModel.setProperty("/itemGrossAmount", this.nanValCheck(tax.grossTotal));
		oPOModel.setProperty("/grossAmount", this.nanValCheck(tax.headerGross));
		oPOModel.setProperty("/balanceAmount", this.nanValCheck(tax.bal));
		oPOModel.refresh();

	},

	calculateGrossAmount: function (oContextObj) {
		var unitPrice = this.nanValCheck(oContextObj.unitPrice);
		var quantity = this.nanValCheck(oContextObj.invQty);
		var priceunit = this.nanValCheck(oContextObj.pricingUnit);
		var grossAmount = (unitPrice * quantity) / priceunit;
		grossAmount = grossAmount.toFixed(2);
		return grossAmount;
	},

	calculateLineItemTax: function (oContextObj) {
		var taxPercentage = this.nanValCheck(oContextObj.taxPercentage);
		var grossPrice = this.nanValCheck(oContextObj.grossPrice);
		var taxAmount = (taxPercentage * grossPrice) / 100;
		taxAmount = taxAmount.toFixed(2);
		return taxAmount;
	},

	onGrossPriceChange: function (oEvent, oController) {
		this.setChangeInd(oEvent, oController, "itemChange");
		var oPOModel = oController.oPOModel;
		var oContextObj = oEvent.getSource().getBindingContext("oPOModel").getObject();
		var sPath = oEvent.getSource().getBindingContext("oPOModel").getPath();
		var gross = this.nanValCheck(oContextObj.grossPrice);
		var unitPrice = this.calculateUnitPrice(oContextObj);
		oContextObj.unitPrice = unitPrice;
		var taxAmount = this.calculateLineItemTax(oContextObj);
		oContextObj.taxValue = taxAmount;
		var netWorth = parseFloat(gross) + parseFloat(taxAmount);
		netWorth = netWorth.toFixed(2);
		oContextObj.netWorth = netWorth;
		oPOModel.setProperty(sPath, oContextObj);
		var selectedItems = oEvent.getSource().getParent().getParent().getSelectedItems();
		var tax = this.calculateTax(selectedItems, oController);
		oPOModel.setProperty("/taxValue", this.nanValCheck(tax.totalVax));
		oPOModel.setProperty("/itemGrossAmount", this.nanValCheck(tax.grossTotal));
		oPOModel.setProperty("/grossAmount", this.nanValCheck(tax.headerGross));
		oPOModel.setProperty("/balanceAmount", this.nanValCheck(tax.bal));
		oPOModel.refresh();
	},

	calculateUnitPrice: function (oContextObj) {
		var priceunit = this.nanValCheck(oContextObj.pricingUnit);
		var grossPrice = this.nanValCheck(oContextObj.grossPrice);
		var quantity = this.nanValCheck(oContextObj.invQty);
		var unitPrice = (grossPrice * priceunit) / quantity;
		return unitPrice;
	},

	onPricingUnitChange: function (oEvent, oController) {
		this.setChangeInd(oEvent, oController, "itemChange");
		var oPOModel = oController.oPOModel;
		var oContextObj = oEvent.getSource().getBindingContext("oPOModel").getObject();
		var sPath = oEvent.getSource().getBindingContext("oPOModel").getPath();
		var gross = oContextObj.grossPrice;
		var unitPrice = this.calculateUnitPrice(oContextObj);
		oContextObj.unitPrice = unitPrice;
		var taxAmount = this.calculateLineItemTax(oContextObj);
		oContextObj.taxValue = taxAmount;
		var netWorth = parseFloat(gross) + parseFloat(taxAmount);
		netWorth = netWorth.toFixed(2);
		oContextObj.netWorth = netWorth;
		oPOModel.setProperty(sPath, oContextObj);
		var selectedItems = oEvent.getSource().getParent().getParent().getSelectedItems();
		var tax = this.calculateTax(selectedItems, oController);
		oPOModel.setProperty("/taxValue", this.nanValCheck(tax.totalVax));
		oPOModel.setProperty("/itemGrossAmount", this.nanValCheck(tax.grossTotal));
		oPOModel.setProperty("/grossAmount", this.nanValCheck(tax.headerGross));
		oPOModel.setProperty("/balanceAmount", this.nanValCheck(tax.bal));
		oPOModel.refresh();
	},

};