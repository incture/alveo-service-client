jQuery.sap.declare("com.menabev.AP.formatter.formatter");
com.menabev.AP.formatter.formatter = {

	formatDate: function (date) {
		if (!date) {
			return;
		}
		date = new Date(date);
		var dd = date.getDate();
		if (dd < 10) {
			dd = dd.toString();
			dd = "0" + dd;
		} else {
			dd = dd.toString();
		}
		var mm = date.getMonth() + 1;
		if (mm < 10) {
			mm = mm.toString();
			mm = "0" + mm;
		} else {
			mm = mm.toString();
		}
		var yyyy = date.getFullYear();
		yyyy = yyyy.toString();

		var date1 = dd + "/" + mm + "/" + yyyy;
		return date1;
	},

	formatDateTime: function (date) {
		if (!date) {
			return;
		}
		date = new Date(date);
		var dd = date.getDate();
		if (dd < 10) {
			dd = dd.toString();
			dd = "0" + dd;
		} else {
			dd = dd.toString();
		}
		var mm = date.getMonth() + 1;
		if (mm < 10) {
			mm = mm.toString();
			mm = "0" + mm;
		} else {
			mm = mm.toString();
		}
		var yyyy = date.getFullYear();
		yyyy = yyyy.toString();

		var tt = date.getHours();
		if (tt < 10) {
			tt = tt.toString();
			tt = "0" + tt;
		} else {
			tt = tt.toString();
		}

		var min = date.getMinutes();
		if (min < 10) {
			min = min.toString();
			min = "0" + min;
		} else {
			min = min.toString();
		}

		var date1 = dd + "/" + mm + "/" + yyyy + " " + tt + ":" + min;
		return date1;
	},
	returnNot: function (value) {
		if (value)
			return false;
		else
			return true;
	},
	getMessageColor: function (sMessageType) {
		"use strict";

		var sColorValue = "#3498db";

		if (sMessageType) {
			switch (sMessageType) {
			case "Warning":
				sColorValue = "#f39c12";
				break;
			case "Error":
				sColorValue = "#c0392b";
				break;
			case "Success":
				sColorValue = "#27ae60";
				break;
			case "Default":
				sColorValue = "#3498db";
				break;
			}
		}

		return sColorValue;

	},

	/** Visibility of PO and Invoice Toggle Button in the Invoice Line Item */
	reverseThirdMsg: function (vendor, thirdParty) {
		if (vendor == "true" && thirdParty == "true") {
			return false;
		}
		return true;
	},

	crDbIndicator: function (value) {
		var returnValue = "";
		if (value) {
			if (value === "H") {
				returnValue = "Debit";
			} else {
				returnValue = "Credit";
			}
		}
		return returnValue;
	},

	/** To Remove Leading Zero */
	removeZero: function (value) {
		if (value) {
			return value.replace(/\b0+/g, '');
		} else {
			return "";
		}
	},

	/** Currency Symbol */
	currencySymbolWithValue: function (curVal) {
		if (curVal) {
			var currSymbol = {
				"USD": "$",
				"EUR": "€",
				"CRC": "₡",
				"GBP": "£",
				"ILS": "₪",
				"INR": "₹",
				"JPY": "¥",
				"KRW": "₩",
				"NGN": "₦",
				"PHP": "₱",
				"PLN": "zł",
				"PYG": "₲",
				"THB": "฿",
				"UAH": "₴",
				"VND": "₫"
			};
			var currncy = currSymbol[curVal] ? currSymbol[curVal] : "";
			return currncy;
		}
	},

	getDate: function (value) {
		if (value)
			return new Date(value).toISOString().split("T")[0];
		return "";
	},

	getUpdatedBy: function (value) {
		if (!value) {
			return "-";
		}
	}

};