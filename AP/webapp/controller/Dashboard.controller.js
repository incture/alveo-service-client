sap.ui.define([
	"sap/ui/core/mvc/Controller"
], function (Controller) {
	"use strict";

	return Controller.extend("com.menabev.AP.controller.Dashboard", {
		
		onInit: function () {
			var mDashboardModel = this.getOwnerComponent().getModel("mDashboardModel");
			this.mDashboardModel = mDashboardModel;
			this.fnChartsDataDefault();
		},
		
		fnChartsDataDefault: function () {
			var mDashboardModel = this.mDashboardModel;
			var today = new Date();
			var past = new Date();
			var rcvdOnFrom = past.setDate(past.getDate() - 3650);
			var rcvdOnTo = today.getTime();
			var pastDate = new Date(rcvdOnFrom);
			mDashboardModel.setProperty("/toDate", today);
			mDashboardModel.setProperty("/fromDate", pastDate);
			var obj = {
				"companyCode": "",
				"currency": "",
				"rcvdOnFrom": rcvdOnFrom,
				"rcvdOnTo": rcvdOnTo,
				"vendorId": []
			};
			var url = "/menabevdev/apDashboardCharts/getDashboardChartDetails";
			var url2 = "/menabevdev/apDashboardCharts/getKPIDetails";
			jQuery
				.ajax({
					url: url,
					dataType: "json",
					data: JSON.stringify(obj),
					contentType: "application/json",
					type: "POST",
					success: function (results) {
						mDashboardModel.refresh(true);
						var data = results.charts;
						var exceptionReport = data[0].values;
						var agingReport = data[1];
						for (var i = 0; i < exceptionReport.length; i++) {
							var aTempData = exceptionReport.filter(function (oRow) {
								if (oRow.statusText === "Duplicate Invoice" || oRow.statusText === "PO Missing or Invalid" || oRow.statusText === "No-GRN" || oRow.statusText ===
									"Partial GRN" || oRow.statusText === "UoM Mismatch" || oRow.statusText === "Item Mismatch" || oRow.statusText ===
									"Qty Mismatch" || oRow.statusText === "Price Mismatch" || oRow.statusText === "Balance Mismatch" || oRow.statusText ===
									"Price/Qty") {
									return oRow;
								}
							});
						}
						mDashboardModel.setProperty("/exceptionReport", aTempData);
						mDashboardModel.setProperty("/agingReport", agingReport);
					},
					error: function (e) {
						sap.m.MessageToast.show("Data Not Found");

					}
				});
				jQuery
				.ajax({
					url: url2,
					dataType: "json",
					data: JSON.stringify(obj),
					contentType: "application/json",
					type: "POST",
					success: function (results) {
						mDashboardModel.refresh(true);
						var data = results.charts;
						var length = data.length;
						if (length) {
							mDashboardModel.setProperty("/Kpi", data);
						}
					},
					error: function (e) {
						sap.m.MessageToast.show("Data Not Found");

					}
				});
		},
		
		fnChartsData: function () {
			var mDashboardModel = this.mDashboardModel;
			var receivedFrom = mDashboardModel.getProperty("/fromDate");
			var rcvdOnFrom = receivedFrom.getTime();
			var receivedTo = mDashboardModel.getProperty("/toDate");
			var rcvdOnTo = receivedTo.getTime();
			var obj = {
				"companyCode": "",
				"currency": "",
				"rcvdOnFrom": rcvdOnFrom,
				"rcvdOnTo": rcvdOnTo,
				"vendorId": []
			};
			var url = "/menabevdev/apDashboardCharts/getDashboardChartDetails";
			var url2 = "/menabevdev/apDashboardCharts/getKPIDetails";
			jQuery
				.ajax({
					url: url,
					dataType: "json",
					data: JSON.stringify(obj),
					contentType: "application/json",
					type: "POST",
					success: function (results) {
						mDashboardModel.refresh(true);
						var data = results.charts;
						var exceptionReport = data[0].values;
						var agingReport = data[1];
						for (var i = 0; i < exceptionReport.length; i++) {
							var aTempData = exceptionReport.filter(function (oRow) {
								if (oRow.statusText === "Duplicate Invoice" || oRow.statusText === "PO Missing or Invalid" || oRow.statusText === "No-GRN" || oRow.statusText ===
									"Partial GRN" || oRow.statusText === "UoM Mismatch" || oRow.statusText === "Item Mismatch" || oRow.statusText ===
									"Qty Mismatch" || oRow.statusText === "Price Mismatch" || oRow.statusText === "Balance Mismatch" || oRow.statusText ===
									"Price/Qty") {
									return oRow;
								}
							});
						}
						mDashboardModel.setProperty("/exceptionReport", aTempData);
						mDashboardModel.setProperty("/agingReport", agingReport);
					},
					error: function (e) {
						sap.m.MessageToast.show("Data Not Found");

					}
				});
			jQuery
				.ajax({
					url: url2,
					dataType: "json",
					data: JSON.stringify(obj),
					contentType: "application/json",
					type: "POST",
					success: function (results) {
						mDashboardModel.refresh(true);
						var data = results.charts;
						var length = data.length;
						if (length) {
							mDashboardModel.setProperty("/Kpi", data);
						}
					},
					error: function (e) {
						sap.m.MessageToast.show("Data Not Found");

					}
				});
		}

	});

});