sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox"
], function (Controller, JSONModel, MessageBox) {
	"use strict";

	var mandatoryFields = ["pOrder", "deliveryNote"];

	return Controller.extend("com.menabev.AP.controller.UploadInvoice", {

		onInit: function () {
			this.busyDialog = new sap.m.BusyDialog();
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

		_doAjax: function (sUrl, sMethod, oData, bAbort) {
			if (bAbort && this.PrevAjax) {
				this.PrevAjax.abort();
			}
			if (oData) {
				oData = JSON.stringify(oData);
			}
			var xhr = $.ajax({
				url: sUrl,
				method: sMethod,
				data: oData || ""
			});
			if (bAbort) {
				this.PrevAjax = xhr;
			}
			return xhr;

		},

		//Upload Invoice Fragment:UploadInvoiceFrag
		inputErrorHandler: function (oEvent) {
			var input = oEvent.getParameter("value");
			if (!input) {
				oEvent.getSource().setValueState("Error");
			} else {
				oEvent.getSource().setValueState("None");
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
					this.busyDialog.open();
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
							that.busyDialog.close();
							var errorMsg = "";
							if (success.status === "Error") {
								errorMsg = "Request timed-out. Please contact your administrator";
								that.errorMsg(errorMsg);
							} else {
								MessageBox.success(success.message, {
									actions: [MessageBox.Action.OK],
									onClose: function (sAction) {
										if (sAction === MessageBox.Action.OK) {
											// that.UploadInvoice.close();
											that.onUploadInvoiceFragCancel();
										}
									}
								});
							}

						},
						error: function (fail) {
							that.busyDialog.close();
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
				oDataModel = this.getView().getModel("ZP2P_API_PODETAILS_SRV");
			oDataModel.read("/HeaderSet('" + PONumber + "')?$format=json", {
				async: false,
				success: function (oData, oResponse) {
					sap.m.MessageToast.show("PO Validated");
					this.pFlag = true;
				}.bind(this),
				error: function (error) {
					var errorMsg = "";
					errorMsg = JSON.parse(error.responseText);
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