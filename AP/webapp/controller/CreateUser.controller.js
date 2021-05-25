sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"com/menabev/AP/formatter/formatter"
], function (Controller, formatter) {
	"use strict";

	return Controller.extend("com.menabev.AP.controller.CreateUser", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.menabev.AP.view.CreateUser
		 */
		onInit: function () {
			var that = this;
			this.oRouter = sap.ui.core.UIComponent.getRouterFor(this);
			var oUserDetailModel = this.getOwnerComponent().getModel("oUserDetailModel");
			this.oUserDetailModel = oUserDetailModel;
			var StaticDataModel = this.getOwnerComponent().getModel("StaticDataModel");
			this.StaticDataModel = StaticDataModel;
			var oMandatoryModel = this.getOwnerComponent().getModel("oMandatoryModel");
			this.oMandatoryModel = oMandatoryModel;
			var oVisibilityModel = this.getOwnerComponent().getModel("oVisibilityModel");
			this.oVisibilityModel = oVisibilityModel;
			var oDropDownModel = this.getOwnerComponent().getModel("oDropDownModel");
			this.oDropDownModel = oDropDownModel;

			this.fetchCountryList();
			this.oRouter.attachRoutePatternMatched(function (oEvent) {
				if (oEvent.getParameter("name") === "CreateUser") {
					that.id = oEvent.getParameter("arguments").id;
					oVisibilityModel.setProperty("/createUser", {});
					oMandatoryModel.setProperty("/createUser", {});
					oUserDetailModel.setProperty("/createUser", {});
					if (that.id !== "new") {
						that.fetchUser(that.id);
						oVisibilityModel.setProperty("/createUser/editable", false);
					} else {
						oVisibilityModel.setProperty("/createUser/editable", true);
					}
				}
			});

		},

		fetchCountryList: function () {
			var oDropDownModel = this.oDropDownModel;
			var oServiceModel = new sap.ui.model.json.JSONModel();
			var sUrl = "/IDPDEST/md/countries";
			var oHeader = {
				"Content-Type": "application/scim+json"
			};
			oServiceModel.loadData(sUrl, "", true, "GET", false, false, oHeader);
			oServiceModel.attachRequestCompleted(function (oEvent) {
				var data = oEvent.getSource().getData();
				var arr = [];
				for (var i = 0; i < Object.keys(data).length; i++) {
					arr.push({
						key: Object.keys(data)[i],
						value: data[Object.keys(data)[i]]
					});
				}
				oDropDownModel.setSizeLimit(arr.length);
				oDropDownModel.setProperty("/country", arr);
			});
		},

		onNavBack: function () {
			this.oRouter.navTo("UserManagement");
		},
		fetchUser: function (pid) {
			var oUserDetailModel = this.oUserDetailModel;
			var oServiceModel = new sap.ui.model.json.JSONModel();
			var sUrl = "/IDPDEST/service/scim/Users/" + pid;
			var busy = new sap.m.BusyDialog();
			var oHeader = {
				"Content-Type": "application/scim+json"
			};
			oServiceModel.loadData(sUrl, "", true, "GET", false, false, oHeader);
			oServiceModel.attachRequestCompleted(function (oEvent) {
				var data = oEvent.getSource().getData();
				if (data) {
					var obj = {
						"firstName": data.name.givenName,
						"lastName": data.name.familyName,
						"Email": data.emails[0].value,
						"id": data.id,
					};
					if (data.phoneNumbers) {
						obj.phoneNum = data.phoneNumbers[0].value;
					}
					if (data.addresses) {
						obj.country = data.addresses[0].country;
					}
					if (data["urn:sap:cloud:scim:schemas:extension:custom:2.0:User"]) {
						obj.vendorId = data["urn:sap:cloud:scim:schemas:extension:custom:2.0:User"].attributes[0].value;
					}
					if (data["urn:sap:cloud:scim:schemas:extension:custom:2.0:User"]) {
						obj.companyCode = data["urn:sap:cloud:scim:schemas:extension:custom:2.0:User"].attributes[1].value;
					}
					if (data["urn:sap:cloud:scim:schemas:extension:custom:2.0:User"]) {
						obj.purchaseOrg = data["urn:sap:cloud:scim:schemas:extension:custom:2.0:User"].attributes[2].value;
					}
					if (data.groups) {
						obj.userGroup = data.groups[0].value;
					}
					oUserDetailModel.setProperty("/createUser", obj);
				}
			});
		},
		onGroupSelect: function (oEvent) {
			var key = oEvent.getSource().getSelectedKey();
			var text = oEvent.getSource().getSelectedItem().getText();
			var oUserDetailModel = this.oUserDetailModel;
			oUserDetailModel.setProperty("/groupText", text);
		},

		onCreateUser: function () {
			var that = this;
			var oUserDetailModel = this.oUserDetailModel;
			var StaticDataModel = this.StaticDataModel;
			var oMandatoryModel = this.oMandatoryModel;
			var manFields = StaticDataModel.getProperty("/mandatoryFields/userDetail");
			var manLength = manFields.length;
			var data, count = 0,
				sUrl;
			for (var i = 0; i < manLength; i++) {
				data = oUserDetailModel.getProperty("/createUser/" + manFields[i]);
				if (data) {
					oMandatoryModel.setProperty("/createUser/" + manFields[i] + "State", "None");
				} else {
					count += 1;
					oMandatoryModel.setProperty("/createUser/" + manFields[i] + "State", "Error");
				}
			}
			if (count) {
				// message = resourceBundle.getText("MANDATORYFIELDERROR");
				var message = "Please fill all mandatory fields";
				sap.m.MessageToast.show(message);
				return;

			}
			var email = oUserDetailModel.getProperty("/createUser/Email");
			var validEmail = this.validateEmail(email);
			if (validEmail === "E") {
				var message = "Please enter valid Email ID";
				sap.m.MessageToast.show(message);
				oMandatoryModel.setProperty("/createUser/EmailState", "Error");
				return;
			} else {
				oMandatoryModel.setProperty("/createUser/EmailState", "None");
			}
			data = oUserDetailModel.getProperty("/createUser");
			var groupsTemp = [];
			groupsTemp.push({
				"value": data.userGroup,
				"display": data.groupText
			});
			var data = oUserDetailModel.getProperty("/createUser");
			var oPayload = {
				"groups": groupsTemp,
				"userName": data.firstName + " " + data.lastName,
				"name": {
					"givenName": data.firstName,
					"familyName": data.lastName
				},
				"emails": [{
					"value": data.Email
				}],
				"displayName": data.firstName + " " + data.lastName,
				"addresses": [{
					"type": "home",
					"country": data.selectedCountry
				}],
				"phoneNumbers": [{
					"value": data.phoneNo,
					"type": "mobile"
				}],
				"urn:sap:cloud:scim:schemas:extension:custom:2.0:User": {
					"attributes": [{
						"name": "customAttribute1",
						"value": data.vendorId
					}, {
						"name": "customAttribute2",
						"value": data.companyCode
					}, {
						"name": "customAttribute3",
						"value": data.purchaseOrg
					}]
				},
				"password": "Resetme1",
				//"passwordPolicy": "https://accounts.sap.com/policy/passwords/sap/enterprise/1.0",
				"active": true,
				"sendMail": "true",
				"mailVerified": "true"
			};
			var oHeader = {
				"Content-Type": "application/scim+json"
			};
			var oServiceModel = new sap.ui.model.json.JSONModel();
			if (this.id === "new") {
				sUrl = "/IDPDEST/service/scim/Users";
				var mes =  "user is created successfully with User ID";
				oServiceModel.loadData(sUrl, JSON.stringify(oPayload), true, "POST", false, false, oHeader);
			} else {
				sUrl = "/IDPDEST/service/scim/Users/" + this.id;
					var mes =  "user is updated successfully with User ID";
				oPayload.id =  this.id;
				oServiceModel.loadData(sUrl, JSON.stringify(oPayload), true, "PUT", false, false, oHeader);
			}
			var message;
			oServiceModel.attachRequestCompleted(function (oEvent) {
				if (oEvent.getParameter("success")) {
					message = oEvent.getSource().getData().name.givenName + " " + oEvent.getSource().getData().name.familyName +
					mes + oEvent.getSource().getData().id;
					sap.m.MessageBox.success(message, {
						actions: [sap.m.MessageBox.Action.OK],
						onClose: function (e) {
							if (e === "OK") {
								that.oRouter.navTo("UserManagement");
							}
						}
					});
				} else {
					message = oEvent.getParameters().errorobject.responseText;
					sap.m.MessageBox.error(message, {
						actions: [sap.m.MessageBox.Action.OK],
						onClose: function (e) {
							if (e === "OK") {
								that.oRouter.navTo("UserManagement");
							}
						}
					});
				}
				oUserDetailModel.setProperty("/createUser", {});
			});

			oMandatoryModel.refresh();
		},

		validateEmail: function (value) {
			var oRegex = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
			var sText = value;
			if (oRegex.test(sText)) {
				return "S";
			} else {
				return "E";
			}
		},

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf com.menabev.AP.view.CreateUser
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf com.menabev.AP.view.CreateUser
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf com.menabev.AP.view.CreateUser
		 */
		//	onExit: function() {
		//
		//	}

	});

});