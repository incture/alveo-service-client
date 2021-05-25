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

	});

});