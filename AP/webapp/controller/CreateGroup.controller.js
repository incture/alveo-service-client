sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"com/menabev/AP/formatter/formatter"
], function (Controller, formatter) {
	"use strict";

	return Controller.extend("com.menabev.AP.controller.createGroup", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.menabev.AP.view.createGroup
		 */
		onInit: function () {
			var that = this;
			var oUserDetailModel = this.getOwnerComponent().getModel("oUserDetailModel");
			this.oUserDetailModel = oUserDetailModel;
			var StaticDataModel = this.getOwnerComponent().getModel("StaticDataModel");
			this.StaticDataModel = StaticDataModel;
			var oMandatoryModel = this.getOwnerComponent().getModel("oMandatoryModel");
			this.oMandatoryModel = oMandatoryModel;
			var oVisibilityModel = this.getOwnerComponent().getModel("oVisibilityModel");
			this.oVisibilityModel = oVisibilityModel;
			this.oRouter = sap.ui.core.UIComponent.getRouterFor(this);
			this.oRouter.attachRoutePatternMatched(function (oEvent) {
				if (oEvent.getParameter("name") === "CreateGroup") {
					that.id = oEvent.getParameter("arguments").id;
					oMandatoryModel.setProperty("/createGroup", {});
					oUserDetailModel.setProperty("/createGroup", {});
					oVisibilityModel.setProperty("/createGroup", {});
					if (that.id !== "new") {
						that.fetchGroup(that.id);
						oVisibilityModel.setProperty("/createGroup/editable", false);
					} else {
						oVisibilityModel.setProperty("/createGroup/editable", true);
					}
				}
			});

		},

		onNavBack: function () {
			this.oRouter.navTo("UserManagement");
		},
		fetchGroup: function (pid) {
			var oUserDetailModel = this.oUserDetailModel;
			var oServiceModel = new sap.ui.model.json.JSONModel();
			var sUrl = "/IDPDEST/service/scim/Groups/" + pid;
			var busy = new sap.m.BusyDialog();
			var oHeader = {
				"Content-Type": "application/scim+json"
			};
			oServiceModel.loadData(sUrl, "", true, "GET", false, false, oHeader);
			oServiceModel.attachRequestCompleted(function (oEvent) {
				var data = oEvent.getSource().getData();
				if (data) {
					var obj = {
						"displayName": data.Resources.displayName,
						"description": data.Resources[0]["urn:sap:cloud:scim:schemas:extension:custom:2.0:Group"].description,
						"groupName": data.Resources[0]["urn:sap:cloud:scim:schemas:extension:custom:2.0:Group"].name
					};
					oUserDetailModel.setProperty("/createUser", obj);
				}
			});
		},

		onCreateGroup: function () {
			var that = this;
			var oUserDetailModel = this.oUserDetailModel;
			var StaticDataModel = this.StaticDataModel;
			var oMandatoryModel = this.oMandatoryModel;
			var manFields = StaticDataModel.getProperty("/mandatoryFields/createGroup");
			var manLength = manFields.length;
			var data, count = 0;
			for (var i = 0; i < manLength; i++) {
				data = oUserDetailModel.getProperty("/createGroup/" + manFields[i]);
				if (data) {
					oMandatoryModel.setProperty("/createGroup/" + manFields[i] + "State", "None");
				} else {
					count += 1;
					oMandatoryModel.setProperty("/createGroup/" + manFields[i] + "State", "Error");
				}
			}
			if (count) {
				// message = resourceBundle.getText("MANDATORYFIELDERROR");
				var message = "Please fill all mandatory fields";
				sap.m.MessageToast.show(message);
			} else {
				var sUrl;
				data = oUserDetailModel.getProperty("/createGroup");
				var oPayload = {
					"displayName": data.displayName,
					"urn:sap:cloud:scim:schemas:extension:custom:2.0:Group": {
						"name": data.groupName,
						"description": data.description
					}

				};
				var oHeader = {
					"Content-Type": "application/scim+json"
				};
				var oServiceModel = new sap.ui.model.json.JSONModel();
				if (this.id === "new") {
					sUrl = "/IDPDEST/service/scim/Groups";
					oServiceModel.loadData(sUrl, JSON.stringify(oPayload), true, "POST", false, false, oHeader);
				} else {
					sUrl = "/IDPDEST/service/scim/Groups/" + this.id;
					oServiceModel.loadData(sUrl, JSON.stringify(oPayload), true, "PUT", false, false, oHeader);
				}
				var message;
				oServiceModel.attachRequestCompleted(function (oEvent) {
					if (oEvent.getParameter("success")) {
						message = oEvent.getSource().getData().displayName +
							" group is created successfully with group ID - " + oEvent.getSource().getData().id;
						sap.m.MessageBox.success(message, {
							actions: [sap.m.MessageBox.Action.OK],
							onClose: function (e) {
								if (e === "OK") {
									that.oRouter.navTo("UserManagement");
								}
							}
						});
					} else {
						var message = oEvent.getParameters().errorobject.responseText;
						sap.m.MessageBox.error(message, {
							actions: [sap.m.MessageBox.Action.OK],
							onClose: function (e) {
								if (e === "OK") {
									that.oRouter.navTo("UserManagement");
								}
							}
						});
					}
					oUserDetailModel.setProperty("/createGroup", {});
				});
			}
			oMandatoryModel.refresh();
		}

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.menabev.AP.view.createGroup
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.menabev.AP.view.createGroup
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.menabev.AP.view.createGroup
		 */
		//	onExit: function() {
		//
		//	}

	});

});