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
					var oArgs = oEvent.getParameter("arguments"),
						requestId = oArgs.id,
						status = oArgs.status;
					POServices.getPONonPOData("", that, requestId);
					var invoiceItems = oPOModel.getProperty("/invoiceItems");
					var arrUniqueInvoiceItems = that.fnFindUniqueInvoiceItems(invoiceItems);
					that.getReferencePo(arrUniqueInvoiceItems);
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
			this.getBtnVisibility();
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

		onSubmitForRemediationFrag: function () {
			var oPOModel = this.getModel("oPOModel");
			this.SubmitDialog = sap.ui.xmlfragment("com.menabev.AP.fragment.SubmitDialog", this);
			this.getView().addDependent(this.SubmitDialog);
			this.SubmitDialog.setModel(oPOModel, "oPOModel");
			oPOModel.setProperty("/submitTypeTitle", "Submit For Remediation");
			this.SubmitDialog.open();
		},

		onSubmitForApprovalFrag: function () {
			var oPOModel = this.getModel("oPOModel");
			this.SubmitDialog = sap.ui.xmlfragment("com.menabev.AP.fragment.SubmitDialog", this);
			this.getView().addDependent(this.SubmitDialog);
			this.SubmitDialog.setModel(oPOModel, "oPOModel");
			oPOModel.setProperty("/submitTypeTitle", "Submit For Approval");
			this.SubmitDialog.open();
		},

		onCancelSubmitDialog: function () {
			this.SubmitDialog.close();
		},

		onNonPoSubmit: function (oEvent) {
			var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			POServices.onNonPoSubmit(oEvent, this, MandatoryFileds, "ASA");
			// POServices.onAccSubmit(oEvent, oPayload, "POST", "/menabevdev/invoiceHeader/accountant/invoiceSubmit", "ASA");
		},

		SubmitForRemidiation: function (oEvent) {
			var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			POServices.onNonPoSubmit(oEvent, this, MandatoryFileds, "ASR");
			// POServices.onAccSubmit(oEvent, oPayload, "POST", "/menabevdev/invoiceHeader/accountant/invoiceSubmit", "ASA");
		},
		
		onPressOK: function (oEvent) {
			var oPayload = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			POServices.onAccSubmit(oEvent, oPayload, "POST", "/menabevdev/invoiceHeader/accountant/invoiceSubmit", "ASA");
		},

		onNonPoSave: function (oEvent) {
			var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			POServices.onNonPoSave(oEvent, this);
		},

		onPostingDateChange: function (oEvent) {
			// var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			POServices.onNonPoSave(oEvent, this);
		},

		onDueDateChange: function (oEvent) {
			// var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			POServices.onDueDateChange(oEvent, this);
		},

		onClickOK: function (oEvent) {
			var oPayload = this.oPOModel.getData();
			POServices.onAccSubmit(oEvent, oPayload, "POST", "/menabevdev/invoiceHeader/accountant/invoiceSubmit", "ASA","ok");
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
					this.oPOModel.setProperty("/getReferencedByPO", data);
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

		findPOItemDetails: function (sDocumentItem, sDocumentNumber, poHistory) {
			var newArray = [];
			for (var i = 0; i < poHistory.length; i++) {
				if (poHistory[i]["documentItem"] === sDocumentItem && poHistory[i]["documentNumber"] === sDocumentNumber) {
					newArray.push(poHistory[i]);
				}
			}
			return newArray;
		},

	});

});