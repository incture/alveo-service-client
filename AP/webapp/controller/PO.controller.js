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
					var invoiceType = oEvent.getParameter("name");
					POServices.getPONonPOData("", that, that.requestId);
					this.getBtnVisibility(that.status, that.requestId, invoiceType);
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
			this.getTaxCode(oHeader, countryKey);
			this.getCostCenter("1010", oLanguage);
		},

		hdrInvAmtCalu: function (oEvent) {
			POServices.hdrInvAmtCalu(oEvent, this);
		},
		onVendorIdChange: function (oEvent) {

		},

		onVendorNameChange: function (oEvent) {

		},

		getPONonPOData: function (oEvent) {
			POServices.getPONonPOData(oEvent, this);
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
			if (userList) {
				this.fetchUserList();
			}
			var oPOModel = this.getModel("oPOModel");
			this.SubmitDialog = sap.ui.xmlfragment("com.menabev.AP.fragment.SubmitDialog", this);
			this.getView().addDependent(this.SubmitDialog);
			this.SubmitDialog.setModel(oPOModel, "oPOModel");
			oPOModel.setProperty("/submitTypeTitle", "Submit For Remediation");
			this.SubmitDialog.open();
		},

		onSubmitForApprovalFrag: function (userList) {
			if (userList) {
				this.fetchUserList();
			}
			var oPOModel = this.getModel("oPOModel");
			this.SubmitDialog = sap.ui.xmlfragment("com.menabev.AP.fragment.SubmitDialog", this);
			this.getView().addDependent(this.SubmitDialog);
			this.SubmitDialog.setModel(oPOModel, "oPOModel");
			oPOModel.setProperty("/submitTypeTitle", "Submit For Approval");
			this.SubmitDialog.open();
		},

		fetchUserList: function (userList) {

			var oPOModel = this.oPOModel;
			var length = userList.length;
			var GRN = 0,
				buyer = 0,
				processLead = 0;
			for (var i = 0; i < length; i++) {
				if (userList[i].type === "GRN") {
					oPOModel.setProperty("/GRNUser", userList[i].users);
					GRN += 1;
				} else if (userList[i].type === "BUYER ") {
					oPOModel.setProperty("/BuyerUser", userList[i].users);
					buyer += 1;
				} else if (userList[i].type === "ProcessLead") {
					oPOModel.setProperty("/ProcessLeadUser", userList[i].users);
					processLead += 1;
				}
			}
			if (GRN && buyer) {
				oPOModel.setProperty("/remidiationSwitchVisible", true);
				oPOModel.setProperty("/selectedRemidiationGroup", "GRN");
				oPOModel.setProperty("/userList", oPOModel.getProperty("/GRNUser"));
			}
			if (GRN) {
				oPOModel.setProperty("/remidiationSwitchVisible", false);
				oPOModel.setProperty("/selectedRemidiationGroup", "GRN");
				oPOModel.setProperty("/userList", oPOModel.getProperty("/GRNUser"));
			}
			if (buyer) {
				oPOModel.setProperty("/remidiationSwitchVisible", false);
				oPOModel.setProperty("/selectedRemidiationGroup", "BUYER");
				oPOModel.setProperty("/userList", oPOModel.getProperty("/BuyerUser"));
			}
			if (processLead) {
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

		formUserListPayload: function () {
			var oPOModel = this.oPOModel;
			var userList = oPOModel.getProperty("/userList");
			var user, count = 0;
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
			var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			POServices.onPoSubmit(oEvent, this, MandatoryFileds, "ASA");
			// POServices.onAccSubmit(oEvent, oPayload, "POST", "/menabevdev/invoiceHeader/accountant/invoiceSubmit", "ASA");
		},

		SubmitForRemidiation: function (oEvent) {
			var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			POServices.onPoSubmit(oEvent, this, MandatoryFileds, "ASR");
			// POServices.onAccSubmit(oEvent, oPayload, "POST", "/menabevdev/invoiceHeader/accountant/invoiceSubmit", "ASA");
		},

		onPressOK: function (oEvent) {
			var oPayload = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			var userList = this.formUserListPayload();
			POServices.onAccSubmit(oEvent, oPayload, "POST", "/menabevdev/invoiceHeader/accountant/invoiceSubmit", "ASA", userList, "ok");
		},

		onNonPoSave: function (oEvent) {
			var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			POServices.onNonPoSave(oEvent, this);
		},

		onPostingDateChange: function (oEvent) {
			// var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			POServices.onPostingDateChange(oEvent, this);
		},

		onDueDateChange: function (oEvent) {
			// var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			POServices.onDueDateChange(oEvent, this);
		},

		onClickOK: function (oEvent) {
			var oPayload = this.oPOModel.getData();
			POServices.onAccSubmit(oEvent, oPayload, "POST", "/menabevdev/invoiceHeader/accountant/invoiceSubmit", "ASA", "ok");
		},
		onItemSelect: function (oEvent) {
			POServices.onItemSelect(oEvent, this);
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
			var invoiceItemAccAssgn = oPOModel.getProperty(sPath + "/invoiceItemAccAssgn");
			oPOModel.setProperty("/invoiceItemAccAssgn", invoiceItemAccAssgn);
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
			if (!oPOModelData.invoiceItemAccAssgn) {
				oPOModelData.invoiceItemAccAssgn = [];
			}
			oPOModelData.invoiceItemAccAssgn.unshift({
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
			this.oRouter.navTo("ItemMatch");
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