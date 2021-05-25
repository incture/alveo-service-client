sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/json/JSONModel",
	"sap/ui/core/ValueState",
	"sap/m/MessageBox",
	"sap/m/MessageToast"
], function (Controller, JSONModel, ValueState, MessageBox, MessageToast) {
	"use strict";

	return Controller.extend("com.menabev.AP.controller.ConfigCockpit", {
		onInit: function () {
			// var MasterData = {
			// 	schedulerConfigurationdto: [{
			// 		"isActive": false
			// 	}, {
			// 		"isActive": false
			// 	}],
			// 	configurationDto: {
			// 		"createdBy": null,
			// 		"createdAt": null,
			// 		"updatedBy": null,
			// 		"updatedAt": null,
			// 		"ocrSource": "",
			// 		"defaultTaxCode": "",
			// 		"companyCode": "",
			// 		"configurationId": "f0ca0680-8e30-4c02-bcc1-decbd4dae004",
			// 		"maximumNoofUsers": "",
			// 		"version": "CURRENT"
			// 	},
			// 	vendorDetailsDto: [{
			// 		"vendorId": "",
			// 		"companyCode": "",
			// 		"autoPosting": false,
			// 		"partialPosting": false,
			// 		"autoRejection": false
			// 	}],
			// 	mailTemplateDto: [{
			// 		"subject": "",
			// 		"actionType": "Exception Mail Template",
			// 		"body": "",
			// 		"mailTemplateId": "fcf20584-e7e2-4c65-9091-bfc65d840265",
			// 		"configurationId": "f0ca0680-8e30-4c02-bcc1-decbd4dae004"
			// 	}, {
			// 		"subject": "",
			// 		"actionType": "Rejection Mail Template",
			// 		"body": "",
			// 		"mailTemplateId": "9761e59d-8a3a-460b-a873-82a3921b00e7",
			// 		"configurationId": "f0ca0680-8e30-4c02-bcc1-decbd4dae004"
			// 	}],
			// 	emailTeamDto: [{
			// 		"emailId": [""],
			// 		"actionType": "Accounts Payablle Mailbox Id",
			// 		"isActive": true,
			// 		"emailTeamApid": "0bccc8cf-d65b-4fa8-9b5c-36099503a678",
			// 		"configurationId": "f0ca0680-8e30-4c02-bcc1-decbd4dae004"
			// 	}, {
			// 		"emailId": [""],
			// 		"actionType": "Accounts Payable Scanning Team",
			// 		"isActive": true,
			// 		"emailTeamApid": "8411d7c7-6a14-4331-89cd-0b066ac2ad66",
			// 		"configurationId": "f0ca0680-8e30-4c02-bcc1-decbd4dae004"
			// 	}]
			// };
			// var oMasterModel = new JSONModel(MasterData);
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
			var oPODetailModel = new sap.ui.model.odata.ODataModel("DEC_NEW/sap/opu/odata/sap/Z_ODATA_SERV_OPEN_PO_SRV");
			this.getView().setModel(oPODetailModel, "oPODetailModel");
			this.handleLoadCompany();
			oMasterModel.refresh();
			this._fnGetMasterData();
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
					masterModel.setData(data);
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

		onCompanySelect: function (oEvent) {
			var that = this;
			var sKey = oEvent.getSource().getSelectedKey();
			var oMasterModel = this.getView().getModel("oMasterModel");
			oMasterModel.getData().companyCode = sKey;
			var taxUrl = "DEC_NEW/sap/opu/odata/sap/ZRTV_RETURN_DELIVERY_SRV/TaxCodeSet?$filter=companyCode eq '" + sKey + "'";
			$.ajax({
				type: "GET",
				url: taxUrl,
				async: true,
				dataType: "json",
				contentType: "application/json; charset=utf-8",
				error: function (err) {

				},
				success: function (data, textStatus, jqXHR) {
					var mTaxModel = new JSONModel({
						"results": data.d.results
					});

					that.getView().setModel(mTaxModel, "mTaxModel");
					oMasterModel.getData().configurationDto.defaultTaxCode = data.d.results[0].taxCode;
					oMasterModel.refresh();
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
				oEvent.getSource().setValue("");
			} else {
				oEvent.getSource().setValueState("None");
			}
		},

		fnValidateDate: function (oEvent) {
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
				odatePicker.setValueStateText("Start Date should be lesser than End Date");
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
			var aList = this.byId("idProductsTable").getSelectedContextPaths();
			this.byId("idProductsTable").removeSelections();
			if (aList.length === 0) {
				MessageToast.show("Please select items to delete");
			} else {
				for (var i = aList.length - 1; i >= 0; i--) {
					oMasterModel.getData().vendorDetailsDto.splice(aList[i].split("/").pop(), 1);
				}
				oMasterModel.refresh();
			}
		},
		// Vendor search and validation starts
		onVendorSelected: function () {
			this.vendorFlag = true;
		},

		searchVendorId: function (oEvent) {
			oEvent.getSource().setValueState("None");
			this.vendorFlag = false;
			var searchVendorModel = new sap.ui.model.json.JSONModel();
			this.getView().setModel(searchVendorModel, "suggestionModel");
			var value = oEvent.getParameter("suggestValue").trim();
			if (value && value.length > 2) {
				var url = "DEC_NEW/sap/opu/odata/sap/ZAP_VENDOR_SRV/VendSearchSet?$filter=SearchString eq '" + value + "'";
				searchVendorModel.loadData(url, null, true);
				searchVendorModel.attachRequestCompleted(null, function () {
					searchVendorModel.refresh();
				});
			}
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
			oMasterModel.getData().emailTeamDto[0].emailId.push("");
			oMasterModel.refresh();
		},

		fnAddAPScanTeam: function () {
			var oMasterModel = this.getView().getModel("oMasterModel");
			oMasterModel.getData().emailTeamDto[1].emailId.push("");
			oMasterModel.refresh();
		},

		fnDeleteAPMailBox: function (oEvent) {
			var oMasterModel = this.getView().getModel("oMasterModel");
			var aList = this.byId("apMailboxTable").getSelectedContextPaths();
			this.byId("apMailboxTable").removeSelections();
			if (aList.length === 0) {
				MessageToast.show("Please select items to delete");
			} else {
				for (var i = aList.length - 1; i >= 0; i--) {
					oMasterModel.getData().emailTeamDto[0].emailId.splice(aList[i].split("/").pop(), 1);
				}
				oMasterModel.refresh();
			}
		},

		fnDeleteAPScanTeam: function (oEvent) {
			var oMasterModel = this.getView().getModel("oMasterModel");
			var aList = this.byId("apScanTable").getSelectedContextPaths();
			this.byId("apScanTable").removeSelections();
			if (aList.length === 0) {
				MessageToast.show("Please select items to delete");
			} else {
				for (var i = aList.length - 1; i >= 0; i--) {
					oMasterModel.getData().emailTeamDto[1].emailId.splice(aList[i].split("/").pop(), 1);
				}
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
			var data2 = sData.schedulerConfigurationdto;
			for (var j = 0; j < data2.length; j++) {
				if (data2[j].isActive) {
					for (var _i = 0, _Object$keys = Object.keys(data2[j]); _i < _Object$keys.length; _i++) {
						var i2 = _Object$keys[_i];
						if (!data2[j][i2] && (i2 == "startDate" || i2 == "endDate" || i2 == "frequencyNumber" || i2 == "frequencyUnit")) {
							this.getView().getModel("baseModel").setProperty("/" + i2 + j + "Error", "Error");
							bError = true;
						}
					}
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
				$.ajax({
					type: "POST",
					url: "/menabevdev/configurationCockpit",
					dataType: "json",
					data: JSON.stringify(MasterData),
					contentType: "application/json",
					async: true,
					error: function (err) {
						// sap.m.MessageToast.show("Destination Failed");
					},
					success: function (data, textStatus, jqXHR) {
						MessageBox.success("Configuration saved successfully");
					}

				});
			} else {
				MessageBox.success("Please enter all the mandatory fields highlighted");
			}
		},

	});
});