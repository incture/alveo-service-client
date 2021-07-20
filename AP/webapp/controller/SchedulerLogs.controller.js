sap.ui.define([
	"sap/ui/core/mvc/Controller"
], function (Controller) {
	"use strict";

	return Controller.extend("com.menabev.AP.controller.SchedulerLogs", {

		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf com.menabev.AP.view.SchedulerLogs
		 */
		onInit: function () {

			var oSchedulerLogsModel = this.getOwnerComponent().getModel("oSchedulerLogsModel");
			this.oSchedulerLogsModel = oSchedulerLogsModel;
			var oDropDownModel = this.getOwnerComponent().getModel("oDropDownModel");
			this.oDropDownModel = oDropDownModel;
			var oUserDetailModel = this.getOwnerComponent().getModel("oUserDetailModel");
			this.oUserDetailModel = oUserDetailModel;
			var oVisibilityModel = this.getOwnerComponent().getModel("oVisibilityModel");
			this.oVisibilityModel = oVisibilityModel;
			this.oVisibilityModel.setProperty("/SchedulerLogs", {});

			this.setFilterBar();
			this.fnGetDefaultSchedulerLogs();
		},

		setFilterBar: function () {
			var filterBar = this.getView().byId("SchedulerFilterBarId");
			filterBar._oFiltersButton.setVisible(false);
			filterBar._oHideShowButton
				.setType("Default").addStyleClass("sapUiSizeCompact");
			filterBar._oClearButtonOnFB.setType("Default").addStyleClass(
				"sapUiSizeCompact");
			filterBar._oSearchButton.setText("Search").addStyleClass("sapUiSizeCompact").setWidth("5rem");
			filterBar._oClearButtonOnFB
				.setVisible(true);
		},

		onSelectAll: function (oEvent) {
			var bSelected = oEvent.getParameter("selected");
			var oSchedulerLogsModel = this.oSchedulerLogsModel;
			oSchedulerLogsModel.setProperty("/isEmailScheduler", bSelected);
			oSchedulerLogsModel.setProperty("/isOCRScheduler", bSelected);
			oSchedulerLogsModel.setProperty("/isGRNScheduler", bSelected);
			oSchedulerLogsModel.refresh();
		},

		handleDateChange: function (oEvent) {
			var oSchedulerLogsModel = this.oSchedulerLogsModel.getData();
			var fromdate, toDate;
			fromdate = oSchedulerLogsModel.fromdate;
			toDate = oSchedulerLogsModel.toDate;
			if (fromdate && fromdate != "" && toDate && toDate != "") {
				fromdate = new Date(fromdate).getTime();
				toDate = new Date(toDate).getTime();
				if (fromdate > toDate) {
					sap.m.MessageToast.show("From Date cannot be greater than To Date");
					if (oEvent) {
						oEvent.getSource().setValue();
					}
					return false;
				}
			}
		},

		fnGetDefaultSchedulerLogs: function () {
			var payload = {};
			this.fnGetSchedulerLogs(payload);
		},

		onSearchSchedulerLogs: function () {
			var oSchedulerLogsModel = this.oSchedulerLogsModel;
			var fromDate = oSchedulerLogsModel.getProperty("/fromdate");
			if (fromDate) {
				fromDate =this.fnGetFormatedDate(fromDate);
			}
			var toDate = oSchedulerLogsModel.getProperty("/toDate");
			if (toDate) {
				toDate =this.fnGetFormatedDate(toDate);
			}
			var payload = {
				"fromdate": fromDate,
				"toDate": toDate,
				"isEmailScheduler": oSchedulerLogsModel.getProperty("/isEmailScheduler"),
				"isOCRScheduler": oSchedulerLogsModel.getProperty("/isOCRScheduler"),
				"isGRNScheduler": oSchedulerLogsModel.getProperty("/isGRNScheduler")
			};
			this.fnGetSchedulerLogs(payload);
		},
		
		fnGetFormatedDate:function(date){
			var aDate = new Date(date),
				 DD = aDate.getDate(),
				 month = aDate.getMonth() + 1,
				 yy = aDate.getFullYear(),
				 hh = aDate.getHours(),
				 min = aDate.getMinutes(),
				 ss = aDate.getSeconds();
			var formatDate = yy + "-" + month + "-" + DD + " " + hh + ":" + min + ":" + ss;
			return formatDate;
		},

		fnGetSchedulerLogs: function (payload) {
			var that = this;
			var oSchedulerLogsModel = that.oSchedulerLogsModel;
			var url = "/menabevdev/schedulerLog/filterLog";
			var busy = new sap.m.BusyDialog();
			busy.open();
			jQuery.ajax({
				url: url,
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				data: JSON.stringify(payload),
				success: function (result) {
					busy.close();
					oSchedulerLogsModel.setProperty("/aSchedulerConfiguration", result.schedulerConfigurationDtoList);
					oSchedulerLogsModel.setProperty("/aSchedulerLogs", {});
					var aSchedulerLogs = [];
					if (result.schedulerLogDtoList) {
						for (var i = 0; i < result.schedulerLogDtoList.length; i++) {
							var obj = result.schedulerLogDtoList[i].schedulerRunDto;
							obj.schedulerCycle = result.schedulerLogDtoList[i].schedulerCycle;
							aSchedulerLogs.push(obj);
						}
					}
					oSchedulerLogsModel.setProperty("/aSchedulerLogs", aSchedulerLogs);
					sap.m.MessageToast.show(result.message);
					oSchedulerLogsModel.refresh();
				}.bind(this),
				error: function (error) {
					busy.close();
					var errorMsg = error.responseText;
					this.errorMsg(errorMsg);
				}.bind(this)
			});

		},

		onClickCycleId: function (oEvent) {
			var object = oEvent.getSource().getBindingContext("oSchedulerLogsModel").getObject();
			var schedulerCycleLogs = object.schedulerCycleLogs;
			this.oSchedulerLogsModel.setProperty("/schedulerCycleLogs", schedulerCycleLogs);
			var oFragmentName = "com.menabev.AP.fragment.SchedulerCycleLogs";
			if (!this.SchedulerCycleLogsFragment) {
				this.SchedulerCycleLogsFragment = sap.ui.xmlfragment(oFragmentName, this);
				this.getView().addDependent(this.SchedulerCycleLogsFragment);
			}
			this.SchedulerCycleLogsFragment.open();
		},

		onCloseSchedulerCycleLogs: function () {
			this.SchedulerCycleLogsFragment.close();
		},

		onPressClear: function () {
			var oSchedulerLogsModel = this.oSchedulerLogsModel;
			oSchedulerLogsModel.setProperty("/fromdate", null);
			oSchedulerLogsModel.setProperty("/toDate", null);
			oSchedulerLogsModel.setProperty("/isEmailScheduler", false);
			oSchedulerLogsModel.setProperty("/isOCRScheduler", false);
			oSchedulerLogsModel.setProperty("/isGRNScheduler", false);
			oSchedulerLogsModel.setProperty("/all", false);
			oSchedulerLogsModel.refresh();
		},

	});

});