sap.ui.define([
	"sap/ui/core/mvc/Controller"
], function (Controller) {
	"use strict";

	return Controller.extend("com.menabev.AP.controller.ItemMatch", {

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
			this.oRouter.navTo("PO");
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
				var payload = {
					"invoiceItem": this.oSelectedInvoiceItem,
					"manualVsAuto": "MAN",
					"matchOrUnmatchFlag": "M",
					"purchaseDocumentHeader": "",
					"purchaseDocumentItem": oSelectedPOItem,
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
					this.oPOModel.setProperty("/", data);
				}.bind(this),
				error: function (err) {
					busy.close();
					sap.m.MessageToast.show(err.statusText);
				}.bind(this)
			});
		},

	});

});