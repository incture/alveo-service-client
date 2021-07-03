sap.ui.define([
	"com/menabev/AP/controller/BaseController"
], function (BaseController) {
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

			var oPOModel = this.getOwnerComponent().getModel("oPOModel");
			this.oPOModel = oPOModel;

			this.oRouter = this.getOwnerComponent().getRouter();
			this.oRouter.getRoute("ItemMatch").attachPatternMatched(this.onRouteMatched, this);

		},

		onRouteMatched: function (oEvent) {
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
		},

		onNavback: function () {
			var reqId = this.oPOModel.getProperty("/requestId");
			this.oRouter.navTo("PO", {
				id: reqId
			});
		},

		onSelectInvoice: function (oEvent) {
			var sPath = oEvent.getSource().getSelectedItems()[0].getBindingContext("oPOModel").getPath();
			this.oSelectedInvoiceItem = this.oPOModel.getProperty(sPath);
		},

		onClickMatch: function (oEvent) {
			var oPOModel = this.oPOModel;
			if (this.oSelectedInvoiceItem) {
				var sPath = oEvent.getSource().getBindingContext("oPOModel").getPath(),
					oSelectedPOItem = oPOModel.getProperty(sPath);
				
				//Payload Logic //BackTracking to get purchaseDocumentHeader
				//Get POHeader based on selected POItem 
				var getReferencedByPO = $.extend(true, [], this.oPOModel.getProperty("/purchaseOrders"));
				for (var i = 0; i < getReferencedByPO.length; i++) {
					if(oSelectedPOItem.documentNumber === getReferencedByPO[i].documentNumber){
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
						if(data.guid === invoiceItems[i].guid) {
							invoiceItems[i] = data;
						}
					}
					oPOModel.setProperty("/invoiceItems", invoiceItems);
					oPOModel.refresh();
					this.oSelectedInvoiceItem = "";
					this.getView().byId("ItemMatchInvoiceTableId").removeSelections();
				}.bind(this),
				error: function (err) {
					busy.close();
					sap.m.MessageToast.show(err.statusText);
				}.bind(this)
			});
		},

	});

});