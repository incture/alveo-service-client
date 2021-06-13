sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/m/MessageBox",
	"sap/m/MessageToast",
	'sap/ui/model/Filter',
	'sap/ui/model/FilterOperator'
], function (Controller, MessageBox, MessageToast, Filter, FilterOperator) {
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
		fnOpenPDF: function (documentId) {
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
		inputNANCheck: function(oEvent){
			var oValue = oEvent.getSource().getValue();
			if (isNaN(oValue)) {
				oValue = oValue.replace(/[^\d]/g, '');
				oEvent.getSource().setValue(oValue);
			}
		},

	});

});