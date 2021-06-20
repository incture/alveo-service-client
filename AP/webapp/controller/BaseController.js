sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/m/MessageToast",
	'sap/ui/model/Filter',
	'sap/ui/model/FilterOperator'
], function (Controller, JSONModel, MessageBox, MessageToast, Filter, FilterOperator) {
	"use strict";
	return Controller.extend("com.menabev.AP.controller.BaseController", {

		/**
		 * Event handler  for doing an HTTP request (Non Odata).
		 * @public 
		 * @params 
		 * sUrl 	- api URL - {string}
		 * sMethod  - the method -GET or POST or PUT or DELETE (PUT,DELETE -be careful about browser compatibility) -{string}
		 * oData - null if method is GET or the Request Body -{object}
		 * rSuccess - Success callback {function}
		 * rErrror - Error callback {function}
		 * @returns {object} the response data receieved through callback
		 */
		doAjax: function (sUrl, sMethod, oData, rSuccess, rError) {
			if (oData) {
				oData = JSON.stringify(oData);
			}
			// var tempJsonModel = new sap.ui.model.json.JSONModel();
			// this.getView().setModel(tempJsonModel, "tempJsonModel");
			// tempJsonModel.loadData(sUrl, oData, true, sMethod, false, false, {
			// 	"Content-Type": "application/json;charset=utf-8"
			// });
			// tempJsonModel.attachRequestCompleted(function (oEvent) {
			// 	rSuccess(oEvent.getSource().getData());
			// }.bind(rSuccess));
			// tempJsonModel.attachRequestFailed(function (oEvent) {
			// 	rError(oEvent);
			// }.bind(rError));
			jQuery.ajax({
				url: sUrl,
				type: sMethod,
				contentType: "application/json",
				data: oData,
				async: true,
				dataType: "json",
				success: function (data) {
					rSuccess(data);
				},
				error: function (err) {
					rError(err);
				}

			});

		},
		/**
		 * Convenience method for accessing the router.
		 * @public
		 * @returns {sap.ui.core.routing.Router} the router for this component
		 */
		getRouter: function () {
			return sap.ui.core.UIComponent.getRouterFor(this);
		},

		/**
		 * Convenience method for getting the view model by name.
		 * @public
		 * @param {string} [sName] the model name
		 * @returns {sap.ui.model.Model} the model instance
		 */
		getModel: function (sName) {
			return this.getView().getModel(sName);
		},

		/**
		 * Convenience method for setting the view model.
		 * @public
		 * @param {sap.ui.model.Model} oModel the model instance
		 * @param {string} sName the model name
		 * @returns {sap.ui.mvc.View} the view instance
		 */
		setModel: function (oModel, sName) {
			return this.getView().setModel(oModel, sName);
		},

		/**
		 * Getter for the resource bundle.
		 * @public
		 * @returns {sap.ui.model.resource.ResourceModel} the resourceModel of the component
		 */
		getResourceBundle: function () {
			return this.getOwnerComponent().getModel("i18n").getResourceBundle();
		},

		/**
		 * Error Message Display Block
		 * @param {string} [sMessage] the message text
		 * @public
		 */
		showErrorMessage: function (sMessage) {
			if (this._bMessageOpen) {
				return;
			}
			this._bMessageOpen = true;

			var bCompact = !!this.getView().$().closest(".sapUiSizeCompact").length;
			MessageBox.error(
				sMessage, {
					actions: [sap.m.MessageBox.Action.CLOSE],
					styleClass: bCompact ? "sapUiSizeCompact" : "",
					onClose: function (sAction) {
						this._bMessageOpen = false;
						// MessageToast.show("Action selected: " + sAction);
					}.bind(this)
				}
			);
		},

		/** 
		 * Any input or text area value should be trimmed.
		 * @public
		 * @param {String} sValue Input or Text Area Value.
		 * @param {Object} oControl The control from which the event is fired, mainly input and text area.
		 * @return {String} Retruns the trimmed value
		 */
		getTrimValue: function (sValue, oControl) {
			var sReturnValue = sValue;

			if (sValue) {
				sReturnValue = sValue.trim();
				oControl.getSource().setValue(sReturnValue);
			}

			return sReturnValue;
		},

		/** 
		 * Number format to be tested.
		 * @public
		 * @param {String} sValue Input Value.
		 * @return {Number} Retruns the value as a Number Data Type.
		 */
		getNumberValue: function (sValue) {
			var sReturnValue = sValue,
				iReturnValue;

			if (sValue) {
				// Remove all the commas before converting the number
				if (sValue.includes(",")) {
					sReturnValue = sValue.replace(this.oCommaRegEx, "");
				}

				iReturnValue = Number(sReturnValue);
			} else {
				iReturnValue = 0.00;
			}

			return iReturnValue;
		},

		/** 
		 * Number format to be tested.
		 * @public
		 * @param {String} sValue Input Value.
		 * @return {Boolean} Retruns true/false based on the regular expression defined in onInit Method
		 */
		getNumberDataTypeValidation: function (sValue) {
			var bReturnValue = false;

			if (this.oNumberFormatRegEx.test(sValue)) {
				bReturnValue = true;
			} else {
				bReturnValue = false;
			}

			return bReturnValue;
		},

		//Input error handler
		errorHandler: function (oEvent) {
			var input = oEvent.getParameter("value");
			if (!input) {
				oEvent.getSource().setValueState("Error");
			} else {
				oEvent.getSource().setValueState("None");
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

		//Open PDF Area details
		//Start of Open PDF details
		fnOpenPDF: function (documentId, key) {
			//service call to load the pdf document
			// var documentId = this.getModel("nonPOInvoiceModel").getProperty("/invoiceDetailUIDto/invoiceHeader/invoicePdfId");
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
						if (key === "PO") {
							this.byId("POgrid").setDefaultSpan("L6 M6 S12");
							this.byId("POgrid2").setDefaultSpan("L6 M6 S12");
						} else {
							this.byId("grid").setDefaultSpan("L6 M6 S12");
							this.byId("grid2").setDefaultSpan("L6 M6 S12");
						}
						this.addPDFArea(key);
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

		addPDFArea: function (key) {
			var that = this;
			var pdfData = this.pdfData;
			that.pdf = sap.ui.xmlfragment(that.getView().getId(), "com.menabev.AP.fragment.PDF", that);
			var oPdfFrame = that.pdf.getItems()[1];
			oPdfFrame.setContent('<embed width="100%" height="859rem" name="plugin" src="data:application/pdf;base64, ' + pdfData.base64 +
				'" ' + 'type=' + "" + "application/pdf" + " " + 'internalinstanceid="21">');
			if(key){
			var oSplitter = that.byId("idPOMainSplitter");
			}else {
					var oSplitter = that.byId("idMainSplitter");
			}
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

		//Vendor Balances :On click Vendor Balances Btn below function is called to open a fragment VendorBalance
		loadVendorBalanceFrag: function (vendorId, companyCode) {
			if (!vendorId || !companyCode) {
				var sMsg = "Please Enter Vendor Id and Company Code to fetch Vendor Balance!";
				sap.m.MessageBox.error(sMsg);
				return;
			}
			var vendorBalanceModel = new sap.ui.model.json.JSONModel();
			this.getView().setModel(vendorBalanceModel, "vendorBalanceModel");
			var oFragmentName = "com.menabev.AP.fragment.VendorBalance";
			if (!this.vendorBalanceFragment) {
				this.vendorBalanceFragment = sap.ui.xmlfragment(oFragmentName, this);
				this.getView().addDependent(this.vendorBalanceFragment);
			}
			this.getVendorBalance(vendorId, companyCode); //Service Call to fetch the Vendor Balance
		},

		//function call to load Vendor Balances 
		getVendorBalance: function (vendorId, companyCode) {
			var vendorBalanceModel = this.getModel("vendorBalanceModel"),
				currentDate = new Date();
			currentDate = currentDate.getFullYear() + "" + (currentDate.getMonth() + 1) + "" + currentDate.getDate();
			var url =
				"SD4_DEST/sap/opu/odata/sap/ZP2P_API_VENDORBALANCE_SRV/VenBalHeaderSet?$filter=Vendor eq '" + vendorId + "' and CompanyCode eq '" +
				companyCode + "' and KeyDate eq '" +
				currentDate + "'&$expand=ToVendorBalance&$format=json";
			this.busyDialog.open();
			jQuery.ajax({
				type: "GET",
				contentType: "application/json",
				url: url,
				dataType: "json",
				async: true,
				success: function (data, textStatus, jqXHR) {
					this.busyDialog.close();
					vendorBalanceModel.setProperty("/", data.d.results[0]);
					this.vendorBalanceFragment.open();
				}.bind(this),
				error: function (error) {
					this.busyDialog.close();
					var errorMsg = JSON.parse(error.responseText);
					errorMsg = errorMsg.error.message.value;
					this.errorMsg(errorMsg);
				}.bind(this)
			});
		},

		vendorBalancesClose: function () {
			this.vendorBalanceFragment.close();
		},

		//To check if an input value is not a number
		inputNANCheck: function (oEvent) {
			var oValue = oEvent.getSource().getValue();
			if (isNaN(oValue)) {
				oValue = oValue.replace(/[^\d]/g, '');
				oEvent.getSource().setValue(oValue);
			}
		},

		//Payment Term Data
		getPaymentTerm: function (oHeader, language) {
			var oDropDownModel = this.getOwnerComponent().getModel("oDropDownModel");
			var oFilter = [];
			if (language) {
				oFilter.push(new sap.ui.model.Filter("LanguageKey", sap.ui.model.FilterOperator.EQ, language));
			}
			var oDataModel = this.getOwnerComponent().getModel("ZP2P_API_EC_GL_SRV");
			oDataModel.read("/PaymentTermSet", {
				filters: oFilter,
				async: false,
				success: function (oData, oResponse) {
					oDropDownModel.setProperty("/paymentTermResult", oData.results);
				}.bind(this),
				error: function (error) {
					var errorMsg = JSON.parse(error.responseText);
					errorMsg = errorMsg.error.message.value;
					sap.m.MessageToast.show(errorMsg);
				}.bind(this)
			});
		},

		//Payment Method Data
		getPaymentMethod: function (oHeader, language) {
			var oDropDownModel = this.getOwnerComponent().getModel("oDropDownModel");
			var oFilter = [];
			if (language) {
				oFilter.push(new sap.ui.model.Filter("LanguageKey", sap.ui.model.FilterOperator.EQ, language));
			}
			var oDataModel = this.getOwnerComponent().getModel("ZP2P_API_EC_GL_SRV");
			oDataModel.read("/PaymentMethodSet", {
				filters: oFilter,
				async: false,
				success: function (oData, oResponse) {
					oDropDownModel.setProperty("/paymentMethodResult", oData.results);
				}.bind(this),
				error: function (error) {
					var errorMsg = JSON.parse(error.responseText);
					errorMsg = errorMsg.error.message.value;
					sap.m.MessageToast.show(errorMsg);
				}.bind(this)
			});
		},

		//Payment Block Data
		getPaymentBlock: function (oHeader, language) {
			var oDropDownModel = this.getOwnerComponent().getModel("oDropDownModel");
			var oFilter = [];
			if (language) {
				oFilter.push(new sap.ui.model.Filter("LanguageKey", sap.ui.model.FilterOperator.EQ, language));
			}
			var oDataModel = this.getOwnerComponent().getModel("ZP2P_API_EC_GL_SRV");
			oDataModel.read("/PaymentBlockSet", {
				filters: oFilter,
				async: false,
				success: function (oData, oResponse) {
					oDropDownModel.setProperty("/paymentBlockResult", oData.results);
				}.bind(this),
				error: function (error) {
					var errorMsg = JSON.parse(error.responseText);
					errorMsg = errorMsg.error.message.value;
					sap.m.MessageToast.show(errorMsg);
				}.bind(this)
			});
		},

		//Tax Code Data
		getTaxCode: function (oHeader, CountryKey) {
			var oDropDownModel = this.getOwnerComponent().getModel("oDropDownModel");
			var oFilter = [];
			if (CountryKey) {
				oFilter.push(new sap.ui.model.Filter("CountryKey", sap.ui.model.FilterOperator.EQ, CountryKey));
			}
			var oDataModel = this.getOwnerComponent().getModel("ZP2P_API_EC_GL_SRV");
			oDataModel.read("/TaxCodeSet", {
				filters: oFilter,
				async: false,
				success: function (oData, oResponse) {
					oDropDownModel.setProperty("/taxCodeResult", oData.results);
				}.bind(this),
				error: function (error) {
					var errorMsg = JSON.parse(error.responseText);
					errorMsg = errorMsg.error.message.value;
					sap.m.MessageToast.show(errorMsg);
				}.bind(this)
			});
		},

		//function to get GLAccount based on running search
		glAccountSuggest: function (oEvent) {
			var oDropDownModel = this.getOwnerComponent().getModel("oDropDownModel");
			var oValue = oEvent.getParameter("suggestValue");
			var ChartOfAccounts = "1000";
			var oFilter = [];
			if (oValue && oValue.length > 2) {
				oFilter.push(new sap.ui.model.Filter("ChartOfAccounts", sap.ui.model.FilterOperator.EQ, ChartOfAccounts));
				oFilter.push(new sap.ui.model.Filter("GlAccnt", sap.ui.model.FilterOperator.Contains, oValue));
				var oDataModel = this.getOwnerComponent().getModel("ZP2P_API_EC_GL_SRV");
				oDataModel.read("/GlAccountSet", {
					filters: oFilter,
					async: false,
					success: function (oData, oResponse) {
						oDropDownModel.setProperty("/GLAccountResult", oData.results);
					}.bind(this),
					error: function (error) {
						var errorMsg = JSON.parse(error.responseText);
						errorMsg = errorMsg.error.message.value;
						sap.m.MessageToast.show(errorMsg);
					}.bind(this)
				});
			}
		},
		
		//this function triggered when CreateEditTemplate fragment glaccount is selected from the suggestions
		glAccountSelected: function (oEvent) {
			var glAccountDes = oEvent.getParameter("selectedItem").getProperty("additionalText"),
				sPath = oEvent.getSource().getBindingContext("postDataModel").sPath;
			var postDataModel = this.getModel("postDataModel");
			postDataModel.setProperty(sPath + "/materialDescription", glAccountDes);
			postDataModel.refresh();
		},
		
		//this function triggered when NonPOCostCenter fragment glaccount is selected from the suggestions
		glAccountCCSelected: function(oEvent){
			var glAccountDes = oEvent.getParameter("selectedItem").getProperty("additionalText"),
				sPath = oEvent.getSource().getBindingContext("postDataModel").sPath;
			var postDataModel = this.getModel("postDataModel");
			postDataModel.setProperty(sPath + "/materialDesc", glAccountDes);
			postDataModel.refresh();
		},
		
		//function to get GLAccount based on running search
		getCostCenter: function (CompanyCode, LanguageKey) {
			var oDropDownModel = this.getOwnerComponent().getModel("oDropDownModel"),
				currentDate = new Date(),
				currentMonth = currentDate.getMonth() + 1;
			if (currentMonth < 10) {
				currentMonth = '0' + currentMonth;
			}
			currentDate = currentDate.getFullYear() + "" + currentMonth + "" + currentDate.getDate();
			var oFilter = [];
			oFilter.push(new sap.ui.model.Filter("CompanyCode", sap.ui.model.FilterOperator.EQ, CompanyCode));
			oFilter.push(new sap.ui.model.Filter("LanguageKey", sap.ui.model.FilterOperator.EQ, LanguageKey));
			oFilter.push(new sap.ui.model.Filter("StartDate", sap.ui.model.FilterOperator.LE, currentDate));
			oFilter.push(new sap.ui.model.Filter("EndDate", sap.ui.model.FilterOperator.GE, currentDate));
			var oDataModel = this.getOwnerComponent().getModel("ZP2P_API_EC_GL_SRV");
			oDataModel.read("/CostCenterSet", {
				filters: oFilter,
				async: false,
				success: function (oData, oResponse) {
					oDropDownModel.setProperty("/costCenterResult", oData.results);
				}.bind(this),
				error: function (error) {
					var errorMsg = JSON.parse(error.responseText);
					errorMsg = errorMsg.error.message.value;
					sap.m.MessageToast.show(errorMsg);
				}.bind(this)
			});
		},
		
		//Add PO fragment controls
		onChangeSBSearchPO: function (oEvent) {
			var oSelectedKey = oEvent.getSource().getSelectedKey();
			this.fnSearchPOVisibilty(oSelectedKey);
		},

		fnSearchPOVisibilty: function (selectedKey) {
			var addPOModel = this.getView().getModel("addPOModel");
			if (selectedKey === "PO") {
				addPOModel.setProperty("/POFilterVisible", true);
				addPOModel.setProperty("/DOFilterVisible", false);
			} else {
				addPOModel.setProperty("/POFilterVisible", false);
				addPOModel.setProperty("/DOFilterVisible", true);
			}
			addPOModel.setProperty("/documentNumber", "");
			addPOModel.setProperty("/documentCategory", "");
			addPOModel.setProperty("/vendorId", "");
			addPOModel.setProperty("/companyCode", "");
			addPOModel.setProperty("/deliveryNoteNumber", "");
		},

		onClickAddPO: function () {
			var nonPOInvoiceModel = this.getModel("nonPOInvoiceModel"),
				vendorId = nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/vendorId"),
				companyCode = nonPOInvoiceModel.getProperty("/invoiceDetailUIDto/invoiceHeader/companyCode");
			var addPOModel = new JSONModel();
			this.getView().setModel(addPOModel, "addPOModel");
			var oFragmentName = "com.menabev.AP.fragment.AddPO";
			if (!this.addPOFragment) {
				this.addPOFragment = sap.ui.xmlfragment(oFragmentName, this);
				this.getView().addDependent(this.addPOFragment);
			}
			this.fnSearchPOVisibilty("PO");
			addPOModel.setProperty("/vendorId", vendorId);
			addPOModel.setProperty("/companyCode", companyCode);
			this.addPOFragment.open();
		},

		onSearchPO: function () {
			var addPOModel = this.getModel("addPOModel"),
				bError = false;
			if (!addPOModel.getProperty("/documentNumber") &&
				!addPOModel.getProperty("/documentCategory") &&
				!addPOModel.getProperty("/vendorId") &&
				!addPOModel.getProperty("/companyCode") &&
				!addPOModel.getProperty("/deliveryNoteNumber")
			) {
				sap.m.MessageToast.show("Enter atleast one search parameter to search");
				bError = true;
			}
			if (!bError)
				this.getSearchPOData();
		},

		getSearchPOData: function () {
			var addPOModel = this.getModel("addPOModel");
			var payload = {
				"companyCode": addPOModel.getProperty("/companyCode"),
				"createdAtRange": "",
				"deliveryNoteNumber": addPOModel.getProperty("/deliveryNoteNumber"),
				"documentCategory": addPOModel.getProperty("/documentCategory"),
				"documentNumber": addPOModel.getProperty("/documentNumber"),
				"purchaseOrganization": "",
				"vendorId": addPOModel.getProperty("/vendorId"),
				"vendorName": addPOModel.getProperty("/vendorName")
			};
			var url = "/menabevdev/po/search";
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
					addPOModel.setProperty("/result", data);
				}.bind(this),
				error: function (error) {
					this.busyDialog.close();
					var errorMsg = JSON.parse(error.responseText);
					errorMsg = errorMsg.error.message.value;
					this.errorMsg(errorMsg);
				}.bind(this)
			});
		},

		onClearSearchPO: function () {
			var addPOModel = this.getView().getModel("addPOModel");
			addPOModel.setProperty("/documentNumber", "");
			addPOModel.setProperty("/documentCategory", "");
			addPOModel.setProperty("/deliveryNoteNumber", "");
		},

		onCancelAddPO: function () {
			this.addPOFragment.close();
		},
		//End of Add PO

	});

});