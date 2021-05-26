sap.ui.define([
	"sap/ui/core/UIComponent",
	"sap/ui/Device",
	"com/menabev/AP/model/models"
], function (UIComponent, Device, models) {
	"use strict";

	return UIComponent.extend("com.menabev.AP.Component", {

		metadata: {
			manifest: "json"
		},

		/**
		 * The component is initialized by UI5 automatically during the startup of the app and calls the init method once.
		 * @public
		 * @override
		 */
		init: function () {
			// call the base component's init function
			UIComponent.prototype.init.apply(this, arguments);

			// enable routing
			this.getRouter().initialize();

			// set the device model
			this.setModel(models.createDeviceModel(), "device");
			this.getUserDetails();
		},
		getUserDetails: function () {
			var that = this;
			var oServiceModel = new sap.ui.model.json.JSONModel();
			var userModel = this.getModel("oUserDetailModel");
			var oHeader = {
				"Accept": "application/json",
				"Content-Type": "application/json"
			};
			// Service to getLogged in User
			oServiceModel.loadData("../user", "", false, "GET", false, false, this.oHeader);
			var data = oServiceModel.getData();
			userModel.setProperty("/loggedInUserMail", data.emails[0].value);
			userModel.setProperty("/userId", data.id);
			userModel.setProperty("/firstNmae", data.name.givenName);
			userModel.setProperty("/lastNmae", data.name.familyName);
			userModel.refresh();
			this.fetchUserDetails(data.emails[0].value);
		},
		fetchUserDetails: function (mail) {
			var oUserDetailModel = this.getModel("oUserDetailModel");
			var oServiceModel = new sap.ui.model.json.JSONModel();
			var groups = ["IT_Admin", "Accountant", "Buyer", "Process_Lead", "Supplier_Admin", "Supplier_Executive"];
			var sUrl = "/IDPDEST/service/scim/Users/?filter=emails eq " + '"' + mail + '"';
			var busy = new sap.m.BusyDialog();
			var oHeader = {
				"Content-Type": "application/scim+json"
			};
			var data, userGroup = [],
				vendorId, groupData;
			oServiceModel.loadData(sUrl, "", false, "GET", false, false, oHeader);
			// oServiceModel.attachRequestCompleted(function (oEvent) {
			var data = oServiceModel.getData().Resources[0];
			if (data) {
				var group = data.groups,
					length = data.groups.length;
				if (data.groups) {
					for (var i = 0; i < length; i++) {
						groupData = groups.indexOf(data.groups[i].value);
						if (data >= 0) {
							if (data["urn:sap:cloud:scim:schemas:extension:custom:2.0:User"] && data["urn:sap:cloud:scim:schemas:extension:custom:2.0:User"]
								.attributes) {
								vendorId = data["urn:sap:cloud:scim:schemas:extension:custom:2.0:User"].attributes[0].value;
							}
							oUserDetailModel.setProperty("/loggedinUserGroup", data.groups[i]);
							oUserDetailModel.setProperty("/loggedinUserVendorId", vendorId);
							break;
						}

					}
				}
				oUserDetailModel.setProperty("/loggedinUserDetail", data);
			}
			// });
		}

	});
});