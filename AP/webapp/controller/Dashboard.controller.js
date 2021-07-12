sap.ui.define([
	"com/menabev/AP/controller/BaseController"
], function (BaseController) {
	"use strict";

	return BaseController.extend("com.menabev.AP.controller.Dashboard", {

		onInit: function () {
			var mDashboardModel = this.getOwnerComponent().getModel("mDashboardModel");
			this.mDashboardModel = mDashboardModel;
			var oUserDetailModel = this.getOwnerComponent().getModel("oUserDetailModel");
			this.oUserDetailModel = oUserDetailModel;
			var oVisibilityModel = this.getOwnerComponent().getModel("oVisibilityModel");
			this.oVisibilityModel = oVisibilityModel;
			this.oVisibilityModel.setProperty("/TrackInvoice", {});
			this.fnChartsDataDefault();
		},

		fnChartsDataDefault: function () {
			var mDashboardModel = this.mDashboardModel;
			// var userDetail = this.oUserDetailModel.getProperty("/loggedinUserDetail"),
			// 	loggedinUserVendorId = userDetail["urn:sap:cloud:scim:schemas:extension:custom:2.0:User"].attributes[0].value,
			// 	vendorId = [];
			// vendorId.push(loggedinUserVendorId);
			// var compCode = userDetail["urn:sap:cloud:scim:schemas:extension:custom:2.0:User"].attributes[1].value;

			this.getDefaultValues("Dashboard");
			var vendorId = mDashboardModel.getProperty("/vendorId");
			var compCode = mDashboardModel.getProperty("/compCode");
			var today = new Date();
			var rcvdOnFrom = new Date();
			rcvdOnFrom.setMonth(rcvdOnFrom.getMonth() - 3);
			// var past = new Date();
			// var rcvdOnFrom = past.setDate(past.getDate() - 3650);
			var rcvdOnTo = today.getTime();
			var pastDate = new Date(rcvdOnFrom);
			mDashboardModel.setProperty("/toDate", today);
			mDashboardModel.setProperty("/fromDate", pastDate);
			mDashboardModel.setProperty("/compCode", compCode);
			mDashboardModel.setProperty("/vendorId", vendorId);
			var obj = {
				"companyCode": compCode,
				"currency": "",
				"rcvdOnFrom": rcvdOnFrom,
				"rcvdOnTo": rcvdOnTo,
				"vendorId": vendorId
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
								if (oRow.statusText === "Duplicate Invoice" || oRow.statusText === "PO Missing or Invalid" || oRow.statusText === "No-GRN" ||
									oRow.statusText ===
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
			var userDetail = this.oUserDetailModel.getProperty("/loggedinUserDetail"),
				loggedinUserVendorId = userDetail["urn:sap:cloud:scim:schemas:extension:custom:2.0:User"].attributes[0].value,
				vendorId = [];
			vendorId.push(loggedinUserVendorId);
			var compCode = userDetail["urn:sap:cloud:scim:schemas:extension:custom:2.0:User"].attributes[1].value;
			var mDashboardModel = this.mDashboardModel;
			var receivedFrom = mDashboardModel.getProperty("/fromDate");
			var rcvdOnFrom = receivedFrom.getTime();
			var receivedTo = mDashboardModel.getProperty("/toDate");
			var rcvdOnTo = receivedTo.getTime();
			var obj = {
				"companyCode": compCode,
				"currency": "",
				"rcvdOnFrom": rcvdOnFrom,
				"rcvdOnTo": rcvdOnTo,
				"vendorId": vendorId
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
								if (oRow.statusText === "Duplicate Invoice" || oRow.statusText === "PO Missing or Invalid" || oRow.statusText === "No-GRN" ||
									oRow.statusText ===
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