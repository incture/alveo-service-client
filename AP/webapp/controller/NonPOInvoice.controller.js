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
			this.router = oComponent.getRouter();
			this.router.getRoute("NonPOInvoice").attachPatternMatched(this.onRouteMatched, this);
			this.resourceBundle = this.getOwnerComponent().getModel("i18n").getResourceBundle();
		},

		onRouteMatched: function (oEvent) {
			//reading the Arguments from url
			var oArgs = oEvent.getParameter("arguments"),
				requestId = oArgs.id,
				status = oArgs.status;
			this.busyDialog.open();

			//Test Data for Tax Rate
			var aTax = [{
				"taxCode": "I0",
				"taxCodeDescription": "ProductId",
				"taxPer": "0"
			}, {
				"taxCode": "I1",
				"taxCodeDescription": "test",
				"taxPer": "15"
			}, {
				"taxCode": "IE",
				"taxCodeDescription": "ProductId",
				"taxPer": "5"
			}];
			this.setModel(new JSONModel(), "taxCodeModel");
			this.getModel("taxCodeModel").setProperty("/aTax", aTax);
			//End of Tax rate test data

			var oVisibilityModel = this.getOwnerComponent().getModel("oVisibilityModel");
			oVisibilityModel.setProperty("/NonPOInvoice", {});
			oVisibilityModel.setProperty("/NonPOInvoice/editable", true);
			var loggedinUserGroup = this.oUserDetailModel.getProperty("/loggedinUserGroup");
			if (loggedinUserGroup === "Process_Lead") {
				oVisibilityModel.setProperty("/NonPOInvoice/editable", false);
			}

			this.getModel("nonPOInvoiceModel").setProperty("/openPdfBtnVisible", false);
			//handle if route has NonPO request Id 
			if (requestId) {
				this.getNonPOData(requestId);
			} else {
				this.busyDialog.close();
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
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel");
			var inValue = oEvent.getParameter("value");
			if (inValue === "") {
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/vstate/invoiceTotal", "Error");
			} else {
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/vstate/invoiceTotal", "None");
				var invAmt = (parseFloat(inValue)).toFixed(2);
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/invoiceTotal", invAmt);
				var aDetails = nonPOInvoiceModel.getData().invoiceDetailUIDto.invoiceHeader;
				var fBalance = (parseFloat(aDetails.invoiceTotal - aDetails.grossAmount)).toFixed(2);
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/balance", fBalance);
			}
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
			var taxDetails = oEvent.getParameters().selectedItem.getBindingContext("taxCodeModel").getObject();
			var postDataModel = this.getModel("postDataModel"),
				nonPOInvoiceModel = this.getModel("nonPOInvoiceModel"),
				taxCode = taxDetails.taxCode,
				taxPer = taxDetails.taxPer;
			nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/taxPer", taxPer);
			if (postDataModel.getProperty("/listNonPoItem")) {
				var length = postDataModel.getProperty("/listNonPoItem").length;
				for (var i = 0; i < length; i++) {
					postDataModel.setProperty("/listNonPoItem/" + i + "/taxCode", taxCode);
					postDataModel.setProperty("/listNonPoItem/" + i + "/taxPer", taxPer);
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
			var taxDetails = oEvent.getParameters().selectedItem.getBindingContext("taxCodeModel").getObject();
			var postDataModel = this.getModel("postDataModel");
			var sPath = oEvent.getSource().getBindingContext("postDataModel").getPath();
			postDataModel.setProperty(sPath + "/taxPer", taxDetails.taxPer);
			this.amountCal(oEvent);
		},
		//End of Header Filter function

		//PDF Area details
		//Start of Open PDF details
		onPressOpenpdf: function () {
			//service call to load the pdf document
			var documentId = this.getModel("nonPOInvoiceModel").getProperty("/invoiceDetailUIDto/invoiceHeader/invoicePdfId");
			this.busyDialog.open();
			var sUrl = "/menabevdev/document/download/" + documentId;
			jQuery.ajax({
				type: "GET",
				contentType: "application/json",
				url: sUrl,
				dataType: "json",
				async: true,
				success: function (data, textStatus, jqXHR) {
					this.busyDialog.close();
					this.pdfData = data;
					if (data.fileAvailability) {
						this.byId("grid").setDefaultSpan("L6 M6 S12");
						this.byId("grid2").setDefaultSpan("L6 M6 S12");
						this.addPDFArea();
					} else {
						sap.m.MessageToast.show("No PDF is available");
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

		addPDFArea: function () {
			var that = this;
			var pdfData = this.pdfData;
			that.pdf = sap.ui.xmlfragment(that.getView().getId(), "com.menabev.AP.fragment.PDF", that);
			var oPdfFrame = that.pdf.getItems()[1];
			oPdfFrame.setContent('<embed width="100%" height="859rem" name="plugin" src="data:application/pdf;base64, ' + pdfData.base64 +
				'" ' + 'type=' + "" + "application/pdf" + " " + 'internalinstanceid="21">');
			var oSplitter = that.byId("idMainSplitter");
			var oLastContentArea = oSplitter.getContentAreas().pop();
			if (oSplitter.getContentAreas().length > 1)
				oSplitter.removeContentArea(oLastContentArea);
			if (oSplitter.getContentAreas().length === 1)
				oSplitter.insertContentArea(that.pdf, 1);
		},

		//frag:PDF
		//this function will close the PDF Area.
		closePDFArea: function () {
			var oSplitter = this.byId("idMainSplitter");
			var oLastContentArea = oSplitter.getContentAreas().pop();
			if (oSplitter.getContentAreas().length > 1) {
				oSplitter.removeContentArea(oLastContentArea);
			}
			this.resizeSplitterLayout();
			this.byId("grid").setDefaultSpan("L3 M4 S12");
			this.byId("grid2").setDefaultSpan("L3 M6 S12");
		},

		resizeSplitterLayout: function () {
			var oContentLayout;
			var oSplitter = this.byId("idMainSplitter");
			oSplitter.getContentAreas().forEach(function (oElement) {
				oContentLayout = oElement.getLayoutData();
				oContentLayout.setSize("auto");
			});
		},
		//End of Open PDF details

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
				this.getAllTemplate(); //Call service to fetch the templates
				this.selectTemplateFragment.open();
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
			var sValue = oEvt.getParameter("value");
			var oFilter = new Filter("templateName", FilterOperator.Contains, sValue);
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
				var partAmount = (Number(costAllocationList[i].allocationPercent) * Number(totalAmount)) / 100,
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
				taxPer = nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/taxPer");
			for (var i = 0; i < arr.length; i++) {
				postDataModel.setProperty("/listNonPoItem/" + i + "/taxCode", taxCode);
				postDataModel.setProperty("/listNonPoItem/" + i + "/taxPer", taxPer);
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
				var postDataModel = this.getModel("postDataModel");
				var postDataModelData = postDataModel.getData();
				if (!postDataModelData.listNonPoItem) {
					postDataModelData.listNonPoItem = [];
				}
				postDataModelData.listNonPoItem.unshift({
					"templateId": "",
					"glAccount": "",
					"costCenter": "",
					"taxCode": this.getModel("nonPOInvoiceModel").getData().invoiceDetailUIDto.invoiceHeader.taxCode,
					"taxPer": this.getModel("nonPOInvoiceModel").getData().invoiceDetailUIDto.invoiceHeader.taxPer,
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
					totalTax += taxValue;
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
				var grossAmount = that.nanValCheck(totalBaseRate) + that.nanValCheck(totalTax);
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/totalBaseRate", totalBaseRate);
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/grossAmount", grossAmount.toFixed(2));
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/taxAmount", totalTax);
				var invAmt = nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/invoiceTotal");
				var diff = that.nanValCheck(invAmt) - that.nanValCheck(grossAmount);
				diff = that.nanValCheck(diff).toFixed(2);
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/balance", diff);
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
							that.getModel("postDataModel").setProperty("/listNonPoItem", data);
							that.getModel("postDataModel").refresh();
							errorMsg = "Succefully imported data from excel file";
							sap.m.MessageToast.show(errorMsg);
						}
					},
					error: function (result, xhr, data) {
						that.busyDialog.close();
						var errorMsg = "";
						if (result.status === 504) {
							errorMsg = "Request timed-out. Please refresh your page";
							that.errorMsg(errorMsg);
						} else {
							errorMsg = data;
							that.errorMsg(errorMsg);
						}
					}
				});
			}
		},

		//onFileSizeExceed is triggerred when Excel file size exceeded more than 10MB 
		onFileSizeExceed: function (error) {
			var errorMsg = "File size has exceeded it max limit of 10MB";
			this.errorMsg(errorMsg);
		},
		/*End of Excel Upload*/

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

		/*Start of Attachments tab*/
		onBeforeUploadStarts: function (oEvent) {
			var that = this;
			var nonPOInvoiceModel = this.getView().getModel("nonPOInvoiceModel");
			var nonPOInvoiceModelData = this.getView().getModel("nonPOInvoiceModel").getData();
			var fileName = oEvent.getParameter("newValue"),
				fileList = oEvent.getSource().oFileUpload.files[0],
				fileType = fileList.type;
			String.prototype.replaceAll = function (search, replacement) {
				var target = this;
				return target.replace(new RegExp(search, "g"), replacement);
			};

			fileName = fileName.replaceAll(" ", "_");
			fileName = fileName.replaceAll("#", "_");
			var reader = new FileReader();
			reader.onload = function (event) {
				var s = event.target.result;
				var base64 = s.substr(s.lastIndexOf(","));
				base64 = base64.split(",")[1];
				var cDate = new Date().getTime();
				// var sDate = Date.now(cDate);
				var docDetails = {
					"requestId": nonPOInvoiceModelData.invoiceDetailUIDto.invoiceHeader.requestId,
					"fileName": fileName,
					"fileType": fileType,
					"fileBase64": base64,
					"createdBy": that.getView().getModel("oUserModel").getProperty("/email"),
					"createdAt": cDate,
					"updatedBy": null,
					"updatedAt": null,
					"master": null
				};
				if (!nonPOInvoiceModel.getData().docManagerDto) {
					nonPOInvoiceModel.getData().docManagerDto = [];
				}
				nonPOInvoiceModel.getData().docManagerDto.push(docDetails);

				for (var i = that.count; i < nonPOInvoiceModel.getData().docManagerDto.length; i++) {
					if (nonPOInvoiceModel.getData().docManagerDto[i].createdAt) {
						var date = new Date(nonPOInvoiceModel.getData().docManagerDto[i].createdAt).toLocaleDateString();
						nonPOInvoiceModel.setProperty("/docManagerDto/" + i + "/date", date);
					}
					if (nonPOInvoiceModel.getData().docManagerDto[i].fileType == "pdf" || nonPOInvoiceModel.getData().docManagerDto[i].fileType ==
						"application/pdf") {
						nonPOInvoiceModel.setProperty("/docManagerDto/" + i + "/type",
							"https://image.shutterstock.com/image-vector/pdf-icon-260nw-215496328.jpg");
					} else {
						nonPOInvoiceModel.setProperty("/docManagerDto/" + i + "/type",
							"https://thumbs.dreamstime.com/z/txt-file-red-arrow-download-button-white-background-71307470.jpg");
					}
				}
				nonPOInvoiceModel.refresh();

			};
			if (fileList) {
				reader.readAsDataURL(fileList);
			}

		},

		onSearchAttachments: function (oEvt) {
			var aFilters = [];
			var sQuery = oEvt.getSource().getValue();
			if (sQuery && sQuery.length > 0) {
				var afilter = new sap.ui.model.Filter([
						new sap.ui.model.Filter("fileName", sap.ui.model.FilterOperator.Contains, sQuery)
					],
					false);
				aFilters.push(afilter);
			}
			var oBinding = this.getView().byId("attachListItems").getBinding("items");
			oBinding.filter(aFilters, false);
		},

		//function onChangeAddAttachment is triggered when the file selected from the browse
		//this function will add the selected file in the attachment list
		onChangeAddAttachment: function (oEvent) {
			var nonPOInvoiceModel = this.getView().getModel("nonPOInvoiceModel");
			var nonPOInvoiceModelData = this.getView().getModel("nonPOInvoiceModel").getData();
			var fileName = oEvent.getParameter("newValue"),
				fileList = oEvent.getSource().oFileUpload.files[0],
				fileType = fileList.type,
				that = this;
			String.prototype.replaceAll = function (search, replacement) {
				var target = this;
				return target.replace(new RegExp(search, "g"), replacement);
			};

			fileName = fileName.replaceAll(" ", "_");
			fileName = fileName.replaceAll("#", "_");
			var reader = new FileReader();
			reader.onload = function (event) {
				var s = event.target.result;
				var base64 = s.substr(s.lastIndexOf(","));
				base64 = base64.split(",")[1];
				var cDate = new Date();
				var sDate = Date.now(cDate);
				var docDetails = {
					"requestId": nonPOInvoiceModelData.invoiceDetailUIDto.invoiceHeader.requestId,
					"fileName": fileName,
					"fileType": fileType,
					"fileBase64": base64,
					"createdBy": nonPOInvoiceModelData.invoiceDetailUIDto.invoiceHeader.clerkEmail,
					"createdAt": sDate,
					"updatedBy": null,
					"updatedAt": null,
					"master": null
				};
				if (!nonPOInvoiceModel.getData().docManagerDto) {
					nonPOInvoiceModel.getData().docManagerDto = [];
				}
				nonPOInvoiceModel.getData().docManagerDto.push(docDetails);
				nonPOInvoiceModel.refresh();
			};
			if (fileList) {
				reader.readAsDataURL(fileList);
			}
		},

		//This function will list the attachment  
		fnUploadDoc: function (oEvent) {
			var nonPOInvoiceModel = this.getView().getModel("nonPOInvoiceModel");
			var attachData = oEvent.getSource().getBindingContext("nonPOInvoiceModel").getObject();
			var apptype = "text/html";
			var byteCode = attachData.fileBase64;
			var u8_2 = new Uint8Array(atob(byteCode).split("").map(function (c) {
				return c.charCodeAt(0);
			}));
			var a = document.createElement("a");
			document.body.appendChild(a);
			a.style = "display: none";
			var blob = new Blob([u8_2], {
				type: apptype
			});
			var url = window.URL.createObjectURL(blob);
			a.href = url;
			a.download = attachData.fileName;
			a.click();
			window.URL.revokeObjectURL(url);
		},

		//This function will delete the attachment from the attach ment list
		fnDeleteAttachment: function (oEvent) {
			var sPath = oEvent.getSource().getBindingContext("nonPOInvoiceModel").getPath();
			var nonPOInvoiceModel = this.getView().getModel("nonPOInvoiceModel");
			var sId = nonPOInvoiceModel.getProperty(sPath).attachmentId;
			var index = sPath.split("/").pop();
			nonPOInvoiceModel.getData().docManagerDto.splice(index, 1);
			nonPOInvoiceModel.refresh();
			if (sId) {
				var url = "InctureApDest/attachment/delete/" + sId;
				jQuery
					.ajax({
						url: url,
						type: "DELETE",
						headers: {
							"X-CSRF-Token": this.getCSRFToken()
						},
						dataType: "json",
						success: function (result) {}.bind(this)
					});
			}

		},
		/*****End of Attachment Tab*****/

		/****Start of Comments Section****/
		//To post the comment entered by the user
		// onPostComment: function (oEvent) {
		// 	var nonPOInvoiceModel = this.getView().getModel("nonPOInvoiceModel");
		// 	var sValue = oEvent.getParameter("value");
		// 	var nonPOInvoiceModelData = nonPOInvoiceModel.getData().invoiceDetailUIDto.invoiceHeader;
		// 	var sDate = new Date().getTime();
		// 	if (!nonPOInvoiceModel.getData().invoiceDetailUIDto.commentDto) {
		// 		nonPOInvoiceModel.getData().invoiceDetailUIDto.commentDto = [];
		// 	}
		// 	var sId = nonPOInvoiceModel.getProperty("/commentId");
		// 	var cValue = nonPOInvoiceModel.getProperty("/input");
		// 	var aCommentSelected = nonPOInvoiceModel.getData().invoiceDetailUIDto.commentDto;
		// 	var aComItem = aCommentSelected.find(function (oRow, index) {
		// 		return oRow.comment === cValue;
		// 	});

		// 	var aSelected = nonPOInvoiceModel.getData().invoiceDetailUIDto.commentDto;
		// 	var aSelectedItem = aSelected.find(function (oRow, index) {
		// 		return oRow.commentId === sId;
		// 	});
		// 	if (aSelectedItem) {
		// 		var lDate = new Date();
		// 		var uDate = lDate.getTime();
		// 		aSelectedItem.comment = cValue;
		// 		aSelectedItem.updatedAt = uDate;
		// 		aSelectedItem.updatedBy = aSelectedItem.createdBy;

		// 	} else if (aComItem) {
		// 		var cDate = new Date();
		// 		var nCDate = cDate.getTime();
		// 		aComItem.comment = cValue;
		// 		aComItem.updatedAt = nCDate;
		// 		aComItem.updatedBy = aComItem.createdBy;
		// 	} else {
		// 		var oComment = {
		// 			"requestId": nonPOInvoiceModelData.requestId,
		// 			"comment": sValue,
		// 			"createdBy": nonPOInvoiceModelData.emailFrom,
		// 			"createdAt": sDate,
		// 			"updatedBy": null,
		// 			"updatedAt": null,
		// 			"user": nonPOInvoiceModelData.emailFrom
		// 		};
		// 		var aEntries = nonPOInvoiceModel.getData().invoiceDetailUIDto.commentDto;
		// 		aEntries.unshift(oComment);
		// 	}
		// 	nonPOInvoiceModel.setProperty("/commentId", "");
		// 	this.getView().getModel("nonPOInvoiceModel").refresh();
		// },

		fnEditComment: function (oEvent) {
			var nonPOInvoiceModel = this.getView().getModel("nonPOInvoiceModel");
			var sPath = oEvent.getSource().getBindingContext("nonPOInvoiceModel").getPath();
			var sId = nonPOInvoiceModel.getProperty(sPath).commentId;
			var sValue = nonPOInvoiceModel.getProperty(sPath).comment;
			nonPOInvoiceModel.setProperty("/input", sValue);
			nonPOInvoiceModel.setProperty("/commentId", sId);
			nonPOInvoiceModel.refresh();
		},

		fnDeleteComment: function (oEvent) {
			var sPath = oEvent.getSource().getBindingContext("nonPOInvoiceModel").getPath();
			var nonPOInvoiceModel = this.getView().getModel("nonPOInvoiceModel");
			var sId = nonPOInvoiceModel.getProperty(sPath).commentId;
			var index = sPath.split("/").pop();
			nonPOInvoiceModel.getData().invoiceDetailUIDto.commentDto.splice(index, 1);
			nonPOInvoiceModel.refresh();
			if (sId) {
				var url = "InctureApDest/comment/delete/" + sId;
				jQuery
					.ajax({
						url: url,
						type: "DELETE",
						headers: {
							"X-CSRF-Token": this.getCSRFToken()
						},
						dataType: "json",
						success: function (result) {}.bind(this)
					});
			}
		},
		/***End of Comments Section***/

		//Called when the save button is clicked.
		onNonPoSave: function () {
			var oSaveData = this.fnCreatePayloadSaveSubmit();
			oSaveData.invoiceHeaderDto.taskOwner = this.oUserDetailModel.getProperty("/loggedInUserMail");
			oSaveData.invoiceHeaderDto.docStatus = "Draft";
			var postingDate = oSaveData.invoiceHeaderDto.postingDate;
			if (postingDate) {
				var sUrl = "/menabevdev/invoiceHeader/accountantSave",
					sMethod = "POST";
				this.saveSubmitServiceCall(oSaveData, sMethod, sUrl);
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
					//To handle validations
					var bValidate = false;
					if (alistNonPoData[i].glAccount === "" || alistNonPoData[i].glError === "Error") {
						bValidate = true;
						alistNonPoData[i].glError = "Error";
					}
					if (alistNonPoData[i].netValue === "" || alistNonPoData[i].amountError === "Error") {
						bValidate = true;
						alistNonPoData[i].amountError = "Error";
					}
					if (alistNonPoData[i].costCenter === "" || alistNonPoData[i].costCenterError === "Error") {
						bValidate = true;
						alistNonPoData[i].costCenterError = "Error";
					}
					if (alistNonPoData[i].itemText === "" || alistNonPoData[i].itemTextError === "Error") {
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
						sap.m.MessageToast.show("Balance is not 0");
					}
					var totalTax = this.getModel("nonPOInvoiceModel").getProperty("/invoiceDetailUIDto/invoiceHeader/taxAmount");
					var userInputTaxAmount = this.getModel("nonPOInvoiceModel").getProperty("/invoiceDetailUIDto/invoiceHeader/taxValue");
					var taxDifference = this.nanValCheck(userInputTaxAmount) - this.nanValCheck(totalTax);
					if (this.nanValCheck(taxDifference) !== 0) {
						sap.m.MessageToast.show("Tax MisMatched");
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
					
					if (result.status === 200) {
						sap.m.MessageBox.success(message, {
							actions: [sap.m.MessageBox.Action.OK],
							onClose: function (sAction) {
								if(!save){
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
					objectIsNew.costAllocation.push({
						"allocationPercent": postDataModel.getData().listNonPoItem[i].allocationPercent,
						"assetNo": null,
						"costAllocationId": null,
						"costCenter": postDataModel.getData().listNonPoItem[i].costCenter,
						"crDbIndicator": postDataModel.getData().listNonPoItem[i].crDbIndicator,
						"deleteInd": null,
						"distrPerc": null,
						"glAccount": postDataModel.getData().listNonPoItem[i].glAccount,
						"materialDesc": postDataModel.getData().listNonPoItem[i].materialDesc,
						"internalOrderId": postDataModel.getData().listNonPoItem[i].internalOrderIdId,
						"itemId": itemId,
						"itemText": postDataModel.getData().listNonPoItem[i].itemText,
						"netValue": postDataModel.getData().listNonPoItem[i].netValue,
						"profitCenter": postDataModel.getData().listNonPoItem[i].profitCenter,
						"taxValue": postDataModel.getData().listNonPoItem[i].taxValue,
						"taxPer": postDataModel.getData().listNonPoItem[i].taxPer,
						"taxRate": postDataModel.getData().listNonPoItem[i].taxValue,
						"quantity": "0.00",
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
				"assignedTo": "",
				"balance": objectIsNew.invoiceHeader.balance,
				"balanceCheck": true,
				"totalBaseRate": objectIsNew.invoiceHeader.totalBaseRate,
				"channelType": "",
				"clearingAccountingDocument": "",
				"clearingDate": "",
				"clerkEmail": "",
				"clerkId": 0,
				"compCode": "",
				"createdAt": "",
				"createdAtFrom": "",
				"createdAtInDB": 0,
				"createdAtTo": "",
				"createdByInDb": "",
				"currency": "",
				"deposit": "",
				"disAmt": "",
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
				"id": "",
				"invoiceDate": objectIsNew.invoiceHeader.invoiceDate,
				"invoiceDateFrom": "",
				"invoiceDateTo": "",
				"invoiceItems": [],
				"invoiceTotal": objectIsNew.invoiceHeader.invoiceTotal,
				"invoiceTotalFrom": "",
				"invoiceTotalTo": "",
				"invoiceType": "NON-PO",
				"lifecycleStatus": "",
				"lifecycleStatusText": "",
				"manualpaymentBlock": "",
				"nonPoOrPo": true,
				"ocrBatchId": objectIsNew.invoiceHeader.ocrBatchId,
				"paymentBlock": "",
				"paymentBlockDesc": "",
				"paymentStatus": "",
				"paymentTerms": "",
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
				"taskOwner": objectIsNew.invoiceHeader.taskOwner,
				"taskOwnerId": this.oUserDetailModel.getProperty("/loggedinUserDetail/id"),
				"taskStatus": "",
				"taxCode": objectIsNew.invoiceHeader.taxCode,
				"taxAmount": objectIsNew.invoiceHeader.taxAmount,
				"taxValue": objectIsNew.invoiceHeader.taxValue,
				"updatedAt": 0,
				"updatedBy": "",
				"validationStatus": "",
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
				"createdAt": new Date(),
				"user": oUserDetailModel.getProperty("/name/givenName"),
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
						"createdAt": new Date(),
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
			var nonPOInvoiceModel = this.nonPOInvoiceModel;
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
				attachments.splice(index, 1);
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/attachments", attachments);

			});
		},

		onChangeUserInputTaxAmount: function (oEvent) {
			this.errorHandler(oEvent);
		},

	});

});