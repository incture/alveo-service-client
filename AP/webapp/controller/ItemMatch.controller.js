sap.ui.define([
	"com/menabev/AP/controller/BaseController",
	"com/menabev/AP/util/POServices"
], function (BaseController, POServices) {
	"use strict";

	return BaseController.extend("com.menabev.AP.controller.ItemMatch", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.menabev.AP.view.ItemMatch
		 */
		onInit: function () {
			var oComponent = this.getOwnerComponent();
			this.oRouter = oComponent.getRouter();

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

			this.oRouter = this.getOwnerComponent().getRouter();
			this.oRouter.getRoute("ItemMatch").attachPatternMatched(this.onRouteMatched, this);

		},

		onRouteMatched: function (oEvent) {
			var oArgs = oEvent.getParameter("arguments");
			this.requestId = oArgs.id;
			this.status = oArgs.status;
			this.taskId = oArgs.taskId;
			this.getItemMatchPOData();
			this.getView().byId("ItemMatchInvoiceTableId").removeSelections();
		},

		getItemMatchPOData: function () {
			var arr = [];
			var getReferencedByPO = $.extend(true, [], this.oPOModel.getProperty("/purchaseOrders"));
			for (var i = 0; i < getReferencedByPO.length; i++) {
				arr = arr.concat(getReferencedByPO[i].poItem);
			}
			this.oPOModel.setProperty("/aItemMatchPO", arr);
			this.fnHideMatchedPO();
		},

		onNavback: function () {
			var reqId = this.oPOModel.getProperty("/requestId");
			var changeIndicators = this.oPOModel.getProperty("/changeIndicators");
			if(changeIndicators) {
				this.onClickThreeWayMatch("");
			}
			this.oRouter.navTo("PO", {
				id: reqId,
				status: this.status,
				taskId: this.taskId
			});
		},

		onSelectInvoice: function (oEvent) {
			var sPath = oEvent.getSource().getSelectedItems()[0].getBindingContext("oPOModel").getPath();
			this.oSelectedInvoiceItem = this.oPOModel.getProperty(sPath);
		},

		onClickMatch: function (oEvent) {
			POServices.setChangeInd(oEvent, this, "itemChange");
			var oPOModel = this.oPOModel;
			if (this.oSelectedInvoiceItem) {
				var sPath = oEvent.getSource().getBindingContext("oPOModel").getPath(),
					oSelectedPOItem = oPOModel.getProperty(sPath);

				//Payload Logic //BackTracking to get purchaseDocumentHeader
				//Get POHeader based on selected POItem 
				var getReferencedByPO = $.extend(true, [], this.oPOModel.getProperty("/purchaseOrders"));
				for (var i = 0; i < getReferencedByPO.length; i++) {
					if (oSelectedPOItem.documentNumber === getReferencedByPO[i].documentNumber) {
						var poHeader = getReferencedByPO[i];
					}
				}

				//To find POHistory based on the selected PO Item 
				var poHistory = this.findPOItemDetails(oSelectedPOItem.documentItem, oSelectedPOItem.documentNumber, poHeader.poHistory);

				// Creation of purchaseDocumentHeader with POHistory
				poHeader.poHistory = poHistory;
				var poItem = [];
				poItem.push(oSelectedPOItem);
				poHeader.poItem = poItem;
				var purchaseDocumentHeader = [];
				purchaseDocumentHeader.push(poHeader);
				var payload = {
					"invoiceItem": this.oSelectedInvoiceItem,
					"manualVsAuto": "MAN",
					"matchOrUnmatchFlag": "M",
					"purchaseDocumentHeader": purchaseDocumentHeader,
					"vendorId": oPOModel.getProperty("/vendorId")
				};
				this.fnTwoWayMatch(payload);
			} else {
				sap.m.MessageToast.show("Select atleast one Invoice item to Match");
			}
		},

		fnTwoWayMatch: function (payload) {
			var that = this;
			var url = "/menabevdev/duplicateCheck/twoWayMatch";
			var busy = new sap.m.BusyDialog();
			busy.open();
			jQuery.ajax({
				type: "POST",
				contentType: "application/json",
				url: url,
				dataType: "json",
				data: JSON.stringify(payload),
				async: true,
				success: function (data, textStatus, jqXHR) {
					busy.close();
					var oPOModel = this.oPOModel;
					var invoiceItems = oPOModel.getProperty("/invoiceItems");
					for (var i = 0; i < invoiceItems.length; i++) {
						if (data.guid === invoiceItems[i].guid) {
							invoiceItems[i] = data;
						}
					}
					oPOModel.setProperty("/invoiceItems", invoiceItems);
					oPOModel.setProperty("/changeIndicators", true);
					oPOModel.refresh();
					this.fnHideMatchedPO();
					this.oSelectedInvoiceItem = "";
					this.getView().byId("ItemMatchInvoiceTableId").removeSelections();
					POServices.onNonPoSave("", that);
				}.bind(this),
				error: function (err) {
					busy.close();
					sap.m.MessageToast.show(err.statusText);
				}.bind(this)
			});
		},

	});

});