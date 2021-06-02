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
			var oUserDetailModel = this.getOwnerComponent().getModel("oUserDetailModel");
			this.oUserDetailModel = oUserDetailModel;
			var userGroup = oUserDetailModel.getProperty("/loggedinUserGroup");
			var obj = [];
			StaticDataModel.loadData("model/staticData.json");
			this.oRouter = sap.ui.core.UIComponent.getRouterFor(this);
			this.oRouter.attachRoutePatternMatched(function (oEvent) {
				if (oEvent.getParameter("name") === "App") {}
			});
			StaticDataModel.attachRequestCompleted(function (oEvent) {
				var navItems = StaticDataModel.getProperty("/navItems");
				if (userGroup === "IT_Admin") {
					obj.push(navItems[0]);
					obj.push(navItems[1]);
					obj.push(navItems[2]);
					obj.push(navItems[3]);
					that.oRouter.navTo("UserManagement");
				} else if (userGroup === "Accountant") {
					obj.push(navItems[0]);
					that.oRouter.navTo("Inbox");
				} else if (userGroup === "Buyer") {
					obj.push(navItems[0]);
					that.oRouter.navTo("Inbox");
				} else if (userGroup === "Process_Lead") {
					obj.push(navItems[0]);
					that.oRouter.navTo("Inbox");
				} else if (userGroup === "Supplier_Admin") {
					obj.push(navItems[1]);
					obj.push(navItems[2]);
					that.oRouter.navTo("UploadInvoice");
				} else if (userGroup === "Supplier_Executive") {
					obj.push(navItems[1]);
					that.oRouter.navTo("UploadInvoice");
				}

				StaticDataModel.setProperty("/leftPane", obj);
				StaticDataModel.refresh();
				if (userGroup != "IT_Admin") {
					that.getView().byId("sideNav").getItems()[0].addStyleClass("sideNavItemSelected");
				} else {
					that.getView().byId("sideNav").getItems()[2].addStyleClass("sideNavItemSelected");
				}
			});
		},
		onSideNavItemSelection: function (oEvent) {
			var items = oEvent.getSource().getParent().getItems();
			for (var i = 0; i < items.length; i++) {
				items[i].removeStyleClass("sideNavItemSelected");
			}
			oEvent.getSource().addStyleClass("sideNavItemSelected");
			var key = oEvent.getSource().getKey();
			this.oRouter.navTo(key);
		},
		onUserDetailPressed: function (oEvent) {
			var that = this;
			var oButton = oEvent.getSource();
			if (!that.userDetail) {
				that.userDetail = sap.ui.xmlfragment("com.menabev.AP.fragment.userDetail", this);
				that.getView().addDependent(that.userDetail);
			}
			that.userDetail.openBy(oButton);
		},
		onPressLogout: function (oEvent) {
			sap.m.URLHelper.redirect("/my/logout", false);
		},

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