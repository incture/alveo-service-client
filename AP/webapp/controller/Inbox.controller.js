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
				this.setFilterBar();
				this.oRouter = sap.ui.core.UIComponent.getRouterFor(this);
				this.oRouter.attachRoutePatternMatched(function (oEvent) {
					if (oEvent.getParameter("name") === "Inbox") {
						that.getInboxData("", "", "OPEN ", "openTask", "openTaskCount");
						that.getInboxData("", "", "MYTASK", "myTask", "myTaskCount");
						that.getInboxData("", "", "DRAFT", "draftTask", "draftCount");
					}
				});
			},
			setFilterBar: function () {
				var filterBar = this.getView().byId("filterBarId");
				filterBar._oFiltersButton.setVisible(false);
				filterBar._oHideShowButton
					.setType("Default").addStyleClass("sapUiSizeCompact");
				filterBar._oClearButtonOnFB.setType("Default").addStyleClass(
					"sapUiSizeCompact");
				filterBar._oSearchButton.setText("Search").addStyleClass("sapUiSizeCompact").setWidth("5rem");
				filterBar._oClearButtonOnFB
					.setVisible(true);
			},

			onCreateInvoice: function (oEvent) {
				this.oRouter.navTo("NonPOInvoice", {
					id: "NEW"
				});
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

			getInboxData: function (pageNo, scroll, taskStatus, object, count) {
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
				var taskDataFilterModelData = jQuery.extend(true, {}, this.getView().getModel("taskDataFilterModel").getData());
				if (taskDataFilterModelData.createdAtFrom) {
					taskDataFilterModelData.createdAtFrom = new Date(taskDataFilterModelData.createdAtFrom).getTime();
				}
				if (taskDataFilterModelData.createdAtTo) {
					taskDataFilterModelData.createdAtTo = new Date(taskDataFilterModelData.createdAtTo).getTime();
				}
				if (taskDataFilterModelData.dueDateFrom) {
					taskDataFilterModelData.dueDateFrom = new Date(taskDataFilterModelData.dueDateFrom).getTime();
				}
				if (taskDataFilterModelData.dueDateTo) {
					taskDataFilterModelData.dueDateTo = new Date(taskDataFilterModelData.dueDateTo).getTime();
				}
				taskDataFilterModelData.userId = this.oUserDetailModel.getProperty("/loggedinUserDetail/id");
				taskDataFilterModelData.indexNum = scroll ? pageNo : 1;
				taskDataFilterModelData.count = 100;
				taskDataFilterModelData.myTask = taskStatus;
				taskDataFilterModelData.roleOfUser = this.oUserDetailModel.getProperty("/loggedinUserGroup");
				taskDataFilterModelData.userEmailId = this.oUserDetailModel.getProperty("/loggedInUserMail");
				jQuery.ajax({
					url: url,
					dataType: "json",
					data: JSON.stringify(taskDataFilterModelData),
					contentType: "application/json",
					type: "POST",
					success: function (oData) {
						if (oData && Array.isArray(oData.body.listOfTasks)) {
							oInboxModel.setProperty("/" + count, oData.body.count);
							oInboxModel.setProperty("/" + object, oData.body.listOfTasks);
							// oInboxModel.setProperty("/resultCount", totalCount);
							// if (oData.body[0].claimed == true) {
							// oInboxModel.setProperty("/result", oData.body);
							// oInboxModel.setProperty("/resultCount", totalCount);
							// } else {
							// 	oInboxModel.setProperty("/openResult", oData.body);
							// 	oInboxModel.setProperty("/openResultCount", totalCount);
							// }
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
				var key = oEvent.getSource().getSelectedKey();
				if (key == "openTask") {
					this.getInboxData("", "", "OPEN ", "openTask", "openTaskCount");
				} else if (key == "myTask") {
					this.getInboxData("", "", "MYTASK", "myTask", "myTaskCount");
				} else if (key == "draft") {
					this.getInboxData("", "", "DRAFT", "draftTask", "draftCount");
				}
			},

			SearchInboxData: function (oEvent) {
				this.getInboxData("", "", "OPEN ", "openTask", "openTaskCount");
				this.getInboxData("", "", "MYTASK", "myTask", "myTaskCount");
				this.getInboxData("", "", "DRAFT", "draftTask", "draftCount");
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
					"userId": this.oUserDetailModel.getProperty("/loggedinUserDetail/id")
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
								that.getInboxData("", "", "OPEN ", "openTask", "openTaskCount");
								that.getInboxData("", "", "MYTASK", "myTask", "myTaskCount");
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
			},

			onChangeInvoiceValue: function (oEvent) {
				var oValue = oEvent.getSource().getValue();
				oValue = (oValue.indexOf(".") >= 0) ? (oValue.substr(0, oValue.indexOf(".")) + oValue.substr(oValue.indexOf("."), 3)) : oValue;
				oEvent.getSource().setValue(oValue);
			},

			onDeleteDraft: function (oEvent) {
				var that = this;
				var taskDataFilterModel = this.getView().getModel("taskDataFilterModel");
				var oContextObj = oEvent.getSource().getBindingContext("taskDataFilterModel").getObject();
				var reqId = oContextObj.requestId;
				sap.m.MessageBox.confirm("Are you sure you want to delete - " + reqId, {
					styleClass: "sapUiSizeCompact",
					actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							that.triggerDeleteDraft(reqId);
						}
					}
				});
			},

			triggerDeleteDraft: function (reqId) {
				var that = this;
				var oServiceModel = new sap.ui.model.json.JSONModel();
				var sUrl = "/menabevdev/delete/" + reqId;
				var busy = new sap.m.BusyDialog();
				busy.open();
				var oHeader = {
					"Content-Type": "application/scim+json"
				};
				oServiceModel.loadData(sUrl, "", true, "DELETE", false, false, oHeader);
				oServiceModel.attachRequestCompleted(function (oEvent) {
					busy.close();
					that.getInboxData("", "", "DRAFT", "draftTask", "draftCount");
					sap.m.MessageToast.show(oEvent.getSource().getData().message);
				});
			}
		});
	});