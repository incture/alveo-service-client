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
		},

		onRouteMatched: function (oEvent) {
			//reading the Arguments from url
			var oArgs = oEvent.getParameter("arguments");
			var requestId = oArgs.value;
			this.busyDialog.open();
			//handle if route has NonPO request Id 
			//requestId = "APA-000004"; //Test Data

			//Test Data for Tax Rate
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
			//End of Tax rate test data

			if (requestId) {
				this.getNonPOData(requestId);
			} else {
				this.busyDialog.close();
				var initializeModelData = {
					"invoiceHeader": {
						"attachments":[],
						"balance": "",
						"balanceCheck": true,
						"comments":[],
						"clerkId": 0,
						"createdAtInDB": 0,
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
						"vendorId": "",
						"vendorName": ""
					},
					"vstate": {}
				};
				this.getModel("nonPOInvoiceModel").setProperty("/invoiceDetailUIDto", initializeModelData);
				this.getModel("nonPOInvoiceModel").setProperty("/openPdfBtnVisible", false);
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
						
						this.getModel("nonPOInvoiceModel").setProperty("/openPdfBtnVisible", false);
						var attachments = this.getModel("nonPOInvoiceModel").getProperty("/invoiceDetailUIDto/invoiceHeader/attachments");
						if(attachments.length){
							for(var i=0; i<attachments.length; i++){
								if(attachments[i].isInvoicePdf){
									this.getModel("nonPOInvoiceModel").setProperty("/openPdfBtnVisible", true);
									this.getModel("nonPOInvoiceModel").setProperty("/invoicePdf", attachments[i]);
								}
							}
						}
					}
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

		//function hdrInvAmtCalu is triggered on change of Invoice Amount in the header field
		hdrInvAmtCalu: function (oEvent) {
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel");
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

		//onChange Header level tax
		onChangeHeaderTax: function (oEvent) {
			var taxDetails = oEvent.getParameters().selectedItem.getBindingContext("taxCodeModel").getObject();
			this.getModel("nonPOInvoiceModel").setProperty("/invoiceDetailUIDto/invoiceHeader/taxRatePercentage", taxDetails.taxRatePercentage);
			this.getModel("nonPOInvoiceModel").refresh();
		},
		//onChange CC item level tax
		onChangeTax: function (oEvent) {
			var taxDetails = oEvent.getParameters().selectedItem.getBindingContext("taxCodeModel").getObject();
			var postDataModel = this.getModel("postDataModel");
			var sPath = oEvent.getSource().getBindingContext("postDataModel").getPath();
			postDataModel.setProperty(sPath + "/taxRatePercentage", taxDetails.taxRatePercentage);
			this.amountCal(oEvent);
		},
		//End of Header Filter function

		//PDF Area details
		//Start of Open PDF details
		onPressOpenpdf: function () {
			//service call to load the pdf document
			var documentId = this.getModel("nonPOInvoiceModel").getProperty("/invoicePdf").attachmentId;
			// var documentId = "GxeeFQVf6vtzNXhiLtz_-nYvdlckdEQ9X38qhsbKwi4";//Test data
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
						errorMsg = "Request timed-out. Please contact your administrator";
						this.errorMsg(errorMsg);
					} else {
						errorMsg = result.responseJSON.error;
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
					var sUrl = "/menabevdev/NonPoTemplate/getItemsByTemplateId/" + tempid;
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
			postDataModelData.listNonPoItem.unshift({
				"templateId": "",
				"glAccount": "",
				"costCenter": "",
				"taxCode": this.getModel("nonPOInvoiceModel").getData().invoiceDetailUIDto.invoiceHeader.taxCode,
				"taxRatePercentage": this.getModel("nonPOInvoiceModel").getData().invoiceDetailUIDto.invoiceHeader.taxRatePercentage,
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
			var totalAmt = 0,
				totalTax = 0;
			if (postDataModel.getProperty("/listNonPoItem")) {
				var length = postDataModel.getProperty("/listNonPoItem").length;
				for (var i = 0; i < length; i++) {
					//Tax Calculations
					var taxRatePercentage = postDataModel.getProperty("/listNonPoItem/" + i + "/taxRatePercentage"),
						netValue = postDataModel.getProperty("/listNonPoItem/" + i + "/netValue"),
						taxValue = (that.nanValCheck(netValue) * that.nanValCheck(taxRatePercentage)) / 100;
					totalTax += taxValue;
					postDataModel.setProperty("/listNonPoItem/" + i + "/taxValue", taxValue);
					//End of Tax Calulations
					if (netValue && postDataModel.getProperty("/listNonPoItem/" + i +
							"/crDbIndicator") === "H") {
						totalAmt += that.nanValCheck(postDataModel.getProperty("/listNonPoItem/" + i + "/netValue")) + taxValue;

					} else if (netValue && postDataModel.getProperty("/listNonPoItem/" + i +
							"/crDbIndicator") === "S") {
						totalAmt -= that.nanValCheck(postDataModel.getProperty("/listNonPoItem/" + i + "/netValue")) + taxValue;
					}
				}
				totalAmt = that.nanValCheck(totalAmt).toFixed(3);
				totalTax = this.nanValCheck(totalTax).toFixed(3);
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/grossAmount", totalAmt);
				nonPOInvoiceModel.setProperty("/invoiceDetailUIDto/invoiceHeader/taxAmount", totalTax);
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
							sap.m.MessageToast(errorMsg);
						}
					},
					error: function (result, xhr, data) {
						that.busyDialog.close();
						var errorMsg = "";
						if (result.status === 504) {
							errorMsg = "Request timed-out. Please contact your administrator";
							that.errorMsg(errorMsg);
						} else {
							errorMsg = result.responseJSON.error;
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
				var url = "/menabevdev/invoiceHeader/accountantSave";
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
						var url = "/menabevdev/invoiceHeader/accountantSubmit";
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
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel"),
				nonPOInvoiceModelData = nonPOInvoiceModel.getData();

			var objectIsNew = jQuery.extend({}, nonPOInvoiceModelData.invoiceDetailUIDto);
			var postDataModel = this.getModel("postDataModel");
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
			var invoiceHeaderDto = {
				"accountNumber": "",
				"accountingDoc": "",
				"assignedTo": "",
				"balance": objectIsNew.invoiceHeader.balance,
				"balanceCheck": true,
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
				"dueDate": "",
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
				"invoiceTotal": "",
				"invoiceTotalFrom": "",
				"invoiceTotalTo": "",
				"invoiceType": "",
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
				"requestId": "",
				"sapInvoiceNumber": 0,
				"shippingCost": 0,
				"subTotal": "",
				"taskOwner": objectIsNew.invoiceHeader.taskOwner,
				"taskStatus": "",
				"taxAmount": objectIsNew.invoiceHeader.taxAmount,
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

		onNavBack: function () {
			this.router.navTo("Workbench");
		},

	});

});