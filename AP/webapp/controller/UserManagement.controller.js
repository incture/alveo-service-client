sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/m/MessageBox"
], function (Controller, MessageBox) {
	"use strict";

	return Controller.extend("com.menabev.AP.controller.UserManagement", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.menabev.AP.view.UserManagement
		 */
		onInit: function () {
			var that = this;
			this.oHeader = {
				"Accept": "application/json",
				"Content-Type": "application/json",
			};
			var oUserDetailModel = this.getOwnerComponent().getModel("oUserDetailModel");
			this.oUserDetailModel = oUserDetailModel;
			var userGroup = oUserDetailModel.getProperty("/loggedinUserGroup");
			this.oRouter = sap.ui.core.UIComponent.getRouterFor(this);
			this.oRouter.attachRoutePatternMatched(function (oEvent) {
				if (oEvent.getParameter("name") === "UserManagement") {
					if (userGroup === "IT_Admin") {
						that.getUserDetails("admin");
						that.fetchAllGroups();
					} else if (userGroup === "Supplier_Admin") {
						that.getUserDetails();
						that.fetchAllGroups();
					} else {
						that.oRouter.navTo("Dashboard");
					}
				}
			});
		},
		onAddNewUser: function () {
			this.oRouter.navTo("CreateUser", {
				id: "new"
			});
		},
		onEditUserDetails: function (oEvent) {
			var oContextObj = oEvent.getSource().getBindingContext("oUserDetailModel").getObject();
			var oUserId = oContextObj.id;
			this.oRouter.navTo("CreateUser", {
				id: oUserId
			});
		},
		onAddNewGroup: function () {
			this.oRouter.navTo("CreateGroup", {
				id: "new"
			});
		},
		onEditGroupDetails: function (oEvent) {
			var oContextObj = oEvent.getSource().getBindingContext("oUserDetailModel").getObject();
			var oUserId = oContextObj.id;
			this.oRouter.navTo("CreateGroup", {
				id: oUserId
			});
		},
		getUserDetails: function (admin) {
			var that = this;
			var oUserDetailModel = this.oUserDetailModel;
			var oServiceModel = new sap.ui.model.json.JSONModel();
			var userGroup = oUserDetailModel.getProperty("/loggedinUserGroup");
			var loggedinUserVendorId = oUserDetailModel.setProperty("/loggedinUserVendorId");
			var userGroup = oUserDetailModel.getProperty("/loggedinUserGroup");
			var sUrl = "/IDPDEST/service/scim/Users";
			var busy = new sap.m.BusyDialog();
			var oHeader = {
				"Content-Type": "application/scim+json"
			};
			if (userGroup === "IT_Admin") {
				var groups = ["Supplier_Admin"];
			} else {
				var groups = ["Supplier_Executive"];
			}
			busy.open();
			oServiceModel.loadData(sUrl, "", true, "GET", false, false, oHeader);
			oServiceModel.attachRequestCompleted(function (oEvent) {
				busy.close();
				var data = oEvent.getSource().getData();
				var user, userdata = [],
					vendorId, groupData;
				if (data.Resources) {
					for (var i = 0; i < data.Resources.length; i++) {
						user = data.Resources[i];
						if (user.groups) {
							for (var j = 0; j < user.groups.length; j++) {
								groupData = groups.indexOf(user.groups[j].value);
								if (data["urn:sap:cloud:scim:schemas:extension:custom:2.0:User"] && data[
										"urn:sap:cloud:scim:schemas:extension:custom:2.0:User"]
									.attributes) {
									vendorId = user["urn:sap:cloud:scim:schemas:extension:custom:2.0:User"].attributes[0].value;
								}
								if (groupData >= 0) {
									if (userGroup === "IT_Admin") {
										userdata.push(user);
									} else if (loggedinUserVendorId === vendorId) {
										userdata.push(user);
									}
								}
							}
						}
					}
					oUserDetailModel.setProperty("/users", userdata);
				}
			});
		},
		// getUserDetails1: function (id) {
		// 	var oUserDetailModel = this.oUserDetailModel;
		// 	var oServiceModel = new sap.ui.model.json.JSONModel();
		// 	var sUrl = "/IDPDEST/service/scim/Groups/" + id;
		// 	var busy = new sap.m.BusyDialog();
		// 	var oHeader = {
		// 		"Content-Type": "application/scim+json"
		// 	};
		// 	busy.open();
		// 	oServiceModel.loadData(sUrl, "", true, "GET", true, false, oHeader);
		// 	oServiceModel.attachRequestCompleted(function (oEvent) {
		// 		busy.close();
		// 		var data = oEvent.getSource().getData();
		// 		if (data.Resources) {
		// 			oUserDetailModel.setProperty("/users", data.Resources);
		// 		}
		// 	});
		// },
		fetchAllGroups: function () {
			var oUserDetailModel = this.oUserDetailModel;
			var oServiceModel = new sap.ui.model.json.JSONModel();
			var sUrl = "/IDPDEST/service/scim/Groups";
			var busy = new sap.m.BusyDialog();
			var oHeader = {
				"Content-Type": "application/scim+json"
			};
			oServiceModel.loadData(sUrl, "", true, "GET", false, false, oHeader);
			oServiceModel.attachRequestCompleted(function (oEvent) {
				var data = oEvent.getSource().getData();
				if (data.Resources) {
					oUserDetailModel.setProperty("/groupList", data.Resources);
				}
			});
		},

		onResetPwd: function (oEvent) {
			var that = this;
			var oContextObj = oEvent.getSource().getBindingContext("oUserDetailModel").getObject();
			var oName = oContextObj.name.givenName + " " + oContextObj.name.familyName;
			var oUserId = oContextObj.id;
			var oEmailId = oContextObj.emails[0].value;
			var oMsg = "Are you sure you want to reset password for : " + oName + " (" + oUserId + ") ?";
			sap.m.MessageBox.confirm(oMsg, {
				styleClass: "sapUiSizeCompact",
				actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						that.triggerResetPwdServ(oName, oUserId, oEmailId);
					}
				}
			});
		},

		//function to send reset password link to the Email ID
		triggerResetPwdServ: function (oName, oUserId, oEmailId) {
			var sUrl = "/IDPDEST/service/users/forgotPassword";
			var oPayload = {
				"identifier": oEmailId
			};
			var oHeader = {
				"Content-Type": "application/json; charset=utf-8"
			};
			var oServiceModel = new sap.ui.model.json.JSONModel();
			var oBusyDialog = new sap.m.BusyDialog();
			oBusyDialog.open();
			oServiceModel.loadData(sUrl, JSON.stringify(oPayload), true, "POST", false, false, oHeader);
			oServiceModel.attachRequestCompleted(function (oEvent) {
				oBusyDialog.close();
				sap.m.MessageBox.success("Reset Password link has been sent to the Email ID -  " + oEmailId);
			});
			oServiceModel.attachRequestFailed(function (oEvent) {
				oBusyDialog.close();
				sap.m.MessageBox.error(oEvent.getParameters().errorobject.responseText);
			});
		},

		onDeleteUser: function (oEvent) {
			var that = this;
			var oContextObj = oEvent.getSource().getBindingContext("oUserDetailModel").getObject();
			var oUserId = oContextObj.id;
			var oName = oContextObj.name.givenName + " " + oContextObj.name.familyName;
			sap.m.MessageBox.confirm("Are you sure you want to delete - " + oName + " (" + oUserId + ")", {
				styleClass: "sapUiSizeCompact",
				actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
				onClose: function (oAction) {
					if (oAction === "YES") {
						that.triggerDeleteUserServ(oName, oUserId, oContextObj);
					}
				}
			});
		},

		//function to delete User
		triggerDeleteUserServ: function (oName, oUserId, oUserData) {
			var that = this;
			var oUrl = "/IDPDEST/service/scim/Users/" + oUserId;
			var oBusyDialog = new sap.m.BusyDialog();
			oBusyDialog.open();
			var oServiceModel = new sap.ui.model.json.JSONModel();
			oServiceModel.loadData(oUrl, null, true, "DELETE", false, false);
			oServiceModel.attachRequestCompleted(function (oEvent) {
				oBusyDialog.close();
				if (oEvent.getParameter("success")) {
					that.getUserDetails();
					sap.m.MessageToast.show(oName + " (" + oUserId + ")" + " is deleted successfully");
				} else {
					var oMsg = oEvent.getParameters().errorobject.responseText;
					sap.m.MessageBox.error(oMsg);
				}
			});
			oServiceModel.attachRequestFailed(function (oEvent) {
				var sStatus = oEvent.getParameters();
				var oMsg = sStatus.errorobject.responseText;
				oBusyDialog.close();
				sap.m.MessageBox.error(oMsg);
			});
		},

		onViewGroups: function (oEvent) {
			var that = this;
			var oUserDetailModel = this.oUserDetailModel;
			var oContextObj = oEvent.getSource().getBindingContext("oUserDetailModel").getObject();
			if (oContextObj.groups) {
				oUserDetailModel.setProperty("/groups", oContextObj.groups);
				oUserDetailModel.refresh();
				this.userGroup = sap.ui.xmlfragment("com.menabev.AP.fragment.userGroup", this);
				this.getView().addDependent(this.userGroup);
				this.userGroup.open();
			} else {
				var msg = "No Groups Assigned for this user";
				sap.m.MessageToast.show(msg);
			}
		},

		onCloseGroupFragment: function () {
			this.userGroup.close();
		},

		onSearchUserList: function (oEvent) {
			var that = this;
			var value = oEvent.getSource().getValue();
			var filters = [];
			var oFilter = new sap.ui.model.Filter([new sap.ui.model.Filter("id", sap.ui.model.FilterOperator.Contains, value),
				new sap.ui.model.Filter("displayName", sap.ui.model.FilterOperator.Contains, value),
				new sap.ui.model.Filter("emails/0/value", sap.ui.model.FilterOperator.Contains, value),
				new sap.ui.model.Filter("phoneNumbers/0/value", sap.ui.model.FilterOperator.Contains, value)
			]);
			filters.push(oFilter);
			var oBinding = this.getView().byId("USERMANAGEMENT").getBinding("items");
			oBinding.filter(filters);
		},

		onSearchGroupList: function (oEvent) {
			var that = this;
			var value = oEvent.getSource().getValue();
			var filters = [];
			var oFilter = new sap.ui.model.Filter([new sap.ui.model.Filter("id", sap.ui.model.FilterOperator.Contains, value),
				new sap.ui.model.Filter("urn:sap:cloud:scim:schemas:extension:custom:2.0:Group/description", sap.ui.model.FilterOperator.Contains,
					value),
				new sap.ui.model.Filter("urn:sap:cloud:scim:schemas:extension:custom:2.0:Group/groupId", sap.ui.model.FilterOperator.Contains,
					value)

			]);
			filters.push(oFilter);
			var oBinding = this.getView().byId("USERGROUPS").getBinding("items");
			oBinding.filter(filters);
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.menabev.AP.view.UserManagement
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.menabev.AP.view.UserManagement
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.menabev.AP.view.UserManagement
		 */
		//	onExit: function() {
		//
		//	}

	});

});