sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/json/JSONModel",
	"sap/m/MessageBox",
	"sap/m/MessageToast",
	'sap/ui/model/Filter',
	'sap/ui/model/FilterOperator',
	"com/menabev/AP/util/POServices"
], function (Controller, JSONModel, MessageBox, MessageToast, Filter, FilterOperator, POServices) {
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
		errorHandlerInput: function (oEvent) {
			var input = oEvent.getParameter("value");
			if (!input) {
				oEvent.getSource().setValueState("Error");
			} else {
				oEvent.getSource().setValueState("None");
			}
		},
		errorHandlerselect: function (oEvent) {
			var input = oEvent.getSource().getSelectedKey();
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

		getBtnVisibility: function (status, requestId, invoiceType) {
			var oVisibilityModel = this.getOwnerComponent().getModel("oVisibilityModel");
			oVisibilityModel.setProperty("/NonPOInvoice", {});
			oVisibilityModel.setProperty("/NonPOInvoice/editable", false);
			oVisibilityModel.setProperty("/NonPOInvoice/ApproveBtnVisible", false);
			oVisibilityModel.setProperty("/NonPOInvoice/ReSendBtnVisible", false);
			oVisibilityModel.setProperty("/NonPOInvoice/SaveBtnVisible", false);
			oVisibilityModel.setProperty("/NonPOInvoice/SubmitBtnVisible", false);
			oVisibilityModel.setProperty("/NonPOInvoice/SubmitRemedBtnVisible", false);
			oVisibilityModel.setProperty("/NonPOInvoice/RejectBtnVisible", false);
			oVisibilityModel.setProperty("/NonPOInvoice/CancelBtnVisible", false);
			oVisibilityModel.setProperty("/NonPOInvoice/actionBtnEnable", false);
			var loggedinUserGroup = this.oUserDetailModel.getProperty("/loggedinUserGroup");
			if (requestId === "NEW") {
				oVisibilityModel.setProperty("/NonPOInvoice/editable", true);
				oVisibilityModel.setProperty("/NonPOInvoice/SaveBtnVisible", true);
				oVisibilityModel.setProperty("/NonPOInvoice/SubmitBtnVisible", true);
				oVisibilityModel.setProperty("/NonPOInvoice/CancelBtnVisible", true);
			} else if (status === "RESERVED") {
				if (loggedinUserGroup === "Process_Lead") {
					oVisibilityModel.setProperty("/NonPOInvoice/editable", false);
					oVisibilityModel.setProperty("/NonPOInvoice/ApproveBtnVisible", true);
					oVisibilityModel.setProperty("/NonPOInvoice/RejectBtnVisible", true);
					oVisibilityModel.setProperty("/NonPOInvoice/ReSendBtnVisible", true);
				} else if (loggedinUserGroup === "Accountant") {
					oVisibilityModel.setProperty("/NonPOInvoice/editable", true);
					oVisibilityModel.setProperty("/NonPOInvoice/actionBtnEnable", true);
					oVisibilityModel.setProperty("/NonPOInvoice/SaveBtnVisible", true);
					oVisibilityModel.setProperty("/NonPOInvoice/SubmitBtnVisible", true);
					oVisibilityModel.setProperty("/NonPOInvoice/CancelBtnVisible", true);
					oVisibilityModel.setProperty("/NonPOInvoice/RejectBtnVisible", true);
					if (invoiceType === "PO") {
						oVisibilityModel.setProperty("/NonPOInvoice/SubmitRemediationBtn", true);
					}
				} else if (loggedinUserGroup === "Buyer") {
					oVisibilityModel.setProperty("/NonPOInvoice/editable", false);
					oVisibilityModel.setProperty("/NonPOInvoice/actionBtnEnable", true);
					oVisibilityModel.setProperty("/NonPOInvoice/SubmitBtnVisible", true);
					oVisibilityModel.setProperty("/NonPOInvoice/CancelBtnVisible", true);
				}
			}
		},

		//Open PDF Area details
		//Start of Open PDF details
		fnOpenPDF: function (documentId, key) {
			//service call to load the pdf document
			// var documentId = this.getModel("nonPOInvoiceModel").getProperty("/invoiceDetailUIDto/invoiceHeader/invoicePdfId");
			var busy = new sap.m.BusyDialog();
			busy.open();
			var sUrl = "/menabevdev/document/download/" + documentId;
			jQuery.ajax({
				type: "GET",
				contentType: "application/json",
				url: sUrl,
				dataType: "json",
				async: true,
				success: function (data, textStatus, jqXHR) {
					busy.close();
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
					busy.close();
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
			if (key) {
				var oSplitter = that.byId("idPOMainSplitter");
			} else {
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
			var busy = new sap.m.BusyDialog();
			busy.open();
			jQuery.ajax({
				type: "GET",
				contentType: "application/json",
				url: url,
				dataType: "json",
				async: true,
				success: function (data, textStatus, jqXHR) {
					busy.close();
					vendorBalanceModel.setProperty("/", data.d.results[0]);
					this.vendorBalanceFragment.open();
				}.bind(this),
				error: function (error) {
					busy.close();
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
		glAccountCCSelected: function (oEvent) {
			var glAccountDes = oEvent.getParameter("selectedItem").getProperty("additionalText"),
				sPath = oEvent.getSource().getBindingContext("oPOModel").sPath;
			var oPOModel = this.getOwnerComponent().getModel("oPOModel");
			oPOModel.setProperty(sPath + "/materialDesc", glAccountDes);
			oPOModel.refresh();
		},

		//function to get GLAccount based on running search
		getCostCenter: function (CompanyCode, LanguageKey) {
			var oDropDownModel = this.getOwnerComponent().getModel("oDropDownModel"),
				currentDate = new Date(),
				currentMonth = currentDate.getMonth() + 1,
				currentDay = currentDate.getDate();
			if (currentMonth < 10) {
				currentMonth = '0' + currentMonth;
			}
			if (currentDay < 10) {
				currentDay = '0' + currentDay;
			}
			currentDate = currentDate.getFullYear() + "" + currentMonth + "" + currentDay;
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
			var oPOModel = this.oPOModel,
				vendorId = oPOModel.getProperty("/vendorId"),
				companyCode = oPOModel.getProperty("/companyCode");
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
			this.oVisibilityModel.setProperty("/PO/enabled", false);
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
			var busy = new sap.m.BusyDialog();
			busy.open();
			jQuery.ajax({
				type: "POST",
				contentType: "application/json",
				url: url,
				dataType: "json",
				data: JSON.stringify(payload),
				async: true,
				success: function (data, textStatus, jqXHR) {
					busy.close();
					addPOModel.setProperty("/result", data);
				}.bind(this),
				error: function (error) {
					busy.close();
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

		onSelectAddPO: function (oEvent) {
			var addPOModel = this.getModel("addPOModel"),
				selectedFilters = oEvent.getSource().getSelectedContextPaths();
			addPOModel.setProperty("/selectedFilters", selectedFilters);
			if (selectedFilters.length) {
				this.oVisibilityModel.setProperty("/PO/enabled", true);
			} else {
				this.oVisibilityModel.setProperty("/PO/enabled", false);
			}
		},

		onClickAddPOOk: function () {
			var addPOModel = this.getModel("addPOModel");
			var selectedFilters = addPOModel.getProperty("/selectedFilters");

			var purchaseOrder = [];
			for (var i = 0; i < selectedFilters.length; i++) {
				var sTemp = addPOModel.getProperty(selectedFilters[i]);
				var obj = {
					"documentCategory": sTemp.documentCategory,
					"documentNumber": sTemp.documentNumber
				};
				purchaseOrder.push(obj);
			}
			var payload = {
				"invoiceHeader": this.oPOModel.getData(),
				"purchaseOrder": purchaseOrder,
				"requestId": "string"
			};
			//service call to preview the Add PO
			var busy = new sap.m.BusyDialog();
			busy.open();
			var sUrl = "/menabevdev/purchaseDocumentHeader/savePo";
			jQuery.ajax({
				method: "POST",
				contentType: "application/json",
				url: sUrl,
				dataType: "json",
				data: JSON.stringify(payload),
				async: true,
				success: function (data, textStatus, jqXHR) {
					busy.close();
					var oPOModel = this.oPOModel;
					var aGetReferencedByPO = $.extend(true, [], oPOModel.getProperty("/purchaseOrders"));
					aGetReferencedByPO = aGetReferencedByPO.concat(data.referencePo);
					oPOModel.setProperty("/", data.invoiceObject);
					oPOModel.setProperty("/purchaseOrders", aGetReferencedByPO);
					oPOModel.refresh();
					this.addPOFragment.close();
				}.bind(this),
				error: function (result, xhr, data) {
					busy.close();
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
		//End of Add PO

		//To get the Reason of Rejection list details 
		getReasonOfRejection: function () {
			var oDropDownModel = this.getOwnerComponent().getModel("oDropDownModel");
			var url = "/menabevdev/codesAndtexts/get/uuId=/statusCode=/type=ROR/language=";
			jQuery.ajax({
				type: "GET",
				contentType: "application/json",
				url: url,
				dataType: "json",
				async: true,
				success: function (data, textStatus, jqXHR) {
					oDropDownModel.setProperty("/rejectReasonCodes", data);
				},
				error: function (err) {
					sap.m.MessageToast.show(err.statusText);
				}
			});
		},

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
				var busy = new sap.m.BusyDialog();
				busy.open();
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
						busy.close();
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
						busy.close();
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

		onPostComment: function () {
			var nonPOInvoiceModel = this.oPOModel;
			var oUserDetailModel = this.oUserDetailModel;
			var comment = nonPOInvoiceModel.getProperty("/comment");
			var userComment = nonPOInvoiceModel.getProperty("/commments");
			if (!comment) {
				comment = [];
			}
			if (!userComment) {
				this.getResourceBundle.getText("FILL_COMMENT");
				return;
			}
			var obj = {
				"requestId": nonPOInvoiceModel.getProperty("/requestId"),
				"createdAt": new Date().getTime(),
				"user": oUserDetailModel.getProperty("/loggedinUserDetail/name/familyName"),
				"comment": nonPOInvoiceModel.getProperty("/commments"),
				"createdBy": oUserDetailModel.getProperty("/loggedInUserMail")
			};
			comment.push(obj);
			nonPOInvoiceModel.setProperty("/comment", comment);
			nonPOInvoiceModel.setProperty("/commments", "");
			POServices.setChangeInd("", this, "commentChange");
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
			var nonPOInvoiceModel = this.oPOModel;
			var attachment = nonPOInvoiceModel.getProperty("/attachment");
			var upfile = oEvent.getParameters().files[0];
			var upFileName = upfile.name;
			var fileType = upfile.name.split(".")[upfile.name.split(".").length - 1];
			var allowedTypes = ["pdf", "doc", "docx", "jpg", "jpeg", "png", "xlsx", "xls", "csv"];
			var requestId = nonPOInvoiceModel.getProperty("/requestId");
			if (!attachment) {
				attachment = [];
			}
			if (!requestId) {
				requestId = this.createReqid();
				nonPOInvoiceModel.setProperty("/requestId", requestId);
			}
			if (!allowedTypes.includes(fileType.toLowerCase())) {
				MessageBox.error(that.getResourceBundle.getText("msgFileType"));
				return;
			}
			if (upfile.size > 2097152) {
				MessageBox.error(that.getResourceBundle.getText("msgFileSize"));
				return;
			}
			if (upFileName.length > 60) {
				MessageBox.error(that.getResourceBundle.getText("msgFileName"));
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
					attachment.push(obj);
					nonPOInvoiceModel.setProperty("/attachment", attachment);
					POServices.setChangeInd(oEvent, that, "attachementsChange");
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
			var nonPOInvoiceModel = this.oPOModel;
			var sPath = oEvent.getSource().getBindingContext("oPOModel").getPath();
			var obj = oEvent.getSource().getBindingContext("oPOModel").getObject();

			var parts = sPath.split("/");
			var index = parts[parts.length - 1];
			var documentId = obj.attachmentId;
			var busy = new sap.m.BusyDialog();
			busy.open();
			var sUrl = "/menabevdev/document/download/" + documentId;
			oServiceModel.loadData(sUrl, "", true, "GET", false, false, this.oHeader);
			oServiceModel.attachRequestCompleted(function (oEvent) {
				if (oEvent.getParameters().success) {
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
				} else if (oEvent.getParameters().errorobject.statusCode == 401) {
					var message = "Session Lost. Press OK to refresh the page";
					sap.m.MessageBox.information(message, {
						styleClass: "sapUiSizeCompact",
						actions: [sap.m.MessageBox.Action.OK],
						onClose: function (sAction) {
							location.reload(true);
						}
					});
				} else if (oEvent.getParameters().errorobject.statusCode == 400 || oEvent.getParameters().errorobject.statusCode == 404) {
					var message = "Service Unavailable. Please try after sometime";
					sap.m.MessageBox.information(message, {
						styleClass: "sapUiSizeCompact",
						actions: [sap.m.MessageBox.Action.OK]
					});
				} else if (oEvent.getParameters().errorobject.statusCode == 500) {
					var message = "Service Unavailable. Please contact administrator";
					sap.m.MessageBox.information(message, {
						styleClass: "sapUiSizeCompact",
						actions: [sap.m.MessageBox.Action.OK]
					});
				}
			});
			oServiceModel.attachRequestFailed(function (oEvent) {
				busy.close();
			});
		},

		onDocumentDelete: function (oEvent) {
			var that = this;
			var oServiceModel = new sap.ui.model.json.JSONModel();
			var nonPOInvoiceModel = this.oPOModel;
			var sPath = oEvent.getSource().getBindingContext("oPOModel").getPath();
			var obj = oEvent.getSource().getBindingContext("oPOModel").getObject();
			var attachments = nonPOInvoiceModel.getProperty("/attachments");
			var parts = sPath.split("/");
			var index = parts[parts.length - 1];
			var attachmentId = obj.attachmentId;
			var busy = new sap.m.BusyDialog();
			busy.open();
			var sUrl = "/menabevdev/document/delete/" + attachmentId;
			oServiceModel.loadData(sUrl, "", true, "DELETE", false, false, this.oHeader);
			oServiceModel.attachRequestCompleted(function (oEvent) {
				busy.close();
				if (oEvent.getParameters().success) {
					sap.m.MessageToast.show(oEvent.getSource().getData().message);
					attachments.splice(index, 1);
					nonPOInvoiceModel.setProperty("/attachments", attachments);
					POServices.setChangeInd(oEvent, that, "attachementsChange");
				} else if (oEvent.getParameters().errorobject.statusCode == 401) {
					var message = "Session Lost. Press OK to refresh the page";
					sap.m.MessageBox.information(message, {
						styleClass: "sapUiSizeCompact",
						actions: [sap.m.MessageBox.Action.OK],
						onClose: function (sAction) {
							location.reload(true);
						}
					});
				} else if (oEvent.getParameters().errorobject.statusCode == 400 || oEvent.getParameters().errorobject.statusCode == 404) {
					var message = "Service Unavailable. Please try after sometime";
					sap.m.MessageBox.information(message, {
						styleClass: "sapUiSizeCompact",
						actions: [sap.m.MessageBox.Action.OK]
					});
				} else if (oEvent.getParameters().errorobject.statusCode == 500) {
					var message = "Service Unavailable. Please contact administrator";
					sap.m.MessageBox.information(message, {
						styleClass: "sapUiSizeCompact",
						actions: [sap.m.MessageBox.Action.OK]
					});
				}
			});
		},

		VendorIdSuggest: function (oEvent, oController) {
			var oDataAPIModel = this.oDataAPIModel;
			var oDropDownModel = this.oDropDownModel;
			var oPOModel = this.oPOModel;
			if (oEvent.getSource().getCustomData()[0]) {
				oPOModel.setProperty("/vendorName", "");
			}
			var value = oEvent.getParameter("suggestValue");
			oDropDownModel.setProperty("/VendorIdSuggest", {});
			if (value && value.length > 2) {
				var url = "/A_Supplier?$format=json&$filter=substringof('" + value + "',Supplier) eq true or substringof('" + value +
					"',SupplierName) eq true";
				oDataAPIModel.read(url, {
					success: function (oData, header) {
						var data = oData.results;
						oDropDownModel.setProperty("/VendorIdSuggest", data);
						oDropDownModel.refresh();
					},
					error: function (oData) {}
				});
			}
		},

		onVendorIdChange: function (oEvent) {
			var a = oEvent.getSource().getValue();
			if (a) {
				return true;
			}
		},

		onCancelChanges: function () {
			var that = this;
			var message = this.getResourceBundle.getText("CancelChanges");
			sap.m.MessageBox.success(message, {
				actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.CANCEL],
				onClose: function (e) {
					if (e === "OK") {
						that.oRouter.navTo("Inbox");
					}
				}
			});
		},

		// To find POHistory based on the selection documentNumber and document Item
		findPOItemDetails: function (sDocumentItem, sDocumentNumber, poHistory) {
			var newArray = [];
			for (var i = 0; i < poHistory.length; i++) {
				if (poHistory[i]["documentItem"] === sDocumentItem && poHistory[i]["documentNumber"] === sDocumentNumber) {
					newArray.push(poHistory[i]);
				}
			}
			return newArray;
		},

		onChangeHdrInvAmt: function (oEvent) {
			var oValue = oEvent.getSource().getValue();
			if (!(oValue.indexOf(".") >= 0) && oValue.length > 8) {
				oValue = oValue.slice(0, 8);
			}
			oValue = (oValue.indexOf(".") >= 0) ? (oValue.substr(0, oValue.indexOf(".")) + oValue.substr(oValue.indexOf("."), 3)) : oValue;
			oEvent.getSource().setValue(oValue);
		},

		onPressRefresh: function () {

		},

		fnHideMatchedPO: function () {
			var oPOModel = this.oPOModel;
			var POItem = oPOModel.getProperty("/aItemMatchPO");
			var invoiceItems = oPOModel.getProperty("/invoiceItems");
			for (var i = 0; i < POItem.length; i++) {
				var PODocNum = POItem[i].documentNumber;
				var PODocItem = POItem[i].documentItem;
				for (var j = 0; j < invoiceItems.length; j++) {
					if (invoiceItems[j].isTwowayMatched && !invoiceItems[j].isDeleted) {
						if(PODocNum === invoiceItems[j].matchDocNum && PODocItem === invoiceItems[j].matchDocItem) {
							POItem[i].POMatched = true;
						}
					}
				}
			}
			oPOModel.setProperty("/aItemMatchPO", POItem);
			oPOModel.refresh();
		}

	});

});