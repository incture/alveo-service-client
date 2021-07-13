sap.ui.define([
	"com/menabev/AP/controller/BaseController",
	"com/menabev/AP/util/POServices",
	"com/menabev/AP/formatter/formatter"
], function (BaseController, POServices, formatter) {
	"use strict";

	return BaseController.extend("com.menabev.AP.controller.PO", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.menabev.AP.view.PO
		 */
		onInit: function () {
			var that = this;
			var StaticDataModel = this.getOwnerComponent().getModel("StaticDataModel");
			this.StaticDataModel = StaticDataModel;
			var oUserDetailModel = this.getOwnerComponent().getModel("oUserDetailModel");
			this.oUserDetailModel = oUserDetailModel;
			var oPOModel = this.getOwnerComponent().getModel("oPOModel");
			this.oPOModel = oPOModel;
			var oDropDownModel = this.getOwnerComponent().getModel("oDropDownModel");
			this.oDropDownModel = oDropDownModel;
			var oVisibilityModel = this.getOwnerComponent().getModel("oVisibilityModel");
			this.oVisibilityModel = oVisibilityModel;
			var oMandatoryModel = this.getOwnerComponent().getModel("oMandatoryModel");
			this.oMandatoryModel = oMandatoryModel;
			var oDataModel = this.getOwnerComponent().getModel("oDataModel");
			this.oDataModel = oDataModel;
			var oDPODetailsModel = this.getOwnerComponent().getModel("oDPODetailsModel");
			this.oDPODetailsModel = oDPODetailsModel;
			var oDataAPIModel = this.getOwnerComponent().getModel("oDataAPIModel");
			this.oDataAPIModel = oDataAPIModel;
			var ZP2P_API_EC_GL_SRV = this.getOwnerComponent().getModel("ZP2P_API_EC_GL_SRV");
			this.ZP2P_API_EC_GL_SRV = ZP2P_API_EC_GL_SRV;
			var getResourceBundle = this.getOwnerComponent().getModel("i18n").getResourceBundle();
			this.getResourceBundle = getResourceBundle;
			oMandatoryModel.setProperty("/NonPO", {});
			this.oVisibilityModel.setProperty("/PO", {});
			var userGroup = oUserDetailModel.getProperty("/loggedinUserGroup");
			oPOModel.loadData("model/UIDataModel.json");
			this.oRouter = sap.ui.core.UIComponent.getRouterFor(this);
			this.oRouter.attachRoutePatternMatched(function (oEvent) {
				if (oEvent.getParameter("name") === "PO") {
					that.oMandatoryModel.setProperty("/NonPO", {});
					var oArgs = oEvent.getParameter("arguments");
					that.requestId = oArgs.id;
					that.status = oArgs.status;
					that.taskId = oArgs.taskId;
					that.source = oArgs.source;
					var invoiceType = oEvent.getParameter("name");
					if (that.source != "itemMatch") {
						POServices.getPONonPOData("", that, that.requestId);
					}
					that.getBtnVisibility(that.status, that.requestId, invoiceType);
					oVisibilityModel.setProperty("/selectedInvKey", "Invoice");
					oVisibilityModel.setProperty("/selectedtabKey", "invoiceheaderdetails");
				}
			});
			oPOModel.attachRequestCompleted(function (oEvent) {

			});
			var oHeader = {
				"Content-Type": "application/json; charset=utf-8"
			};

			var oLanguage = "E";
			var countryKey = "SA";
			//To load all OData lookups
			this.getPaymentTerm(oHeader, oLanguage);
			this.getPaymentMethod(oHeader, oLanguage);
			this.getPaymentBlock(oHeader, oLanguage);
			this.getTaxCode(oHeader, oLanguage, countryKey);
			this.getCostCenter("1010", oLanguage);
		},

		hdrInvAmtCalu: function (oEvent) {
			POServices.calculateGross(oEvent, this);
		},
		onVendorIdChange: function (oEvent) {

		},

		onVendorNameChange: function (oEvent) {

		},

		getPONonPOData: function (oEvent) {
			POServices.getPONonPOData(oEvent, this);
		},

		onChangeUserInputTaxAmount: function (oEvent) {
			POServices.onChangeUserInputTaxAmount(oEvent, this);
		},

		calculateGross: function (oEvent) {
			POServices.setChangeInd(oEvent, this, "headerChange");
			POServices.calculateGross(oEvent, this);
		},

		// VendorIdSuggest: function (oEvent) {
		// 	POServices.VendorIdSuggest(oEvent, this);
		// },

		onTransactionChange: function (oEvent) {
			POServices.onTransactionChange(oEvent, this);
		},

		vendorIdSelected: function (oEvent) {
			POServices.vendorIdSelected(oEvent, this);
		},

		onCompanyCodeChange: function (oEvent) {
			POServices.onCompanyCodeChange(oEvent, this);
		},

		onInvRefChange: function (oEvent) {
			POServices.onInvRefChange(oEvent, this);
		},

		onCurrencyChange: function (oEvent) {
			POServices.onCurrencyChange(oEvent, this);
		},

		onInvDateChange: function (oEvent) {
			POServices.onInvDateChange(oEvent, this);
		},

		onBaseLineDateChange: function (oEvent) {
			POServices.onBaseLineDateChange(oEvent, this);
		},

		onPaymentTermsChange: function (oEvent) {
			POServices.onPaymentTermsChange(oEvent, this);
		},

		onInvTypeChange: function (oEvent) {
			POServices.onInvTypeChange(oEvent, this);
		},

		onTaxCodeChange: function (oEvent) {
			POServices.onTaxCodeChange(oEvent, this);
		},

		onTaxAmtChange: function (oEvent) {
			POServices.onTaxAmtChange(oEvent, this);
		},

		onInvAmtChange: function (oEvent) {
			POServices.onInvAmtChange(oEvent, this);
		},

		onClickVendorBalances: function () {
			var nonPOInvoiceModel = this.oPOModel,
				vendorId = nonPOInvoiceModel.getProperty("/vendorId"),
				companyCode = nonPOInvoiceModel.getProperty("/compCode");
			this.loadVendorBalanceFrag(vendorId, companyCode);
		},

		onPressOpenpdf: function (oEvent) {
			var oSelectedBtnKey = oEvent.getSource().getCustomData()[0].getKey();
			var documentId = this.getModel("oPOModel").getProperty("/invoicePdfId");
			this.fnOpenPDF(documentId, oSelectedBtnKey);
		},

		onSubmitForRemediationFrag: function (userList) {
			var oMandatoryModel = this.oMandatoryModel;
			oMandatoryModel.setProperty("/NonPO/reasonForRejectionState", "None");
			oMandatoryModel.setProperty("/NonPO/commmentsState", "None");
			var oPOModel = this.getModel("oPOModel");
			if (userList) {
				this.fetchUserList(userList);
			} else {
				oPOModel.setProperty("/selectedRemidiationGroup", "BUYER");
				oPOModel.setProperty("/remidiationSwitchVisible", true);
			}
			this.SubmitDialog = sap.ui.xmlfragment("com.menabev.AP.fragment.SubmitDialog", this);
			this.getView().addDependent(this.SubmitDialog, this);
			this.SubmitDialog.setModel(oPOModel, "oPOModel");
			oPOModel.setProperty("/submitTypeTitle", "Submit For Remediation");
			this.SubmitDialog.open();
		},

		onSubmitForApprovalFrag: function (userList) {
			var oMandatoryModel = this.oMandatoryModel;
			oMandatoryModel.setProperty("/NonPO/reasonForRejectionState", "None");
			oMandatoryModel.setProperty("/NonPO/commmentsState", "None");
			if (userList) {
				this.fetchUserList(userList);
			}
			var oPOModel = this.getModel("oPOModel");
			oPOModel.setProperty("/selectedRemidiationGroup", "ProcessLead");
			oPOModel.setProperty("/remidiationSwitchVisible", false);
			oPOModel.setProperty("/userList", oPOModel.getProperty("/ProcessLeadUser"));
			this.SubmitDialog = sap.ui.xmlfragment("com.menabev.AP.fragment.SubmitDialog", this);
			this.getView().addDependent(this.SubmitDialog, this);
			this.SubmitDialog.setModel(oPOModel, "oPOModel");
			oPOModel.setProperty("/submitTypeTitle", "Submit For Approval");
			this.SubmitDialog.open();
		},

		openCommentsFrag: function (userList) {
			var oMandatoryModel = this.oMandatoryModel;
			if (!this.commentsDialog) {
				this.commentsDialog = sap.ui.xmlfragment("com.menabev.AP.fragment.Submitcomments", this);
				this.getView().addDependent(this.commentsDialog, this);
			}
			this.commentsDialog.open();
		},

		onSubmitForRejection: function (userList, title) {
			if (userList) {
				this.fetchUserList(userList);
			}
			this.getReasonOfRejection();
			var oPOModel = this.getModel("oPOModel");
			oPOModel.setProperty("/selectedRemidiationGroup", "ProcessLead");
			oPOModel.setProperty("/remidiationSwitchVisible", false);
			oPOModel.setProperty("/userList", oPOModel.getProperty("/ProcessLeadUser"));
			this.RejectDialog = sap.ui.xmlfragment("com.menabev.AP.fragment.RejectDialog", this);
			this.getView().addDependent(this.RejectDialog, this);
			this.RejectDialog.setModel(oPOModel, "oPOModel");
			oPOModel.setProperty("/submitTypeTitle", "Reject Task");
			this.RejectDialog.open();
		},

		fetchUserList: function (userList) {

			var oPOModel = this.oPOModel;
			var length = userList.length;
			oPOModel.setProperty("/remidiationSwitchVisible", false);
			oPOModel.setProperty("/selectedRemidiationGroup", "BUYER");
			var GRN = 0,
				buyer = 0,
				processLead = 0;
			for (var i = 0; i < length; i++) {
				if (userList[i].type === "GRN") {
					oPOModel.setProperty("/GRNUser", userList[i].users);
					GRN += 1;
				} else if (userList[i].type === "BUYER") {
					oPOModel.setProperty("/BuyerUser", userList[i].users);
					buyer += 1;
				} else if (userList[i].type === "ProcessLead") {
					oPOModel.setProperty("/ProcessLeadUser", userList[i].users);
					processLead += 1;
				}
			}
			if (GRN && buyer) {
				oPOModel.setProperty("/remidiationSwitchVisible", true);
				oPOModel.setProperty("/selectedRemidiationGroup", "BUYER");
				oPOModel.setProperty("/userList", oPOModel.getProperty("/BuyerUser"));
			} else if (GRN) {
				oPOModel.setProperty("/remidiationSwitchVisible", false);
				oPOModel.setProperty("/selectedRemidiationGroup", "GRN");
				oPOModel.setProperty("/userList", oPOModel.getProperty("/GRNUser"));
			} else if (buyer) {
				oPOModel.setProperty("/remidiationSwitchVisible", false);
				oPOModel.setProperty("/selectedRemidiationGroup", "BUYER");
				oPOModel.setProperty("/userList", oPOModel.getProperty("/BuyerUser"));
			} else if (processLead) {
				oPOModel.setProperty("/remidiationSwitchVisible", false);
				oPOModel.setProperty("/selectedRemidiationGroup", "ProcessLead");
				oPOModel.setProperty("/userList", oPOModel.getProperty("/ProcessLeadUser"));
			}
		},

		onRemidiationSwitchToggle: function (oEvent) {
			var oPOModel = this.oPOModel;
			var state = oEvent.getSource().getState();
			if (state) {
				oPOModel.setProperty("/selectedRemidiationGroup", "GRN");
				oPOModel.setProperty("/userList", oPOModel.getProperty("/GRNUser"));
			} else {
				oPOModel.setProperty("/selectedRemidiationGroup", "BUYER");
				oPOModel.setProperty("/userList", oPOModel.getProperty("/BuyerUser"));
			}
			oPOModel.refresh();
		},

		onAddRemidiationUser: function (oEvent) {
			var oPOModel = this.oPOModel;
			var obj = {
				"user": ""
			};
			var userList = oPOModel.getProperty("/userList");
			if (!userList) {
				userList = [];
			}
			userList.push(obj);
			oPOModel.setProperty("/userList", userList);
		},

		onDeleteRemidiationUser: function (oEvent) {
			var oPOModel = this.oPOModel;
			var userList = oPOModel.getProperty("/userList");
			var sPath = oEvent.getSource().getBindingContext("oPOModel").getPath();
			var spathArray = sPath.split("/");
			var length = spathArray.length;
			var index = spathArray[length - 1];
			userList.splice(index, 1);
			oPOModel.setProperty("/userList", userList);
			oPOModel.refresh();
		},
		onDeleteInvLineItem: function (oEvent) {
			var oPOModel = this.oPOModel;
			var userList = oPOModel.getProperty("/userList");
			var sPath = oEvent.getSource().getBindingContext("oPOModel").getPath();
			oPOModel.setProperty(sPath + "/isDeleted", true);
			oPOModel.setProperty(sPath + "/isSelected", false);
			oPOModel.refresh();
			POServices.onFilterItemDetails();
		},

		formUserListPayload: function () {
			var oPOModel = this.oPOModel;
			var userList = oPOModel.getProperty("/userList");
			var user, count = 0;
			if (!userList) {
				userList = [];
			}
			var length = userList.length;
			if (!length) {
				sap.m.MessageToast.show("Please enter one remidiation user");
				return;
			}
			for (var i = 0; i < userList.length; i++) {
				user = userList[i].user;
				if (!user) {
					oPOModel.setProperty("/userList/" + i + "/userState", "Error");
					count += 1;
				} else {
					oPOModel.setProperty("/userList/" + i + "/userState", "None");
				}
			}
			if (count) {
				sap.m.MessageToast.show("Please enter all mandatory fields");
				return;
			} else {
				var obj = {
					"userList": [{
						"type": oPOModel.getProperty("/selectedRemidiationGroup"),
						"users": oPOModel.getProperty("/userList")
					}]
				};
			}
			return obj;
		},

		onCancelSubmitDialog: function () {
			this.SubmitDialog.close();
		},

		onNonPoSubmit: function (oEvent) {
			var userGroup = this.oUserDetailModel.getProperty("/loggedinUserGroup");
			var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			var sUrl, actionCode, threewayMatch;
			var loggedinUser = this.oUserDetailModel.getProperty("/loggedInUserMail");
			var changeIndicators = this.oPOModel.getProperty("/changeIndicators");
			if (userGroup === "Accountant") {
				sUrl = "/menabevdev/invoiceHeader/accountant/invoiceSubmit";
				actionCode = "ASA";
			} else if (userGroup === "Buyer") {
				sUrl = "/menabevdev/invoiceHeader/buyer/buyerSubmit";
				actionCode = "A";
			} else if (userGroup === "Process_Lead") {
				sUrl = "/menabevdev/invoiceHeader/processLead/processLeadSubmit";
				actionCode = "PA";
			}
			if (changeIndicators && changeIndicators.itemChange) {
				threewayMatch = true;
			}
			POServices.onPoSubmit(oEvent, this, MandatoryFileds, actionCode, sUrl, "Saving....", "", threewayMatch);
			// POServices.onAccSubmit(oEvent, oPayload, "POST", "/menabevdev/invoiceHeader/accountant/invoiceSubmit", "ASA");
		},
		
		

		// onNonPoRejectConfirm: function (oEvent) {
		// 	var oPOModel = this.oPOModel;
		// 	var oMandatoryModel = this.oMandatoryModel;
		// 	var userGroup = this.oUserDetailModel.getProperty("/loggedinUserGroup");
		// 	var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
		// 	var sUrl, actionCode;
		// 	var actioncode = oPOModel.getProperty("/actionCode");
		// 	if (actioncode === "AR") {
		// 		var reasonForRejection = this.oPOModel.getProperty("/reasonForRejection");
		// 		if (!reasonForRejection) {
		// 			oMandatoryModel.setProperty("/NonPO/reasonForRejectionState", "Error");
		// 			sap.m.MessageToast.show("Please select reason for rejection");
		// 			return;
		// 		}
		// 	}
		// 	var loggedinUser = this.oUserDetailModel.getProperty("/loggedInUserMail");
		// 	if (userGroup === "Accountant") {
		// 		sUrl = "/menabevdev/invoiceHeader/accountant/invoiceSubmit";
		// 		actionCode = "AR";
		// 	}
		// 	POServices.onPoSubmit(oEvent, this, MandatoryFileds, actionCode, sUrl, userList, "ok");

		// 	// POServices.onAccSubmit(oEvent, oPayload, "POST", "/menabevdev/invoiceHeader/accountant/invoiceSubmit", "ASA");
		// },
		onNonPoRejectCancel: function () {
			this.RejectDialog.close();
		},

		onNonPoReSend: function (oEvent) {
			var userGroup = this.oUserDetailModel.getProperty("/loggedinUserGroup");
			var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			var sUrl, actionCode;
			var loggedinUser = this.oUserDetailModel.getProperty("/loggedInUserMail");
			if (userGroup === "Accountant") {
				sUrl = "/menabevdev/invoiceHeader/accountant/invoiceSubmit";
				actionCode = "ASA";
			} else if (userGroup === "Buyer") {
				sUrl = "/menabevdev/invoiceHeader/buyer/buyerSubmit";
				actionCode = "A";
			} else if (userGroup === "Process_Lead") {
				sUrl = "/menabevdev/invoiceHeader/processLead/processLeadSubmit";
				actionCode = "PRS";
			}

			POServices.onPoSubmit(oEvent, this, MandatoryFileds, actionCode, sUrl, "Saving....");
			// POServices.onAccSubmit(oEvent, oPayload, "POST", "/menabevdev/invoiceHeader/accountant/invoiceSubmit", "ASA");
		},

		SubmitForRemidiation: function (oEvent) {
			var userGroup = this.oUserDetailModel.getProperty("/loggedinUserGroup");
			var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			var sUrl, actionCode, threewayMatch;
			var changeIndicators = this.oPOModel.getProperty("/changeIndicators");
			// if (changeIndicators && changeIndicators.itemChange) {
			// 	this.onClickThreeWayMatch("", true);
			// }
			if (userGroup === "Accountant") {
				sUrl = "/menabevdev/invoiceHeader/accountant/invoiceSubmit";
				actionCode = "ASR";
			}
			// else if (userGroup === "Buyer") {
			// 	sUrl = "/menabevdev/invoiceHeader/buyer/buyerSubmit";
			// 	actionCode = "";
			// } else if (userGroup === "Process_Lead") {
			// 	sUrl = "/menabevdev/invoiceHeader/processLead/processLeadSubmit";
			// 	actionCode = "";
			// }
			if (changeIndicators && changeIndicators.itemChange) {
				threewayMatch = true;
			}
			POServices.onPoSubmit(oEvent, this, MandatoryFileds, actionCode, sUrl, "Saving....", "", threewayMatch);
			// POServices.onAccSubmit(oEvent, oPayload, "POST", "/menabevdev/invoiceHeader/accountant/invoiceSubmit", "ASA");
		},

		onRejectTask: function (oEvent) {
			var userGroup = this.oUserDetailModel.getProperty("/loggedinUserGroup");
			var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			var sUrl, actionCode;
			if (userGroup === "Accountant") {
				sUrl = "/menabevdev/invoiceHeader/accountant/invoiceSubmit";
				actionCode = "AR";
			} else if (userGroup === "Buyer") {
				sUrl = "/menabevdev/invoiceHeader/buyer/buyerSubmit";
				actionCode = "R";
			} else if (userGroup === "Process_Lead") {
				sUrl = "/menabevdev/invoiceHeader/processLead/processLeadSubmit";
				actionCode = "PR";
			}
			POServices.onPoSubmit(oEvent, this, MandatoryFileds, actionCode, sUrl, "Saving....");
		},

		onPressOK: function (oEvent) {
			var oPOModel = this.oPOModel;
			var oData = oPOModel.getProperty("/");
			var oMandatoryModel = this.oMandatoryModel;
			if (!oMandatoryModel.getProperty("/NonPO")) {
				oMandatoryModel.setProperty("/NonPO", {});
			}
			var sMethod = "POST";
			var sUrl, actionCode = oPOModel.getProperty("/actionCode");
			var userList = this.formUserListPayload();
			var reasonforReject = oPOModel.getProperty("/reasonForRejection");
			var comments = oPOModel.getProperty("/commments");
			var count = 0;
			if (actionCode === "AR") {
				if (!reasonforReject) {
					count += 1;
					oMandatoryModel.setProperty("/NonPO/reasonForRejectionState", "Error");
				}
			}
			if (!comments) {
				count += 1;
				oMandatoryModel.setProperty("/NonPO/commmentsState", "Error");
			}
			if (count) {
				sap.m.MessageToast.show("Please fill all Mandatory Fields");
				return;
			} else {
				oMandatoryModel.setProperty("/NonPO/reasonForRejectionState", "None");
				oMandatoryModel.setProperty("/NonPO/commmentsState", "None");
			}
			if (userList) {
				var oPayload = {
					"invoice": oData,
					"requestId": oData.requestId,
					"taskId": this.taskId,
					"actionCode": actionCode,
					"purchaseOrders": jQuery.extend(true, [], oData.purchaseOrders),
					"userList": userList.userList
				};
				oPayload.invoice.taskOwner = this.oUserDetailModel.getProperty("/loggedInUserMail");
				sUrl = "/menabevdev/invoiceHeader/accountant/invoiceSubmit";
				this.SubmitDialog.close();
				POServices.onAccSubmit(this, oPayload, "POST", "/menabevdev/invoiceHeader/accountant/invoiceSubmitOk", actionCode, userList, "ok");
			}

		},

		onNonPoSave: function (oEvent) {
			var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			var changeIndicators = this.oPOModel.getProperty("/changeIndicators");
			var threewayMatch;
			if (changeIndicators && changeIndicators.itemChange) {
				threewayMatch = true;
			}
			POServices.onNonPoSave(oEvent, this, threewayMatch);
		},

		onPostingDateChange: function (oEvent) {
			// var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			POServices.onPostingDateChange(oEvent, this);
		},

		onDueDateChange: function (oEvent) {
			// var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			POServices.onDueDateChange(oEvent, this);
		},

		onItemSelect: function (oEvent) {
			POServices.onItemSelect(oEvent, this);
		},

		onItemSelectAll: function (oEvent) {
			var selected = oEvent.getSource().getSlected();
			var oPOModel = this.oPOModel;
			var selItems = oPOModel.getProperty("/invoiceItems");
			var length = selItems.length;
			for (var i = 0; i < length; i++) {
				if (!selItems[i].isDeleted && selItems[i].isTwowayMatched) {
					selItems[i].isSelected = selected;
				}
			}
			oPOModel.setProperty("/invoiceItems", selItems);
			var tax = POServices.calculateTax(oEvent, this);
			oPOModel.setProperty("/sysSusgestedTaxAmount", POServices.nanValCheck(tax.totalVax));
			oPOModel.setProperty("/totalBaseRate", POServices.nanValCheck(tax.gross));
			oPOModel.setProperty("/grossAmount", POServices.nanValCheck(tax.headerGross));
			oPOModel.setProperty("/balanceAmount", POServices.nanValCheck(tax.bal));
		},

		onInvQtyChange: function (oEvent) {
			POServices.onInvQtyChange(oEvent, this);
		},
		onViewUOMDetails: function (oEvent) {
			POServices.onViewUOMDetails(oEvent, this);
		},
		onGrossPriceChange: function (oEvent) {
			POServices.onGrossPriceChange(oEvent, this);
		},
		onItemTaxCodeChange: function (oEvent) {
			POServices.onItemTaxCodeChange(oEvent, this);
		},

		onClickInvoiceAccAssignment: function (oEvent) {
			var sPath = oEvent.getSource().getBindingContext("oPOModel").getPath();
			var oPOModel = this.oPOModel;
			var invoiceItemAccAssgn = oPOModel.getProperty(sPath + "/invItemAcctDtoList");
			oPOModel.setProperty("/invItemAcctDtoList", invoiceItemAccAssgn);
			this.GLCodingDialog = sap.ui.xmlfragment("com.menabev.AP.fragment.GLCoding", this);
			this.getView().addDependent(this.GLCodingDialog);
			this.GLCodingDialog.open();
		},

		onCancelGLCoding: function () {
			this.GLCodingDialog.close();
		},

		addGLCodingRow: function (oEvent) {
			var oPOModel = this.getView().getModel("oPOModel"),
				oPOModelData = oPOModel.getData();
			if (!oPOModelData.invItemAcctDtoList) {
				oPOModelData.invItemAcctDtoList = [];
			}
			oPOModelData.invItemAcctDtoList.unshift({
				"accountAssgnGuid": "",
				"requestId": "",
				"itemId": "",
				"serialNo": "",
				"isDeleted": "",
				"isUnplanned": "",
				"qty": 0,
				"qtyUnit": "",
				"distPerc": "",
				"netValue": "",
				"glAccount": "",
				"costCenter": "",
				"debitOrCredit": "",
				"text": "",
				"taxValue": "",
				"taxPercentage": "",
				"taxCode": ""
			});
			oPOModel.refresh();
		},

		deleteNonPoItemData: function (oEvent) {
			var oPOModel = this.getView().getModel("oPOModel");
			var index = oEvent.getSource().getParent().getBindingContextPath().split("/")[2];
			oPOModel.getData().invoiceItemAccAssgn.splice(index, 1);
			oPOModel.refresh();
		},

		onClickItemMatch: function () {
			var oPOModel = this.oPOModel;
			var that = this;
			this.oRouter.navTo("ItemMatch", {
				id: oPOModel.getProperty("/requestId"),
				status: that.status,
				taskId: that.taskId
			});
		},

		fnFindUniqueInvoiceItems: function (arr) {
			var newArray = [];
			var uniqueObject = {};
			for (var i in arr) {
				var refDocNum = arr[i]['refDocNum'];
				uniqueObject[refDocNum] = arr[i];
			}
			for (i in uniqueObject) {
				var obj = {
					"documentCategory": uniqueObject[i].refDocCategory,
					"documentNumber": uniqueObject[i].refDocNum
				};
				newArray.push(obj);
			}
			return newArray;
		},

		getReferencePo: function (arrUniqueInvoiceItems) {
			var payload = {
				"requestId": "",
				"purchaseOrder": arrUniqueInvoiceItems
			};
			//Test Payload
			// payload = {
			// 	"requestId": null,
			// 	"purchaseOrder": [{
			// 		"documentCategory": "",
			// 		"documentNumber": "1000000002"
			// 	}]
			// };
			var url = "/menabevdev/purchaseDocumentHeader/getReferencePoApi";
			jQuery.ajax({
				type: "POST",
				contentType: "application/json",
				url: url,
				dataType: "json",
				data: JSON.stringify(payload),
				async: true,
				success: function (data, textStatus, jqXHR) {
					this.oPOModel.setProperty("/purchaseOrders", data);
				}.bind(this),
				error: function (err) {
					sap.m.MessageToast.show(err.statusText);
				}.bind(this)
			});
		},

		onClickPODetailView: function (oEvent) {
			var sPath = oEvent.getSource().getBindingContext("oPOModel").getPath(),
				oPOModel = this.oPOModel,
				selectedItem = oPOModel.getProperty(sPath),
				sDocumentItem = selectedItem.documentItem,
				sDocumentNumber = selectedItem.documentNumber;
			var poHistoryPath = this.getPathForPOHistory(sPath);
			var poHistory = oPOModel.getProperty(poHistoryPath + "/poHistory");
			var aPOItemDetails = this.findPOItemDetails(sDocumentItem, sDocumentNumber, poHistory);
			oPOModel.setProperty("/aPOItemDetails", aPOItemDetails);
			oPOModel.setProperty("/aPOItemDocumentItem", sDocumentItem);
			this.POItemDetails = sap.ui.xmlfragment("com.menabev.AP.fragment.POItemDetails", this);
			this.getView().addDependent(this.POItemDetails);
			this.POItemDetails.setModel(oPOModel, "oPOModel");
			this.POItemDetails.open();
		},

		getPathForPOHistory: function (sPath) {
			var newPath = sPath.split("/").slice(0, 3).join("/");
			return newPath;
		},

		onClosePODetailsDialog: function () {
			this.POItemDetails.close();
		},

		onViewMessages: function (oEvent) {
			var that = this;
			var oPOModel = this.oPOModel;
			var oContextObj = oEvent.getSource().getBindingContext("oPOModel").getObject();
			if (oContextObj.invoiceItemMessages) {
				oPOModel.setProperty("/invoiceItemMessages", oContextObj.invoiceItemMessages);
				oPOModel.refresh();
				this.userGroup = sap.ui.xmlfragment("com.menabev.AP.fragment.ItemMessage", this);
				this.getView().addDependent(this.userGroup);
				this.userGroup.open();
			} else {
				var msg = "No messages available";
				sap.m.MessageToast.show(msg);
			}
		},

		onCloseFragment: function (oEvent) {
			this.userGroup.close();
		},

		onClickAddInvoiceItem: function () {
			var oPOModel = this.getOwnerComponent().getModel("oPOModel"),
				oPOModelData = oPOModel.getData();
			if (!oPOModelData.invoiceItems) {
				oPOModelData.invoiceItems = [];
			}
			oPOModelData.invoiceItems.unshift({
				"accAssignmentCat": null,
				"accountAssignmentCat": null,
				"alvQtyOPU": null,
				"alvQtyOU": null,
				"alvQtyUOM": null,
				"articleNum": "",
				"contractItem": null,
				"contractNum": null,
				"convDen1": null,
				"convNum1": null,
				"currency": "",
				"customerItemId": "",
				"disPerentage": 0,
				"discountValue": 0,
				"extItemId": "",
				"grBsdIv": null,
				"grFlag": null,
				"grossPrice": 0,
				"guid": "",
				"invItemAcctDtoList": [],
				"invQty": 0,
				"invoiceItemMessages": [],
				"isAccAssigned": false,
				"isDeleted": false,
				"isSelected": false,
				"isThreewayMatched": false,
				"isTwowayMatched": false,
				"itemCategory": null,
				"itemCode": "",
				"itemRequisationNum": null,
				"itemStatusCode": "",
				"itemStatusText": "",
				"itemText": "",
				"ivFlag": null,
				"matchDocItem": null,
				"matchDocNum": null,
				"matchParam": null,
				"matchType": null,
				"matchedBy": null,
				"matchpackageNumber": null,
				"matchserviceNumber": null,
				"netWorth": 0,
				"orderPriceUnit": null,
				"orderPriceUnitISO": null,
				"orderUnit": null,
				"orderUnitISO": null,
				"poItemText": null,
				"poMatNum": null,
				"poQtyOPU": null,
				"poQtyOU": null,
				"poTaxCode": null,
				"poUnitPriceOPU": null,
				"poUnitPriceOU": null,
				"poUnitPriceUOM": null,
				"pricingUnit": 0,
				"productType": null,
				"refDocCat": null,
				"refDocNum": 0,
				"requestId": oPOModelData.requestId,
				"requisationNum": null,
				"setPoMaterialNum": null,
				"srvBsdIv": null,
				"sysSuggTax": null,
				"taxCode": null,
				"taxPercentage": 0,
				"taxValue": 0,
				"unitPrice": 0,
				"uom": "",
				"upcCode": null,
				"updatedAt": null,
				"updatedBy": null
			});
			oPOModel.refresh();
		}

	});

});