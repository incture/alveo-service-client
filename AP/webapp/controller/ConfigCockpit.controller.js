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
			var oPODetailModel = new sap.ui.model.odata.ODataModel("DEC_NEW/sap/opu/odata/sap/Z_ODATA_SERV_OPEN_PO_SRV");
			this.getView().setModel(oPODetailModel, "oPODetailModel");
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
					if (!data.schedulerConfigurationdto) {
						// var data = {
						// 	"vendorDetailsDto": [{
						// 		"createdBy": null,
						// 		"createdAt": null,
						// 		"updatedBy": null,
						// 		"updatedAt": null,
						// 		"vendorId": "a96ccdba-1a96-437e-aaaa-09c7f69f2219",
						// 		"companyCode": "1011",
						// 		"autoPosting": false,
						// 		"partialPosting": true,
						// 		"autoRejection": true,
						// 		"configurationId": "d36fb9ba-e55a-4965-9cb3-c759b2cbbeef",
						// 		"vendor_id_payload": null
						// 	}, {
						// 		"createdBy": null,
						// 		"createdAt": null,
						// 		"updatedBy": null,
						// 		"updatedAt": null,
						// 		"vendorId": "64223ad4-6b83-4f27-b808-3d74a3d34ad9",
						// 		"companyCode": "1234",
						// 		"autoPosting": false,
						// 		"partialPosting": true,
						// 		"autoRejection": true,
						// 		"configurationId": "d36fb9ba-e55a-4965-9cb3-c759b2cbbeef",
						// 		"vendor_id_payload": null
						// 	}, {
						// 		"createdBy": null,
						// 		"createdAt": null,
						// 		"updatedBy": null,
						// 		"updatedAt": null,
						// 		"vendorId": "abfaba72-51fd-489e-aadf-34b1ea922af0",
						// 		"companyCode": "1001",
						// 		"autoPosting": true,
						// 		"partialPosting": false,
						// 		"autoRejection": false,
						// 		"configurationId": "d36fb9ba-e55a-4965-9cb3-c759b2cbbeef",
						// 		"vendor_id_payload": null
						// 	}, {
						// 		"createdBy": null,
						// 		"createdAt": null,
						// 		"updatedBy": null,
						// 		"updatedAt": null,
						// 		"vendorId": "4637ce3e-86a0-4c71-a231-e1a110aa8f54",
						// 		"companyCode": "1011",
						// 		"autoPosting": true,
						// 		"partialPosting": false,
						// 		"autoRejection": false,
						// 		"configurationId": "d36fb9ba-e55a-4965-9cb3-c759b2cbbeef",
						// 		"vendor_id_payload": null
						// 	}],
						// 	"configurationDto": {
						// 		"createdBy": "Dipanjan Test",
						// 		"createdAt": null,
						// 		"updatedBy": null,
						// 		"updatedAt": null,
						// 		"ocrSource": "Abby Flexi Capture",
						// 		"defaultTaxCode": "1234",
						// 		"companyCode": "1234",
						// 		"configurationId": "d36fb9ba-e55a-4965-9cb3-c759b2cbbeef",
						// 		"maximumNoofUsers": 0,
						// 		"version": "CURRENT"
						// 	},
						// 	"emailTeamDto": [{
						// 		"emailId": ["niharika.thakur@incture.com", "niharika.thakur@incture.com"],
						// 		"actionType": "Accounts Payablle Mailbox Id",
						// 		"isActive": true,
						// 		"emailTeamApid": "6d78d1a6-4b2d-46a5-961f-040eb6ca6eb4",
						// 		"configurationId": "d36fb9ba-e55a-4965-9cb3-c759b2cbbeef"
						// 	}, {
						// 		"emailId": ["sowndharya.k@incture.com", "sowndharya.k@incture.com", "niharika.thakur@incture.com"],
						// 		"actionType": "Accounts Payable Scanning Team",
						// 		"isActive": true,
						// 		"emailTeamApid": "5b2c7481-67c2-477c-890a-f3f57cf8d11b",
						// 		"configurationId": "d36fb9ba-e55a-4965-9cb3-c759b2cbbeef"
						// 	}],
						// 	"mailTemplateDto": [{
						// 		"subject": "Exception Subject New 1",
						// 		"actionType": "Exception Mail Template",
						// 		"body": "exception mail body",
						// 		"mailTemplateId": "49661bce-9695-485d-8998-9882eaa1fcd0",
						// 		"configurationId": "d36fb9ba-e55a-4965-9cb3-c759b2cbbeef"
						// 	}, {
						// 		"subject": "Rejection Subject New Dipanjan test",
						// 		"actionType": "Rejection Mail Template",
						// 		"body": "rejection mail body",
						// 		"mailTemplateId": "2e085549-97b5-4bbb-93f0-82f289c280a8",
						// 		"configurationId": "d36fb9ba-e55a-4965-9cb3-c759b2cbbeef"
						// 	}],
						// 	"schedulerConfigurationdto": [{
						// 		"createdBy": null,
						// 		"createdAt": null,
						// 		"updatedBy": null,
						// 		"updatedAt": null,
						// 		"scId": "e3632a11-e6f2-472b-8ca5-65fdcfb92e2c",
						// 		"configurationId": "d36fb9ba-e55a-4965-9cb3-c759b2cbbeef",
						// 		"startDate": "05/29/2021",
						// 		"endDate": "05/31/2021",
						// 		"frequencyNumber": 0,
						// 		"frequencyUnit": "min",
						// 		"isActive": true,
						// 		"actionType": "GRN Scheduler Configuration"
						// 	}, {
						// 		"createdBy": null,
						// 		"createdAt": null,
						// 		"updatedBy": null,
						// 		"updatedAt": null,
						// 		"scId": "644ce7e1-89c8-433a-b915-2fc7e8862a34",
						// 		"configurationId": "d36fb9ba-e55a-4965-9cb3-c759b2cbbeef",
						// 		"startDate": "05/28/2021",
						// 		"endDate": "05/31/2021",
						// 		"frequencyNumber": 0,
						// 		"frequencyUnit": "min",
						// 		"isActive": true,
						// 		"actionType": "Email Scheduler Configuration"
						// 	}]
						// };
					}
					masterModel.setData(data);
					masterModel.referesh();
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
			var Obj = {
				"configurationId": "",
				"emailId": "",
				"emailTeamApid": ""
			};
			oMasterModel.getData().accountsPayableMailbox(Obj);
			oMasterModel.refresh();
		},

		fnAddAPScanTeam: function () {
			var oMasterModel = this.getView().getModel("oMasterModel");
			var Obj = {
				"configurationId": "",
				"emailId": "",
				"emailTeamApid": ""
			};
			oMasterModel.getData().accountsPayableScanningTeam(Obj);
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
						MessageBox.success(data.message);
					}

				});
			} else {
				MessageBox.success("Please enter all the mandatory fields highlighted");
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