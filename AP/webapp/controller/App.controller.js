sap.ui.define([
	"sap/ui/core/mvc/Controller"
], function (Controller) {
	"use strict";

	return Controller.extend("com.menabev.AP.controller.App", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.menabev.AP.view.App
		 */
		onInit: function () {
			var that = this;
			var StaticDataModel = this.getOwnerComponent().getModel("StaticDataModel");
			StaticDataModel.loadData("model/staticData.json");
			StaticDataModel.attachRequestCompleted(function (oEvent) {
				// that.getView().byId("sideNav").getItems().setSelectedKey("UserManagement");
				that.getView().byId("sideNav").getItems()[1].addStyleClass("sideNavItemSelected");
			});
			this.oRouter = sap.ui.core.UIComponent.getRouterFor(this);
			this.oRouter.attachRoutePatternMatched(function (oEvent) {
				if (oEvent.getParameter("name") === "App") {}
			});

		},
		onSideNavItemSelection: function(oEvent){
			var  items = oEvent.getSource().getParent().getItems();
			for (var i=0;i<items.length;i++){
				items[i].removeStyleClass("sideNavItemSelected");
			}
			oEvent.getSource().addStyleClass("sideNavItemSelected");
			var key = oEvent.getSource().getKey();
		}

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.menabev.AP.view.App
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.menabev.AP.view.App
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.menabev.AP.view.App
		 */
		//	onExit: function() {
		//
		//	}

	});

});