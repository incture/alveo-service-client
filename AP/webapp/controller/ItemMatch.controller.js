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
		
		getItemMatchPOData: function(){
			var arr = [];
			var getReferencedByPO = $.extend(true, [], this.oPOModel.getProperty("/purchaseOrders"));
			for (var i = 0; i < getReferencedByPO.length; i++) {
				arr = arr.concat(getReferencedByPO[i].poItem);
			}
			this.oPOModel.setProperty("/aItemMatchPO",arr);
		},

		onNavback: function(){
			this.oRouter.navTo("PO");
		}

	});

});