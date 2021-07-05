sap.ui.define([
	"com/menabev/AP/controller/BaseController"
], function (BaseController) {
	"use strict";

	return BaseController.extend("com.menabev.AP.controller.SchedulerLogs", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.menabev.AP.view.SchedulerLogs
		 */
		onInit: function () {
			this.setFilterBar();
		},
		
		setFilterBar: function () {
				var filterBar = this.getView().byId("SchedulerFilterBarId");
				filterBar._oFiltersButton.setVisible(false);
				filterBar._oHideShowButton
					.setType("Default").addStyleClass("sapUiSizeCompact");
				filterBar._oClearButtonOnFB.setType("Default").addStyleClass(
					"sapUiSizeCompact");
				filterBar._oSearchButton.setText("Search").addStyleClass("sapUiSizeCompact").setWidth("5rem");
				filterBar._oClearButtonOnFB
					.setVisible(true);
			},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.menabev.AP.view.SchedulerLogs
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.menabev.AP.view.SchedulerLogs
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.menabev.AP.view.SchedulerLogs
		 */
		//	onExit: function() {
		//
		//	}

	});

});