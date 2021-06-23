sap.ui.define([
	"com/menabev/AP/controller/BaseController",
	"sap/ui/model/json/JSONModel"
], function (BaseController, JSONModel) {
	"use strict";
	var num;
	return BaseController.extend("com.menabev.AP.controller.TemplateManagement", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.menabev.AP.view.TemplateManagement
		 */
		onInit: function () {
			this.busyDialog = new sap.m.BusyDialog();
			this.setModel(new JSONModel(), "templateModel");
			this.getModel("templateModel").setSizeLimit(5000);
			this.setModel(new JSONModel(), "postDataModel");
			var oUserDetailModel = this.getOwnerComponent().getModel("oUserDetailModel");
			this.oUserDetailModel = oUserDetailModel;

			var oComponent = this.getOwnerComponent();
			this.oRouter = oComponent.getRouter();
			this.oRouter.getRoute("TemplateManagement").attachPatternMatched(this.onRouteMatched, this);
			this.clicks = 0;
		},

		onRouteMatched: function (oEvent) {
			var templateModel = this.getModel("templateModel");
			templateModel.setData({});
			templateModel.setProperty("/tempDeleteBtnEnabled", false);
			this.getView().byId("btnPrevious").setEnabled(false);
			this.getAllTemplateWithPagination(null, null, 0);
		},

		//function call to load all templates with pagination
		getAllTemplateWithPagination: function (sTempName, sAccNumber, offset) {
			this.getView().byId("templateTableId").removeSelections();
			var payload = {
				"templateName": sTempName,
				"accountNo": sAccNumber,
				"pagination": {
					"limit": 10,
					"offset": offset
				}
			};
			var url = "/menabevdev/NonPoTemplate/getAll";
			this.busyDialog.open();
			jQuery.ajax({
				type: "POST",
				contentType: "application/json",
				url: url,
				dataType: "json",
				data: JSON.stringify(payload),
				async: true,
				success: function (data, textStatus, jqXHR) {
					this.busyDialog.close();
					if (data.length) {
						this.getModel("templateModel").setProperty("/aNonPoTemplate", data);
						var count = data[0].count;
						if (num + 10 >= count || data.length < 10) {
							this.getView().byId("btnNext").setEnabled(false);
						} else {
							this.getView().byId("btnNext").setEnabled(true);
						}
					} else {
						var errorMsg = "No Templates Found";
						sap.m.MessageToast.show(errorMsg);
					}
				}.bind(this),
				error: function (result, xhr, data) {
					this.busyDialog.close();
					var errorMsg = "";
					if (result.status === 504) {
						errorMsg = "Request timed-out. Please refresh page";
						this.errorMsg(errorMsg);
					} else {
						errorMsg = data;
						this.errorMsg(errorMsg);
					}
				}.bind(this)
			});
		},

		//this function will be triggered on Selection of each row in SelectTemplate
		onSelectionChangeTemplate: function (oEvent) {
			var templateModel = this.getModel("templateModel");
			var oSelectedItems = oEvent.getSource().getSelectedItems();
			var selectedFilters = oEvent.getSource().getSelectedContextPaths();
			templateModel.setProperty("/selectedFilters", selectedFilters);
			if (oSelectedItems.length) {
				templateModel.setProperty("/tempDeleteBtnEnabled", true);
			} else {
				templateModel.setProperty("/tempDeleteBtnEnabled", false);
			}
		},

		//function onDeleteTemp is triggered on delete template
		onDeleteofNonpoTemplate: function (oEvent) {
			var templateModel = this.getModel("templateModel");
			var selectedFilters = templateModel.getProperty("/selectedFilters");
			this.oDeletePayload = [];
			for (var i = 0; i < selectedFilters.length; i++) {
				var sTemp = templateModel.getProperty(selectedFilters[i]);
				this.oDeletePayload.push(sTemp.nonPoTemplate.templateId);
			}

			if (!this.oApproveDeleteDialog) {
				this.oApproveDeleteDialog = new sap.m.Dialog({
					type: sap.m.DialogType.Message,
					title: "Confirm",
					content: new sap.m.Text({
						text: "Do you want to Delete these Templates?"
					}),
					beginButton: new sap.m.Button({
						type: sap.m.ButtonType.Emphasized,
						text: "Yes",
						press: function () {
							this.deleteTemplateServiceCall(this.oDeletePayload);
							this.oApproveDeleteDialog.close();
						}.bind(this)
					}),
					endButton: new sap.m.Button({
						text: "No",
						press: function () {
							this.oApproveDeleteDialog.close();
						}.bind(this)
					})
				});
			}

			this.oApproveDeleteDialog.open();
		},

		deleteTemplateServiceCall: function (payload) {
			var sUrl = "/menabevdev/NonPoTemplate/delete";
			var that = this;
			jQuery.ajax({
				method: "DELETE",
				contentType: "application/json",
				url: sUrl,
				dataType: "json",
				data: JSON.stringify(payload),
				async: true,
				success: function (result, textStatus, jqXHR) {
					this.busyDialog.close();
					var message = result.message;
					if (result.status === "Success") {
						sap.m.MessageBox.success(message, {
							actions: [sap.m.MessageBox.Action.OK],
							onClose: function (sAction) {
								that.getAllTemplateWithPagination(null, null, 0);
							}
						});
					} else {
						sap.m.MessageBox.error(message, {
							actions: [sap.m.MessageBox.Action.OK]
						});
					}

				}.bind(this),
				error: function (result, xhr, data) {
					this.busyDialog.close();
					var errorMsg = "";
					if (result.status === 504) {
						errorMsg = "Request timed-out. Please refresh page";
						this.errorMsg(errorMsg);
					} else {
						errorMsg = data;
						this.errorMsg(errorMsg);
					}
				}.bind(this)
			});
		},

		//Frag:CreateEditTemplate
		//onChange function triggered when the Account number in CreateEditTemplate frag is changed
		//Text column value will be updated as same as Account Number
		onChangeAccountNo: function (oEvent) {
			var input = oEvent.getParameter("value"),
				sPath = oEvent.getSource().getBindingContext("postDataModel").getPath();
			this.getModel("postDataModel").setProperty(sPath + "/itemText", input);
			this.errorHandler(oEvent);
		},

		onClickEditTemplate: function (oEvent) {
			this.cFalg = "Edit";
			var sPath = oEvent.getSource().getBindingContext("templateModel").getPath(),
				sTemplate = this.getModel("templateModel").getProperty(sPath);
			this.getModel("postDataModel").setProperty("/", sTemplate);
			this.getModel("postDataModel").setProperty("/updateBtnVisible", true);
			this.getModel("postDataModel").setProperty("/createBtnVisible", false);
			this.getModel("postDataModel").setProperty("/templateTitle", "Update Template");
			if (!this.CreateEditTemplate) {
				this.CreateEditTemplate = sap.ui.xmlfragment("previewTemplate", "com.menabev.AP.fragment.CreateEditTemplate", this);
			}
			this.getView().addDependent(this.CreateEditTemplate);
			this.CreateEditTemplate.open();
		},

		onCancelCreateEditTemp: function () {
			this.CreateEditTemplate.close();
		},

		onClickCreateTemplate: function () {
			var jsonData = {
				nonPoTemplate: {
					"accClerkId": null,
					"basecoderId": null,
					"vendorId": "INC",
					"templateName": "",
					"createdBy": this.oUserDetailModel.getProperty("/loggedInUserMail"),
					"createdAt": null,
					"updatedBy": null,
					"updatedAt": null
				},
				nonPoTemplateItems: []
			};
			this.getModel("postDataModel").setData(jsonData);
			this.cFalg = "Create";
			this.getModel("postDataModel").setProperty("/updateBtnVisible", false);
			this.getModel("postDataModel").setProperty("/createBtnVisible", true);
			this.getModel("postDataModel").setProperty("/templateTitle", "Create Template");
			var oFragmentId = "createEditTemplate",
				oFragmentName = "com.menabev.AP.fragment.CreateEditTemplate";
			if (!this.CreateEditTemplate) {
				this.CreateEditTemplate = sap.ui.xmlfragment(oFragmentId, oFragmentName, this);
			}
			this.getView().addDependent(this.CreateEditTemplate);
			this.CreateEditTemplate.open();
		},

		//function addItem triggered when Add Row button is clicked
		//This function will add an empty row in the Cost Allocation table
		addItem: function (oEvt) {
			var postDataModel = this.getModel("postDataModel");
			var postDataModelData = postDataModel.getData();
			if (!postDataModelData.nonPoTemplateItems) {
				postDataModelData.nonPoTemplateItems = [];
			}
			postDataModelData.nonPoTemplateItems.unshift({
				"templateId": "",
				"glAccount": "",
				"costCenter": "",
				"internalOrderId": "",
				"materialDescription": "",
				"crDbIndicator": "H",
				"netValue": "",
				"profitCenter": "",
				"itemText": "",
				"companyCode": "",
				"assetNo": null,
				"subNumber": null,
				"wbsElement": null,
				"isNonPo": true,
				"accountNo": "",
				"allocationPercent": ""
			});
			postDataModel.refresh();
		},

		//function deleteNonPoItemData is triggred when the bin(Delete) icon is clicked in each row in Cost Allocation table
		//This function is associated with frag:CreateEditTemplate
		deleteNonPoItemData: function (oEvent) {
			var nonPOInvoiceModel = this.getModel("postDataModel");
			var index = oEvent.getSource().getParent().getBindingContextPath().split("/")[2];
			nonPOInvoiceModel.getData().nonPoTemplateItems.splice(index, 1);
			nonPOInvoiceModel.refresh();
			var nonPoItemLength = nonPOInvoiceModel.getData().nonPoTemplateItems.length;
			// if (nonPoItemLength === 0) {
			// 	this.getView().byId("btnUpdateSavetemplate").setEnabled(false);
			// }
		},

		//Frag:CreateEditTemplate
		onOkSaveTemplate: function () {
			var alistNonPoData = $.extend(true, [], this.getModel("postDataModel").getProperty("/nonPoTemplateItems")),
				bCostAllocation = false,
				sMsg;
			if (alistNonPoData.length > 0) {
				bCostAllocation = true;
			} else {
				bCostAllocation = false;
				sap.m.MessageBox.error("Please Enter Cost Allocation Details!");
				return;
			}
			if (bCostAllocation) {
				//COST ALLOCATION VALIDATION START 
				var postDataModel = this.getView().getModel("postDataModel");
				var bflag = true;
				for (var i = 0; i < alistNonPoData.length; i++) {
					//To handle validations
					var bValidate = false;
					if (!alistNonPoData[i].accountNo || alistNonPoData[i].accountNoError === "Error") {
						bValidate = true;
						alistNonPoData[i].accountNoError = "Error";
					}
					if (!alistNonPoData[i].glAccount || alistNonPoData[i].glError === "Error") {
						bValidate = true;
						alistNonPoData[i].glError = "Error";
					}
					if (!alistNonPoData[i].costCenter || alistNonPoData[i].costCenterError === "Error") {
						bValidate = true;
						alistNonPoData[i].costCenterError = "Error";
					}
					if (!alistNonPoData[i].allocationPercent || alistNonPoData[i].allocationPercentError === "Error") {
						bValidate = true;
						alistNonPoData[i].allocationPercentError = "Error";
					}

					if (bValidate) {
						bflag = false;
						continue;
					}
				}
				if (!bflag) {
					postDataModel.setProperty("/nonPoTemplateItems", alistNonPoData);
					sMsg = "Please Enter Required Fields Account No., G/L Account,Cost Center & Percentage Allocation!";
					sap.m.MessageBox.error(sMsg);
					return;
				}
				//COST ALLOCATION VALIDATION END
				var templateName = postDataModel.getData().nonPoTemplate.templateName;
				if (!templateName) {
					sMsg = "Please Enter template name!";
					sap.m.MessageBox.error(sMsg);
					return;
				}
				var allocationPercentage = 0;
				for (var t = 0; t < alistNonPoData.length; t++) {
					allocationPercentage = Number(allocationPercentage) + Number(alistNonPoData[t].allocationPercent);
				}
				var jsonData = this.fnOkSaveTemplateCall(this.cFalg);
				if (Number(allocationPercentage) === 100) {
					jsonData = JSON.stringify(jsonData);
					var sUrl, sType;
					if (this.cFalg === "Create") {
						sUrl = "/menabevdev/NonPoTemplate/save";
						sType = "POST";
					} else {
						sUrl = "/menabevdev/NonPoTemplate/update";
						sType = "PUT";
					}
					var that = this;
					$.ajax({
						url: sUrl,
						method: sType,
						async: false,
						contentType: "application/json",
						dataType: "json",
						data: jsonData,
						success: function (result, xhr, data) {
							var message = result.message;
							if (result.status === "Success") {
								sap.m.MessageBox.success(message, {
									actions: [sap.m.MessageBox.Action.OK],
									onClose: function (sAction) {
										if (that.cFalg === "Create") {
											that.clicks = 0;
											num = that.clicks * 10;
											that.getView().byId("btnPrevious").setEnabled(false);
										} else {
											num = that.clicks * 10;
										}
										that.getAllTemplateWithPagination(null, null, num);
										that.CreateEditTemplate.close();
									}
								});
							} else {
								sap.m.MessageBox.error(message, {
									actions: [sap.m.MessageBox.Action.OK]
								});
							}
						}.bind(this),
						error: function (result, xhr, data) {
							sap.m.MessageToast.show("Failed");
						}.bind(this)
					});

				} else {
					sMsg = "Percentage Allocation must be equal to 100!";
					sap.m.MessageBox.error(sMsg);
				}

			}
		},

		fnOkSaveTemplateCall: function (type) {
			var postDataModel = this.getModel("postDataModel"),
				currentDate = new Date().getTime(),
				jsonData;
			if (type === "Create") {
				jsonData = {
					nonPoTemplate: {
						"accClerkId": null,
						"basecoderId": null,
						"vendorId": "INC",
						"templateName": postDataModel.getData().nonPoTemplate.templateName,
						"createdBy": this.oUserDetailModel.getProperty("/loggedInUserMail"),
						"createdAt": currentDate,
						"updatedBy": null,
						"updatedAt": null
					},
					nonPoTemplateItems: []
				};
				if (postDataModel.getData().nonPoTemplateItems) {
					for (var i = 0; i < postDataModel.getData().nonPoTemplateItems.length; i++) {
						jsonData.nonPoTemplateItems.push({
							"glAccount": postDataModel.getData().nonPoTemplateItems[i].glAccount,
							"costCenter": postDataModel.getData().nonPoTemplateItems[i].costCenter,
							"internalOrderId": "",
							"assetNo": null,
							"subNumber": null,
							"wbsElement": null,
							"materialDescription": postDataModel.getData().nonPoTemplateItems[i].materialDescription,
							"crDbIndicator": "H",
							"profitCenter": "",
							"itemText": postDataModel.getData().nonPoTemplateItems[i].itemText,
							"companyCode": "",
							"isNonPo": true,
							"accountNo": postDataModel.getData().nonPoTemplateItems[i].accountNo,
							"allocationPercent": postDataModel.getData().nonPoTemplateItems[i].allocationPercent
						});
					}
				}
			} else {
				if (postDataModel.getData().nonPoTemplateItems) {
					jsonData = {
						nonPoTemplate: {
							"uuid": postDataModel.getData().nonPoTemplate.uuid,
							"templateId": postDataModel.getData().nonPoTemplate.templateId,
							"accClerkId": null,
							"basecoderId": null,
							"vendorId": "INC",
							"templateName": postDataModel.getData().nonPoTemplate.templateName,
							"updatedBy": this.oUserDetailModel.getProperty("/loggedInUserMail"),
							"updatedAt": currentDate
						},
						nonPoTemplateItems: []
					};
					for (var i = 0; i < postDataModel.getData().nonPoTemplateItems.length; i++) {
						jsonData.nonPoTemplateItems.push({
							"itemId": postDataModel.getData().nonPoTemplateItems[i].itemId,
							"templateId": postDataModel.getData().nonPoTemplateItems[i].templateId,
							"glAccount": postDataModel.getData().nonPoTemplateItems[i].glAccount,
							"costCenter": postDataModel.getData().nonPoTemplateItems[i].costCenter,
							"internalOrderId": "",
							"assetNo": null,
							"subNumber": null,
							"wbsElement": null,
							"materialDescription": postDataModel.getData().nonPoTemplateItems[i].materialDescription,
							"crDbIndicator": "H",
							"profitCenter": "",
							"itemText": postDataModel.getData().nonPoTemplateItems[i].itemText,
							"companyCode": "",
							"isNonPo": true,
							"accountNo": postDataModel.getData().nonPoTemplateItems[i].accountNo,
							"allocationPercent": postDataModel.getData().nonPoTemplateItems[i].allocationPercent
						});
					}
				}
			}
			return jsonData;
		},

		onNavBack: function () {
			this.oRouter.navTo("NonPOInvoice");
		},

		onClickSearchTemplate: function () {
			var sTempName = this.getModel("templateModel").getProperty("/inputTempName"),
				sAccNumber = this.getModel("templateModel").getProperty("/inputAccountNumber");
			this.clicks = 0;
			num = this.clicks * 10;
			this.getView().byId("btnPrevious").setEnabled(false);
			this.getAllTemplateWithPagination(sTempName, sAccNumber, this.clicks);
		},

		onCLickClearInputTemp: function () {
			this.getModel("templateModel").setProperty("/inputTempName", null);
			this.getModel("templateModel").setProperty("/inputAccountNumber", null);
			this.clicks = 0;
			num = this.clicks * 10;
			this.getView().byId("btnPrevious").setEnabled(false);
			this.getAllTemplateWithPagination(null, null, this.clicks);
		},

		//Pagination of Template 
		onClickNext: function () {
			if (this.clicks < 0) {
				this.clicks = 0;
				this.clicks += 1;
			} else {
				this.clicks += 1;
			}
			num = this.clicks * 10;
			if (num >= 10) {
				this.getView().byId("btnPrevious").setEnabled(true);
			}
			this.getAllTemplateWithPagination(null, null, num);
		},

		onClickPrevious: function () {
			this.clicks -= 1;
			if (this.clicks <= 0) {
				num = 0;
			} else {
				num = this.clicks * 10;
			}
			if (num === 0) {
				this.getView().byId("btnPrevious").setEnabled(false);
			}
			this.getAllTemplateWithPagination(null, null, num);
		},

		/*Start of Excel Upload*/
		//this function will allow user import cost Allocation data from an excel file
		//function handleImportFromExcel will import the data to cost allocation table from an excel file uploaded from the local system.
		handleImportFromExcel: function (oEvent) {
			var excelFile = oEvent.getParameter("files")[0];
			if (excelFile) {
				var oFormData = new FormData();
				oFormData.set("file", excelFile);
				var url = "/menabevdev/NonPoTemplate/uploadExcel";
				this.busyDialog.open();
				var that = this;
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
					success: function (data, xhr, success) {
						that.busyDialog.close();
						var errorMsg = "";
						if (success.statusText === "Error") {
							errorMsg = "Request timed-out. Please contact your administrator";
							that.errorMsg(errorMsg);
						} else {
							// var arr = $.extend(true, [], that.getModel("postDataModel").getProperty("/nonPoTemplateItems"));
							// arr = arr.concat(data);
							that.getModel("postDataModel").setProperty("/nonPoTemplateItems", data);
							that.getModel("postDataModel").refresh();
							errorMsg = "Succefully imported data from excel file";
							sap.m.MessageToast.show(errorMsg);
						}
					}.bind(this),
					error: function (result, xhr, data) {
						this.busyDialog.close();
						var errorMsg = "";
						if (result.status === 504) {
							errorMsg = "Request timed-out. Please refresh your page";
							this.errorMsg(errorMsg);
						} else {
							errorMsg = result.responseJSON.error;
							this.errorMsg(errorMsg);
						}
					}.bind(this)
				});
			}
		},

		//onFileSizeExceed is triggerred when Excel file size exceeded more than 10MB 
		onFileSizeExceed: function (error) {
			var errorMsg = "File size has exceeded it max limit of 10MB";
			this.errorMsg(errorMsg);
		},

		uploadExcelTypeMismatch: function (oEvent) {
				var errorMsg = "File type Mismatch";
				this.errorMsg(errorMsg);
			}
			/*End of Excel Upload*/

	});

});