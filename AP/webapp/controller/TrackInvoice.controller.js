sap.ui.define([
	"com/menabev/AP/controller/BaseController",
	"sap/ui/model/json/JSONModel",
	"com/menabev/AP/formatter/formatter",
	"sap/ui/Device",
	"sap/m/MessageToast",
	"sap/ui/unified/library",
	"sap/ui/unified/DateTypeRange",
	"sap/ui/core/library",
	"sap/m/MessageBox"
], function (BaseController, JSONModel, formatter, Device, MessageToast, UnifiedLibrary, DateTypeRange, MessageBox) {
	"use strict";

	var mandatoryFields = ["pOrder", "deliveryNote"];

	return BaseController.extend("com.menabev.AP.controller.TrackInvoice", {
		formatter: formatter,

		onInit: function () {
			var oTrackInvoiceModel = this.getOwnerComponent().getModel("oTrackInvoiceModel");
			var oDataAPIModel = this.getOwnerComponent().getModel("oDataAPIModel");
			var oDropDownModel = this.getOwnerComponent().getModel("oDropDownModel");
			// var oVisibilityModel = this.getOwnerComponent().getModel("oVisibilityModel");
			oTrackInvoiceModel.setProperty("/pdfBtnVisible", false);
			var mFilterModel = new sap.ui.model.json.JSONModel();
			this.getView().setModel(mFilterModel, "mFilterModel");
			var oUserDetailModel = this.getOwnerComponent().getModel("oUserDetailModel");
			this.oUserDetailModel = oUserDetailModel;
			// this.oVisibilityModel = oVisibilityModel;
			this.oTrackInvoiceModel = oTrackInvoiceModel;
			this.mFilterModel = mFilterModel;
			this.oDataAPIModel = oDataAPIModel;
			this.oDropDownModel = oDropDownModel;
			var screen = "Web";
			if (sap.ui.Device.system.phone === true) {
				screen = "Phone";
			}
			var deviceModel = new sap.ui.model.json.JSONModel();
			this.getView().setModel(deviceModel, "deviceModel");
			if (screen == "Web") {
				this.getView().getModel("deviceModel").setProperty("/screen", true);
				this.getView().getModel("deviceModel").setProperty("/expand", true);
				this.getView().getModel("deviceModel").setProperty("/buttonText", true);
				this.getView().getModel("deviceModel").setProperty("/buttonIcon", false);
				deviceModel.setProperty("/menubtnVisibility", false);
			} else if (screen == "Phone") {
				this.getView().getModel("deviceModel").setProperty("/screen", false);
				this.getView().getModel("deviceModel").setProperty("/expand", false);
				this.getView().getModel("deviceModel").setProperty("/buttonText", false);
				this.getView().getModel("deviceModel").setProperty("/buttonIcon", true);
				this.getView().byId("ID_TABLE").setContextualWidth("400px");

			}

			this.fnDefaultFilter();
			this.fnVendorSuggest();
			this.fnStatusSuggest();
		},

		fnStatusSuggest: function () {
			var oDropDownModel = this.oDropDownModel;
			var url = "/menabevdev/codesAndtexts/get/uuId=/statusCode=/type=PAY/language=E";
			jQuery
				.ajax({
					url: url,
					type: "GET",
					contentType: "application/json",
					success: function (result) {
						var data = result;
						oDropDownModel.setProperty("/statusCodeSuggest", data);
					},

					error: function (e) {
						var msgText = e;
						MessageToast.show(msgText.responseJSON.error);
					}
				});
		},

		onTableSearch: function (oEvent) {
			var aFilters = [];
			var sQuery = oEvent.getSource().getValue();
			if (sQuery && sQuery.length > 0) {
				var filter = new sap.ui.model.Filter([new sap.ui.model.Filter("vendorName", sap.ui.model.FilterOperator.Contains, sQuery), new sap
					.ui.model.Filter("extInvNum", sap.ui.model.FilterOperator.Contains, sQuery),
					new sap.ui.model.Filter("vendorId", sap.ui.model.FilterOperator.Contains, sQuery)
				], false);
				aFilters.push(filter);
			}
			var oTable = this.byId("idTable");
			var oBinding = oTable.getBinding("items");
			oBinding.filter(aFilters);
		},

		onDownloadInvoice: function (oEvent) {
			var sPath = oEvent.getSource().getSelectedItem().getBindingContext("oTrackInvoiceModel").getPath();
			var oTrackInvoiceModel = this.oTrackInvoiceModel;
			var oList = oTrackInvoiceModel.getProperty(sPath);
			var pdf = oList.invoicePdfId;
			var sUrl = "/menabevdev/document/download/" + pdf;
			jQuery
				.ajax({
					url: sUrl,
					type: "GET",
					contentType: "application/json",
					success: function (result) {
						var data = result;
						var Base64 = data.base64;
						var fileName = data.documentName;
						if (fileName && fileName.split(".") && fileName.split(".")[fileName.split(".").length - 1]) {
							var fileType = fileName.split(".")[fileName.split(".").length - 1].toLowerCase();
						}
						if (!jQuery.isEmptyObject(Base64)) {
							var u8_2 = new Uint8Array(atob(Base64).split("").map(function (c) {
								return c.charCodeAt(0);
							}));
							var a = document.createElement("a");
							document.body.appendChild(a);
							a.style = "display: none";
							var blob = new Blob([u8_2], {
								type: "application/" + fileType,
								name: fileName
							});
							var url = window.URL.createObjectURL(blob);
							a.href = url;
							a.download = fileName;
							a.click();
						}
					},

					error: function (e) {
						var msgText = e;
						MessageToast.show(msgText.responseJSON.error);
					}
				});
		},

		onDownloadExcel: function () {
			var mFilterModel = this.mFilterModel;
			mFilterModel.refresh(true);
			var that = this,
				vendorId = [];
			var invReference = mFilterModel.getProperty("/invReference"),
				selectedCompany = mFilterModel.getProperty("/selectedCompanyCode");
			vendorId = mFilterModel.getProperty("/vendorId");
			// vendorId.push(vendor);
			var statusCode = mFilterModel.getProperty("/statusCode");
			var invDateFrom = mFilterModel.getProperty("/invDateFrom");
			if (invDateFrom !== undefined) {
				if (invDateFrom !== null) {
					var invoiceDateFrom = invDateFrom.getTime();
				} else {
					invoiceDateFrom = 0;
				}
			} else {
				invoiceDateFrom = 0;
			}
			var invDateTo = mFilterModel.getProperty("/invDateTo");
			if (invDateTo !== undefined) {
				if (invDateTo !== null) {
					var invoiceDateTo = invDateTo.getTime();
				} else {
					invoiceDateTo = 0;
				}
			} else {
				invoiceDateTo = 0;
			}
			var reqDateFrom = mFilterModel.getProperty("/receivedDateFrom");
			if (reqDateFrom !== undefined) {
				if (reqDateFrom !== null) {
					var requestCreatedOn = reqDateFrom.getTime();
				} else {
					requestCreatedOn = 0;
				}
			} else {
				requestCreatedOn = 0;
			}
			var reqDateTo = mFilterModel.getProperty("/receivedDateTo");
			if (reqDateTo !== undefined) {
				if (reqDateTo !== null) {
					var requestCreatedTo = reqDateTo.getTime();
				} else {
					requestCreatedTo = 0;
				}
			} else {
				requestCreatedTo = 0;
			}
			var dueDateFrom = mFilterModel.getProperty("/dueDateFrom");
			if (dueDateFrom !== undefined) {
				if (dueDateFrom !== null) {
					var dueDateFromEpoch = dueDateFrom.getTime();
				} else {
					dueDateFromEpoch = 0;
				}
			} else {
				dueDateFromEpoch = 0;
			}
			var dueDateTo = mFilterModel.getProperty("/dueDateTo");
			if (dueDateTo !== undefined) {
				if (dueDateTo !== null) {
					var dueDateToEpoch = dueDateTo.getTime();
				} else {
					dueDateToEpoch = 0;
				}
			} else {
				dueDateToEpoch = 0;
			}
			var obj = {
				"invoiceRefNum": invReference,
				"invoiceDateFrom": invoiceDateFrom,
				"invoiceDateTo": invoiceDateTo,
				"dueDateFrom": dueDateFromEpoch,
				"dueDateTo": dueDateToEpoch,
				"requestCreatedOnFrom": requestCreatedOn,
				"requestCreatedOnTo": requestCreatedTo,
				"vendorId": vendorId,
				"companyCode": selectedCompany,
				"invoiceStatus": statusCode,
				"top": "",
				"skip": ""
			};
			var url = "/menabevdev/trackinvoice/downloadExcel";
			jQuery
				.ajax({
					url: url,
					type: "POST",
					contentType: "application/json",
					data: JSON.stringify(obj),
					success: function (result) {
						var Base64 = result.base64;
						var fileName = result.documentName;
						if (fileName && fileName.split(".") && fileName.split(".")[fileName.split(".").length - 1]) {
							var fileType = fileName.split(".")[fileName.split(".").length - 1].toLowerCase();
						}
						if (!jQuery.isEmptyObject(Base64)) {
							var u8_2 = new Uint8Array(atob(Base64).split("").map(function (c) {
								return c.charCodeAt(0);
							}));
							var a = document.createElement("a");
							document.body.appendChild(a);
							a.style = "display: none";
							var blob = new Blob([u8_2], {
								type: "application/" + fileType,
								name: fileName
							});
							var bUrl = window.URL.createObjectURL(blob);
							a.href = bUrl;
							a.download = fileName;
							a.click();
						}

					},
					error: function (e) {
						var msgText = e;
						MessageToast.show(msgText.responseJSON.error);
					}
				});
		},

		onOpenActivitylog: function (oEvent) {
			var oTrackInvoiceModel = this.oTrackInvoiceModel;
			var sPath = oEvent.getSource().getBindingContext("oTrackInvoiceModel").getPath();
			var oList = oTrackInvoiceModel.getProperty(sPath);
			// var oVisibilityModel = this.oVisibilityModel;
			var olocalModel = new sap.ui.model.json.JSONModel();
			this.getView().setModel(olocalModel, "olocalModel");
			this.olocalModel = olocalModel;
			// oVisibilityModel.setProperty("/paymentStatus", oList);
			olocalModel.setProperty("/paymentStatus", oList);
			if (oList.invoiceStatus === "14") {
				olocalModel.setProperty("/paiUnpaidPostDate", true);
				olocalModel.setProperty("/rejDetails", false);
				olocalModel.setProperty("/paidDetails", true);
			}
			if (oList.invoiceStatus === "15") {
				olocalModel.setProperty("/paiUnpaidPostDate", false);
				olocalModel.setProperty("/rejDetails", true);
				olocalModel.setProperty("/paidDetails", false);
			}
			if (oList.invoiceStatus === "16") {
				olocalModel.setProperty("/paiUnpaidPostDate", false);
				olocalModel.setProperty("/rejDetails", false);
				olocalModel.setProperty("/paidDetails", false);
			}
			if (oList.invoiceStatus === "13") {
				olocalModel.setProperty("/paiUnpaidPostDate", true);
				olocalModel.setProperty("/rejDetails", false);
				olocalModel.setProperty("/paidDetails", false);
			}

			if (!this.fragActivityLog) {
				this.fragActivityLog = sap.ui.xmlfragment("com.menabev.AP.fragment.TrackInvActivityLog", this);
				this.getView().addDependent(this.fragActivityLog);
			}
			this.fragActivityLog.open();
		},

		fncloseActivity: function () {
			this.fragActivityLog.close();
			this.olocalModel.refresh(true);
		},

		fnDefaultFilter: function () {
			var that = this;
			var userDetail = this.oUserDetailModel.getProperty("/loggedinUserDetail");
			var compCode = userDetail["urn:sap:cloud:scim:schemas:extension:custom:2.0:User"].attributes[1].value;
			var mFilterModel = this.mFilterModel;
			var vendorId = mFilterModel.getProperty("/vendorId");
			if (!vendorId) {
				vendorId = [];
				var loggedinUserVendorId = userDetail["urn:sap:cloud:scim:schemas:extension:custom:2.0:User"].attributes[0].value;
				vendorId.push(loggedinUserVendorId);
			}
			mFilterModel.refresh(true);
			// var compCode = "1010";
			mFilterModel.setProperty("/vendorId", vendorId);
			mFilterModel.setProperty("/selectedCompanyCode", compCode);
			var obj = {
				"companyCode": compCode,
				"vendorId": vendorId
			};
			var url = "/menabevdev/trackinvoice/fetchTrackInvoice";
			jQuery
				.ajax({
					url: url,
					type: "POST",
					contentType: "application/json",
					dataType: "json",
					data: JSON.stringify(obj),
					success: function (result) {
						var oTrackInvoiceModel = that.oTrackInvoiceModel;
						oTrackInvoiceModel.setProperty("/invoiceDetails", []);
						var dateFormat = sap.ui.core.format.DateFormat.getDateInstance({
							pattern: "MM/dd/yyyy"
						});
						var data = result.payload;
						var length = data.length;
						if (length === 0) {
							MessageToast.show("No Data Found");
							oTrackInvoiceModel.setProperty("/invoiceDetails", []);
							oTrackInvoiceModel.setProperty("/count", 0);
							oTrackInvoiceModel.refresh(true);
						} else {
							for (var i = 0; i < length; i++) {
								if (data[i].invoiceDate) {
									var invDate = dateFormat.format(new Date(data[i].invoiceDate));
									data[i].invoiceDate = invDate;
								}
								if (data[i].dueDate) {
									var dDate = dateFormat.format(new Date(data[i].dueDate));
									data[i].dueDate = dDate;
								}
								if (data[i].request_created_at) {
									var reqDate = dateFormat.format(new Date(data[i].request_created_at));
									data[i].request_created_at = reqDate;
								}
								if (data[i].postingDate) {
									var pDate = dateFormat.format(new Date(data[i].postingDate));
									data[i].postingDate = pDate;
								}
								if (data[i].clearingDate) {
									var clrDate = dateFormat.format(new Date(data[i].clearingDate));
									data[i].clearingDate = clrDate;
								}
								if (data[i].attachment !== null) {
									oTrackInvoiceModel.setProperty("/pdfBtnVisible", true);
								}
							}
							if (data) {
								oTrackInvoiceModel.setProperty("/invoiceDetails", data);
								oTrackInvoiceModel.setProperty("/count", length);
							}
							oTrackInvoiceModel.refresh();
						}

					},

					error: function (e) {
						var msgText = e;
						MessageToast.show(msgText.responseJSON.error);
					}
				});
		},

		fnGetFilter: function () {
			var userDetail = this.oUserDetailModel.getProperty("/loggedinUserDetail");
			var mFilterModel = this.mFilterModel;
			mFilterModel.refresh(true);
			var that = this,
				vendorId = [];
			var invReference = mFilterModel.getProperty("/invReference"),
				selectedCompany = mFilterModel.getProperty("/selectedCompanyCode");
			vendorId = mFilterModel.getProperty("/vendorId");
			if (!vendorId) {
				vendorId = [];
				var loggedinUserVendorId = userDetail["urn:sap:cloud:scim:schemas:extension:custom:2.0:User"].attributes[0].value;
				vendorId.push(loggedinUserVendorId);
			}
			
			var statusCode = mFilterModel.getProperty("/statusCode");
			var invDateFrom = mFilterModel.getProperty("/invDateFrom");
			if (invDateFrom !== undefined) {
				if (invDateFrom !== null) {
					var invoiceDateFrom = invDateFrom.getTime();
				} else {
					invoiceDateFrom = 0;
				}
			} else {
				invoiceDateFrom = 0;
			}
			var invDateTo = mFilterModel.getProperty("/invDateTo");
			if (invDateTo !== undefined) {
				if (invDateTo !== null) {
					var invoiceDateTo = invDateTo.getTime();
				} else {
					invoiceDateTo = 0;
				}
			} else {
				invoiceDateTo = 0;
			}
			var reqDateFrom = mFilterModel.getProperty("/receivedDateFrom");
			if (reqDateFrom !== undefined) {
				if (reqDateFrom !== null) {
					var requestCreatedOn = reqDateFrom.getTime();
				} else {
					requestCreatedOn = 0;
				}
			} else {
				requestCreatedOn = 0;
			}
			var reqDateTo = mFilterModel.getProperty("/receivedDateTo");
			if (reqDateTo !== undefined) {
				if (reqDateTo !== null) {
					var requestCreatedTo = reqDateTo.getTime();
				} else {
					requestCreatedTo = 0;
				}
			} else {
				requestCreatedTo = 0;
			}
			var dueDateFrom = mFilterModel.getProperty("/dueDateFrom");
			if (dueDateFrom !== undefined) {
				if (dueDateFrom !== null) {
					var dueDateFromEpoch = dueDateFrom.getTime();
				} else {
					dueDateFromEpoch = 0;
				}
			} else {
				dueDateFromEpoch = 0;
			}
			var dueDateTo = mFilterModel.getProperty("/dueDateTo");
			if (dueDateTo !== undefined) {
				if (dueDateTo !== null) {
					var dueDateToEpoch = dueDateTo.getTime();
				} else {
					dueDateToEpoch = 0;
				}
			} else {
				dueDateToEpoch = 0;
			}

			var obj = {
				"invoiceRefNum": invReference,
				"invoiceDateFrom": invoiceDateFrom,
				"invoiceDateTo": invoiceDateTo,
				"dueDateFrom": dueDateFromEpoch,
				"dueDateTo": dueDateToEpoch,
				"requestCreatedOnFrom": requestCreatedOn,
				"requestCreatedOnTo": requestCreatedTo,
				"vendorId": vendorId,
				"companyCode": selectedCompany,
				"invoiceStatus": statusCode,
				"top": "",
				"skip": ""
			};
			var url = "/menabevdev/trackinvoice/fetchTrackInvoice";
			jQuery
				.ajax({
					url: url,
					type: "POST",
					contentType: "application/json",
					dataType: "json",
					data: JSON.stringify(obj),
					success: function (result) {
						var oTrackInvoiceModel = that.oTrackInvoiceModel;
						oTrackInvoiceModel.setProperty("/invoiceDetails", []);
						var dateFormat = sap.ui.core.format.DateFormat.getDateInstance({
							pattern: "MM/dd/yyyy"
						});
						var data = result.payload;
						var length = data.length;
						if (length === 0) {
							MessageToast.show("No Data Found");
							oTrackInvoiceModel.setProperty("/invoiceDetails", []);
							oTrackInvoiceModel.setProperty("/count", 0);
							oTrackInvoiceModel.refresh(true);
						} else {
							for (var i = 0; i < length; i++) {
								if (data[i].invoiceDate) {
									var invDate = dateFormat.format(new Date(data[i].invoiceDate));
									data[i].invoiceDate = invDate;
								}
								if (data[i].dueDate) {
									var dDate = dateFormat.format(new Date(data[i].dueDate));
									data[i].dueDate = dDate;
								}
								if (data[i].request_created_at) {
									var reqDate = dateFormat.format(new Date(data[i].request_created_at));
									data[i].request_created_at = reqDate;
								}
								if (data[i].postingDate) {
									var pDate = dateFormat.format(new Date(data[i].postingDate));
									data[i].postingDate = pDate;
								}
								if (data[i].clearingDate) {
									var clrDate = dateFormat.format(new Date(data[i].clearingDate));
									data[i].clearingDate = clrDate;
								}
								if (data[i].attachment !== null) {
									oTrackInvoiceModel.setProperty("/pdfBtnVisible", true);
								}
							}
							if (data) {
								oTrackInvoiceModel.setProperty("/invoiceDetails", data);
								oTrackInvoiceModel.setProperty("/count", length);
							}
							oTrackInvoiceModel.refresh();
						}

					},

					error: function (e) {
						var msgText = e;
						MessageToast.show(msgText.responseJSON.error);
					}
				});

		},

		fnReciveValidRange: function (oEvent) {
			var mFilterModel = this.mFilterModel;
			var sValue = oEvent.getParameter("value"),
				bValid = oEvent.getParameter("valid");
			if (sValue) {
				if (bValid) {
					mFilterModel.setProperty("/RecivedateRangeState", "None");
				} else {
					mFilterModel.setProperty("/RecivedateRangeState", "Error");

				}
			}

		},

		fnDueValidRange: function (oEvent) {
			var mFilterModel = this.mFilterModel;
			var sValue = oEvent.getParameter("value"),
				bValid = oEvent.getParameter("valid");
			if (sValue) {
				if (bValid) {
					mFilterModel.setProperty("/dueDateRangeState", "None");
				} else {
					mFilterModel.setProperty("/dueDateRangeState", "Error");

				}
			}

		},

		fnInvValidRange: function (oEvent) {
			var mFilterModel = this.mFilterModel;
			var sValue = oEvent.getParameter("value"),
				bValid = oEvent.getParameter("valid");
			if (sValue) {
				if (bValid) {
					mFilterModel.setProperty("/invDateRangeState", "None");
				} else {
					mFilterModel.setProperty("/invDateRangeState", "Error");

				}
			}

		},

		onPressReset: function () {
			var userDetail = this.oUserDetailModel.getProperty("/loggedinUserDetail"),
				compCode = userDetail["urn:sap:cloud:scim:schemas:extension:custom:2.0:User"].attributes[1].value,
				vendorId = [],
				loggedinUserVendorId = userDetail["urn:sap:cloud:scim:schemas:extension:custom:2.0:User"].attributes[0].value;
			vendorId.push(loggedinUserVendorId);
			
			var mFilterModel = this.mFilterModel;
			var oTrackInvoiceModel = this.oTrackInvoiceModel;
			mFilterModel.setProperty("/invReference", "");
			mFilterModel.setProperty("/vendorId", vendorId);
			mFilterModel.setProperty("/selectedCompanyCode", compCode);
			mFilterModel.setProperty("/invDateFrom", null);
			mFilterModel.setProperty("/invDateTo", null);
			mFilterModel.setProperty("/receivedDateFrom", null);
			mFilterModel.setProperty("/receivedDateTo", null);
			mFilterModel.setProperty("/dueDateFrom", null);
			mFilterModel.setProperty("/dueDateTo", null);
			mFilterModel.setProperty("/statusCode", []);
			oTrackInvoiceModel.setProperty("/invoiceDetails", []);
			oTrackInvoiceModel.setProperty("/count", 0);
			oTrackInvoiceModel.refresh(true);
			mFilterModel.refresh(true);
		},

		//Start of Upload Invoice:UploadInvoiceFrag
		//To open Uplaod Invoice Fragment:UploadInvoiceFrag 
		onPressUploadInvoice: function (oEvent) {
			if (!this.UploadInvoice) {
				this.UploadInvoice = sap.ui.xmlfragment("com.menabev.AP.fragment.UploadInvoiceFrag", this);
				this.getView().addDependent(this.UploadInvoice);
			}
			var pdfModel = new JSONModel();
			var aData = {
				"fileName": "",
				"pOrder": "",
				"deliveryNote": "",
				"pOrder_placeholder": "Enter Purchase Order",
				"deliveryNote_placeholder": "Enter Delivery Note",
				"fldVisble": false,
				"crDbPOVisble": false,
				"vstate": {
					"pOrder": "None",
					"deliveryNote": "None",
				}
			};
			pdfModel.setData(aData);
			this.getView().setModel(pdfModel, "pdfModel");
			this.pFlag = false;
			this.UploadInvoice.open();
		},

		//On Cancel Upload Invoice Fragment:UploadInvoiceFrag
		onUploadInvoiceFragCancel: function () {
			this.getView().getModel("pdfModel").setData({});
			this.UploadInvoice.close();
		},

		//File Uploader
		handleUploadChange: function (oEvent) {
			var that = this;
			if (oEvent.getParameter("newValue")) {
				this.file = oEvent.mParameters.files[0];
				this.fileName = this.file.name;
				this.getView().getModel("pdfModel").setProperty("/fileName", this.fileName);
				// encode the file using the FileReader API
				var reader = new FileReader();
				reader.onloadend = function () {
					var base64String = reader.result
						.replace("data:", "")
						.replace(/^.+,/, "");
					var decodedPdfContent = atob(base64String);
					var byteArray = new Uint8Array(decodedPdfContent.length);
					for (var i = 0; i < decodedPdfContent.length; i++) {
						byteArray[i] = decodedPdfContent.charCodeAt(i);
					}
					var blob = new Blob([byteArray.buffer], {
						type: 'application/pdf'
					});
					jQuery.sap.addUrlWhitelist("blob");
					var _pdfurl = URL.createObjectURL(blob);
					var pdfModel = that.getView().getModel("pdfModel");
					pdfModel.setProperty("/Source", _pdfurl);
					pdfModel.refresh();
				};
				reader.readAsDataURL(this.file);
			}
			this.getView().getModel("pdfModel").setProperty("/fileName", this.fileName);
		},

		//function onFileSizeExceed is triggered when upload file size exceeds it limit
		//File size limit is set in XML Fragment:UploadInvoiceFrag
		onFileSizeExceed: function (error) {
			var errorMsg = "File size has exceeded it max limit of 10MB";
			this.errorMsg(errorMsg);
		},

		//Upload Invoice Fragment:UploadInvoiceFrag
		onChangeDeliveryNote: function (oEvent) {
			var input = oEvent.getParameter("value");
			input = input.trim();
			if (!input) {
				oEvent.getSource().setValueState("Error");
			} else {
				oEvent.getSource().setValueState("None");
			}
		},

		//function will trim if the user input is alphabets and special characters on live change
		inputLiveChange: function (oEvent) {
			var oValue = oEvent.getSource().getValue();
			if (isNaN(oValue)) {
				oValue = oValue.replace(/[^\d]/g, '');
				oEvent.getSource().setValue(oValue);
			}
		},

		inputPOValidate: function (oEvent) {
			var PONumber = oEvent.getParameter("value");
			if (!PONumber) {
				oEvent.getSource().setValueState("Error");
			} else {
				oEvent.getSource().setValueState("None");
				this.POOdataServiceCall(PONumber);
			}
		},

		crDbPOChange: function (oEvent) {
			var PONumber = oEvent.getParameter("value");
			if (PONumber) {
				this.POOdataServiceCall(PONumber);
			} else {
				this.pFlag = false;
			}
		},

		//Mandatory field check of Upload Invoice Fragment:UploadInvoiceFrag
		validateMandatoryFields: function () {
			var oData = this.getView().getModel("pdfModel").getData();
			var key;
			var flag = true;
			var transactionType = this.getView().getModel("pdfModel").getProperty("/transactionType");
			if (transactionType === "INVOICE") {
				for (key in oData) {
					if (mandatoryFields.includes(key)) {
						oData[key] = oData[key].trim();
						if (!oData[key]) {
							this.getView().getModel("pdfModel").setProperty("/vstate/" + key, "Error");
							flag = false;
						} else {
							this.getView().getModel("pdfModel").setProperty("/vstate/" + key, "None");
						}
					}
				}
			} else {
				if (!transactionType) {
					this.getView().getModel("pdfModel").setProperty("/vstate/transactionType", "Error");
					flag = false;
				} else {
					this.getView().getModel("pdfModel").setProperty("/vstate/transactionType", "None");
				}
			}
			return flag;
		},

		//App: Track Invoice(Upload Invoice) On submit of Upload Invoice Fragment:UploadInvoiceFrag
		onSubmitUploadInvoice: function () {
			var that = this;
			var pdfModel = this.getView().getModel("pdfModel");
			var oSource = pdfModel.getProperty("/Source");
			if (oSource) {
				var validationState = this.validateMandatoryFields();
				if (validationState) {
					var oFormData = new FormData();
					var transactionType = pdfModel.getProperty("/transactionType");
					var body = JSON.stringify({
						"transactionType": transactionType
					});
					if (transactionType === "INVOICE") {
						if (!this.pFlag) {
							sap.m.MessageToast.show("Please enter valid PO");
							return;
						}
						body = JSON.stringify({
							"transactionType": transactionType,
							"PO_Number": pdfModel.getData().pOrder,
							"Delivery_Note": pdfModel.getData().deliveryNote
						});
					} else {
						var PONumber = pdfModel.getData().crDbPO;
						if (PONumber) {
							if (!this.pFlag) {
								sap.m.MessageToast.show("Please enter valid PO");
								return;
							}
						}
						body = JSON.stringify({
							"transactionType": transactionType,
							"PO_Number": pdfModel.getData().crDbPO
						});
					}
					oFormData.set("body", body);
					// jQuery.sap.domById(oFileUploader.getId() + "-fu").setAttribute("type", "file");
					// oFormData.set("file", jQuery.sap.domById(oFileUploader.getId() + "-fu").files[0]);
					oFormData.set("file", this.file);
					var url = "/menabevdev/uploadInvoice";
					var busy = new sap.m.BusyDialog();
					busy.open();
					jQuery.ajax({
						url: url,
						method: "POST",
						timeout: 0,
						headers: {
							"Accept": "application/json"
						},
						enctype: "multipart/form-data",
						contentType: false,
						processData: false,
						crossDomain: true,
						cache: false,
						data: oFormData,
						success: function (success) {
							busy.close();
							var errorMsg = "";
							if (success.status === "Error") {
								errorMsg = "Request timed-out. Please contact your administrator";
								that.errorMsg(errorMsg);
							} else {
								sap.m.MessageBox.success(success.message, {
									actions: [sap.m.MessageBox.Action.OK],
									onClose: function (sAction) {
										if (sAction === sap.m.MessageBox.Action.OK) {
											that.onUploadInvoiceFragCancel();
										}
									}
								});
							}

						},
						error: function (fail) {
							busy.close();
							var errorMsg = "";
							if (fail.status === 504) {
								errorMsg = "Request timed-out. Please contact your administrator";
								that.errorMsg(errorMsg);
							} else {
								errorMsg = fail.responseJSON.message;
								that.errorMsg(errorMsg);
							}
						}
					});
				} else {
					that.errorMsg("Please enter all mandatory fields");
				}
			} else {
				that.errorMsg("Upload an Invoice to process");
			}
		},

		//Support function to display error message
		errorMsg: function (errorMsg) {
			sap.m.MessageBox.show(
				errorMsg, {
					styleClass: 'sapUiSizeCompact',
					icon: sap.m.MessageBox.Icon.ERROR,
					title: "Error",
					actions: [sap.m.MessageBox.Action.OK],
					onClose: function (oAction) {}
				}
			);
		},
		//End of Upload Invoice Fragment

		onChangeTransactionType: function (oEvent) {
			var pdfModel = this.getView().getModel("pdfModel");
			var selectedKey = oEvent.getSource().getSelectedKey();
			if (selectedKey) {
				pdfModel.setProperty("/vstate/transactionType", "None");
				if (selectedKey === "INVOICE") {
					pdfModel.setProperty("/fldVisble", true);
					pdfModel.setProperty("/pOrder", "");
					pdfModel.setProperty("/vstate/pOrder", "None");
					pdfModel.setProperty("/deliveryNote", "");
					pdfModel.setProperty("/vstate/deliveryNote", "None");
					pdfModel.setProperty("/crDbPOVisble", false);
				} else {
					pdfModel.setProperty("/fldVisble", false);
					pdfModel.setProperty("/crDbPOVisble", true);
					pdfModel.setProperty("/crDbPO", "");
				}
			} else {
				pdfModel.setProperty("/vstate/transactionType", "Error");
			}
		},

		// OData Service call To Validate PO Number
		POOdataServiceCall: function (PONumber) {
			this.pFlag = false;
			var pdfModel = this.getView().getModel("pdfModel"),
				oDataModel = this.getView().getModel("oDPODetailsModel");
			oDataModel.read("/HeaderSet('" + PONumber + "')?$format=json", {
				async: false,
				success: function (oData, oResponse) {
					sap.m.MessageToast.show("PO Validated");
					this.pFlag = true;
				}.bind(this),
				error: function (error) {
					var errorMsg = "";
					errorMsg = JSON.parse(error.response.body);
					errorMsg = errorMsg.error.message.value;
					this.errorMsg(errorMsg);
				}.bind(this)
			});
		},

		uploadFileTypeMismatch: function (oEvent) {
			var fileType = oEvent.getParameter("fileType");
			var errorMsg = "Please select only PDF file.";
			this.errorMsg(errorMsg);
		}

	});

});