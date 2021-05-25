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
			var sUrl = "/IDPDEST/service/scim/Users/?filter=emails eq " + '"' + mail + '"';
			var busy = new sap.m.BusyDialog();
			var oHeader = {
				"Content-Type": "application/scim+json"
			};
			oServiceModel.loadData(sUrl, "", true, "GET", false, false, oHeader);
			oServiceModel.attachRequestCompleted(function (oEvent) {
				var data = oEvent.getSource().getData();
				if (data.Resources) {
					oUserDetailModel.setProperty("/loggedinUserDetail", data.Resources);
				}
			});
		}

	});
});