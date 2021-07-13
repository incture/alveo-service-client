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

		var date1 = mm + "/" + dd + "/" + yyyy;
		return date1;
	},

	formatSchedulerDate: function (date) {
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

		var date1 = yyyy + "-" + mm + "-" + dd;
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
			return new Date(value).getFullYear() + "-" + (new Date(value).getMonth() + 1) + "-" + new Date(value).getDate();
		return "";
	},

	getUpdatedBy: function (value) {
		if (!value) {
			return "-";
		} else
			return value;
	},

	// Pagination

	setLeftPaginationVisible: function (currentPage, totalPage) {
		if (totalPage < 6) {
			return false;
		} else if (currentPage != 1) {
			return true;
		} else {
			return false;
		}
	},

	setRightPaginationVisible: function (currentPage, totalPage) {
		if (totalPage < 6) {
			return false;
		} else if (currentPage != totalPage) {
			return true;
		} else {
			return false;
		}
	},

	// Inbox

	createInvoiceVisible: function (loggedinUser) {
		if (loggedinUser === "Accountant") {
			return true;
		} else {
			return false;
		}
	},
	setPaginationClass: function (selectedPage, currentPage) {
		if (selectedPage == currentPage) {
			// this.removeStyleClass("nonSelectedPage");
			this.addStyleClass("selectedPage");
		} else {
			this.removeStyleClass("selectedPage");
			// this.addStyleClass("nonSelectedPage");
		}
		return true;
	},
	setClaimVisible: function (status) {
		if (status === "READY") {
			return true;
		} else {
			return false;
		}
	},

	setReleaseVisible: function (status) {
		if (status === "RESERVED") {
			return true;
		} else {
			return false;

		}
	},
	formatRecipient: function (array) {
		if (array) {
			var len = array.length,
				val = "";
			if (len) {
				val = array[0];
			}
			return val;
		} else {
			return "";
		}
	},
	arraytoString: function (array) {
		if (array) {
			var len = array.length,
				val = "";
			for (var i = 0; i < len; i++) {
				val += array[i] + "\n";
			}
			return val;
		} else {
			return "";
		}
	},
	batchIDVisible: function (invType) {
		if (invType === "PO") {
			return true;
		} else {
			return false;
		}
	},

	ThreeWayMatchHighlight: function (isThreewayMatched) {
		if (isThreewayMatched) {
			return "Success";
		}
		return "None";
	},

	formatPOVisible: function (POState) {
		if (POState === true) {
			return true;
		} else {
			return false;
		}
	},
	formatPOInvVisible: function (POState) {
		if (POState === true) {
			return false;
		} else {
			return true;
		}
	},

	formatPOItemCatVisible: function (POState, itemCat) {
		if (POState === true && itemCat == "D") {
			return true;
		} else {
			return false;
		}
	},

	formatPrice: function (value) {
		if (value) {
			value = parseFloat(value);
			value = value.toFixed(2);
		} else {
			value = 0.00;
		}
		return value;
	},
	formatQuantity: function (value) {
		if (value) {
			value = parseFloat(value);
			value = value.toFixed(3);
		} else {
			value = 0.00;
		}
		return value;
	},

	getInvoiceItems: function (isTwoWayMatched, isDeleted) {
		if (!isTwoWayMatched && !isDeleted) {
			return true;
		}
		return false;
	},

	getPurchaseOrders: function (isTwoWayMatched, isDeleted) {
		if (isTwoWayMatched && isDeleted) {
			return true;
		}
		return false;
	},

	getMatchedItems: function (isTwoWayMatched, isDeleted) {
		if (isTwoWayMatched && !isDeleted) {
			return true;
		}
		return false;
	},

	getMatchedBtnVisible: function (itemCategory) {
		if (itemCategory === "D") {
			return false;
		}
		return true;
	},

	formatSelectionVisible: function (twowaymatch, statusCode, isSelected, threewayMatched) {
		this.removeStyleClass("disableSelect");
		this.setSelected(false);
		// this.removeStyleClass("threewayMatchedNS");
		// this.removeStyleClass("threewayMatchedS");
		// this.removeStyleClass("twoWaymatchedNS");
		// if (threewayMatched && isSelected) {
		// 	this.addStyleClass("threewayMatchedS");
		// } else if (threewayMatched && !isSelected) {
		// 	this.addStyleClass("threewayMatchedNS");
		// } else if (twowaymatch && !isSelected) {
		// 	this.addStyleClass("twoWaymatchedNS");
		// }

		if (!twowaymatch || statusCode == "5" || statusCode == "6") {
			this.addStyleClass("disableSelect");
		} else if (twowaymatch && isSelected) {
			this.setSelected(true);
		} else {
			this.setSelected(false);
		}
		return true;
	},

	formatRemidiationUser: function (remidiationUser) {
		if (remidiationUser === "GRN") {
			return true;
		} else if (remidiationUser === "BUYER") {
			return false;
		}
	},

	changeColor: function (sValue) {
		if (sValue === "14") {
			sValue = "Success";
		} else if (sValue === "13") {
			sValue = "Indication03";
		} else if (sValue === "15") {
			sValue = "Error";
		} else {
			sValue = "None";
		}
		return sValue;
	},

	changeStatustext: function (sValue) {
		if (sValue === "14") {
			sValue = "Paid";
		} else if (sValue === "15") {
			sValue = "Rejected";
		} else if (sValue === "13") {
			sValue = "Unpaid";
		} else {
			sValue = "Pending";
		}
		return sValue;
	},

	showIcon: function (value) {
		if (value === "15") {
			value = true;
		} else {
			value = false;
		}
		return value;
	},

	showPaymentRef: function (sValue) {
		if (sValue == "NA") {
			sValue = "";
		}
		return sValue;

	},

	formatActivityHeader: function (vendorName, vendorId) {
		if (vendorName && vendorId) {
			var value = vendorName;
		} else if (vendorName === "") {
			value = vendorId;
		} else if (vendorId === "") {
			value = vendorName;
		} else {
			value = vendorName;
		}
		return value;
	},

	changeValidationIconCss: function (sValue) {
		if (sValue === "14" || sValue === "13") {
			this.removeStyleClass("doneProcessCircle onGoingProcessCircle rejectedProcessCircle toBeDoneProcessCircle");
			this.addStyleClass("doneProcessCircle");
			sValue = true;
		} else if (sValue === "16") {
			this.removeStyleClass("doneProcessCircle onGoingProcessCircle rejectedProcessCircle toBeDoneProcessCircle");
			this.addStyleClass("onGoingProcessCircle");
			sValue = true;
		} else if (sValue === "15") {
			this.removeStyleClass("doneProcessCircle onGoingProcessCircle rejectedProcessCircle toBeDoneProcessCircle");
			this.addStyleClass("rejectedProcessCircle");
			sValue = true;
		}
		return sValue;
	},

	changValidationeDashLineCss: function (sValue) {
		if (sValue === "14" || sValue === "13") {
			this.removeStyleClass("dashLineDone dashLineToBeDone dashLineOnGoing");
			this.addStyleClass("dashLineDone");
			sValue = true;
		} else if (sValue === "16" || sValue === "15") {
			this.removeStyleClass("dashLineDone dashLineToBeDone dashLineOnGoing");
			this.addStyleClass("dashLineToBeDone");
			sValue = true;
		}
		return sValue;
	},

	changeValidationBoxCss: function (sValue) {
		if (sValue === "14" || sValue === "13") {
			this.removeStyleClass(
				"poSearchactivitylogVboxDone poSearchactivitylogVboxOnGoing poSearchactivitylogVboxRejected poSearchactivitylogVboxToBeDone");
			this.addStyleClass("poSearchactivitylogVboxDone");
			sValue = true;
		} else if (sValue === "16") {
			this.removeStyleClass(
				"poSearchactivitylogVboxDone poSearchactivitylogVboxOnGoing poSearchactivitylogVboxRejected poSearchactivitylogVboxToBeDone");
			this.addStyleClass("poSearchactivitylogVboxOnGoing");
			sValue = true;
		} else if (sValue === "15") {
			this.removeStyleClass(
				"poSearchactivitylogVboxDone poSearchactivitylogVboxOnGoing poSearchactivitylogVboxRejected poSearchactivitylogVboxToBeDone");
			this.addStyleClass("poSearchactivitylogVboxRejected");
			sValue = true;
		}
		return sValue;
	},

	changeValidationTextCss: function (sValue) {
		if (sValue === "14" || sValue === "13") {
			this.removeStyleClass("statusHeaderDone statusHeaderOnGoing statusHeaderRejected statusHeaderToBeDone");
			this.addStyleClass("statusHeaderDone");
			sValue = true;
		} else if (sValue === "16") {
			this.removeStyleClass("statusHeaderDone statusHeaderOnGoing statusHeaderRejected statusHeaderToBeDone");
			this.addStyleClass("statusHeaderOnGoing");
			sValue = true;
		} else if (sValue === "15") {
			this.removeStyleClass("statusHeaderDone statusHeaderOnGoing statusHeaderRejected statusHeaderToBeDone");
			this.addStyleClass("statusHeaderRejected");
			sValue = true;
		}
		return sValue;
	},

	changePaymentIconCss: function (sValue) {
		if (sValue === "14") {
			this.removeStyleClass("doneProcessCircle onGoingProcessCircle rejectedProcessCircle toBeDoneProcessCircle");
			this.addStyleClass("doneProcessCircle");
			sValue = true;
		} else if (sValue === "13") {
			this.removeStyleClass("doneProcessCircle onGoingProcessCircle rejectedProcessCircle toBeDoneProcessCircle");
			this.addStyleClass("onGoingProcessCircle");
			sValue = true;
		} else if (sValue === "16" || sValue === "15") {
			this.removeStyleClass("doneProcessCircle onGoingProcessCircle rejectedProcessCircle toBeDoneProcessCircle");
			this.addStyleClass("toBeDoneProcessCircle");
			sValue = true;
		}
		return sValue;
	},

	changePaymentBoxCss: function (sValue) {
		if (sValue === "14") {
			this.removeStyleClass(
				"poSearchactivitylogVboxDone poSearchactivitylogVboxOnGoing poSearchactivitylogVboxRejected poSearchactivitylogVboxToBeDone");
			this.addStyleClass("poSearchactivitylogVboxDone");
			sValue = true;
		} else if (sValue === "16" || sValue === "15") {
			this.removeStyleClass(
				"poSearchactivitylogVboxDone poSearchactivitylogVboxOnGoing poSearchactivitylogVboxRejected poSearchactivitylogVboxToBeDone");
			this.addStyleClass("poSearchactivitylogVboxToBeDone");
			sValue = true;
		} else if (sValue === "13") {
			this.removeStyleClass(
				"poSearchactivitylogVboxDone poSearchactivitylogVboxOnGoing poSearchactivitylogVboxRejected poSearchactivitylogVboxToBeDone");
			this.addStyleClass("poSearchactivitylogVboxOnGoing");
			sValue = true;
		}
		return sValue;
	},

	changePaymentTextCss: function (sValue) {
		if (sValue === "14") {
			this.removeStyleClass("statusHeaderDone statusHeaderOnGoing statusHeaderRejected statusHeaderToBeDone");
			this.addStyleClass("statusHeaderDone");
			sValue = true;
		} else if (sValue === "16" || sValue === "15") {
			this.removeStyleClass("statusHeaderDone statusHeaderOnGoing statusHeaderRejected statusHeaderToBeDone");
			this.addStyleClass("statusHeaderToBeDone");
			sValue = true;
		} else if (sValue === "13") {
			this.removeStyleClass("statusHeaderDone statusHeaderOnGoing statusHeaderRejected statusHeaderToBeDone");
			this.addStyleClass("statusHeaderOnGoing");
			sValue = true;
		}
		return sValue;
	},
	AccAsssVisible: function (accass) {
		if (accass) {
			return true;
		} else {
			return false;
		}
	},
	
	getInvStatusColorScheme: function(invStatus){
		if(invStatus != 17){
			return 3;
		} else {
			return 7;
		}
	},
	
	removeBlankSpace: function(costCenter){
		var costCtr= costCenter.trim();
		return costCtr;
	}

};