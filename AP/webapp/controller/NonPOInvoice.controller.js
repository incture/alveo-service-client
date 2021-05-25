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

			var oComponent = this.getOwnerComponent();
			this.router = oComponent.getRouter();
			this.router.getRoute("NonPOInvoice").attachPatternMatched(this.onRouteMatched, this);
			// this.oRouter = sap.ui.core.UIComponent.getRouterFor(this);
			// this.oRouter.attachRoutePatternMatched(this.onRouteMatched, this);

		},

		onRouteMatched: function (oEvent) {
			//reading the Arguments from url
			var oArgs = oEvent.getParameter("arguments");
			var requestId = oArgs.value;
			this.busyDialog.open();
			//handle if route has NonPO request Id 
			//requestId = "APA-000004"; //Test Data
			if (requestId) {
				this.getNonPOData(requestId);
			} else {
				this.busyDialog.close();
				//Start of Test Data
				var initializeModelData = {
					"invoiceHeader": {
						"accountNumber": "",
						"accountingDoc": "",
						"assignedTo": "",
						"attachments": [],
						"balance": "",
						"balanceCheck": true,
						"channelType": "",
						"clearingAccountingDocument": "",
						"clearingDate": "",
						"clerkEmail": "",
						"clerkId": 0,
						"comments": [],
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
						"dueDate": "",
						"dueDateFrom": "",
						"dueDateTo": "",
						"emailFrom": "",
						"extInvNum": "",
						"filterFor": "",
						"fiscalYear": "",
						"forwaredTaskOwner": "",
						"grossAmount": "",
						"headerText": "",
						"id": "",
						"invoiceDate": "",
						"invoiceDateFrom": "",
						"invoiceDateTo": "",
						"invoiceTotal": "",
						"invoiceTotalFrom": "",
						"invoiceTotalTo": "",
						"invoiceItems": [],
						"invoiceType": "",
						"lifecycleStatus": "",
						"lifecycleStatusText": "",
						"manualpaymentBlock": "",
						"nonPoOrPo": true,
						"ocrBatchId": "",
						"paymentBlock": "",
						"paymentBlockDesc": "",
						"paymentStatus": "",
						"paymentTerms": "",
						"postingDate": 0,
						"postingDateFrom": 0,
						"postingDateTo": 0,
						"reasonForRejection": "",
						"refDocCat": "",
						"refDocNum": 0,
						"rejectionText": "",
						"requestId": "",
						"sapInvoiceNumber": 0,
						"shippingCost": 0,
						"subTotal": "",
						"taskOwner": "",
						"taskStatus": "",
						"taxAmount": "",
						"updatedAt": 0,
						"updatedBy": "",
						"validationStatus": "",
						"vendorId": "",
						"vendorName": "",
						"version": 0
							// "invoiceTotal": "",
							// "requestId": "",
							// "vendorId": "",
							// "extInvNum": "",
							// "invoiceDate": "",
							// "postingDate": ""
					},
					"vstate": {}
				};
				this.getModel("nonPOInvoiceModel").setProperty("/invoiceDetailUIDto", initializeModelData);
				//End of Test data
			}
		},

		//Service call to load Non PO data
		//Parameter: requestId
		getNonPOData: function (requestId) {
			// var sUrl = "menabev-dev/invoiceHeader?requestId=" + requestId;
			var sUrl = "menabev-dev/invoiceHeader/getInvoiceByRequestId/" + requestId;

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
					}
					// this.amtCalculation();
					// this.getPdfData();
					// this.getCommentData();
					// this.selectedObjects = [];
					// this.selectedTab = "nonPO";
					// this._getUser();
					this.getModel("nonPOInvoiceModel").refresh();
					this.getModel("postDataModel").refresh();

				}.bind(this),
				error: function (result, xhr, data) {
					this.busyDialog.close();
					var errorMsg = "";
					if (result.status === 504) {
						errorMsg = "Request timed-out. Please try again using different search filters or add more search filters.";
						this.errorMsg(errorMsg);
					} else {
						errorMsg = result.responseJSON.error.message.value;
						this.errorMsg(errorMsg);
					}
				}.bind(this)
			});
		},

		//Support function called on intialize of the page
		//requestID is passed to load all the comments associated with that.
		getCommentData: function () {
			var nonPOInvoiceModel = this.getView().getModel("nonPOInvoiceModel");
			$.ajax({
				url: "InctureApDest/comment?requestId=" + nonPOInvoiceModel.getData().invoiceDetailUIDto.invoiceHeader.requestId,
				method: "GET",
				async: true,
				success: function (result, xhr, data) {
					nonPOInvoiceModel.getData().invoiceDetailUIDto.commentDto = result.commentDtos;
					nonPOInvoiceModel.refresh();
				}.bind(this)
			});
		},

		//Support function to load the PDF data on the initialization
		getPdfData: function () {
			var nonPOInvoiceModel = this.getView().getModel("nonPOInvoiceModel");
			var oInvoiceModel = this.getView().getModel("oInvoiceModel");
			$.ajax({
				url: "InctureApDest/attachment?requestId=" + nonPOInvoiceModel.getData().invoiceDetailUIDto.invoiceHeader.requestId,
				method: "GET",
				async: true,
				success: function (result, xhr, data) {
					oInvoiceModel.setProperty("/bPdfBtn", false);
					for (var i = 0; i < result.attachmentList.length; i++) {
						if (result.attachmentList[i].master) {
							nonPOInvoiceModel.getData().docManagerDto = result.attachmentList.splice(i, 1);
							oInvoiceModel.setProperty("/bPdfBtn", true);
						}
					}
					nonPOInvoiceModel.getData().docManagerDto = result.attachmentList;
					nonPOInvoiceModel.refresh();
				}.bind(this)
			});
		},

		//function hdrInvAmtCalu is triggered on change of Invoice Amount in the header field
		hdrInvAmtCalu: function (oEvent) {
			var nonPOInvoiceModel = this.getView().getModel("nonPOInvoiceModel");
			var inValue = oEvent.getParameter("value");
			if (inValue === "") {
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/vstate/invoiceTotal", "Error");
			} else {
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/vstate/invoiceTotal", "None");
				var invAmt = (parseFloat(inValue)).toFixed(3);
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/invoiceTotal", invAmt);
				var aDetails = nonPOInvoiceModel.getData().invoiceDetailUIDto.invoiceHeader;
				var fBalance = (parseFloat(aDetails.invoiceTotal - aDetails.grossAmount)).toFixed(3);
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/balance", fBalance);
			}
		},

		//function getGLaccountValue is triggered on change of GL Account in the Cost Center table
		//Frag:NonPOCostCenter
		getGLaccountValue: function (oEvent) {
			var glValue = oEvent.getParameter("value");
			var postDataModel = this.getView().getModel("postDataModel");
			var sPath = oEvent.getSource().getBindingContext("postDataModel").getPath();
			if (glValue === "") {
				postDataModel.setProperty(sPath + "/glError", "Error");
			} else {
				postDataModel.setProperty(sPath + "/glError", "None");
			}
		},
		// function #toggleHdrLayout is triggered when the segmented btn is toggled
		toggleHdrLayout: function (oEvent) {
			var selKey = oEvent.getSource().getProperty("selectedKey");
			var flag = true;
			this.getView().byId("invoiceDetailTab").setVisible(!flag);
			this.getView().byId("pdftableVbox").setVisible(!flag);
			this.getView().byId("emailSection").setVisible(!flag);
			this.getView().byId("attachmentTab").setVisible(!flag);
			this.getView().byId("commnetTab").setVisible(!flag);
			this.getView().byId("nonPoAbby").setVisible(!flag);
			if (selKey === "invoice") {
				this.getView().byId("invoiceDetailTab").setVisible(flag);
				this.getView().byId("emailSection").setVisible(flag);
				this.getView().byId("attachmentTab").setVisible(flag);
				this.getView().byId("commnetTab").setVisible(flag);
				this.getView().byId("nonPoAbby").setVisible(flag);
			} else {
				this.getView().byId("pdftableVbox").setVisible(flag);
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
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel");
			var vendorId = oEvent.getParameter("value");
			if (vendorId === "") {
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/vstate/vendorId", "Error");
			} else {
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/vstate/vendorId", "None");
			}
		},

		errorHandler: function (oEvent) {
			var input = oEvent.getParameter("value");
			if (!input) {
				oEvent.getSource().setValueState("Error");
			} else {
				oEvent.getSource().setValueState("None");
			}
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
			var nonPOInvoiceModel = this.getView().getModel("nonPOInvoiceModel");
			var invoiceNo = oEvent.getParameter("value");
			if (invoiceNo === "") {
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/vstate/extInvNum", "Error");
			} else {
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/vstate/extInvNum", "None");
			}
		},

		onPostingDateChange: function (oEvent) {
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel");
			nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/postingDate", oEvent.getSource()._getSelectedDate().getTime());
			var postingDate = oEvent.getParameter("value");
			if (postingDate === "") {
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/vstate/postingDate", "Error");
			} else {
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/vstate/postingDate", "None");
			}
		},

		//onclick of Tax Code ValueHelp
		onValueHelpRequest: function () {
			var oView = this.getView();

			if (!this._pValueHelpDialog) {
				this._pValueHelpDialog = Fragment.load({
					id: oView.getId(),
					name: "com.menabev.AP.fragment.TaxCodeValueHelp",
					controller: this
				}).then(function (oValueHelpDialog) {
					oView.addDependent(oValueHelpDialog);
					return oValueHelpDialog;
				});
			}
			this._pValueHelpDialog.then(function (oValueHelpDialog) {
				this._configValueHelpDialog();
				oValueHelpDialog.open();
			}.bind(this));
		},

		_configValueHelpDialog: function () {
			var aTax = [{
				"taxCode": "I0",
				"taxCodeDescription": "ProductId",
				"taxRatePercentage": "0"
			}, {
				"taxCode": "I1",
				"taxCodeDescription": "test",
				"taxRatePercentage": "15"
			}, {
				"taxCode": "IE",
				"taxCodeDescription": "ProductId",
				"taxRatePercentage": "5"
			}];
			this.setModel(new JSONModel(), "taxCodeModel");
			this.getModel("taxCodeModel").setProperty("/aTax", aTax);
			var sInputValue = this.byId("taxCodeId").getValue(),
				aProducts = this.getModel("taxCodeModel").getProperty("/aTax");

			aProducts.forEach(function (oProduct) {
				oProduct.selected = (oProduct.taxCode === sInputValue);
			});

		},

		onValueHelpDialogClose: function (oEvent) {
			var oSelectedItem = oEvent.getParameter("selectedItem"),
				oInput = this.byId("taxCodeId");

			if (!oSelectedItem) {
				// oInput.resetProperty("value");
				return;
			}
			oInput.setValue(oSelectedItem.getTitle());
		},

		onValueHelpOkPress: function (oEvent) {
			var aTokens = oEvent.getParameter("tokens");
			this.getView().byId("taxCodeId").setSelectedKey(aTokens[0].getKey());
			this.oValueHelpDialogTaxCode.close();
		},

		onValueHelpCancelPress: function () {
			this.oValueHelpDialogTaxCode.close();
		},
		//End of Header Filter function

		onPressOpenpdf: function () {
			this.byId("grid").setDefaultSpan("L6 M6 S12");
			this.byId("grid2").setDefaultSpan("L6 M6 S12");
			this.addPDFArea();
		},

		addPDFArea: function () {
			var that = this;
			var pdfData = this.getView().getModel("nonPOInvoiceModel").getData().docManagerDto[0];
			that.pdf = sap.ui.xmlfragment(that.getView().getId(), "com.menabev.AP.fragment.PDF", that);
			var oPdfFrame = that.pdf.getItems()[1];
			oPdfFrame.setContent('<embed width="100%" height="859rem" name="plugin" src="data:application/pdf;base64, ' + pdfData.fileBase64 +
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
			this._resizeSplitterLayout();
			this.byId("grid").setDefaultSpan("L3 M4 S12");
			this.byId("grid2").setDefaultSpan("L3 M6 S12");
		},

		//This function will route to TemplateManagement view
		onClickManageTemplate: function () {
			this.router.navTo("TemplateManagement");
		},

		//function onSelectTemplate is triggered on click of Select template
		//To open a Fragment Select Template Frag:SelectTemplate
		onSelectTemplate: function (oEvt) {
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
		},

		//function call to load all templates 
		getAllTemplate: function () {
			var templateModel = this.getModel("templateModel");
			/*	var growCount = 10;
				templateModel.setProperty("/growCount",growCount);*/
			//	var pageNo = 0;
			//	var url = "InctureApDest/NonPoTemplate/getAll/50/" + pageNo;
			templateModel.setProperty("/allocateTempBtnEnabled", false);
			var url = "menabev-dev/NonPoTemplate/selectNonPoTemplate";
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
						errorMsg = "Request timed-out. Please try again using different search filters or add more search filters.";
						this.errorMsg(errorMsg);
					} else {
						errorMsg = result.responseJSON.error.message.value;
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
			// var aFilters = [];
			// var sQuery = oEvt.getParameter("newValue");
			// if (sQuery && sQuery.length > 0) {
			// 	var afilter = new sap.ui.model.Filter([
			// 			new sap.ui.model.Filter("templateName", sap.ui.model.FilterOperator.Contains, sQuery)
			// 		],
			// 		false);
			// 	aFilters.push(afilter);
			// }
			// var oList = this.getView().byId("templateSelectId");
			// var oBinding = oList.getBinding("items");
			// oBinding.filter([aFilters]);
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
					partAmtValue = this.nanValCheck(partAmount).toFixed(3);
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
			var sUrl = "menabev-dev/costAllocation/getCostAllocationForTemplate";
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
						errorMsg = "Request timed-out. Please try again using different search filters or add more search filters.";
						this.errorMsg(errorMsg);
					} else {
						errorMsg = result.responseJSON.error.message.value;
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
			var previewListNonPoItem = this.getModel("postDataModel").getProperty("/previewListNonPoItem");
			// var arr = [];
			var arr = $.extend(true, [], this.getModel("postDataModel").getProperty("/listNonPoItem"));
			for (var i = 0; i < previewListNonPoItem.length; i++) {
				arr = arr.concat(previewListNonPoItem[i].costAllocationList);
			}
			this.getModel("postDataModel").setProperty("/listNonPoItem", arr);
			var postDataModel = this.getModel("postDataModel");
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel");
			var totalAmt = 0;
			if (postDataModel.getProperty("/listNonPoItem")) {
				var length = postDataModel.getProperty("/listNonPoItem").length;
				for (var i = 0; i < length; i++) {
					if (postDataModel.getProperty("/listNonPoItem/" + i + "/netValue") && postDataModel.getProperty("/listNonPoItem/" + i +
							"/crDbIndicator") === "H") {
						totalAmt += this.nanValCheck(postDataModel.getProperty("/listNonPoItem/" + i + "/netValue"));

					} else if (postDataModel.getProperty("/listNonPoItem/" + i + "/netValue") && postDataModel.getProperty("/listNonPoItem/" + i +
							"/crDbIndicator") === "S") {
						totalAmt -= this.nanValCheck(postDataModel.getProperty("/listNonPoItem/" + i + "/netValue"));
					}
				}
				totalAmt = this.nanValCheck(totalAmt).toFixed(3);
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/grossAmount", totalAmt);
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/balance", totalAmt);
				var invAmt = this.nanValCheck(nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/invoiceTotal"));
				var diff = this.nanValCheck(invAmt) - this.nanValCheck(totalAmt);
				diff = this.nanValCheck(diff).toFixed(3);
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/balance", diff);
				nonPOInvoiceModel.refresh();
				postDataModel.refresh();
			}
			this.previewTemplateDialog.close();
			this.selectTemplateFragment.close();
		},

		onSelectokTemp: function (oEvent) {
			var that = this;
			var oInvoiceModel = this.getModel("oInvoiceModel");
			var templateModel = this.getModel("templateModel");
			var postDataModel = this.getModel("postDataModel");
			var aNonPoTemplate = templateModel.getProperty("/aNonPoTemplate");
			var len = this.selectTemplateFragment._oTable._aSelectedPaths.length;
			if (len > 0) {
				var arr = [];
				for (var i = 0; i < len; i++) {
					var sIndx = this.selectTemplateFragment._oTable._aSelectedPaths[i].split("/")[2];
					var aIndexValue = Number(sIndx);
					arr.push(aIndexValue);
				}
				that.openBusyDialog();
				var listNonPoItem = $.extend(true, [], postDataModel.getProperty("/listNonPoItem"));
				var arrLength = arr.length;
				for (var j = 0; j < arrLength; j++) {
					var tempid = aNonPoTemplate[arr[j]].templateId;
					var sUrl = "menabev-dev/NonPoTemplate/getItemsByTemplateId/" + tempid;
					jQuery.ajax({
						type: "GET",
						contentType: "application/json",
						url: sUrl,
						dataType: "json",
						async: true,
						success: function (data, textStatus, jqXHR) {

							var aData = data;
							for (var k = 0; k < aData.length; k++) {
								listNonPoItem.push(aData[k]);
							}
							postDataModel.setProperty("/listNonPoItem", listNonPoItem);
							that.selectTemplateFragment.exit();
							that.closeBusyDialog();
						},
						error: function (err) {
							that.closeBusyDialog();
							sap.m.MessageToast.show(err.statusText);
						}
					});
					postDataModel.refresh();
					templateModel.refresh();
				}
			} else {
				MessageBox.error("Please select atleast template!");
			}
		},

		//function addItem triggered when Add Row button is clicked
		//This function will add an empty row in the Cost Allocation table
		addItem: function (oEvt) {
			var postDataModel = this.getModel("postDataModel");
			var postDataModelData = postDataModel.getData();
			if (!postDataModelData.listNonPoItem) {
				postDataModelData.listNonPoItem = [];
			}
			var glCode = "",
				materialDescription = "",
				crDbIndicator = "H",
				netValue = "",
				costCenter = "",
				// taxCode = this.getView().byId("taxCodeId").getValue(),
				internalOrderId = "",
				profitCenter = "",
				itemText = "",
				companyCode = "",
				templateId = "";
			postDataModelData.listNonPoItem.unshift({
				"templateId": templateId,
				"glAccount": glCode,
				"costCenter": costCenter,
				// "taxCode": taxCode,
				"internalOrderId": internalOrderId,
				"materialDescription": materialDescription,
				"crDbIndicator": crDbIndicator,
				"netValue": netValue,
				"profitCenter": profitCenter,
				"itemText": itemText,
				"companyCode": companyCode,
				"assetNo": null,
				"subNumber": null,
				"wbsElement": null,
				"isNonPo": true
			});
			postDataModel.refresh();
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
			var nonPOInvoiceModel = that.getModel("nonPOInvoiceModel");
			var sDecValue = parseFloat(amountVal).toFixed(3);
			postDataModel.setProperty(sPath + "/netValue", sDecValue);
			var totalAmt = 0;
			if (postDataModel.getProperty("/listNonPoItem")) {
				var length = postDataModel.getProperty("/listNonPoItem").length;
				for (var i = 0; i < length; i++) {
					if (postDataModel.getProperty("/listNonPoItem/" + i + "/netValue") && postDataModel.getProperty("/listNonPoItem/" + i +
							"/crDbIndicator") === "H") {
						totalAmt += that.nanValCheck(postDataModel.getProperty("/listNonPoItem/" + i + "/netValue"));

					} else if (postDataModel.getProperty("/listNonPoItem/" + i + "/netValue") && postDataModel.getProperty("/listNonPoItem/" + i +
							"/crDbIndicator") === "S") {
						totalAmt -= that.nanValCheck(postDataModel.getProperty("/listNonPoItem/" + i + "/netValue"));
					}
				}
				totalAmt = that.nanValCheck(totalAmt).toFixed(3);
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/grossAmount", totalAmt);
				var invAmt = that.nanValCheck(nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/invoiceTotal"));
				var diff = that.nanValCheck(invAmt) - that.nanValCheck(totalAmt);
				diff = that.nanValCheck(diff).toFixed(3);
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/balance", diff);
				nonPOInvoiceModel.refresh();
				postDataModel.refresh();
			}
		},

		//Support function to validate Amount field in Cost Allocation table based on the Debit/Credit changed
		//Frag:NonPOCostCenter
		nanValCheck: function (value) {
			if (!value || value === NaN || value === "NaN" || isNaN(value)) {
				return 0;
			} else if (Number(value) != NaN) {
				if (parseFloat(value) === -0) {
					return 0;
				}
				return parseFloat(value);
			} else if (value === "0" || value === "0.00" || value === "-0.00") {
				return 0;
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

		/*Start of Excel Upload*/
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
						content: "{materialDescription}"
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

		//function openFileExplorer is triggered when the upload icon(Excel Upload) is triggered.
		//this function will open a fragment:OpenFileExplorer which will allow user to browse option to upload an excel.
		openFileExplorer: function () {
			if (!this.openFileExplorerFrag) {
				sap.ui.getCore().byId("idOpenFileExplorer");
				this.openFileExplorerFrag = sap.ui.xmlfragment("com.menabev.AP.fragment.OpenFileExplorer", this);
				this.getView().addDependent(this.openFileExplorerFrag);
			}
			this.openFileExplorerFrag.open();
			var oFileUploader = sap.ui.getCore().byId("fileUploader");
			oFileUploader.setValue(null);
		},

		//frag:OpenFileExplorer
		handleUploadComplete: function (oEvent) {
			var sResponse = oEvent.getParameter("response");
			if (sResponse) {
				var sMsg = "";
				var m = /^\[(\d\d\d)\]:(.*)$/.exec(sResponse);
				if (m[1] === "200") {
					sMsg = "Return Code: " + m[1] + "\n" + m[2] + "(Upload Success)";
					oEvent.getSource().setValue("");
				} else {
					sMsg = "Return Code: " + m[1] + "\n" + m[2] + "(Upload Error)";
				}
				sap.m.MessageToast.show(sMsg);
			}
		},

		//frag:OpenFileExplorer
		//function handleUploadPress will import the data to cost allocation table from an excel file uploaded from the local system.
		handleUploadPress: function () {
			this.openFileExplorerFrag.close();
			var postDataModel = this.getView().getModel("postDataModel");
			var ext = sap.ui.getCore().byId("fileUploader").getValue().split(".");
			if (ext[0] === "") {
				sap.m.MessageToast.show("Please select a .csv file to upload");
				return;
			}
			var oFileFormat = ext[1].toLowerCase();

			var oBulkUploadModelData;
			var oFileUploader = sap.ui.getCore().byId("fileUploader");
			postDataModel.setProperty("/listNonPoItem", []);
			this.fileType = oFileUploader.getValue().split(".")[1];
			var domRef = oFileUploader.getFocusDomRef();
			var file = domRef.files[0];
			// Create a File Reader object
			var reader = new FileReader();
			var that = this;
			reader.onload = function (e) {
				if (that.fileType === "csv") {
					var bytes = new Uint8Array(reader.result);
					var binary = "";
					var length = bytes.byteLength;
					for (var i = 0; i < length; i++) {
						binary += String.fromCharCode(bytes[i]);
					}
					var strCSV = e.target.result;
					var arrCSV = binary.split("\n");
					// To ignore the first row which is header
					var oTblData = arrCSV.splice(1);
					oBulkUploadModelData = [];

					for (var j = 0; j < oTblData.length; j++) {
						var oRowDataArray = oTblData[j].split(';');
						var oTblRowData = {
							glAccount: oRowDataArray[0],
							materialDescription: oRowDataArray[1],
							crDbIndicator: oRowDataArray[2],
							netValue: parseFloat(oRowDataArray[3]),
							costCenter: oRowDataArray[4],
							internalOrderId: oRowDataArray[5],
							profitCentre: oRowDataArray[6],
							itemText: oRowDataArray[7],
							companyCode: oRowDataArray[8].replace(/['"]+/g, "")
						};
						oBulkUploadModelData.push(oTblRowData);
					}

					postDataModel.setProperty("/listNonPoItem", oBulkUploadModelData);
					postDataModel.refresh();

				} else if (that.fileType === "xlsx" || that.fileType === "xls") {
					var workbook = XLSX.read(e.target.result, {
						type: 'binary'
					});
					var sheet_name_list = workbook.SheetNames;
					sheet_name_list.forEach(function (y) { /* iterate through sheets */
						//Convert the cell value to Json
						that.ExcelData = XLSX.utils.sheet_to_json(workbook.Sheets[y]);
					});
					oBulkUploadModelData = that.ExcelData;

					postDataModel.setProperty("/listNonPoItem", oBulkUploadModelData);
					postDataModel.refresh();

				} else {
					sap.m.MessageBox.information("inCorrect Format");
				}

				postDataModel.refresh();
				oFileUploader.setValue(null);
			};
			reader.readAsArrayBuffer(file);

		},

		//frag:OpenFileExplorer
		//function closeFileExplDialog will close the frag:OpenFileExplorer
		closeFileExplDialog: function () {
			this.openFileExplorerFrag.close();
		},
		/*End of Excel Upload*/

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
		onPostComment: function (oEvent) {
			var nonPOInvoiceModel = this.getView().getModel("nonPOInvoiceModel");
			var sValue = oEvent.getParameter("value");
			var nonPOInvoiceModelData = nonPOInvoiceModel.getData().invoiceDetailUIDto.invoiceHeader;
			var sDate = new Date().getTime();
			if (!nonPOInvoiceModel.getData().invoiceDetailUIDto.commentDto) {
				nonPOInvoiceModel.getData().invoiceDetailUIDto.commentDto = [];
			}
			var sId = nonPOInvoiceModel.getProperty("/commentId");
			var cValue = nonPOInvoiceModel.getProperty("/input");
			var aCommentSelected = nonPOInvoiceModel.getData().invoiceDetailUIDto.commentDto;
			var aComItem = aCommentSelected.find(function (oRow, index) {
				return oRow.comment === cValue;
			});

			var aSelected = nonPOInvoiceModel.getData().invoiceDetailUIDto.commentDto;
			var aSelectedItem = aSelected.find(function (oRow, index) {
				return oRow.commentId === sId;
			});
			if (aSelectedItem) {
				var lDate = new Date();
				var uDate = lDate.getTime();
				aSelectedItem.comment = cValue;
				aSelectedItem.updatedAt = uDate;
				aSelectedItem.updatedBy = aSelectedItem.createdBy;

			} else if (aComItem) {
				var cDate = new Date();
				var nCDate = cDate.getTime();
				aComItem.comment = cValue;
				aComItem.updatedAt = nCDate;
				aComItem.updatedBy = aComItem.createdBy;
			} else {
				var oComment = {
					"requestId": nonPOInvoiceModelData.requestId,
					"comment": sValue,
					"createdBy": nonPOInvoiceModelData.emailFrom,
					"createdAt": sDate,
					"updatedBy": null,
					"updatedAt": null,
					"user": nonPOInvoiceModelData.emailFrom
				};
				var aEntries = nonPOInvoiceModel.getData().invoiceDetailUIDto.commentDto;
				aEntries.unshift(oComment);
			}
			nonPOInvoiceModel.setProperty("/commentId", "");
			this.getView().getModel("nonPOInvoiceModel").refresh();
		},

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
			// var token = this.getCSRFToken();
			var oSaveData = this.fnOnSubmitCall();
			oSaveData.invoiceHeaderDto.taskOwner = "Prashanth.Shekar@incture.com"; //Test data// Need to include logged in user email address
			oSaveData.invoiceHeaderDto.docStatus = "Draft";
			var postingDate = oSaveData.invoiceHeaderDto.postingDate;
			if (postingDate) {
				var that = this;
				var url = "menabev-dev/invoiceHeader/accountantSave";
				$.ajax({
					url: url,
					method: "POST",
					async: false,
					contentType: 'application/json',
					dataType: "json",
					data: JSON.stringify(oSaveData),
					success: function (result, xhr, data) {
						var message = result.message;
						if (result.status === "Success") {
							sap.m.MessageBox.success(message, {
								actions: [sap.m.MessageBox.Action.OK],
								onClose: function (sAction) {
									that.oRouter.navTo("Workbench"); //Test data//Need to add workbench route
								}
							});
						} else {
							sap.m.MessageBox.information(message, {
								actions: [sap.m.MessageBox.Action.OK]
							});
						}
					},
					error: function (result, xhr, data) {
						sap.m.MessageToast.show("Failed");
					}
				});
			} else {
				sap.m.MessageBox.error("Please Enter Posting Date!");
			}
		},

		//Called on click of Reject Button.
		//Frag:RejectDialog will open  
		onNonPoReject: function () {
			var mRejectModel = this.getView().getModel("mRejectModel");
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
					success: function (result, xhr, data) {
						var message = result.message;
						if (result.status === "Success") {
							mRejectModel.setProperty("/selectedKey", "");
							sap.m.MessageBox.success(message, {
								actions: [sap.m.MessageBox.Action.OK],
								onClose: function (sAction) {
									that.oRouter.navTo("Workbench"); //Need  to workbench route name
								}
							});
						} else {
							sap.m.MessageBox.information(message, {
								actions: [sap.m.MessageBox.Action.OK]
							});
						}
					},
					error: function (result, xhr, data) {
						sap.m.MessageToast.show("Failed");
					}
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

		//Called on click of Submit Button.
		onNonPoSubmit: function () {
			// var token = this.getCSRFToken();
			var jsonData = this.fnOnSubmitCall();
			var bCostAllocation = false;
			//Header Mandatory feilds validation
			var validateMandatoryFields = this.validateMandatoryFields(jsonData.invoiceHeaderDto);
			if (!validateMandatoryFields) {
				sap.m.MessageBox.error("Please fill all mandatory fields!");
			}

			if (validateMandatoryFields) {
				if (jsonData.costAllocationDto.length > 0) {
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
					jsonData.invoiceHeaderDto.taskOwner = "Prashanth.Shekar@incture.com"; //Test data// Need to include logged in user email address
					jsonData.invoiceHeaderDto.docStatus = "Created";
					var postingDate = jsonData.invoiceHeaderDto.postingDate;
					if (postingDate) {
						jsonData = JSON.stringify(jsonData);
						var that = this;
						var url = "menabev-dev/invoiceHeader/accountantSubmit";
						$.ajax({
							url: url,
							method: "POST",
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
											that.oRouter.navTo("Workbench");
										}
									});
								} else {
									sap.m.MessageBox.information(message, {
										actions: [sap.m.MessageBox.Action.OK]
									});
								}
							},
							error: function (result, xhr, data) {
								sap.m.MessageToast.show("Failed");
							}
						});
					} else {
						sap.m.MessageBox.error("Please Enter Posting Date!");
					}
				}
			}

		},

		fnOnSubmitCall: function (oEvent) {
			var nonPOInvoiceModel = this.getView().getModel("nonPOInvoiceModel"),
				nonPOInvoiceModelData = nonPOInvoiceModel.getData();
			var invoiceDate = nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/invoiceDate");

			var objectIsNew = jQuery.extend({}, nonPOInvoiceModelData.invoiceDetailUIDto);
			var postDataModel = this.getView().getModel("postDataModel");
			objectIsNew.costAllocation = [];
			if (postDataModel.getData().listNonPoItem) {
				for (var i = 0; i < postDataModel.getData().listNonPoItem.length; i++) {
					var reqId = objectIsNew.invoiceHeader.requestId ? objectIsNew.invoiceHeader.requestId : null;
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
						"internalOrderId": postDataModel.getData().listNonPoItem[i].internalOrderIdId,
						"itemId": itemId,
						"itemText": postDataModel.getData().listNonPoItem[i].itemText,
						"netValue": postDataModel.getData().listNonPoItem[i].netValue,
						"profitCenter": postDataModel.getData().listNonPoItem[i].profitCenter,
						"quantity": "0.00",
						"requestId": reqId,
						"serialNo": 0,
						"subNumber": null,
						"wbsElement": null
					});

				}
			}

			if (objectIsNew.invoiceHeader.createdAt) {
				// objectIsNew.invoiceHeader.createdAt = objectIsNew.invoiceHeader.createdAt.split("-").join("");
				objectIsNew.invoiceHeader.createdAt = objectIsNew.invoiceHeader.createdAt;
			}
			if (objectIsNew.invoiceHeader.invoiceDate) {
				// objectIsNew.invoiceHeader.invoiceDate = objectIsNew.invoiceHeader.invoiceDate.split("-").join("");
				objectIsNew.invoiceHeader.invoiceDate = objectIsNew.invoiceHeader.invoiceDate;
			}
			objectIsNew.invoiceHeader.postingDate = objectIsNew.invoiceHeader.postingDate ? new Date(objectIsNew.invoiceHeader.postingDate).getTime() :
				null;
			var obj = {};
			obj.invoiceHeaderDto = objectIsNew.invoiceHeader;
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

		onNavBack: function () {
			this.router.navTo("Workbench");
		},

		onUploadInvoice: function () {
			this.router.navTo("UploadInvoice");
		}

	});

});