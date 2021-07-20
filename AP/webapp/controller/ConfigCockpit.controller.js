sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/json/JSONModel",
	"sap/ui/core/ValueState",
	"sap/m/MessageBox",
	"sap/m/MessageToast",
	"com/menabev/AP/formatter/formatter"
], function (Controller, JSONModel, ValueState, MessageBox, MessageToast, formatter) {
	"use strict";

	return Controller.extend("com.menabev.AP.controller.ConfigCockpit", {
		onInit: function () {
			var that = this;
			this.oRouter = sap.ui.core.UIComponent.getRouterFor(this);
			var oMasterModel = new JSONModel();
			this.getView().setModel(oMasterModel, "oMasterModel");
			var baseData = {
				"submitBtn": true,
				"editBtn": false,
				"Editable": true,
				"submitFlag": false,
			};
			var baseModel = new sap.ui.model.json.JSONModel(baseData);
			this.getView().setModel(baseModel, "baseModel");
			baseModel.refresh();
			this.handleLoadCompany();
			oMasterModel.refresh();
			this.oRouter.attachRoutePatternMatched(function (oEvent) {
				if (oEvent.getParameter("name") === "ConfigCockpit") {
					oMasterModel.setData({});
					that._fnGetMasterData();
				}
			});

		},
		_fnGetMasterData: function () {
			var masterModel = this.getView().getModel("oMasterModel");
			$.ajax({
				type: "GET",
				url: "/menabevdev/configurationCockpit",
				async: true,
				error: function (err) {
					// sap.m.MessageToast.show("Destination Failed");
				},
				success: function (data, textStatus, jqXHR) {
					if (!data.schedulerConfigurationdto) {}
					masterModel.setData(data);
					masterModel.refresh();
				}
			});
		},
		onSelect: function (oEvent) {
			oEvent.getSource().setValueState("None");
		},
		onNumberCheck: function (oEvent) {
			if (oEvent.getSource().getValue())
				oEvent.getSource().setValueState("None");
			// var reg = /^\d+$/;
			// if (!reg.test(value)) {
			// 	value.pop();
			// }
		},
		handleLoadCompany: function () {
			var that = this;
			var oPODetailModel = this.getView().getModel("oPODetailModel");
			oPODetailModel.read("/CompanyCodeSet", {
				success: function (oData) {
					var mCompanyModel = new JSONModel({
						"results": oData.results
					});
					that.getView().setModel(mCompanyModel, "mCompanyModel");
				},
				error: function (oError) {
					var mCompanyModel = new JSONModel({
						"results": [{
							"companyCode": 1234,
							"companyName": "abcd"
						}, {
							"companyCode": 1011,
							"companyName": "inc"
						}, {
							"companyCode": 1001,
							"companyName": "tech"
						}]
					});
					that.getView().setModel(mCompanyModel, "mCompanyModel");
					var mTaxModel = new JSONModel({
						"results": [{
							"taxCode": 1234,
							"description": "abcd"
						}, {
							"taxCode": 1011,
							"description": "inc"
						}, {
							"taxCode": 1001,
							"description": "tech"
						}]
					});

					that.getView().setModel(mTaxModel, "mTaxModel");
				}
			});
		},

		/* ************************ Scheduler Configuration :START *********************************************** */
		fnValidateEmail: function (oEvent) {
			var mail = oEvent.getSource().getValue();
			var mailregex = /^\w+[\w-+\.]*\@\w+([-\.]\w+)*\.[a-zA-Z]{2,}$/;
			if (!mailregex.test(mail)) {
				sap.m.MessageToast.show(mail + " is not a valid email address.");
				oEvent.getSource().setValueState("Error");
			} else {
				oEvent.getSource().setValueState("None");
			}
		},

		fnValidateDate: function (oEvent) {
			var data = oEvent.getSource().getValue();
			var dateVal = new Date(data);
			var day = dateVal.getDate();
			if (!day) {
				MessageToast.show("Please enter valid date");
				oEvent.getSource().setValue("");
				return;
			} else {
				oEvent.getSource().setDateValue(dateVal);
			}
			var sPath = oEvent.getSource().getBindingInfo("value").binding.sPath.split("/");
			sPath.pop();
			sPath = sPath.join("/");
			var odatePicker = oEvent.getSource();
			var oMasterModel = this.getView().getModel("oMasterModel");
			var startDate = new Date(oMasterModel.getProperty(sPath + "/startDate")).getTime();
			var endDate = new Date(oMasterModel.getProperty(sPath + "/endDate")).getTime();
			if (startDate && endDate && startDate > endDate) {
				odatePicker.setValueState("Error");
				odatePicker.setValue("");
				odatePicker.setValueStateText("From date should be lesser that To date");
			} else {
				odatePicker.setValueState("None");
			}
		},
		/* ************************ Scheduler Configuration :END *********************************************** */
		/* ************************ Vendor Scheduler Configuration :START *********************************************** */
		fnAddItem: function () {
			var oMasterModel = this.getView().getModel("oMasterModel");
			var aPostingData = oMasterModel.getData().vendorDetailsDto;
			aPostingData.push({
				"vendorId": "",
				"companyCode": "",
				"autoPosting": false,
				"partialPosting": false,
				"autoRejection": false

			});
			oMasterModel.refresh();
		},
		fnDeleteItem: function () {
			var oMasterModel = this.getView().getModel("oMasterModel");
			var aList = this.byId("idProductsTable").getSelectedContexts();
			this.byId("idProductsTable").removeSelections();
			var vendor = oMasterModel.getProperty("/vendorDetailsDto");
			var index;
			if (aList.length === 0) {
				MessageToast.show("Please select items to delete");
			} else {
				for (var i = aList.length - 1; i >= 0; i--) {
					index = aList[i].getPath().split("/").pop();
					vendor.splice(index, 1);
				}
				oMasterModel.setProperty("/vendorDetailsDto", vendor);
				oMasterModel.refresh();
			}
		},
		// Vendor search and validation starts
		onVendorSelected: function () {
			this.vendorFlag = true;
		},

		chkSelectedVendor: function (oEvent) {
			if (this.vendorFlag) {
				oEvent.getSource().setValueState("None");
			} else {
				oEvent.getSource().setValue("").setValueState("Error");
			}
		},
		// Vendor search and validation ends
		/* ************************ Vendor Scheduler Configuration :END *********************************************** */

		/* ************************ Mail Id Configuration :START *********************************************** */
		fnAddAPMailBox: function () {
			var oMasterModel = this.getView().getModel("oMasterModel");
			var Obj = {
				"configurationId": "",
				"emailId": "",
				"emailTeamApid": ""
			};
			oMasterModel.getData().accountsPayableMailbox.push(Obj);
			oMasterModel.refresh();
		},

		fnAddAPScanTeam: function () {
			var oMasterModel = this.getView().getModel("oMasterModel");
			var Obj = {
				"configurationId": "",
				"emailId": "",
				"emailTeamApid": ""
			};
			oMasterModel.getData().accountsPayableScanningTeam.push(Obj);
			oMasterModel.refresh();
		},

		fnDeleteAPMailBox: function (oEvent) {
			var oMasterModel = this.getView().getModel("oMasterModel");
			var aList = this.byId("apMailboxTable").getSelectedContexts();
			var email = oMasterModel.getData().accountsPayableMailbox;
			this.byId("apMailboxTable").removeSelections();
			var index;
			if (aList.length === 0) {
				MessageToast.show("Please select items to delete");
			} else {

				for (var i = aList.length - 1; i >= 0; i--) {
					index = aList[i].getPath().split("/").pop();
					email.splice(index, 1);
				}
				oMasterModel.setProperty("/accountsPayableMailbox", email);
				oMasterModel.refresh();
			}
		},

		fnDeleteAPScanTeam: function (oEvent) {
			var oMasterModel = this.getView().getModel("oMasterModel");
			var aList = this.byId("apScanTable").getSelectedContexts();
			var email = oMasterModel.getData().accountsPayableScanningTeam;
			this.byId("apScanTable").removeSelections();
			var index;
			if (aList.length === 0) {
				MessageToast.show("Please select items to delete");
			} else {
				for (var i = aList.length - 1; i >= 0; i--) {
					index = aList[i].getPath().split("/").pop();
					email.splice(index, 1);
				}
				oMasterModel.setProperty("/accountsPayableScanningTeam", email);
				oMasterModel.refresh();
			}
		},
		/* ************************ Mail Id Configuration :END *********************************************** */
		setBaseData: function () {
			var data = {
				"companyCodeError": "None",
				"defaultTaxCodeError": "None",
				"ocrSourceError": "None",
				"startDate0Error": "None",
				"startDate1Error": "None",
				"endDate0Error": "None",
				"endDate1Error": "None",
				"frequencyNumber0Error": "None",
				"frequencyNumber1Error": "None",
				"frequencyUnit0Error": "None",
				"frequencyUnit1Error": "None",
				"subject0Error": "None",
				"subject1Error": "None",
				"body0Error": "None",
				"body1Error": "None",
				"maximumNoofUsersError": "None",
				"submitBtn": true,
				"submitFlag": true,
				"editBtn": false,
				"Editable": true
			};
			this.getView().getModel("baseModel").setData(data);
		},
		_fnSubmitValidate: function (sData) {
			var data = sData.configurationDto;
			var bError = false;
			this.setBaseData();
			for (var _i2 = 0, _Object$keys2 = Object.keys(data); _i2 < _Object$keys2.length; _i2++) {
				var i = _Object$keys2[_i2];
				if (!data[i] && (i == "companyCode" || i == "defaultTaxCode" || i == "ocrSource" || i == "maximumNoofUsers")) {
					this.getView().getModel("baseModel").setProperty("/" + i + "Error", "Error");
					bError = true;
				}
			}
			// var data2 = sData.schedulerConfigurationdto;
			// for (var j = 0; j < data2.length; j++) {
			// 	if (data2[j].isActive) {
			// 		for (var _i = 0, _Object$keys = Object.keys(data2[j]); _i < _Object$keys.length; _i++) {
			// 			var i2 = _Object$keys[_i];
			// 			if (!data2[j][i2] && (i2 == "startDate" || i2 == "endDate" || i2 == "frequencyNumber" || i2 == "frequencyUnit")) {
			// 				this.getView().getModel("baseModel").setProperty("/" + i2 + j + "Error", "Error");
			// 				bError = true;
			// 			}
			// 		}
			// 	}
			// }

			var accountsPayableMailbox = sData.accountsPayableMailbox,
				email;
			for (var k = 0; k < accountsPayableMailbox.length; k++) {
				email = accountsPayableMailbox[k].emailId;
				if (!email) {
					bError = true;
				}
			}

			var accountsPayableScanningTeam = sData.accountsPayableScanningTeam,
				email1;
			for (var s = 0; s < accountsPayableScanningTeam.length; s++) {
				email1 = accountsPayableScanningTeam[s].emailId;
				if (!email1) {
					bError = true;
				}
			}
			var data3 = sData.mailTemplateDto;
			for (var k = 0; k < data3.length; k++) {
				for (var _j = 0, _Object$keys3 = Object.keys(data3[k]); _j < _Object$keys3.length; _j++) {
					var i3 = _Object$keys3[_j];
					if (!data3[k][i3]) {
						this.getView().getModel("baseModel").setProperty("/" + i3 + k + "Error", "Error");
						bError = true;
					}
				}
			}
			var data4 = sData.vendorDetailsDto;
			for (var a = 0; a < data4.length; a++) {
				data4[a].vendorIdError = "None";
				data4[a].companyCodeError = "None";
				if (!data4[a].vendorId) {
					data4[a].vendorIdError = "Error";
					bError = true;
				}
				if (!data4[a].companyCode) {
					data4[a].companyCodeError = "Error";
					bError = true;
				}
			}
			this.getView().getModel("oMasterModel").refresh();
			return bError;
		},
		onSubmit: function () {
			var MasterData = this.getView().getModel("oMasterModel").getData();
			if (!this._fnSubmitValidate(MasterData)) {
				var schedulerConfigurationdto = MasterData.schedulerConfigurationdto;
				MasterData.schedulerConfigurationdto[0].endDate = formatter.formatSchedulerDate(schedulerConfigurationdto[0].endDate);
				MasterData.schedulerConfigurationdto[1].endDate = formatter.formatSchedulerDate(schedulerConfigurationdto[0].endDate);
				MasterData.schedulerConfigurationdto[0].startDate = formatter.formatSchedulerDate(schedulerConfigurationdto[0].startDate);
				MasterData.schedulerConfigurationdto[1].startDate = formatter.formatSchedulerDate(schedulerConfigurationdto[0].startDate);
				MasterData.schedulerConfigurationdto[0].createdBy = this.oUserDetailModel.getProperty("/loggedInUserMail");
				MasterData.schedulerConfigurationdto[1].createdBy = this.oUserDetailModel.getProperty("/loggedInUserMail");
				var busy = new sap.m.BusyDialog();
				busy.open();
				$.ajax({
					type: "POST",
					url: "/menabevdev/configurationCockpit",
					dataType: "json",
					data: JSON.stringify(MasterData),
					contentType: "application/json",
					async: true,
					error: function (err) {
						busy.close();
						// sap.m.MessageToast.show("Destination Failed");
					},
					success: function (data, textStatus, jqXHR) {
						busy.close();
						MessageBox.success(data.message);
					}

				});
			} else {
				MessageBox.warning("Please enter all the mandatory fields highlighted");
			}
		},

		onCancelChanges: function () {
			var that = this;
			var message = "Are you sure You want to cancel changes?";
			sap.m.MessageBox.warning(message, {
				actions: [sap.m.MessageBox.Action.OK],
				onClose: function (e) {
					if (e === "OK") {
						that._fnGetMasterData();
					}
				}
			});
		}

	});
});