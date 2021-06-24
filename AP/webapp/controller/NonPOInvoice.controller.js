sap.ui.define([
	"com/menabev/AP/controller/BaseController",
	"sap/ui/model/json/JSONModel",
	"com/menabev/AP/util/POServices",
	"sap/m/MessageBox",
	"com/menabev/AP/formatter/formatter",
	"sap/ui/core/util/Export",
	"sap/ui/core/util/ExportTypeCSV",
	"sap/ui/core/routing/History",
	"sap/ui/model/Filter",
	"sap/ui/model/FilterOperator",
	"sap/ui/core/Fragment"
], function (BaseController, JSONModel, POServices, MessageBox, formatter, Export, ExportTypeCSV, History, Filter, FilterOperator,
	Fragment) {
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
			var StaticDataModel = this.getOwnerComponent().getModel("StaticDataModel");
			this.StaticDataModel = StaticDataModel;
			var oUserDetailModel = this.getOwnerComponent().getModel("oUserDetailModel");
			this.oUserDetailModel = oUserDetailModel;
			var oDropDownModel = this.getOwnerComponent().getModel("oDropDownModel");
			this.oDropDownModel = oDropDownModel;
			var oPOModel = this.getOwnerComponent().getModel("oPOModel");
			this.oPOModel = oPOModel;
			var oVisibilityModel = this.getOwnerComponent().getModel("oVisibilityModel");
			this.oVisibilityModel = oVisibilityModel;
			var oMandatoryModel = this.getOwnerComponent().getModel("oMandatoryModel");
			this.oMandatoryModel = oMandatoryModel;
			var oDataModel = this.getOwnerComponent().getModel("oDataModel");
			this.oDataModel = oDataModel;
			var oDPODetailsModel = this.getOwnerComponent().getModel("oDPODetailsModel");
			this.oDPODetailsModel = oDPODetailsModel;
			var oDataAPIModel = this.getOwnerComponent().getModel("oDataAPIModel");
			this.oDataAPIModel = oDataAPIModel;
			var oDPODetailsModel = this.getOwnerComponent().getModel("oDPODetailsModel");
			this.oDPODetailsModel = oDPODetailsModel;
			var oDataAPIModel = this.getOwnerComponent().getModel("oDataAPIModel");
			this.oDataAPIModel = oDataAPIModel;
			var ZP2P_API_EC_GL_SRV = this.getOwnerComponent().getModel("ZP2P_API_EC_GL_SRV");
			this.ZP2P_API_EC_GL_SRV = ZP2P_API_EC_GL_SRV;
			var getResourceBundle = this.getOwnerComponent().getModel("i18n").getResourceBundle();
			this.getResourceBundle = getResourceBundle;

			this.setModel(new JSONModel(), "templateModel");

			this.getBtnVisibility();
			this.oRouter = this.getOwnerComponent().getRouter();
			this.oRouter.getRoute("NonPOInvoice").attachPatternMatched(this.onRouteMatched, this);
			this.resourceBundle = this.getOwnerComponent().getModel("i18n").getResourceBundle();
			oMandatoryModel.setProperty("/NonPO", {});
			var oHeader = {
				"Content-Type": "application/json; charset=utf-8"
			};
			var oLanguage = "E";
			var countryKey = "SA";
			this.getPaymentTerm(oHeader, oLanguage);
			this.getPaymentMethod(oHeader, oLanguage);
			this.getPaymentBlock(oHeader, oLanguage);
			this.getTaxCode(oHeader, countryKey);
			this.getCostCenter("1010", oLanguage);
			//To load all OData lookups
		},

		onRouteMatched: function (oEvent) {
			//reading the Arguments from url
			var oArgs = oEvent.getParameter("arguments"),
				requestId = oArgs.id,
				status = oArgs.status;
			this.oMandatoryModel.setProperty("/NonPO", {});
			var oPOModel = this.getOwnerComponent().getModel("oPOModel");
			if (requestId === "NEW") {
				var initializeModelData = {
					"requestId": "",
					"request_created_at": 0,
					"request_created_by": "",
					"request_updated_at": 0,
					"request_updated_by": "",
					"invoice_ref_number": "",
					"vendorName": "",
					"ocrBatchId": "",
					"compCode": "1010",
					"extInvNum": "",
					"invoiceTotal": 0,
					"sapInvoiceNumber": 0,
					"invoicePdfId": "",
					"street": "",
					"zipCode": "",
					"city": "",
					"countryCode": "",
					"companyCode": "1010",
					"documentDate": 0,
					"dueDate": null,
					"invoiceDate": new Date().getTime(),
					"vendorId": "",
					"channelType": "",
					"refpurchaseDoc": "",
					"refpurchaseDocCat": "",
					"invoiceType": "NON-PO",
					"invoiceAmount": 0,
					"fiscalYear": "",
					"currency": "SAR",
					"paymentTerms": "",
					"paymentMethod": "",
					"taxAmount": 0,
					"sysSusgestedTaxAmount": 0,
					"shippingCost": 0,
					"taskStatus": "",
					"version": 0,
					"grossAmount": 0,
					"balanceAmount": 0,
					"reasonForRejection": "",
					"updatedBy": 0,
					"updatedAt": 0,
					"accountNumber": "",
					"postingDate": 0,
					"paymentBlock": "",
					"paymentBlockDesc": "",
					"workflowId": "",
					"taskId": null,
					"taskOwner": "",
					"taskGroup": "",
					"isnonPoOrPo": false,
					"transactionType": "Invoice",
					"deliveryNote": "",
					"amountBeforeTax": 0,
					"taxCode": "",
					"taxRate": "",
					"taxPercentage": 0,
					"surcharge": 0,
					"discount": 0,
					"invoiceGross": 0,
					"vatRegNum": "",
					"unplannedCost": 0,
					"plannedCost": 0,
					"baseLineDate": new Date().getTime(),
					"invoiceStatus": "",
					"invoiceStatusText": null,
					"approvalStatus": "",
					"sapInvocieNumber": null,
					"processor": null,
					"invoiceItems": [],
					"costAllocation": [],
					"activityLog": null,
					"comment": [],
					"attachment": [],
					"remediationUserList": null,
					"taxData": null,
					"headerMessages": [],
					"rejected": false
				};
				oPOModel.setProperty("/", initializeModelData);
				this.oVisibilityModel.setProperty("/openPdfBtnVisible", false);
			}
			//handle if route has NonPO request Id 
			else if (requestId) {
				POServices.getPONonPOData("", this, requestId);
			}
		},

		//Service call to load Non PO data
		//Parameter: requestId
		// getNonPOData: function (requestId) {
		// 	var sUrl = "/menabevdev/invoiceHeader/getInvoiceByRequestId/" + requestId;
		// 	jQuery.ajax({
		// 		method: "GET",
		// 		contentType: "application/json",
		// 		url: sUrl,
		// 		dataType: "json",
		// 		async: true,
		// 		success: function (oData, textStatus, jqXHR) {
		// 			this.busyDialog.close();
		// 			if (oData.responseStatus === "Success") {
		// 				this.oPOModel.setProperty("/invoiceDetailUIDto", {
		// 					invoiceHeader: oData.invoiceHeaderDto,
		// 					vstate: {}
		// 				});
		// 				var invoicePdfId = this.oPOModel.getProperty("/invoicePdfId");
		// 				if (invoicePdfId) {
		// 					this.oPOModel.setProperty("/openPdfBtnVisible", true);
		// 				}
		// 			}
		// 			this.oPOModel.refresh();
		// 		}.bind(this),
		// 		error: function (result, xhr, data) {
		// 			this.busyDialog.close();
		// 			var errorMsg = "";
		// 			if (result.status === 504) {
		// 				errorMsg = "Request timed-out. Please refresh your page";
		// 				this.errorMsg(errorMsg);
		// 			} else {
		// 				errorMsg = data;
		// 				this.errorMsg(errorMsg);
		// 			}
		// 		}.bind(this)
		// 	});
		// },

		//function hdrInvAmtCalu is triggered on change of Invoice Amount in the header field
		// hdrInvAmtCalu: function (oEvent) {
		// 	var oPOModel = this.getOwnerComponent().getModel("oPOModel"),
		// 		invoiceAmt = oEvent.getParameter("value");
		// 	if (!invoiceAmt) {
		// 		oPOModel.setProperty("/vstate/invoiceTotal", "Error");
		// 	} else {
		// 		oPOModel.setProperty("/vstate/invoiceTotal", "None");
		// 		invoiceAmt = (parseFloat(invoiceAmt)).toFixed(2);
		// 		oPOModel.setProperty("/invoiceHeader/invoiceTotal", invoiceAmt);
		// 	}
		// 	this.calculateBalance();
		// },

		//function getGLaccountValue is triggered on change of GL Account in the Cost Center table
		//Frag:NonPOCostCenter---- //Updated
		getGLaccountValue: function (oEvent) {
			var glValue = oEvent.getParameter("value");
			var oPOModel = this.getOwnerComponent().getModel("oPOModel");
			var sPath = oEvent.getSource().getBindingContext("oPOModel").getPath();
			if (glValue === "") {
				oPOModel.setProperty(sPath + "/glError", "Error");
			} else {
				oPOModel.setProperty(sPath + "/glError", "None");
			}
		},

		/**
		 * Header Filter function Starts here
		 */

		onPostingDateChange: function (oEvent) {
			// var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			POServices.onPostingDateChange(oEvent, this);
		},

		onDueDateChange: function (oEvent) {
			// var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			POServices.onDueDateChange(oEvent, this);
		},
		hdrInvAmtCalu: function (oEvent) {
			POServices.hdrInvAmtCalu(oEvent, this);
		},

		onVendorIdChange: function (oEvent) {

		},

		onVendorNameChange: function (oEvent) {

		},

		getPONonPOData: function (oEvent) {
			POServices.getPONonPOData(oEvent, this);
		},

		// VendorIdSuggest: function (oEvent) {
		// 	POServices.VendorIdSuggest(oEvent, this);
		// },

		onTransactionChange: function (oEvent) {
			POServices.onTransactionChange(oEvent, this);
		},

		vendorIdSelected: function (oEvent) {
			POServices.vendorIdSelected(oEvent, this);
		},

		onCompanyCodeChange: function (oEvent) {
			POServices.onCompanyCodeChange(oEvent, this);
		},

		onInvRefChange: function (oEvent) {
			POServices.onInvRefChange(oEvent, this);
		},

		onCurrencyChange: function (oEvent) {
			POServices.onCurrencyChange(oEvent, this);
		},

		onInvDateChange: function (oEvent) {
			POServices.onInvDateChange(oEvent, this);
		},

		onBaseLineDateChange: function (oEvent) {
			POServices.onBaseLineDateChange(oEvent, this);
		},

		onPaymentTermsChange: function (oEvent) {
			POServices.onPaymentTermsChange(oEvent, this);
		},

		onInvTypeChange: function (oEvent) {
			POServices.onInvTypeChange(oEvent, this);
		},

		onTaxCodeChange: function (oEvent) {
			POServices.onTaxCodeChange(oEvent, this);
		},

		onTaxAmtChange: function (oEvent) {
			POServices.onTaxAmtChange(oEvent, this);
		},

		onInvAmtChange: function (oEvent) {
			POServices.onInvAmtChange(oEvent, this);
		},

		//To open vendor balance
		onClickVendorBalances: function () {
			var nonPOInvoiceModel = this.oPOModel,
				vendorId = nonPOInvoiceModel.getProperty("/vendorId"),
				companyCode = nonPOInvoiceModel.getProperty("/compCode");
			this.loadVendorBalanceFrag(vendorId, companyCode);
		},

		onPressOpenpdf: function (oEvent) {
			var oPOModel = this.getOwnerComponent().getModel("oPOModel");
			var documentId = oPOModel.getProperty("/invoicePdfId");
			this.fnOpenPDF(documentId);
		},
		//End of Header Filter function

		//onChange CC item level tax
		onChangeTax: function (oEvent) {
			var taxDetails = oEvent.getParameters().selectedItem.getBindingContext("oDropDownModel").getObject(),
				sPath = oEvent.getSource().getBindingContext("oPOModel").getPath();
			var oPOModel = this.getOwnerComponent().getModel("oPOModel");
			oPOModel.setProperty(sPath + "/taxPer", taxDetails.TaxRate);
			oPOModel.setProperty(sPath + "/taxConditionType", taxDetails.TaxConditionType);
			this.amountCal(oEvent);
		},

		//This function will route to TemplateManagement view
		onClickManageTemplate: function () {
			this.oRouter.navTo("TemplateManagement");
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
		//liveChange of selected Amount in Preview Template
		onChangePreviewTotalAmt: function (oEvent) {
			var totalAmount = oEvent.getParameter("value");
			var oPOModel = this.getOwnerComponent().getModel("oPOModel");
			var sPath = oEvent.getSource().getBindingContext("oPOModel").getPath();
			if (totalAmount === "") {
				oPOModel.setProperty(sPath + "/glError", "Error");
			} else {
				oPOModel.setProperty(sPath + "/glError", "None");
			}
			var costAllocationList = oPOModel.getProperty(sPath + "/costAllocationList");
			for (var i = 0; i < costAllocationList.length; i++) {
				var partAmount = (Number(costAllocationList[i].distrPerc) * Number(totalAmount)) / 100,
					partAmtValue = this.nanValCheck(partAmount).toFixed(2);
				oPOModel.setProperty(sPath + "/costAllocationList/" + [i] + "/netValue", partAmtValue);
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
				if (!aData.amount || aData.amountError === "Error") {
					bValidate = true;
					this.getModel("templateModel").setProperty(selectedFilters[i] + "/amountError", "Error");
				}
				if (bValidate) {
					bflag = false;
					continue;
				}
			}
			if (!bflag) {
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
					this.oPOModel.setProperty("/previewListNonPoItem", data);
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

		//Frag:PreviewTemplate ---Updated
		onOkSelectTemplate: function () {
			var previewListNonPoItem = this.oPOModel.getProperty("/previewListNonPoItem");
			// var arr = [];
			var arr = $.extend(true, [], this.oPOModel.getProperty("/costAllocation"));
			for (var i = 0; i < previewListNonPoItem.length; i++) {
				arr = arr.concat(previewListNonPoItem[i].costAllocationList);
			}
			this.oPOModel.setProperty("/costAllocation", arr);
			var oPOModel = this.oPOModel;
			var taxCode = oPOModel.getProperty("/taxCode"),
				taxPercentage = oPOModel.getProperty("/taxPercentage"),
				taxConditionType = oPOModel.getProperty("/taxConditionType");
			for (var i = 0; i < arr.length; i++) {
				oPOModel.setProperty("/costAllocation/" + i + "/taxCode", taxCode);
				oPOModel.setProperty("/costAllocation/" + i + "/taxPer", taxPercentage);
				oPOModel.setProperty("/costAllocation/" + i + "/taxConditionType", taxConditionType);
			}
			oPOModel.refresh();
			POServices.costAllocationTaxCalc("", this);
			this.previewTemplateDialog.close();
			this.selectTemplateFragment.close();
		},

		//function addItem triggered when Add Row button is clicked
		//This function will add an empty row in the Cost Allocation table ----Updated
		addItem: function (oEvt) {
			var mandatoryFeildsForCC = this.fnMandatoryFeildsForCC();
			if (!mandatoryFeildsForCC) {
				var oPOModel = this.getOwnerComponent().getModel("oPOModel"),
					oPOModelData = oPOModel.getData();
				if (!oPOModelData.costAllocation) {
					oPOModelData.costAllocation = [];
				}
				oPOModelData.costAllocation.unshift({
					"templateId": "",
					"glAccount": "",
					"costCenter": "",
					"taxCode": oPOModel.getData().taxCode,
					"taxPer": oPOModel.getData().taxPercentage,
					"taxConditionType": oPOModel.getData().taxConditionType,
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
				oPOModel.refresh();
			} else {
				sap.m.MessageToast.show("Fill Invoice Amount, Tax Code and Tax Value to proceed with Cost Allocation");
			}
		},

		//function deleteNonPoItemData is triggred when the bin(Delete) icon is clicked in each row in Cost Allocation table
		//This function is associated with frag:NonPOCostCenter ----Updated
		deleteNonPoItemData: function (oEvent) {
			var oPOModel = this.getOwnerComponent().getModel("oPOModel");
			var index = oEvent.getSource().getParent().getBindingContextPath().split("/")[2];
			var sPath = oEvent.getSource().getParent().getBindingContextPath();
			var sValue = oPOModel.getProperty(sPath + "/netValue");
			oPOModel.getData().costAllocation.splice(index, 1);
			oPOModel.refresh();
			if (sValue) {
				this.amountCal(oEvent);
			}
		},

		//Support function amountCal is triggered when an row is deleted in the cost allocation table
		//Frag:NonPOCostCenter
		//on Change function that of Amount ----Updated
		amountCal: function (oEvent) {
			if (!that) {
				var that = this;
			}
			var oPOModel = this.getOwnerComponent().getModel("oPOModel");
			var sPath = oEvent.getSource().getBindingContext("oPOModel").getPath();
			var amountVal = oPOModel.getProperty(sPath + "/netValue");
			if (amountVal === "") {
				oPOModel.setProperty(sPath + "/amountError", "Error");
				sap.m.MessageBox.information("Please enter Amount!");
			} else {
				oPOModel.setProperty(sPath + "/amountError", "None");
			}
			var sDecValue = parseFloat(amountVal).toFixed(2);
			oPOModel.setProperty(sPath + "/netValue", sDecValue);
			POServices.costAllocationTaxCalc(oEvent, this);
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
		//on Change function of Cost Center ----Updated
		onChangeCostCenter: function (oEvent) {
			var sCostCenter = oEvent.getParameter("value");
			var oPOModel = this.getOwnerComponent().getModel("oPOModel");
			var sPath = oEvent.getSource().getBindingContext("oPOModel").getPath();
			if (!sCostCenter) {
				oPOModel.setProperty(sPath + "/costCenterError", "Error");
			} else {
				oPOModel.setProperty(sPath + "/costCenterError", "None");
			}
		},

		//Frag:NonPOCostCenter
		//on Change function of Text
		onChangeText: function (oEvent) {
			var sText = oEvent.getParameter("value");
			var oPOModel = this.getOwnerComponent().getModel("oPOModel");
			var sPath = oEvent.getSource().getBindingContext("oPOModel").getPath();
			if (!sText) {
				oPOModel.setProperty(sPath + "/itemTextError", "Error");
			} else {
				oPOModel.setProperty(sPath + "/itemTextError", "None");
			}
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
			var oPOModel = this.getOwnerComponent().getModel("oPOModel");
			this.taxDetails.setModel(oPOModel, "oPOModel");
			this.taxDetails.open();
		},

		taxDetailsDialogClose: function () {
			this.taxDetails.close();
		},

		//Called when the save button is clicked.
		onNonPoSave: function (oEvent) {
			// var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/PO");
			POServices.onNonPoSave(oEvent, this);
		},

		//Called on click of Submit Button.
		// onNonPoSubmit: function () {
		// 	var oSubmitData = this.fnCreatePayloadSaveSubmit();
		// 	var bCostAllocation = false;
		// 	//Header Mandatory feilds validation
		// 	var validateMandatoryFields = this.validateMandatoryFields(oSubmitData.invoiceHeaderDto);
		// 	if (!validateMandatoryFields) {
		// 		sap.m.MessageBox.error("Please fill all mandatory fields!");
		// 	}
		// 	if (validateMandatoryFields) {
		// 		if (oSubmitData.invoiceHeaderDto.costAllocation.length > 0) {
		// 			bCostAllocation = true;
		// 		} else {
		// 			bCostAllocation = false;
		// 			sap.m.MessageBox.error("Please Enter Cost Allocation Details!");
		// 		}
		// 	}

		// 	if (validateMandatoryFields && bCostAllocation) {
		// 		//COST ALLOCATION VALIDATION START 
		// 		var oPOModel = this.getOwnerComponent().getModel("oPOModel");
		// 		var costAllocation = $.extend(true, [], oPOModel.getProperty("/costAllocation"));
		// 		var bflag = true;
		// 		for (var i = 0; i < costAllocation.length; i++) {
		// 			var bValidate = false;
		// 			if (!costAllocation[i].glAccount || costAllocation[i].glError === "Error") {
		// 				bValidate = true;
		// 				costAllocation[i].glError = "Error";
		// 			}
		// 			if (!costAllocation[i].netValue || costAllocation[i].amountError === "Error") {
		// 				bValidate = true;
		// 				costAllocation[i].amountError = "Error";
		// 			}
		// 			if (!costAllocation[i].costCenter || costAllocation[i].costCenterError === "Error") {
		// 				bValidate = true;
		// 				costAllocation[i].costCenterError = "Error";
		// 			}
		// 			if (!costAllocation[i].itemText || costAllocation[i].itemTextError === "Error") {
		// 				bValidate = true;
		// 				costAllocation[i].itemTextError = "Error";
		// 			}
		// 			if (bValidate) {
		// 				bflag = false;
		// 				continue;
		// 			}
		// 		}
		// 		if (!bflag) {
		// 			oPOModel.setProperty("/costAllocation", costAllocation);
		// 			var sMsg = "Please Enter Required Fields G/L Account,Amount,Cost Center & Text!";
		// 			sap.m.MessageBox.error(sMsg);
		// 			return;
		// 		} else {
		// 			//COST ALLOCATION VALIDATION END
		// 			oSubmitData.invoiceHeaderDto.taskOwner = this.oUserDetailModel.getProperty("/loggedInUserMail");
		// 			oSubmitData.invoiceHeaderDto.docStatus = "Created";
		// 			var balance = oSubmitData.invoiceHeaderDto.balance;
		// 			if (this.nanValCheck(balance) !== 0) {
		// 				sap.m.MessageBox.error("Balance is not 0");
		// 				return;
		// 			}
		// 			var totalTax = oPOModel.getProperty("/taxAmount");
		// 			var userInputTaxAmount = oPOModel.getProperty("/taxValue");
		// 			var taxDifference = this.nanValCheck(userInputTaxAmount) - this.nanValCheck(totalTax);
		// 			if (this.nanValCheck(taxDifference) !== 0) {
		// 				sap.m.MessageBox.error("Tax MisMatched");
		// 				return;
		// 			}

		// 			var sUrl = "/menabevdev/invoiceHeader/accountantSubmit",
		// 				sMethod = "POST";
		// 			this.saveSubmitServiceCall(oSubmitData, sMethod, sUrl);
		// 		}
		// 	}

		// },
		onNonPoSubmit: function (oEvent) {
			var MandatoryFileds = this.StaticDataModel.getProperty("/mandatoryFields/NonPo");
			POServices.onNonPoSubmit(oEvent, this, MandatoryFileds);
		},

		//function saveSubmitServiceCall is triggered on SUBMIT or SAVE
		// saveSubmitServiceCall: function (oData, sMethod, sUrl, save) {
		// 	var that = this;
		// 	var oPOModel = this.getOwnerComponent().getModel("oPOModel");
		// 	this.busyDialog.open();
		// 	$.ajax({
		// 		url: sUrl,
		// 		method: sMethod,
		// 		async: false,
		// 		contentType: "application/json",
		// 		dataType: "json",
		// 		data: JSON.stringify(oData),
		// 		success: function (data, xhr, result) {
		// 			this.busyDialog.close();
		// 			var message = data.responseStatus;
		// 			var ReqId = data.invoiceHeaderDto.requestId;
		// 			oPOModel.setProperty("/requestId", ReqId);
		// 			if (result.status === 200) {
		// 				sap.m.MessageBox.success(message, {
		// 					actions: [sap.m.MessageBox.Action.OK],
		// 					onClose: function (sAction) {
		// 						if (!save) {
		// 							that.oRouter.navTo("Inbox");
		// 						}
		// 					}
		// 				});
		// 			} else {
		// 				sap.m.MessageBox.information(message, {
		// 					actions: [sap.m.MessageBox.Action.OK]
		// 				});
		// 			}
		// 		}.bind(this),
		// 		error: function (result, xhr, data) {
		// 			this.busyDialog.close();
		// 			var errorMsg = "";
		// 			if (result.status === 504) {
		// 				errorMsg = "Request timed-out. Please refresh your page";
		// 				this.errorMsg(errorMsg);
		// 			} else {
		// 				errorMsg = data;
		// 				this.errorMsg(errorMsg);
		// 			}
		// 		}.bind(this)
		// 	});
		// },

		//this function used to form payload for SUBMIT and SAVE
		// fnCreatePayloadSaveSubmit: function (oEvent) {
		// 	var oPOModel = this.getOwnerComponent().getModel("oPOModel"),
		// 		nonPOInvoiceModelData = oPOModel.getData();

		// 	var objectIsNew = jQuery.extend({}, nonPOInvoiceModelData);
		// 	objectIsNew.costAllocation = [];
		// 	var reqId = objectIsNew.invoiceHeader.requestId ? objectIsNew.invoiceHeader.requestId : null;
		// 	if (nonPOInvoiceModelData.costAllocation) {
		// 		for (var i = 0; i < nonPOInvoiceModelData.costAllocation.length; i++) {
		// 			var itemId = nonPOInvoiceModelData.costAllocation[i].itemId ? nonPOInvoiceModelData.costAllocation[i].itemId : null;
		// 			var costAllocationId = nonPOInvoiceModelData.costAllocation[i].costAllocationId ? nonPOInvoiceModelData.costAllocation[i].costAllocationId :
		// 				null;
		// 			objectIsNew.costAllocation.push({
		// 				"accountNum": nonPOInvoiceModelData.costAllocation[i].accountNum,
		// 				"assetNo": null,
		// 				"baseRate": nonPOInvoiceModelData.costAllocation[i].baseRate,
		// 				"costAllocationId": costAllocationId,
		// 				"costCenter": nonPOInvoiceModelData.costAllocation[i].costCenter,
		// 				"crDbIndicator": nonPOInvoiceModelData.costAllocation[i].crDbIndicator,
		// 				"deleteInd": null,
		// 				"distrPerc": nonPOInvoiceModelData.costAllocation[i].distrPerc,
		// 				"glAccount": nonPOInvoiceModelData.costAllocation[i].glAccount,
		// 				"internalOrderId": nonPOInvoiceModelData.costAllocation[i].internalOrderId,
		// 				"itemId": itemId,
		// 				"itemText": nonPOInvoiceModelData.costAllocation[i].itemText,
		// 				"lineText": "",
		// 				"materialDesc": nonPOInvoiceModelData.costAllocation[i].materialDesc,
		// 				"netValue": nonPOInvoiceModelData.costAllocation[i].netValue,
		// 				"orderId": "",
		// 				"profitCenter": nonPOInvoiceModelData.costAllocation[i].profitCenter,
		// 				"quantity": "0.00",
		// 				"quantityUnit": "",
		// 				"taxCode": nonPOInvoiceModelData.costAllocation[i].taxCode,
		// 				"taxPer": nonPOInvoiceModelData.costAllocation[i].taxPer,
		// 				"taxValue": nonPOInvoiceModelData.costAllocation[i].taxValue,
		// 				"taxConditionType": nonPOInvoiceModelData.costAllocation[i].taxConditionType,
		// 				"requestId": reqId,
		// 				"serialNo": 0,
		// 				"subNumber": null,
		// 				"wbsElement": null
		// 			});

		// 		}
		// 	}
		// 	var obj = {};
		// 	var invoiceHeaderDto = {
		// 		"accountNumber": "",
		// 		"accountingDoc": "",
		// 		"amountBeforeTax": "",
		// 		"assignedTo": "",
		// 		"balance": objectIsNew.balance,
		// 		"balanceCheck": true,
		// 		"baseLine": "",
		// 		"channelType": "",
		// 		"claimed": "",
		// 		"claimedUser": "",
		// 		"clearingAccountingDocument": "",
		// 		"clearingDate": "",
		// 		"clerkEmail": "",
		// 		"clerkId": 0,
		// 		"compCode": objectIsNew.compCode,
		// 		"createdAt": "",
		// 		"createdAtFrom": "",
		// 		"createdAtInDB": 0,
		// 		"createdAtTo": "",
		// 		"createdByInDb": "",
		// 		"createdOn": 0,
		// 		"currency": "",
		// 		"deliveryNote": "",
		// 		"deposit": "",
		// 		"disAmt": "",
		// 		"discount": "",
		// 		"docStatus": "",
		// 		"dueDate": objectIsNew.dueDate,
		// 		"dueDateFrom": "",
		// 		"dueDateTo": "",
		// 		"emailFrom": "",
		// 		"extInvNum": objectIsNew.extInvNum,
		// 		"filterFor": "",
		// 		"fiscalYear": "",
		// 		"forwaredTaskOwner": "",
		// 		"grossAmount": objectIsNew.grossAmount,
		// 		"headerText": "",
		// 		"invoiceDate": objectIsNew.invoiceDate,
		// 		"invoiceDateFrom": "",
		// 		"invoiceDateTo": "",
		// 		"invoiceItems": [],
		// 		"invoicePdfId": objectIsNew.invoicePdfId,
		// 		"invoiceTotal": Number(objectIsNe.invoiceTotal),
		// 		"invoiceTotalFrom": "",
		// 		"invoiceTotalTo": "",
		// 		"invoiceType": "NON-PO",
		// 		"lifecycleStatus": "",
		// 		"lifecycleStatusText": "",
		// 		"manualpaymentBlock": "",
		// 		"nonPoOrPo": true,
		// 		"ocrBatchId": objectIsNe.ocrBatchId,
		// 		"paymentBlock": objectIsNew.paymentBlock,
		// 		"paymentBlockDesc": "",
		// 		"paymentStatus": "",
		// 		"paymentMethod": "",
		// 		"paymentTerms": objectIsNew.paymentTerms,
		// 		"plannedCost": "",
		// 		"processor": "",
		// 		"postingDate": objectIsNew.postingDate,
		// 		"reasonForRejection": "",
		// 		"refpurchaseDoc": "",
		// 		"refpurchaseDocCat": "",
		// 		"rejected": "",
		// 		"request_created_at": 0,
		// 		"request_created_by": "",
		// 		"request_updated_at": 0,
		// 		"request_updated_by": "",
		// 		"requestId": reqId,
		// 		"sapInvoiceNumber": 0,
		// 		"shippingCost": 0,
		// 		"subTotal": "",
		// 		"street": "",
		// 		"surcharge": "",
		// 		"sysSusgestedTaxAmount": "",
		// 		"taskGroup": "",
		// 		"taskOwner": objectIsNew.taskOwner,
		// 		"taskOwnerId": this.oUserDetailModel.getProperty("/loggedInUserMail"),
		// 		"taskStatus": objectIsNew.taskStatus,
		// 		"taxAmount": objectIsNew.taxAmount,
		// 		"taxCode": objectIsNew.taxCode,
		// 		"taxRate": objectIsNew.taxPer,
		// 		"taxValue": objectIsNew.taxValue,
		// 		"totalBaseRate": objectIsNew.totalBaseRate,
		// 		"transactionType": "",
		// 		"unplannedCost": "",
		// 		"updatedAt": 0,
		// 		"updatedBy": "",
		// 		"vatRegNum": "",
		// 		"vendorId": objectIsNew.vendorId,
		// 		"vendorName": objectIsNew.vendorName,
		// 		"version": 0,
		// 		"zipCode": ""
		// 	};
		// 	obj.invoiceHeaderDto = invoiceHeaderDto;
		// 	obj.invoiceHeaderDto.attachments = [];
		// 	if (oPOModel.getData().docManagerDto && oPOModel.getData().docManagerDto.length > 0) {
		// 		for (var n = 0; n < oPOModel.getData().docManagerDto.length; n++) {
		// 			obj.invoiceHeaderDto.attachments.push(oPOModel.getData().docManagerDto[n]);
		// 		}
		// 	}
		// 	obj.invoiceHeaderDto.comments = [];
		// 	if (oPOModel.getData().invoiceDetailUIDto.commentDto && oPOModel.getData().invoiceDetailUIDto.commentDto.length >
		// 		0) {
		// 		for (var k = 0; k < oPOModel.getData().invoiceDetailUIDto.commentDto.length; k++) {
		// 			obj.invoiceHeaderDto.comments.push(oPOModel.getData().invoiceDetailUIDto.commentDto[k]);
		// 		}
		// 	}
		// 	obj.invoiceItemAcctAssignmentDto = objectIsNew.invoiceItems;
		// 	obj.costAllocationDto = objectIsNew.costAllocation;
		// 	return obj;
		// },

		//Called on click of Reject Button.
		//Frag:RejectDialog will open  
		onNonPoReject: function () {
			var oPOModel = this.getOwnerComponent().getModel("oPOModel");
			this.rejectDialog = sap.ui.xmlfragment("com.menabev.AP.fragment.RejectDialog", this);
			this.getView().addDependent(this.rejectDialog);
			this.rejectDialog.setModel(oPOModel, "oPOModel");
			this.getReasonOfRejection();
			this.rejectDialog.open();
		},

		//Frag:RejectDialog
		onRejectCombo: function (oEvent) {
			var oPOModel = this.getOwnerComponent().getModel("oPOModel");
			oPOModel.setProperty("/Textvalue", oEvent.getParameter("selectedItem").getProperty("text"));
			oPOModel.setProperty("/Keyvalue", oEvent.getParameter("selectedItem").getProperty("key"));
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
									that.oRouter.navTo("Inbox");
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

		//Mandatory field check of Header Feilds
		validateMandatoryFields: function (oData) {
			var oPOModel = this.getOwnerComponent().getModel("oPOModel");
			var key;
			var flag = true;
			for (key in oData) {
				if (mandatoryFields.includes(key)) {
					if (!oData[key]) {
						oPOModel.setProperty("/vstate/" + key, "Error");
						flag = false;
					} else {
						oPOModel.setProperty("/vstate/" + key, "None");
					}
				}
			}
			return flag;
		},

		//Mandatory fields validation before adding cost allocation 
		fnMandatoryFeildsForCC: function () {
			var oMandatoryModel = this.oMandatoryModel;
			var oPOModel = this.oPOModel,
				nonPOInvoiceModelData = oPOModel.getData(),
				bError = false,
				mandatoryFeildsForCC = ["invoiceTotal", "taxCode", "taxValue"],
				manLength = mandatoryFeildsForCC.length,
				data;

			var key;
			for (var i = 0; i < manLength; i++) {
				data = oPOModel.getProperty("/" + mandatoryFeildsForCC[i]);
				if (data) {
					oMandatoryModel.setProperty("/NonPO/" + mandatoryFeildsForCC[i] + "State", "None");
				} else {
					oMandatoryModel.setProperty("/NonPO/" + mandatoryFeildsForCC[i] + "State", "Error");
					bError = true;
				}
			}
			return bError;
		},

		onNavBack: function () {
			this.oRouter.navTo("Inbox");
		},

		onChangeUserInputTaxAmount: function (oEvent) {
			var oPOModel = this.oPOModel,
				userInputTaxAmount = oEvent.getParameter("value");
			if (!userInputTaxAmount) {
				this.oMandatoryModel.setProperty("NonPO/taxValueState", "Error");
			} else {
				this.oMandatoryModel.setProperty("NonPO/taxValueState", "None");
				userInputTaxAmount = (parseFloat(userInputTaxAmount)).toFixed(2);
				oPOModel.setProperty("/taxValue", userInputTaxAmount);
			}
			POServices.calculateGrossAmount(oEvent, this);
			POServices.calculateBalance(oEvent, this);
		},

		//Vendor search valuehelp request
		onValueHelpRequested: function () {
			var nonPOInvoiceModel = this.oPOModel;
			if (!this.vendorValueHelp) {
				this.vendorValueHelp = sap.ui.xmlfragment("com.menabev.AP.fragment.VendorValueHelp", this);
				this.getView().addDependent(this.vendorValueHelp);
			}
			var vendorSearchModel = new JSONModel();
			this.getView().setModel(vendorSearchModel, "vendorSearchModel");
			vendorSearchModel.setProperty("/vendorId", nonPOInvoiceModel.getProperty("/vendorId"));
			vendorSearchModel.setProperty("/vendorName", nonPOInvoiceModel.getProperty("/vendorName"));
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
			var nonPOInvoiceModel = this.oPOModel;
			nonPOInvoiceModel.setProperty("/vendorId", selectedObject.Supplier);
			nonPOInvoiceModel.setProperty("/vendorName", selectedObject.SupplierName);
			this.vendorValueHelp.close();
		},

		onCloseVendorValueHelp: function () {
			this.vendorValueHelp.close();
		},

	});

});