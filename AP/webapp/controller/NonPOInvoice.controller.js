sap.ui.define([
	"com/menabev/AP/controller/BaseController",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"com/menabev/AP/formatter/formatter",
	"sap/ui/core/util/Export",
	"sap/ui/core/util/ExportTypeCSV",
	"sap/ui/core/routing/History",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator",
	"sap/ui/core/Fragment"
], function (BaseController, JSONModel, MessageBox, formatter, Export, ExportTypeCSV, History, Filter, FilterOperator, Fragment) {
	"use strict";

	var mandatoryFields = ["invoiceTotal", "vendorId", "extInvNum", "invoiceDate", "postingDate"];

	return BaseController.extend("com.menabev.AP.controller.NonPOInvoice", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.menabev.AP.view.NonPOInvoice
		 */
		onInit: function () {

			this.busyDialog = new sap.m.BusyDialog();
			this.setModel(new JSONModel(), "nonPOInvoiceModel");
			this.getModel("nonPOInvoiceModel").setSizeLimit(5000);
			this.setModel(new JSONModel(), "postDataModel");
			this.setModel(new JSONModel(), "mRejectModel");
			this.setModel(new JSONModel(), "taxModel");
			this.setModel(new JSONModel(), "templateModel");
			var oUserDetailModel = this.getOwnerComponent().getModel("oUserDetailModel");
			this.oUserDetailModel = oUserDetailModel;

			var oComponent = this.getOwnerComponent();
			var initializeModelData = {
				"invoiceHeader": {
					"attachments": [],
					"balance": "",
					"balanceCheck": true,
					"comments": [],
					"clerkId": 0,
					"createdAtInDB": 0,
					"dueDate": "",
					"extInvNum": "",
					"grossAmount": "",
					"invoiceDate": "",
					"invoiceTotal": "",
					"manualpaymentBlock": "",
					"ocrBatchId": "",
					"paymentBlock": "",
					"paymentBlockDesc": "",
					"paymentStatus": "",
					"paymentTerms": "",
					"postingDate": "",
					"sapInvoiceNumber": 0,
					"shippingCost": 0,
					"subTotal": "",
					"taskOwner": "",
					"taskStatus": "",
					"taxAmount": "",
					"taxCode": "",
					"vendorId": "",
					"vendorName": "",
					"taxValue": ""
				},
				"vstate": {}
			};
			this.getModel("nonPOInvoiceModel").setProperty("/invoiceDetailUIDto", initializeModelData);
			this.getModel("postDataModel").setProperty("/listNonPoItem", []);

			this.getBtnVisibility();
			this.getModel("nonPOInvoiceModel").setProperty("/openPdfBtnVisible", false);
			this.router = oComponent.getRouter();
			this.router.getRoute("NonPOInvoice").attachPatternMatched(this.onRouteMatched, this);
			this.resourceBundle = this.getOwnerComponent().getModel("i18n").getResourceBundle();
			
			var oHeader = {
				"Content-Type": "application/json; charset=utf-8"
			};
			var oLanguage = "E";
			var countryKey = "SA";
			//To load all OData lookups
			this.getPaymentTerm(oHeader, oLanguage);
			this.getPaymentMethod(oHeader, oLanguage);
			this.getPaymentBlock(oHeader, oLanguage);
			this.getTaxCode(oHeader, countryKey);
			this.getCostCenter("1010", oLanguage);
		},

		getBtnVisibility: function () {
			var oVisibilityModel = this.getOwnerComponent().getModel("oVisibilityModel");
			oVisibilityModel.setProperty("/NonPOInvoice", {});
			oVisibilityModel.setProperty("/NonPOInvoice/editable", true);
			oVisibilityModel.setProperty("/NonPOInvoice/PLBtnVisible", false);
			oVisibilityModel.setProperty("/NonPOInvoice/AccBtnVisible", false);
			var loggedinUserGroup = this.oUserDetailModel.getProperty("/loggedinUserGroup");
			if (loggedinUserGroup === "Process_Lead") {
				oVisibilityModel.setProperty("/NonPOInvoice/editable", false);
				oVisibilityModel.setProperty("/NonPOInvoice/PLBtnVisible", true);
				oVisibilityModel.setProperty("/NonPOInvoice/AccBtnVisible", false);
			}
			if (loggedinUserGroup === "Accountant") {
				oVisibilityModel.setProperty("/NonPOInvoice/PLBtnVisible", false);
				oVisibilityModel.setProperty("/NonPOInvoice/AccBtnVisible", true);
			}
		},

		onRouteMatched: function (oEvent) {
			//reading the Arguments from url
			var oArgs = oEvent.getParameter("arguments"),
				requestId = oArgs.id,
				status = oArgs.status;
			if (requestId === "NEW") {
				var initializeModelData = {
					"invoiceHeader": {
						"attachments": [],
						"balance": "",
						"balanceCheck": true,
						"comments": [],
						"clerkId": 0,
						"createdAtInDB": 0,
						"dueDate": "",
						"extInvNum": "",
						"grossAmount": "",
						"invoiceDate": "",
						"invoiceTotal": "",
						"manualpaymentBlock": "",
						"ocrBatchId": "",
						"paymentBlock": "",
						"paymentBlockDesc": "",
						"paymentStatus": "",
						"paymentTerms": "",
						"postingDate": "",
						"sapInvoiceNumber": 0,
						"shippingCost": 0,
						"subTotal": "",
						"taskOwner": "",
						"taskStatus": "",
						"taxAmount": "",
						"taxCode": "",
						"vendorId": "",
						"vendorName": "",
						"taxValue": ""
					},
					"vstate": {}
				};
				this.getModel("nonPOInvoiceModel").setProperty("/invoiceDetailUIDto", initializeModelData);
				this.getModel("postDataModel").setProperty("/listNonPoItem", []);
			}
			// this.busyDialog.open();

			//handle if route has NonPO request Id 
			else if (requestId) {
				this.getNonPOData(requestId);
			}
		},

		//Service call to load Non PO data
		//Parameter: requestId
		getNonPOData: function (requestId) {
			var sUrl = "/menabevdev/invoiceHeader/getInvoiceByRequestId/" + requestId;
			jQuery.ajax({
				method: "GET",
				contentType: "application/json",
				url: sUrl,
				dataType: "json",
				async: true,
				success: function (oData, textStatus, jqXHR) {
					this.busyDialog.close();
					if (oData.responseStatus === "Success") {
						this.getModel("nonPOInvoiceModel").setProperty("/invoiceDetailUIDto", {
							invoiceHeader: oData.invoiceHeaderDto,
							vstate: {}
						});
						this.getModel("postDataModel").setProperty("/listNonPoItem", oData.costAllocationDto);
						var invoicePdfId = this.getModel("nonPOInvoiceModel").getProperty("/invoiceDetailUIDto/invoiceHeader/invoicePdfId");
						if (invoicePdfId) {
							this.getModel("nonPOInvoiceModel").setProperty("/openPdfBtnVisible", true);
						}
					}
					this.getModel("nonPOInvoiceModel").refresh();
					this.getModel("postDataModel").refresh();
				}.bind(this),
				error: function (result, xhr, data) {
					this.busyDialog.close();
					var errorMsg = "";
					if (result.status === 504) {
						errorMsg = "Request timed-out. Please refresh your page";
						this.errorMsg(errorMsg);
					} else {
						errorMsg = data;
						this.errorMsg(errorMsg);
					}
				}.bind(this)
			});
		},

		//function hdrInvAmtCalu is triggered on change of Invoice Amount in the header field
		hdrInvAmtCalu: function (oEvent) {
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel"),
				invoiceAmt = oEvent.getParameter("value");
			if (!invoiceAmt) {
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/vstate/invoiceTotal", "Error");
			} else {
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/vstate/invoiceTotal", "None");
				invoiceAmt = (parseFloat(invoiceAmt)).toFixed(2);
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/invoiceTotal", invoiceAmt);
			}
			this.calculateBalance();
		},

		//function getGLaccountValue is triggered on change of GL Account in the Cost Center table
		//Frag:NonPOCostCenter
		getGLaccountValue: function (oEvent) {
			var glValue = oEvent.getParameter("value");
			var postDataModel = this.getModel("postDataModel");
			var sPath = oEvent.getSource().getBindingContext("postDataModel").getPath();
			if (glValue === "") {
				postDataModel.setProperty(sPath + "/glError", "Error");
			} else {
				postDataModel.setProperty(sPath + "/glError", "None");
			}
		},

		/**
		 * Header Filter function Starts here
		 */
		//Vendor ID input filter function
		fnVendorIdSuggest: function (oEvent) {
			var searchVendorModel = new sap.ui.model.json.JSONModel();
			this.getView().setModel(searchVendorModel, "searchVendorModel");
			var value = oEvent.getParameter("suggestValue");
			if (value && value.length > 2) {
				var url = "DEC_NEW/sap/opu/odata/sap/ZAP_VENDOR_SRV/VendSearchSet?$filter=Search eq '" + value + "'";
				searchVendorModel.loadData(url, null, true);
				searchVendorModel.attachRequestCompleted(null, function () {
					searchVendorModel.refresh();
				});
			}
		},

		//function onChangeVendorId is triggered on change of Vendor ID 
		onChangeVendorId: function (oEvent) {
			this.errorHandler(oEvent);
		},

		searchVendorAddr: function (oEvt) {
			var nonPOInvoiceModel = this.getView().getModel("nonPOInvoiceModel");
			var sVendorName = oEvt.getParameter("selectedItem").getProperty("additionalText");
			nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/vendorName", sVendorName);
			nonPOInvoiceModel.refresh();
		},

		onChangeInvDate: function (oEvent) {
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel");
			nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/invoiceDate", oEvent.getSource()._getSelectedDate().getTime());
			this.errorHandler(oEvent);
		},

		onChangeInvNumber: function (oEvent) {
			this.errorHandler(oEvent);
		},

		onPostingDateChange: function (oEvent) {
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel");
			nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/postingDate", oEvent.getSource()._getSelectedDate().getTime());
			this.errorHandler(oEvent);
		},

		onDueDateChange: function (oEvent) {
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel");
			nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/dueDate", oEvent.getSource()._getSelectedDate().getTime());
		},

		//onChange Header level tax
		onChangeHeaderTax: function (oEvent) {
			var taxDetails = oEvent.getParameters().selectedItem.getBindingContext("oDropDownModel").getObject();
			var postDataModel = this.getModel("postDataModel"),
				nonPOInvoiceModel = this.getModel("nonPOInvoiceModel"),
				taxCode = taxDetails.TxCode,
				taxPer = taxDetails.TaxRate;
			nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/taxPer", taxPer);
			nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/taxConditionType", taxDetails.TaxConditionType);
			if (postDataModel.getProperty("/listNonPoItem")) {
				var length = postDataModel.getProperty("/listNonPoItem").length;
				for (var i = 0; i < length; i++) {
					postDataModel.setProperty("/listNonPoItem/" + i + "/taxCode", taxCode);
					postDataModel.setProperty("/listNonPoItem/" + i + "/taxPer", taxPer);
					postDataModel.setProperty("/listNonPoItem/" + i + "/taxConditionType", taxDetails.TaxConditionType);
				}
			}
			if (taxDetails) {
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/vstate/taxCode", "None");
			} else {
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/vstate/taxCode", "Error");
			}
			this.costAllocationTaxCalc();
			sap.m.MessageToast.show("Tax Code\r" + taxCode + "\r is applied to all Cost Allocation line items");
		},

		//onChange CC item level tax
		onChangeTax: function (oEvent) {
			var taxDetails = oEvent.getParameters().selectedItem.getBindingContext("oDropDownModel").getObject(),
				sPath = oEvent.getSource().getBindingContext("postDataModel").getPath();
			var postDataModel = this.getModel("postDataModel");
			postDataModel.setProperty(sPath + "/taxPer", taxDetails.TaxRate);
			postDataModel.setProperty(sPath + "/taxConditionType", taxDetails.TaxConditionType);
			this.amountCal(oEvent);
		},
		//End of Header Filter function

		//this function is to open PDF for Non PO Invoice
		onPressOpenpdf: function () {
			var documentId = this.getModel("nonPOInvoiceModel").getProperty("/invoiceDetailUIDto/invoiceHeader/invoicePdfId");
			this.fnOpenPDF(documentId);
		},

		//This function will route to TemplateManagement view
		onClickManageTemplate: function () {
			this.router.navTo("TemplateManagement");
		},

		//function onSelectTemplate is triggered on click of Select template
		//To open a Fragment Select Template Frag:SelectTemplate
		onSelectTemplate: function (oEvt) {
			var mandatoryFeildsForCC = this.fnMandatoryFeildsForCC();
			if (!mandatoryFeildsForCC) {
				var templateModel = new sap.ui.model.json.JSONModel();
				this.getView().setModel(templateModel, "templateModel");
				var oFragmentId = "saveTemplate",
					oFragmentName = "com.menabev.AP.fragment.SelectTemplate";
				if (!this.selectTemplateFragment) {
					this.selectTemplateFragment = sap.ui.xmlfragment(oFragmentId, oFragmentName, this);
					this.getView().addDependent(this.selectTemplateFragment);
				}
				templateModel.setProperty("/searchValue", "");
				this.getAllTemplate(); //Call service to fetch the templates
			} else {
				sap.m.MessageToast.show("Fill Invoice Amount, Tax Code and Tax Value to proceed with Cost Allocation");
			}

		},

		//function call to load all templates 
		getAllTemplate: function () {
			var templateModel = this.getModel("templateModel");
			templateModel.setProperty("/allocateTempBtnEnabled", false);
			var url = "/menabevdev/NonPoTemplate/selectNonPoTemplate";
			this.busyDialog.open();
			jQuery.ajax({
				type: "GET",
				contentType: "application/json",
				url: url,
				dataType: "json",
				async: true,
				success: function (data, textStatus, jqXHR) {
					this.busyDialog.close();
					templateModel.setProperty("/aNonPoTemplate", data);
					this.selectTemplateFragment.open();
				}.bind(this),
				error: function (result, xhr, data) {
					this.busyDialog.close();
					var errorMsg = "";
					if (result.status === 504) {
						errorMsg = "Request timed-out. Please refresh your page";
						this.errorMsg(errorMsg);
					} else {
						errorMsg = data;
						this.errorMsg(errorMsg);
					}
				}.bind(this)
			});
		},

		//Frag:getAllTemplate
		//this function will be triggered on Selection of each row in SelectTemplate
		onSelectionChangeTemplate: function (oEvent) {
			var templateModel = this.getModel("templateModel"),
				selectedFilters = oEvent.getSource().getSelectedContextPaths();
			templateModel.setProperty("/selectedFilters", selectedFilters);
			if (selectedFilters.length) {
				templateModel.setProperty("/allocateTempBtnEnabled", true);
			} else {
				templateModel.setProperty("/allocateTempBtnEnabled", false);
			}
		},

		//Frag: SelectTemplate
		//on click of cancel btn To close the Template Dialog
		onSelectTempDialogClose: function () {
			this.selectTemplateFragment.close();
		},

		//Frag:SelectTemplate
		handleTemplateSearch: function (oEvt) {
			var sValue = oEvt.getParameter("query");
			var oFilter = new sap.ui.model.Filter({
				filters: [
					new Filter("templateName", FilterOperator.Contains, sValue),
					new Filter("accountNumber", FilterOperator.Contains, sValue)
				],
				and: false
			});
			var oBinding = sap.ui.getCore().byId("saveTemplate--templateSelectId").getBinding("items");
			oBinding.filter([oFilter]);
		},

		//Frag:SelectTemplate
		//liveChange of selected Amount in Select Template
		onChangePreviewTotalAmt: function (oEvent) {
			var totalAmount = oEvent.getParameter("value");
			var postDataModel = this.getView().getModel("postDataModel");
			var sPath = oEvent.getSource().getBindingContext("postDataModel").getPath();
			if (totalAmount === "") {
				postDataModel.setProperty(sPath + "/glError", "Error");
			} else {
				postDataModel.setProperty(sPath + "/glError", "None");
			}
			var costAllocationList = postDataModel.getProperty(sPath + "/costAllocationList");
			for (var i = 0; i < costAllocationList.length; i++) {
				var partAmount = (Number(costAllocationList[i].distrPerc) * Number(totalAmount)) / 100,
					partAmtValue = this.nanValCheck(partAmount).toFixed(2);
				postDataModel.setProperty(sPath + "/costAllocationList/" + [i] + "/netValue", partAmtValue);
			}
		},

		//Frag:SelectTemplate
		//on click of Allocate 
		onClickPreviewTemplate: function () {
			var selectedFilters = this.getModel("templateModel").getProperty("/selectedFilters");
			var payload = [];
			var bflag = true;
			for (var i = 0; i < selectedFilters.length; i++) {
				//To handle validations
				var bValidate = false;
				var aData = this.getModel("templateModel").getProperty(selectedFilters[i]);
				if (aData.amount === "" || aData.amountError === "Error" || aData.amount === null) {
					bValidate = true;
					this.getModel("templateModel").setProperty(selectedFilters[i] + "/amountError", "Error");
				}
				if (bValidate) {
					bflag = false;
					continue;
				}
			}
			if (!bflag) {
				// postDataModel.setProperty("/listNonPoItem", alistNonPoData);
				var sMsg = "Please Enter Amount for Selected Templates";
				sap.m.MessageBox.error(sMsg);
				return;
			}

			for (var i = 0; i < selectedFilters.length; i++) {
				var sTemp = this.getModel("templateModel").getProperty(selectedFilters[i]);
				payload.push(sTemp);
			}

			//service call to preview the cost allocation
			this.busyDialog.open();
			var sUrl = "/menabevdev/costAllocation/getCostAllocationForTemplate";
			jQuery.ajax({
				method: "POST",
				contentType: "application/json",
				url: sUrl,
				dataType: "json",
				data: JSON.stringify(payload),
				async: true,
				success: function (data, textStatus, jqXHR) {
					this.busyDialog.close();
					this.getModel("postDataModel").setProperty("/previewListNonPoItem", data);
					if (!this.previewTemplateDialog) {
						this.previewTemplateDialog = sap.ui.xmlfragment("previewTemplate", "com.menabev.AP.fragment.PreviewTemplate", this);
					}
					this.getView().addDependent(this.previewTemplateDialog);
					this.previewTemplateDialog.open();

				}.bind(this),
				error: function (result, xhr, data) {
					this.busyDialog.close();
					var errorMsg = "";
					if (result.status === 504) {
						errorMsg = "Request timed-out. Please refresh your page";
						this.errorMsg(errorMsg);
					} else {
						errorMsg = data;
						this.errorMsg(errorMsg);
					}
				}.bind(this)
			});
		},

		//Frag:PreviewTemplate
		onPreviewBack: function () {
			this.previewTemplateDialog.close();
		},

		//Frag:PreviewTemplate
		onOkSelectTemplate: function () {
			var that = this;
			var previewListNonPoItem = this.getModel("postDataModel").getProperty("/previewListNonPoItem");
			// var arr = [];
			var arr = $.extend(true, [], this.getModel("postDataModel").getProperty("/listNonPoItem"));
			for (var i = 0; i < previewListNonPoItem.length; i++) {
				arr = arr.concat(previewListNonPoItem[i].costAllocationList);
			}
			this.getModel("postDataModel").setProperty("/listNonPoItem", arr);
			var postDataModel = this.getModel("postDataModel");
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel"),
				taxCode = nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/taxCode"),
				taxPer = nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/taxPer"),
				taxConditionType = nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/taxConditionType");
			for (var i = 0; i < arr.length; i++) {
				postDataModel.setProperty("/listNonPoItem/" + i + "/taxCode", taxCode);
				postDataModel.setProperty("/listNonPoItem/" + i + "/taxPer", taxPer);
				postDataModel.setProperty("/listNonPoItem/" + i + "/taxConditionType", taxConditionType);
			}
			postDataModel.refresh();
			this.costAllocationTaxCalc();
			this.previewTemplateDialog.close();
			this.selectTemplateFragment.close();
		},

		//function addItem triggered when Add Row button is clicked
		//This function will add an empty row in the Cost Allocation table
		addItem: function (oEvt) {
			var mandatoryFeildsForCC = this.fnMandatoryFeildsForCC();
			if (!mandatoryFeildsForCC) {
				var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel"),
					postDataModel = this.getModel("postDataModel"),
					postDataModelData = postDataModel.getData();
				if (!postDataModelData.listNonPoItem) {
					postDataModelData.listNonPoItem = [];
				}
				postDataModelData.listNonPoItem.unshift({
					"templateId": "",
					"glAccount": "",
					"costCenter": "",
					"taxCode": nonPOInvoiceModel.getData().invoiceDetailUIDto.invoiceHeader.taxCode,
					"taxPer": nonPOInvoiceModel.getData().invoiceDetailUIDto.invoiceHeader.taxPer,
					"taxConditionType": nonPOInvoiceModel.getData().invoiceDetailUIDto.invoiceHeader.taxConditionType,
					"internalOrderId": "",
					"materialDesc": "",
					"crDbIndicator": "H",
					"netValue": "",
					"profitCenter": "",
					"itemText": "",
					"companyCode": "",
					"assetNo": null,
					"subNumber": null,
					"wbsElement": null,
					"isNonPo": true
				});
				postDataModel.refresh();
			} else {
				sap.m.MessageToast.show("Fill Invoice Amount, Tax Code and Tax Value to proceed with Cost Allocation");
			}
		},

		//function deleteNonPoItemData is triggred when the bin(Delete) icon is clicked in each row in Cost Allocation table
		//This function is associated with frag:NonPOCostCenter
		deleteNonPoItemData: function (oEvent) {
			var nonPOInvoiceModel = this.getModel("postDataModel");
			var index = oEvent.getSource().getParent().getBindingContextPath().split("/")[2];
			var sPath = oEvent.getSource().getParent().getBindingContextPath();
			var sValue = nonPOInvoiceModel.getProperty(sPath + "/netValue");
			nonPOInvoiceModel.getData().listNonPoItem.splice(index, 1);
			nonPOInvoiceModel.refresh();
			if (sValue) {
				this.amountCal(oEvent);
			}
		},

		//Support function amountCal is triggered when an row is deleted in the cost allocation table
		//Frag:NonPOCostCenter
		//on Change function that of Amount
		amountCal: function (oEvent) {
			if (!that) {
				var that = this;
			}
			var postDataModel = this.getView().getModel("postDataModel");
			var sPath = oEvent.getSource().getBindingContext("postDataModel").getPath();
			var amountVal = postDataModel.getProperty(sPath + "/netValue");
			if (amountVal === "") {
				postDataModel.setProperty(sPath + "/amountError", "Error");
				sap.m.MessageBox.information("Please enter Amount!");
			} else {
				postDataModel.setProperty(sPath + "/amountError", "None");
			}
			var sDecValue = parseFloat(amountVal).toFixed(2);
			postDataModel.setProperty(sPath + "/netValue", sDecValue);
			this.costAllocationTaxCalc();

		},

		//function costAllocationTaxCalc is used to calculate tax in cost allocation table
		costAllocationTaxCalc: function () {
			var postDataModel = this.getModel("postDataModel"),
				nonPOInvoiceModel = this.getModel("nonPOInvoiceModel"),
				that = this;
			var totalAmt = 0,
				totalTax = 0,
				totalBaseRate = 0;
			if (postDataModel.getProperty("/listNonPoItem")) {
				var length = postDataModel.getProperty("/listNonPoItem").length;
				for (var i = 0; i < length; i++) {
					//Tax Calculations
					var taxPer = postDataModel.getProperty("/listNonPoItem/" + i + "/taxPer"),
						netValue = postDataModel.getProperty("/listNonPoItem/" + i + "/netValue");

					var baseRate = that.nanValCheck(netValue) / (1 + (that.nanValCheck(taxPer) / 100));
					var taxValue = (that.nanValCheck(baseRate) * that.nanValCheck(taxPer)) / 100;
					totalTax += Number(taxValue.toFixed(2));
					postDataModel.setProperty("/listNonPoItem/" + i + "/taxValue", that.nanValCheck(taxValue).toFixed(2));
					postDataModel.setProperty("/listNonPoItem/" + i + "/baseRate", that.nanValCheck(baseRate).toFixed(2));

					//End of Tax Calulations
					if (baseRate && postDataModel.getProperty("/listNonPoItem/" + i +
							"/crDbIndicator") === "H") {
						totalBaseRate += that.nanValCheck(postDataModel.getProperty("/listNonPoItem/" + i + "/baseRate"));

					} else if (baseRate && postDataModel.getProperty("/listNonPoItem/" + i +
							"/crDbIndicator") === "S") {
						totalBaseRate -= that.nanValCheck(postDataModel.getProperty("/listNonPoItem/" + i + "/baseRate"));
					}
				}
				totalBaseRate = that.nanValCheck(totalBaseRate).toFixed(2);
				totalTax = this.nanValCheck(totalTax).toFixed(2);
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/totalBaseRate", totalBaseRate);
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/taxAmount", totalTax);
				this.calculateGrossAmount();
				this.calculateBalance();
				nonPOInvoiceModel.refresh();
				postDataModel.refresh();
			}
		},

		//Support function to validate Amount field in Cost Allocation table based on the Debit/Credit changed
		//Frag:NonPOCostCenter
		nanValCheck: function (value) {
			if (!value || isNaN(value)) {
				return 0;
			} else if (Number(value) === 0 || parseFloat(value) === -0) {
				return 0;
			} else if (!isNaN(value)) {
				return parseFloat(value);
			}
			return value;
		},

		//Frag:NonPOCostCenter
		//Suggest function of Cost Center
		costCenterSuggest: function (oEvent) {
			var sQuery = oEvent.getSource().getValue();
			var sUrl =
				"DEC_NEW/sap/opu/odata/sap/ZAP_NONPO_SEARCH_HELPS_SRV/CostCenterSet?$filter=CompCode eq '0001' and CostCenters eq '*" + sQuery +
				"*'";
			var oHeader = {
				"Content-Type": "application/json; charset=utf-8"
			};
			var costCenterModel = new sap.ui.model.json.JSONModel();
			this.getView().setModel(costCenterModel, "costCenterModel");
			costCenterModel.loadData(sUrl, null, true, "Get", false, false, oHeader);
			costCenterModel.attachRequestCompleted(function (oEvent) {
				costCenterModel.refresh();
			});
		},

		//Frag:NonPOCostCenter
		//on Change function of Cost Center
		onChangeCostCenter: function (oEvent) {
			var sCostCenter = oEvent.getParameter("value");
			var postDataModel = this.getView().getModel("postDataModel");
			var sPath = oEvent.getSource().getBindingContext("postDataModel").getPath();
			if (sCostCenter === "") {
				postDataModel.setProperty(sPath + "/costCenterError", "Error");
			} else {
				postDataModel.setProperty(sPath + "/costCenterError", "None");
			}
		},

		//Frag:NonPOCostCenter
		//Suggest function of Internal Order
		internalOrderSuggest: function (oEvent) {
			var sQuery = oEvent.getSource().getValue();
			/*	var modelData = this.getView().getModel("nonPOInvoiceModel").getData();
				var companyCode = modelData.invoiceDetailUIDto.companyCode;*/
			var sUrl =
				"DEC_NEW/sap/opu/odata/sap/ZAP_NONPO_SEARCH_HELPS_SRV/InternalOrderSearchSet?$filter=Search eq '4' and ImpCompCode eq '0001'";
			var oHeader = {
				"Content-Type": "application/json; charset=utf-8"
			};
			var internalOrderModel = new sap.ui.model.json.JSONModel();
			this.getView().setModel(internalOrderModel, "internalOrderModel");
			internalOrderModel.loadData(sUrl, null, true, "Get", false, false, oHeader);
			internalOrderModel.attachRequestCompleted(function (oEvent) {
				internalOrderModel.refresh();
			});
		},

		//Frag:NonPOCostCenter
		//on Change function of Text
		onChangeText: function (oEvent) {
			var sText = oEvent.getParameter("value");
			var postDataModel = this.getView().getModel("postDataModel");
			var sPath = oEvent.getSource().getBindingContext("postDataModel").getPath();
			if (sText === "") {
				postDataModel.setProperty(sPath + "/itemTextError", "Error");
			} else {
				postDataModel.setProperty(sPath + "/itemTextError", "None");
			}
		},

		//Frag:NonPOCostCenter
		//Suggest function of Company Code
		companyCodeSuggest: function (oEvent) {
			var sQuery = oEvent.getSource().getValue();
			var sUrl =
				"DEC_NEW/sap/opu/odata/sap/ZAP_NONPO_SEARCH_HELPS_SRV/CompanyCodeSet?$filter=subof( '001', CompCode )&$format=json";
			var oHeader = {
				"Content-Type": "application/json; charset=utf-8"
			};
			var companyCodeModel = new sap.ui.model.json.JSONModel();
			this.getView().setModel(companyCodeModel, "companyCodeModel");
			companyCodeModel.loadData(sUrl, null, true, "Get", false, false, oHeader);
			companyCodeModel.attachRequestCompleted(function (oEvent) {
				companyCodeModel.refresh();
			});
		},

		//function onDataExportExcel will triggered on click of excel icon
		//this function will export the cost allocation table data to an excel format
		onDataExportExcel: function (oEvent) {
			var postDataModel = this.getView().getModel("postDataModel");
			var oExport = new Export({

				// Type that will be used to generate the content. Own ExportType's can be created to support other formats
				exportType: new ExportTypeCSV({
					separatorChar: ";"
				}),

				// Pass in the model created above
				models: postDataModel,

				// binding information for the rows aggregation
				rows: {
					path: "/listNonPoItem"
				},

				// column definitions with column name and binding info for the content
				//	aCols = this.createColumnConfig();
				columns: [{
					name: "G/L Account",
					template: {
						content: "{glAccount}"
					}
				}, {
					name: "Description",
					template: {
						content: "{materialDesc}"
					}
				}, {
					name: "Debt/Cred",
					template: {
						content: "{crDbIndicator}"
					}
				}, {
					name: "netValue",
					template: {
						content: "{netValue}"
					}
				}, {
					name: "Cost Centre",
					template: {
						content: "{costCenter}"
					}
				}, {
					name: "Order",
					template: {
						content: "{internalOrderId}"
					}
				}, {
					name: "Profit Centre",
					template: {
						content: "{profitCentre}"
					}
				}, {
					name: "Text",
					template: {
						content: "{itemText}"
					}
				}, {
					name: "Co.Cd",
					template: {
						content: "{companyCode}"
					}
				}]
			});
			// download exported file
			oExport.saveFile("NonPoTemplateData").catch(function (oError) {
				sap.m.MessageBox.error("Error when downloading data. Browser might not be supported!\n\n" + oError);
			}).then(function () {
				oExport.destroy();
			});
		},

		//Tax details Fragment
		//Frag:TaxDetails
		openTaxDetails: function () {
			if (!this.taxDetails) {
				this.taxDetails = sap.ui.xmlfragment("com.menabev.AP.fragment.TaxDetails", this);
				this.getView().addDependent(this.taxDetails);
			}
			this.taxDetails.setModel(this.getModel("postDataModel"), "postDataModel");
			this.taxDetails.open();
		},

		taxDetailsDialogClose: function () {
			this.taxDetails.close();
		},

		//Called when the save button is clicked.
		onNonPoSave: function () {
			var oSaveData = this.fnCreatePayloadSaveSubmit();
			oSaveData.invoiceHeaderDto.taskOwner = this.oUserDetailModel.getProperty("/loggedInUserMail");
			oSaveData.invoiceHeaderDto.docStatus = "Draft";
			var postingDate = oSaveData.invoiceHeaderDto.postingDate;
			if (postingDate) {
				var sUrl = "/menabevdev/invoiceHeader/accountantSave",
					sMethod = "POST";
				this.saveSubmitServiceCall(oSaveData, sMethod, sUrl, "SAVE");
			} else {
				sap.m.MessageBox.error("Please Enter Posting Date!");
			}
		},

		//Called on click of Submit Button.
		onNonPoSubmit: function () {
			var oSubmitData = this.fnCreatePayloadSaveSubmit();
			var bCostAllocation = false;
			//Header Mandatory feilds validation
			var validateMandatoryFields = this.validateMandatoryFields(oSubmitData.invoiceHeaderDto);
			if (!validateMandatoryFields) {
				sap.m.MessageBox.error("Please fill all mandatory fields!");
			}
			if (validateMandatoryFields) {
				if (oSubmitData.costAllocationDto.length > 0) {
					bCostAllocation = true;
				} else {
					bCostAllocation = false;
					sap.m.MessageBox.error("Please Enter Cost Allocation Details!");
				}
			}

			if (validateMandatoryFields && bCostAllocation) {
				//COST ALLOCATION VALIDATION START 
				var postDataModel = this.getView().getModel("postDataModel");
				var alistNonPoData = $.extend(true, [], postDataModel.getProperty("/listNonPoItem"));
				var bflag = true;
				for (var i = 0; i < alistNonPoData.length; i++) {
					var bValidate = false;
					if (!alistNonPoData[i].glAccount || alistNonPoData[i].glError === "Error") {
						bValidate = true;
						alistNonPoData[i].glError = "Error";
					}
					if (!alistNonPoData[i].netValue || alistNonPoData[i].amountError === "Error") {
						bValidate = true;
						alistNonPoData[i].amountError = "Error";
					}
					if (!alistNonPoData[i].costCenter || alistNonPoData[i].costCenterError === "Error") {
						bValidate = true;
						alistNonPoData[i].costCenterError = "Error";
					}
					if (!alistNonPoData[i].itemText || alistNonPoData[i].itemTextError === "Error") {
						bValidate = true;
						alistNonPoData[i].itemTextError = "Error";
					}
					if (bValidate) {
						bflag = false;
						continue;
					}
				}
				if (!bflag) {
					postDataModel.setProperty("/listNonPoItem", alistNonPoData);
					var sMsg = "Please Enter Required Fields G/L Account,Amount,Cost Center & Text!";
					sap.m.MessageBox.error(sMsg);
					return;
				} else {
					//COST ALLOCATION VALIDATION END
					oSubmitData.invoiceHeaderDto.taskOwner = this.oUserDetailModel.getProperty("/loggedInUserMail");
					oSubmitData.invoiceHeaderDto.docStatus = "Created";
					var balance = oSubmitData.invoiceHeaderDto.balance;
					if (this.nanValCheck(balance) !== 0) {
						sap.m.MessageBox.error("Balance is not 0");
						return;
					}
					var totalTax = this.getModel("nonPOInvoiceModel").getProperty("/invoiceDetailUIDto/invoiceHeader/taxAmount");
					var userInputTaxAmount = this.getModel("nonPOInvoiceModel").getProperty("/invoiceDetailUIDto/invoiceHeader/taxValue");
					var taxDifference = this.nanValCheck(userInputTaxAmount) - this.nanValCheck(totalTax);
					if (this.nanValCheck(taxDifference) !== 0) {
						sap.m.MessageBox.error("Tax MisMatched");
						return;
					}

					var sUrl = "/menabevdev/invoiceHeader/accountantSubmit",
						sMethod = "POST";
					this.saveSubmitServiceCall(oSubmitData, sMethod, sUrl);
				}
			}

		},

		//function saveSubmitServiceCall is triggered on SUBMIT or SAVE
		saveSubmitServiceCall: function (oData, sMethod, sUrl, save) {
			var that = this;
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel");
			this.busyDialog.open();
			$.ajax({
				url: sUrl,
				method: sMethod,
				async: false,
				contentType: "application/json",
				dataType: "json",
				data: JSON.stringify(oData),
				success: function (data, xhr, result) {
					this.busyDialog.close();
					var message = data.responseStatus;
					var ReqId = data.invoiceHeaderDto.requestId;
					nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/requestId", ReqId);
					if (result.status === 200) {
						sap.m.MessageBox.success(message, {
							actions: [sap.m.MessageBox.Action.OK],
							onClose: function (sAction) {
								if (!save) {
									that.router.navTo("Inbox");
								}
							}
						});
					} else {
						sap.m.MessageBox.information(message, {
							actions: [sap.m.MessageBox.Action.OK]
						});
					}
				}.bind(this),
				error: function (result, xhr, data) {
					this.busyDialog.close();
					var errorMsg = "";
					if (result.status === 504) {
						errorMsg = "Request timed-out. Please refresh your page";
						this.errorMsg(errorMsg);
					} else {
						errorMsg = data;
						this.errorMsg(errorMsg);
					}
				}.bind(this)
			});
		},

		//this function used to form payload for SUBMIT and SAVE
		fnCreatePayloadSaveSubmit: function (oEvent) {
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel"),
				nonPOInvoiceModelData = nonPOInvoiceModel.getData();

			var objectIsNew = jQuery.extend({}, nonPOInvoiceModelData.invoiceDetailUIDto);
			var postDataModel = this.getModel("postDataModel");
			objectIsNew.costAllocation = [];
			var reqId = objectIsNew.invoiceHeader.requestId ? objectIsNew.invoiceHeader.requestId : null;
			if (postDataModel.getData().listNonPoItem) {
				for (var i = 0; i < postDataModel.getData().listNonPoItem.length; i++) {
					var itemId = postDataModel.getData().listNonPoItem[i].itemId ? postDataModel.getData().listNonPoItem[i].itemId : null;
					var costAllocationId = postDataModel.getData().listNonPoItem[i].costAllocationId ? postDataModel.getData().listNonPoItem[i].costAllocationId :
						null;
					objectIsNew.costAllocation.push({
						"accountNum": postDataModel.getData().listNonPoItem[i].accountNum,
						"assetNo": null,
						"baseRate": postDataModel.getData().listNonPoItem[i].baseRate,
						"costAllocationId": costAllocationId,
						"costCenter": postDataModel.getData().listNonPoItem[i].costCenter,
						"crDbIndicator": postDataModel.getData().listNonPoItem[i].crDbIndicator,
						"deleteInd": null,
						"distrPerc": postDataModel.getData().listNonPoItem[i].distrPerc,
						"glAccount": postDataModel.getData().listNonPoItem[i].glAccount,
						"internalOrderId": postDataModel.getData().listNonPoItem[i].internalOrderId,
						"itemId": itemId,
						"itemText": postDataModel.getData().listNonPoItem[i].itemText,
						"lineText": "",
						"materialDesc": postDataModel.getData().listNonPoItem[i].materialDesc,
						"netValue": postDataModel.getData().listNonPoItem[i].netValue,
						"orderId": "",
						"profitCenter": postDataModel.getData().listNonPoItem[i].profitCenter,
						"quantity": "0.00",
						"quantityUnit": "",
						"taxCode": postDataModel.getData().listNonPoItem[i].taxCode,
						"taxPer": postDataModel.getData().listNonPoItem[i].taxPer,
						"taxValue": postDataModel.getData().listNonPoItem[i].taxValue,
						"taxConditionType": postDataModel.getData().listNonPoItem[i].taxConditionType,
						"requestId": reqId,
						"serialNo": 0,
						"subNumber": null,
						"wbsElement": null
					});

				}
			}
			objectIsNew.invoiceHeader.postingDate = objectIsNew.invoiceHeader.postingDate ? new Date(objectIsNew.invoiceHeader.postingDate).getTime() :
				null;
			var obj = {};
			var invoiceHeaderDto = {
				"accountNumber": "",
				"accountingDoc": "",
				"amountBeforeTax": "",
				"assignedTo": "",
				"balance": objectIsNew.invoiceHeader.balance,
				"balanceCheck": true,
				"baseLine": "",
				"channelType": "",
				"claimed": "",
				"claimedUser": "",
				"clearingAccountingDocument": "",
				"clearingDate": "",
				"clerkEmail": "",
				"clerkId": 0,
				"compCode": "1010", //hardCoded for testing
				"createdAt": "",
				"createdAtFrom": "",
				"createdAtInDB": 0,
				"createdAtTo": "",
				"createdByInDb": "",
				"createdOn": 0,
				"currency": "",
				"deliveryNote": "",
				"deposit": "",
				"disAmt": "",
				"discount": "",
				"docStatus": "",
				"dueDate": objectIsNew.invoiceHeader.dueDate,
				"dueDateFrom": "",
				"dueDateTo": "",
				"emailFrom": "",
				"extInvNum": objectIsNew.invoiceHeader.extInvNum,
				"filterFor": "",
				"fiscalYear": "",
				"forwaredTaskOwner": "",
				"grossAmount": objectIsNew.invoiceHeader.grossAmount,
				"headerText": "",
				"invoiceDate": objectIsNew.invoiceHeader.invoiceDate,
				"invoiceDateFrom": "",
				"invoiceDateTo": "",
				"invoiceItems": [],
				"invoicePdfId": objectIsNew.invoiceHeader.invoicePdfId,
				"invoiceTotal": Number(objectIsNew.invoiceHeader.invoiceTotal),
				"invoiceTotalFrom": "",
				"invoiceTotalTo": "",
				"invoiceType": "NON-PO",
				"lifecycleStatus": "",
				"lifecycleStatusText": "",
				"manualpaymentBlock": "",
				"nonPoOrPo": true,
				"ocrBatchId": objectIsNew.invoiceHeader.ocrBatchId,
				"paymentBlock": objectIsNew.invoiceHeader.paymentBlock,
				"paymentBlockDesc": "",
				"paymentStatus": "",
				"paymentTerms": objectIsNew.invoiceHeader.paymentTerms,
				"plannedCost": "",
				"postingDate": objectIsNew.invoiceHeader.postingDate,
				"postingDateFrom": 0,
				"postingDateTo": 0,
				"reasonForRejection": "",
				"refDocCat": "",
				"refDocNum": 0,
				"rejectionText": "",
				"requestId": reqId,
				"sapInvoiceNumber": 0,
				"shippingCost": 0,
				"subTotal": "",
				"surcharge": "",
				"taskOwner": objectIsNew.invoiceHeader.taskOwner,
				"taskOwnerId": this.oUserDetailModel.getProperty("/loggedInUserMail"),
				"taskStatus": objectIsNew.invoiceHeader.taskStatus,
				"taxAmount": objectIsNew.invoiceHeader.taxAmount,
				"taxCode": objectIsNew.invoiceHeader.taxCode,
				"taxRate": objectIsNew.invoiceHeader.taxPer,
				"taxValue": objectIsNew.invoiceHeader.taxValue,
				"totalBaseRate": objectIsNew.invoiceHeader.totalBaseRate,
				"transactionType": "",
				"unplannedCost": "",
				"updatedAt": 0,
				"updatedBy": "",
				"validationStatus": "",
				"vatRegNum": "",
				"vendorId": objectIsNew.invoiceHeader.vendorId,
				"vendorName": objectIsNew.invoiceHeader.vendorName,
				"version": 0
			};
			obj.invoiceHeaderDto = invoiceHeaderDto;
			obj.invoiceHeaderDto.attachments = [];
			if (nonPOInvoiceModel.getData().docManagerDto && nonPOInvoiceModel.getData().docManagerDto.length > 0) {
				for (var n = 0; n < nonPOInvoiceModel.getData().docManagerDto.length; n++) {
					obj.invoiceHeaderDto.attachments.push(nonPOInvoiceModel.getData().docManagerDto[n]);
				}
			}
			obj.invoiceHeaderDto.comments = [];
			if (nonPOInvoiceModel.getData().invoiceDetailUIDto.commentDto && nonPOInvoiceModel.getData().invoiceDetailUIDto.commentDto.length >
				0) {
				for (var k = 0; k < nonPOInvoiceModel.getData().invoiceDetailUIDto.commentDto.length; k++) {
					obj.invoiceHeaderDto.comments.push(nonPOInvoiceModel.getData().invoiceDetailUIDto.commentDto[k]);
				}
			}
			obj.invoiceItemAcctAssignmentDto = objectIsNew.invoiceItems;
			obj.costAllocationDto = objectIsNew.costAllocation;
			return obj;
		},

		//Called on click of Reject Button.
		//Frag:RejectDialog will open  
		onNonPoReject: function () {
			var mRejectModel = this.getModel("mRejectModel");
			this.rejectDialog = sap.ui.xmlfragment("com.menabev.AP.fragment.RejectDialog", this);
			this.getView().addDependent(this.rejectDialog);
			this.rejectDialog.setModel(mRejectModel, "mRejectModel");
			this.rejectDialog.open();
			// var url = "InctureApDest/rejReason/getAll/EN";
			// jQuery.ajax({
			// 	type: "GET",
			// 	contentType: "application/json",
			// 	url: url,
			// 	dataType: "json",
			// 	async: true,
			// 	success: function (data, textStatus, jqXHR) {
			// 		var aRejectData = data;
			// 		mRejectModel.setProperty("/items", aRejectData);
			// 	},
			// 	error: function (err) {
			// 		sap.m.MessageToast.show(err.statusText);
			// 	}
			// });
		},

		//Frag:RejectDialog
		onRejectCombo: function (oEvent) {
			var mRejectModel = this.getView().getModel("mRejectModel");
			mRejectModel.setProperty("/Textvalue", oEvent.getParameter("selectedItem").getProperty("text"));
			mRejectModel.setProperty("/Keyvalue", oEvent.getParameter("selectedItem").getProperty("key"));
		},

		//Frag:RejectDialog
		onNonPoRejectCancel: function () {
			this.rejectDialog.close();
		},

		//Frag:RejectDialog
		onNonPoRejectConfirm: function () {
			var mRejectModel = this.getView().getModel("mRejectModel");
			var rejectReasonVState = "None";
			var selKey = mRejectModel.getProperty("/selectedKey");
			if (selKey) {
				rejectReasonVState = "None";
				var nonPOInvoiceModel = this.getView().getModel("nonPOInvoiceModel"),
					nonPOInvoiceModelData = nonPOInvoiceModel.getData();
				var objectIsNew = jQuery.extend({}, nonPOInvoiceModelData.invoiceDetailUIDto);
				objectIsNew.invoiceHeader.postingDate = objectIsNew.invoiceHeader.postingDate ? new Date(objectIsNew.invoiceHeader.postingDate).getTime() :
					null;
				objectIsNew.invoiceHeader.reasonForRejection = mRejectModel.getProperty("/Keyvalue");
				objectIsNew.invoiceHeader.rejectionText = mRejectModel.getProperty("/Textvalue");
				var jsonData = objectIsNew.invoiceHeader;
				var url = "InctureApDest/invoiceHeader/updateLifeCycleStatus";
				var that = this;
				this.busyDialog.open();
				$.ajax({
					url: url,
					method: "PUT",
					async: false,
					headers: {
						"X-CSRF-Token": this.getCSRFToken()
					},
					contentType: 'application/json',
					dataType: "json",
					data: JSON.stringify(jsonData),
					success: function (data, xhr, result) {
						this.busyDialog.close();
						var message = data.responseStatus;
						if (result.status === 200) {
							mRejectModel.setProperty("/selectedKey", "");
							sap.m.MessageBox.success(message, {
								actions: [sap.m.MessageBox.Action.OK],
								onClose: function (sAction) {
									that.router.navTo("Inbox");
								}
							});
						} else {
							sap.m.MessageBox.information(message, {
								actions: [sap.m.MessageBox.Action.OK]
							});
						}
					}.bind(this),
					error: function (result, xhr, data) {
						this.busyDialog.close();
						var errorMsg = "";
						if (result.status === 504) {
							errorMsg = "Request timed-out. Please refresh your page";
							this.errorMsg(errorMsg);
						} else {
							errorMsg = data;
							this.errorMsg(errorMsg);
						}
					}.bind(this)
				});
			} else {
				sap.m.MessageBox.error("Please select Reason Code");
				mRejectModel.setProperty("/mRejectModel", "error");
			}
		},

		//Mandatory field check of Upload Invoice Fragment:UploadInvoiceFrag
		validateMandatoryFields: function (oData) {
			var key;
			var flag = true;
			for (key in oData) {
				if (mandatoryFields.includes(key)) {
					if (!oData[key]) {
						this.getView().getModel("nonPOInvoiceModel").setProperty("/invoiceDetailUIDto/vstate/" + key, "Error");
						flag = false;
					} else {
						this.getView().getModel("nonPOInvoiceModel").setProperty("/invoiceDetailUIDto/vstate/" + key, "None");
					}
				}
			}
			return flag;

		},

		//Mandatory fields validation before adding cost allocation 
		fnMandatoryFeildsForCC: function () {
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel"),
				nonPOInvoiceModelData = nonPOInvoiceModel.getData().invoiceDetailUIDto.invoiceHeader,
				bError = false,
				mandatoryFeildsForCC = ["invoiceTotal", "taxCode", "taxValue"];

			var key;
			for (key in nonPOInvoiceModelData) {
				if (mandatoryFeildsForCC.includes(key)) {
					if (!nonPOInvoiceModelData[key]) {
						this.getView().getModel("nonPOInvoiceModel").setProperty("/invoiceDetailUIDto/vstate/" + key, "Error");
						bError = true;
					} else {
						this.getView().getModel("nonPOInvoiceModel").setProperty("/invoiceDetailUIDto/vstate/" + key, "None");
					}
				}
			}

			return bError;
		},

		onNavBack: function () {
			this.router.navTo("Inbox");
		},

		onPostComment: function () {
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel");
			var oUserDetailModel = this.oUserDetailModel;
			var comments = nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/comments");
			var userComment = nonPOInvoiceModel.getProperty("/commments");
			if (!userComment) {
				this.resourceBundle.getText("FILL_COMMENT");
				return;
			}
			var obj = {
				"createdAt": new Date().getTime(),
				"user": oUserDetailModel.getProperty("/loggedinUserDetail/name/familyName"),
				"comment": nonPOInvoiceModel.getProperty("/commments"),
				"createdBy": oUserDetailModel.getProperty("/loggedInUserMail")
			};
			comments.push(obj);
			nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/comments", comments);
			nonPOInvoiceModel.setProperty("/commments", "");
		},
		createReqid: function () {
			var oServiceModel = new sap.ui.model.json.JSONModel();
			var busy = new sap.m.BusyDialog();
			busy.open();
			var sUrl = "/menabevdev/invoiceHeader?sequnceCode=APA";
			oServiceModel.loadData(sUrl, "", false, "GET", false, false, this.oHeader);
			busy.close();
			var ReqId = oServiceModel.getData().message;
			return ReqId;
		},

		handleUploadComplete: function (oEvent) {
			var that = this;
			var oUserDetailModel = this.oUserDetailModel;
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel");
			var attachments = nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/attachments");
			var upfile = oEvent.getParameters().files[0];
			var upFileName = upfile.name;
			var fileType = upfile.name.split(".")[upfile.name.split(".").length - 1];
			var allowedTypes = ["pdf", "doc", "docx", "jpg", "jpeg", "png", "xlsx", "xls", "csv"];
			var requestId = nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/requestId");
			if (!requestId) {
				requestId = this.createReqid();
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/requestId", requestId);
			}
			if (!allowedTypes.includes(fileType.toLowerCase())) {
				MessageBox.error(that.resourceBundle.getText("msgFileType"));
				return;
			}
			if (upfile.size > 2097152) {
				MessageBox.error(that.resourceBundle.getText("msgFileSize"));
				return;
			}
			if (upFileName.length > 60) {
				MessageBox.error(that.resourceBundle.getText("msgFileName"));
				return;
			}
			var sUrl = "/menabevdev/document/upload";
			var oFormData = new FormData;
			var busy = new sap.m.BusyDialog();
			busy.open();

			oFormData.set("requestId", requestId);
			oFormData.set("file", upfile);
			jQuery.ajax({
				url: sUrl,
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
					// if (success.status === "Error") {
					// 	errorMsg = "Request timed-out. Please contact your administrator";
					// 	that.errorMsg(errorMsg);
					// } else {
					var obj = {
						"attachmentId": success.documentId,
						"createdAt": new Date().getTime(),
						"createdBy": oUserDetailModel.getProperty("/loggedInUserMail"),
						"fileName": success.documentName,
						"fileType": "",
						"requestId": requestId
					};
					attachments.push(obj);
					nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/attachments", attachments);
					MessageBox.success(success.response.message, {
						actions: [MessageBox.Action.OK],
						onClose: function (sAction) {
							if (sAction === MessageBox.Action.OK) {}
						}
					});
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

		},

		onDocumentDownload: function (oEvent) {
			var oServiceModel = new sap.ui.model.json.JSONModel();
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel");
			var sPath = oEvent.getSource().getBindingContext("nonPOInvoiceModel").getPath();
			var obj = oEvent.getSource().getBindingContext("nonPOInvoiceModel").getObject();

			var parts = sPath.split("/");
			var index = parts[parts.length - 1];
			var documentId = obj.attachmentId;
			var busy = new sap.m.BusyDialog();
			busy.open();
			var sUrl = "/menabevdev/document/download/" + documentId;
			oServiceModel.loadData(sUrl, "", true, "GET", false, false, this.oHeader);
			oServiceModel.attachRequestCompleted(function (oEvent) {
				var Base64 = oEvent.getSource().getData().base64;
				var fileName = oEvent.getSource().getData().documentName;
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
					// if (fileType === "pdf") {
					// 	// a.target = "_blank";
					// 	window.open(url, fileName, "width=900px, height=600px, scrollbars=yes");
					// } else {
					a.download = fileName;
					a.click();
					// }

					// window.URL.revokeObjectURL(url);
					// }
				}
				busy.close();
			});
			oServiceModel.attachRequestFailed(function (oEvent) {
				busy.close();
			});
		},

		onDocumentDelete: function (oEvent) {
			var oServiceModel = new sap.ui.model.json.JSONModel();
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel");
			var sPath = oEvent.getSource().getBindingContext("nonPOInvoiceModel").getPath();
			var obj = oEvent.getSource().getBindingContext("nonPOInvoiceModel").getObject();
			var attachments = nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/attachments");
			var parts = sPath.split("/");
			var index = parts[parts.length - 1];
			var attachmentId = obj.attachmentId;
			var busy = new sap.m.BusyDialog();
			busy.open();
			var sUrl = "/menabevdev/document/delete/" + attachmentId;
			oServiceModel.loadData(sUrl, "", true, "DELETE", false, false, this.oHeader);
			oServiceModel.attachRequestCompleted(function (oEvent) {
				busy.close();
				sap.m.MessageToast.show(oEvent.getSource().getData().message);
				attachments.splice(index, 1);
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/attachments", attachments);

			});
		},

		onChangeUserInputTaxAmount: function (oEvent) {
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel"),
				userInputTaxAmount = oEvent.getParameter("value");
			if (!userInputTaxAmount) {
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/vstate/taxValue", "Error");
			} else {
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/vstate/taxValue", "None");
				userInputTaxAmount = (parseFloat(userInputTaxAmount)).toFixed(2);
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/taxValue", userInputTaxAmount);
			}
			this.calculateGrossAmount();
			this.calculateBalance();
		},

		//Vendor search valuehelp request
		onValueHelpRequested: function () {
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel");
			if (!this.vendorValueHelp) {
				this.vendorValueHelp = sap.ui.xmlfragment("com.menabev.AP.fragment.VendorValueHelp", this);
				this.getView().addDependent(this.vendorValueHelp);
			}
			var vendorSearchModel = new JSONModel();
			this.getView().setModel(vendorSearchModel, "vendorSearchModel");
			vendorSearchModel.setProperty("/vendorId", nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/vendorId"));
			vendorSearchModel.setProperty("/vendorName", nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/vendorName"));
			vendorSearchModel.setProperty("/VATRegistration", "");
			this.fnVendorIdServiceCall();
			this.vendorValueHelp.open();
		},

		onSearchVendor: function (oEvent) {
			this.fnVendorIdServiceCall();
		},

		onClearSearchVendor: function () {
			var vendorSearchModel = this.getView().getModel("vendorSearchModel");
			vendorSearchModel.setProperty("/vendorId", "");
			vendorSearchModel.setProperty("/vendorName", "");
			vendorSearchModel.setProperty("/VATRegistration", "");
		},

		fnVendorIdServiceCall: function () {
			var vendorSearchModel = this.getView().getModel("vendorSearchModel"),
				vendorId = vendorSearchModel.getProperty("/vendorId"),
				vendorName = vendorSearchModel.getProperty("/vendorName"),
				VATRegistration = vendorSearchModel.getProperty("/VATRegistration");
			var oFilter = [];
			if (vendorId) {
				oFilter.push(new sap.ui.model.Filter("Supplier", sap.ui.model.FilterOperator.EQ, vendorId));
			}
			if (vendorName) {
				vendorName = vendorName.toUpperCase();
				oFilter.push(new sap.ui.model.Filter("SupplierName", sap.ui.model.FilterOperator.Contains, vendorName));
			}
			if (VATRegistration) {
				oFilter.push(new sap.ui.model.Filter("VATRegistration", sap.ui.model.FilterOperator.EQ, VATRegistration));
			}
			var oDataModel = this.getOwnerComponent().getModel("API_BUSINESS_PARTNER");
			this.busyDialog.open();
			oDataModel.read("/A_Supplier", {
				filters: oFilter,
				async: false,
				success: function (oData, oResponse) {
					this.busyDialog.close();
					if (oData.results.length) {
						vendorSearchModel.setProperty("/vendorResult", oData.results);
						// sap.m.MessageToast.show("vendor search successfull");
					} else {
						sap.m.MessageToast.show("No Vendors found for the filters");
					}
				}.bind(this),
				error: function (error) {
					this.busyDialog.close();
					var errorMsg = "";
					if (error.statusCode === 504) {
						errorMsg = "Request timed-out. Please try again!";
						this.errorMsg(errorMsg);
					} else {
						errorMsg = JSON.parse(error.responseText);
						errorMsg = errorMsg.error.message.value;
						this.errorMsg(errorMsg);
					}
				}.bind(this)
			});
		},

		onSelectVendorSearch: function (oEvent) {
			var sPath = oEvent.getSource().getSelectedContextPaths()[0],
				vendorSearchModel = this.getView().getModel("vendorSearchModel"),
				selectedObject = vendorSearchModel.getProperty(sPath);
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel");
			nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/vendorId", selectedObject.Supplier);
			nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/vendorName", selectedObject.SupplierName);
			this.vendorValueHelp.close();
		},

		onCloseVendorValueHelp: function () {
			this.vendorValueHelp.close();
		},

		calculateBalance: function () {
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel"),
				that = this,
				invAmt = nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/invoiceTotal"),
				grossAmount = nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/grossAmount");
			var balance = that.nanValCheck(invAmt) - that.nanValCheck(grossAmount);
			balance = that.nanValCheck(balance).toFixed(2);
			nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/balance", balance);
			nonPOInvoiceModel.refresh();
		},

		calculateGrossAmount: function () {
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel"),
				that = this,
				totalBaseRate = nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/totalBaseRate"),
				userInputTaxAmount = nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/taxValue");
			var grossAmount = that.nanValCheck(totalBaseRate) + that.nanValCheck(userInputTaxAmount);
			grossAmount = that.nanValCheck(grossAmount).toFixed(2);
			nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/grossAmount", grossAmount);
			nonPOInvoiceModel.refresh();
		},

		VendorNameSuggest: function (oEvent) {
			var searchVendorModel = new sap.ui.model.json.JSONModel();
			this.getView().setModel(searchVendorModel, "searchVendorModel");
			var value = oEvent.getParameter("suggestValue");
			value = value.toUpperCase();
			if (value && value.length > 2) {
				var url = "SD4_DEST/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_Supplier?$format=json&$filter=substringof('" + value +
					"',SupplierName) eq true";
				searchVendorModel.loadData(url, null, true);
				searchVendorModel.attachRequestCompleted(null, function (oEvt) {
					searchVendorModel.refresh();
				});
			}
		},

		vendorNameSelected: function (oEvt) {
			var nonPOInvoiceModel = this.getView().getModel("nonPOInvoiceModel");
			var sVendorId = oEvt.getParameter("selectedItem").getProperty("additionalText"),
				sVendorName = oEvt.getParameter("selectedItem").getProperty("text");
			nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/vendorName", sVendorName);
			nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/vendorId", sVendorId);
			nonPOInvoiceModel.refresh();
		},

		VendorIdSuggest: function (oEvent) {
			var searchVendorModel = new sap.ui.model.json.JSONModel();
			this.getView().setModel(searchVendorModel, "searchVendorModel");
			var value = oEvent.getParameter("suggestValue");
			// value = value.toUpperCase();
			if (value && value.length > 2) {
				// var url = "SD4_DEST/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_Supplier?$format=json&$filter=Supplier eq '"+ value +"'";
				var url = "SD4_DEST/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_Supplier?$format=json&$filter=substringof('" + value +
					"',Supplier) eq true";
				searchVendorModel.loadData(url, null, true);
				searchVendorModel.attachRequestCompleted(null, function (oEvt) {
					searchVendorModel.refresh();
				});
			}
		},

		vendorIdSelected: function (oEvt) {
			var nonPOInvoiceModel = this.getView().getModel("nonPOInvoiceModel");
			var sVendorId = oEvt.getParameter("selectedItem").getProperty("text"),
				sVendorName = oEvt.getParameter("selectedItem").getProperty("additionalText");
			nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/vendorName", sVendorName);
			nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/vendorId", sVendorId);
			nonPOInvoiceModel.refresh();
		},

		//To open vendor balance
		onClickVendorBalances: function () {
			var nonPOInvoiceModel = this.getView().getModel("nonPOInvoiceModel"),
				vendorId = nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/vendorId"),
				companyCode = nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/companyCode");
			this.loadVendorBalanceFrag(vendorId, companyCode);
		}

	});

});