sap.ui.define([
		"sap/ui/core/mvc/Controller",
		"com/menabev/AP/formatter/formatter",
	],
	/**
	 * @param {typeof sap.ui.core.mvc.Controller} Controller
	 */
	function (Controller, formatter) {
		"use strict";

		return Controller.extend("com.menabev.AP.controller.Inbox", {
			onInit: function () {
				var that = this;
				var paginatedModel = new sap.ui.model.json.JSONModel();
				this.getView().setModel(paginatedModel, "paginatedModel");
				var taskDataFilterModel = new sap.ui.model.json.JSONModel();
				this.getView().setModel(taskDataFilterModel, "taskDataFilterModel");
				this.myTask = false;

				var oUserDetailModel = this.getOwnerComponent().getModel("oUserDetailModel");
				this.oUserDetailModel = oUserDetailModel;

				this.oRouter = sap.ui.core.UIComponent.getRouterFor(this);
				this.oRouter.attachRoutePatternMatched(function (oEvent) {
					if (oEvent.getParameter("name") === "Inbox") {
						that.getInboxData();
					}
				});
			},
			onCreateInvoice: function (oEvent) {
				this.oRouter.navTo("NonPOInvoice");
			},

			onReqIdOpenSelect: function (oEvent) {
				var reqId = oEvent.getSource().getText();
				this.oRouter.navTo("NonPOInvoice", {
					id: reqId,
					status: "OPEN"
				});
			},

			onReqIdClaimSelect: function (oEvent) {
				var reqId = oEvent.getSource().getText();
				this.oRouter.navTo("NonPOInvoice", {
					id: reqId,
					status: "CLAIMED"
				});
			},

			getInboxData: function (pageNo, scroll) {
				var loggedinUserGroup = this.oUserDetailModel.getProperty("/loggedinUserGroup"),
					oVisibilityModel = this.getOwnerComponent().getModel("oVisibilityModel");
				oVisibilityModel.setProperty("/Inbox", {});
				oVisibilityModel.setProperty("/Inbox/btnCreateInvVisible", false);
				if (loggedinUserGroup === "Accountant") {
					oVisibilityModel.setProperty("/Inbox/btnCreateInvVisible", true);
				}

				var oInboxModel = new sap.ui.model.json.JSONModel();
				this.getView().setModel(oInboxModel, "oInboxModel");
				var taskDataFilterModelData = this.getView().getModel("taskDataFilterModel").getData(),
					url = "/menabevdev/invoiceHeader/inbox",
					that = this;
				taskDataFilterModelData.userId = this.oUserDetailModel.getProperty("/loggedinUserDetail/id");
				taskDataFilterModelData.indexNum = scroll ? pageNo : 1;
				taskDataFilterModelData.count = 100;
				taskDataFilterModelData.myTask = this.myTask;
				taskDataFilterModelData.roleOfUser = this.oUserDetailModel.getProperty("/loggedinUserGroup");
				taskDataFilterModelData.userEmailId = this.oUserDetailModel.getProperty("/loggedInUserMail");
				jQuery
					.ajax({
						url: url,
						dataType: "json",
						data: JSON.stringify(taskDataFilterModelData),
						contentType: "application/json",
						type: "POST",
						success: function (oData) {
							if (oData && Array.isArray(oData.body)) {
								var totalCount = oData.body[0].totalCount;
								oInboxModel.setProperty("/count", totalCount);
								if (oData.body[0].claimed == true) {
									oInboxModel.setProperty("/result", oData.body);
									oInboxModel.setProperty("/resultCount", totalCount);
								} else {
									oInboxModel.setProperty("/openResult", oData.body);
									oInboxModel.setProperty("/openResultCount", totalCount);
								}
							}
							if (!scroll)
								that.generatePagination();
						},
						error: function (oError) {
							sap.m.MessageToast.show(oError.responseText);
						}
					});
			},
			generatePagination: function () {
				var oInboxModel = this.getView().getModel("oInboxModel"),
					totalTasks = oInboxModel.getData().count,
					paginatedModel = this.getView().getModel("paginatedModel"),
					tasksPerPage = 100;
				paginatedModel.setProperty("/prevButton", false);
				paginatedModel.setProperty("/nextButton", true);
				var pageCount = parseInt(totalTasks / tasksPerPage);
				if (totalTasks % tasksPerPage !== 0) {
					pageCount = pageCount + 1;
				}
				oInboxModel.setProperty("/numberOfPages", pageCount);
				var array = [];
				if (pageCount > 5) {
					pageCount = 5;
				} else {
					paginatedModel.setProperty("/nextButton", false);
				}
				for (var i = 1; i <= pageCount; i++) {
					var object = {
						"text": i
					};
					array.push(object);
				}
				this.getView().getModel("paginatedModel").setProperty('/array', array);
				this.getView().getModel("paginatedModel").setProperty('/selectedPage', 1);
				// this.getView().byId("idCurrentPage").setText("Page : " + paginatedModel.getProperty("/selectedPage"));
				if (oInboxModel.getProperty("/numberOfPages") && parseInt(oInboxModel.getProperty("/numberOfPages")) > 1) {
					paginatedModel.setProperty("/pageVisible", true);
				} else {
					paginatedModel.setProperty("/pageVisible", false);
				}
			},
			onScrollLeft: function () {
				var paginatedModel = this.getView().getModel("paginatedModel");
				paginatedModel.setProperty("/prevButton", true);
				paginatedModel.setProperty("/nextButton", true);
				var oInboxModel = this.getView().getModel("oInboxModel");
				var paginatedData = paginatedModel.getData().array;
				var selectedPage = parseInt(paginatedModel.getProperty("/selectedPage"));
				var startValue = parseInt(paginatedData[0].text);
				var startNumber = 1;
				var array = [];
				if ((startValue - 1) === 1) {
					startNumber = 1;
					paginatedModel.setProperty("/prevButton", false);
				} else {
					startNumber = selectedPage - 3;
				}
				for (var i = startNumber; i <= (startNumber + 4); i++) {
					var object = {
						"text": i
					};
					array.push(object);
				}
				this.getView().getModel("paginatedModel").setProperty('/array', array);
				// oInboxModel.setProperty("/selectedPage", (parseInt(paginatedModel.getProperty("/selectedPage")) - 1));
				this.getInboxData(paginatedModel.getProperty("/selectedPage"), true);
			},

			/**
			 * Method is called when user clicks on the next button in the pagination.
			 * @memberOf workbox.Details
			 */
			onScrollRight: function () {
				var paginatedModel = this.getView().getModel("paginatedModel");
				paginatedModel.setProperty("/prevButton", true);
				paginatedModel.setProperty("/nextButton", true);
				var oInboxModel = this.getView().getModel("oInboxModel");
				var paginatedData = paginatedModel.getData().array;
				var selectedPage = parseInt(paginatedModel.getProperty("/selectedPage"));
				var startNumber = 1;
				var array = [];
				if (selectedPage > 2) {
					if ((selectedPage + 3) >= oInboxModel.getProperty("/numberOfPages")) {
						paginatedModel.setProperty("/nextButton", false);
						startNumber = parseInt(oInboxModel.getProperty("/numberOfPages")) - 4;
					} else {
						startNumber = selectedPage - 1;
					}
				} else {
					paginatedModel.setProperty("/prevButton", false);
				}
				for (var i = startNumber; i <= (startNumber + 4); i++) {
					var object = {
						"text": i
					};
					array.push(object);
				}
				this.getView().getModel("paginatedModel").setProperty('/array', array);
				this.getInboxData(paginatedModel.getProperty("/selectedPage"), true);
			},
			onPageClick: function (oEvent) {
				var selectedPage = oEvent.getSource().getText();
				this.getInboxData(selectedPage, true);
			},
			onSearch: function () {
				var taskDataFilterModel = this.getView().getModel("taskDataFilterModel"),
					taskDataFilterModelData = taskDataFilterModel.getData(),
					bError = false;
				if ((!taskDataFilterModelData.createdAtFrom && taskDataFilterModelData.createdAtTo) || (taskDataFilterModelData.createdAtFrom &&
						taskDataFilterModelData.createdAtTo)) {
					sap.m.MessageToast.show("Fill both invoice From Date and to date to proceed");
					bError = true;
				}
				if ((!taskDataFilterModelData.dueDateFrom && taskDataFilterModelData.dueDateTo) || (taskDataFilterModelData.dueDateFrom &&
						taskDataFilterModelData.dueDateTo)) {
					sap.m.MessageToast.show("Fill both due From Date and to date to proceed");
					bError = true;
				}
				if ((!taskDataFilterModelData.invoiceTotalFrom && taskDataFilterModelData.invoiceTotalTo) || (taskDataFilterModelData.invoiceTotalFrom &&
						taskDataFilterModelData.invoiceTotalTo)) {
					sap.m.MessageToast.show("Fill both invoice From value and to value to proceed");
					bError = true;
				}
				if (!taskDataFilterModel.getProperty("/requestId") &&
					!taskDataFilterModel.getProperty("/invoiceTotalFrom") &&
					!taskDataFilterModel.getProperty("/invoiceTotalTo") &&
					!taskDataFilterModel.getProperty("/dueDateFrom") &&
					!taskDataFilterModel.getProperty("/dueDateTo") &&
					!taskDataFilterModel.getProperty("/createdAtFrom") &&
					!taskDataFilterModel.getProperty("/createdAtTo") &&
					!taskDataFilterModel.getProperty("/extInvNum") &&
					!taskDataFilterModel.getProperty("/assignedTo") &&
					!taskDataFilterModel.getProperty("/vendorId") &&
					!taskDataFilterModel.getProperty("/invoiceType") &&
					!taskDataFilterModel.getProperty("/validationStatus")
				) {
					sap.m.MessageToast.show("Enter Atleast one search parameter to search");
					bError = true;
				}
				if (!bError)
					this.getInboxData();
			},
			onSelectTab: function (oEvent) {
				this.myTask = true;
				if (oEvent.getSource().getSelectedKey() == "openTask")
					this.myTask = false;
				this.getInboxData();
			},
			fnClear: function () {
				var taskDataFilterModel = this.getView().getModel("taskDataFilterModel");
				taskDataFilterModel.setData({
					"vendorId": "",
					"vendorName": "",
					"filterRequestId": "",
					"invNo": "",
					"createdAtFrom": "",
					"createdAtTo": "",
					"assignedTo": "",
					"dueDateFrom": "",
					"dueDateTo": "",
					"invoiceTotalFrom": "",
					"invoiceTotalTo": "",
					"lifecycleStatus": "",
					"invoiceType": ""
				});
				taskDataFilterModel.refresh();
			},
			onClaimRelease: function (oEvent) {
				var that = this;
				var obj = {
					"claim": true,
					"taskID": oEvent.getSource().getBindingContext("oInboxModel").getObject().taskId,
					"userId": "P000022"
				};
				if (oEvent.getSource().getTooltip() == "release")
					obj.claim = false;
				jQuery
					.ajax({
						url: "/menabevdev/invoiceHeader/claimOrRelease",
						dataType: "json",
						data: JSON.stringify(obj),
						contentType: "application/json",
						type: "POST",
						success: function () {},
						error: function (oError) {
							if (oError.status == "201") {
								sap.m.MessageToast.show(oError.responseText);
								if (obj.claim)
									that.myTask = false;
								else
									that.myTask = true;
								that.getInboxData();
							} else {
								sap.m.MessageToast.show(oError.responseText);
							}
						}
					});

			},
			onDateRangeChange: function (oEvent) {
				var filterData = this.getView().getModel("taskDataFilterModel").getData();
				var invDateFrom, invDateTo, dueDateFrom, dueDateTo;
				invDateFrom = filterData.createdAtFrom;
				invDateTo = filterData.createdAtTo;
				dueDateFrom = filterData.dueDateFrom;
				dueDateTo = filterData.dueDateTo;
				if (invDateFrom && invDateFrom != "" && invDateTo && invDateTo != "") {
					if (invDateFrom > invDateTo) {
						sap.m.MessageToast.show("Invoice From Date cannot be greater than Invoice To Date");
						if (oEvent) {
							oEvent.getSource().setValue();
						}
						return false;
					}
				}
				if (dueDateFrom && dueDateFrom != "" && dueDateTo && dueDateTo != "") {
					if (dueDateFrom > dueDateTo) {
						sap.m.MessageToast.show("Due From Date cannot be greater than Due To Date");
						if (oEvent) {
							oEvent.getSource().setValue();
						}
						return false;
					}
				}
			}
		});
	});