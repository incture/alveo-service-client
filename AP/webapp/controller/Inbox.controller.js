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
				var oTaskInboxModel = this.getOwnerComponent().getModel("oTaskInboxModel");
				this.oTaskInboxModel = oTaskInboxModel;
				var oPaginationModel = this.getOwnerComponent().getModel("oPaginationModel");
				this.oPaginationModel = oPaginationModel;
				var oVisibilityModel = this.getOwnerComponent().getModel("oVisibilityModel");
				this.oVisibilityModel = oVisibilityModel;
				var oDropDownModel = this.getOwnerComponent().getModel("oDropDownModel");
				this.oDropDownModel = oDropDownModel;

				// var paginatedModel = new sap.ui.model.json.JSONModel();
				// this.getView().setModel(paginatedModel, "paginatedModel");
				// var taskDataFilterModel = new sap.ui.model.json.JSONModel();
				// this.getView().setModel(taskDataFilterModel, "taskDataFilterModel");
				// this.myTask = false;

				var oUserDetailModel = this.getOwnerComponent().getModel("oUserDetailModel");
				this.oUserDetailModel = oUserDetailModel;
				this.setFilterBar();
				this.getProcessStatus();
				this.oRouter = sap.ui.core.UIComponent.getRouterFor(this);
				this.oRouter.attachRoutePatternMatched(function (oEvent) {
					if (oEvent.getParameter("name") === "Inbox") {
						oPaginationModel.setProperty("/openTaskPagination", {});
						oPaginationModel.setProperty("/myTaskPagination", {});
						oPaginationModel.setProperty("/draftedTaskPagination", {});
						oPaginationModel.setProperty("/pagination", {});
						oPaginationModel.setProperty("/openTaskPagination/paginationVisible", false);
						oPaginationModel.setProperty("/myTaskPagination/paginationVisible", false);
						oPaginationModel.setProperty("/draftedTaskPagination/paginationVisible", false);
						oPaginationModel.setProperty("/pagination/paginationVisible", false);
						oTaskInboxModel.getProperty("/selectedFilterTab", "openTask");
						oTaskInboxModel.setProperty("/openTask", {});
						oTaskInboxModel.setProperty("/myTask", {});
						oTaskInboxModel.setProperty("/draftTask", {});
						that.clearFilter();
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

			getProcessStatus: function () {
				var oDropDownModel = this.oDropDownModel;
				var oServiceModel = new sap.ui.model.json.JSONModel();
				var sUrl = "/menabevdev/codesAndtexts/get/uuId=/statusCode=/type=/language=";
				var oHeader = {
					"Content-Type": "application/scim+json"
				};
				oServiceModel.loadData(sUrl, "", true, "GET", false, false, oHeader);
				oServiceModel.attachRequestCompleted(function (oEvent) {
					var data = oEvent.getSource().getData();
					oDropDownModel.setProperty("/validationStatus", data);
				});
			},

			pagination: function (count, objectName, currentPage) {
				var oPaginationModel = this.oPaginationModel;
				var obj = oPaginationModel.getProperty("/" + objectName);
				if (!obj) {
					oPaginationModel.setProperty("/" + objectName, {});
				}

				if (!count) {
					oPaginationModel.setProperty("/" + objectName + "/paginationVisible", false);
					oPaginationModel.setProperty("/" + objectName + "/totalTaskCount", 0);
				} else {
					oPaginationModel.setProperty("/" + objectName + "/totalTaskCount", count);
					var pages = parseInt(count / 6);
					if (pages < 1) {
						oPaginationModel.setProperty("/" + objectName + "/paginationVisible", false);
						return;
					} else {
						oPaginationModel.setProperty("/" + objectName + "/paginationVisible", true);
					}
					var remainder = count % 6;
					if (remainder) {
						pages = pages + 1;
					}
					var pagesNew;

					oPaginationModel.setProperty("/" + objectName + "/pageNextVisible", true);
					if (pages > 5) {
						pagesNew = 5;
					} else {
						pagesNew = pages;
					}
					var a = [],
						initialPage = 0;
					if (currentPage > 5) {
						pagesNew = currentPage;
						initialPage = pagesNew - 5;
					}
					for (var i = initialPage; i < pagesNew; i++) {
						var page = {
							"page": i + 1
						};
						a.push(page);
					}
					oPaginationModel.setProperty("/" + objectName + "/pages", a);
					oPaginationModel.setProperty("/" + objectName + "/currentPage", currentPage);
					oPaginationModel.setProperty("/" + objectName + "/TotalPages", pages);
					var oTaskInboxModel = this.oTaskInboxModel;
					oPaginationModel.refresh();
				}
				// if(oPaginationModel.openTaskPagination&&oPaginationModel.myTaskPagination&&oPaginationModel.draftedTaskPagination){
				this.onTabSelectPagination();
				// }
			},
			onTabSelectPagination: function (oEvent) {
				// var selectedtext = oEvent.getSource().getText();
				var oTaskInboxModel = this.oTaskInboxModel;
				var selectedTabKey = oTaskInboxModel.getProperty("/selectedFilterTab");
				if (selectedTabKey === "openTask") {
					this.setPagination("openTaskPagination");
				} else if (selectedTabKey === "myTask") {
					this.setPagination("myTaskPagination");
				} else if (selectedTabKey === "draft") {
					this.setPagination("draftedTaskPagination");
				}
			},

			setPagination: function (selectedObj) {
				var oPaginationModel = this.oPaginationModel;
				var selectedObject = oPaginationModel.getProperty("/" + selectedObj);
				oPaginationModel.setProperty("/pagination", selectedObject);
				oPaginationModel.refresh();
			},

			onPagesClick: function (oEvent) {
				var selectedPage = oEvent.getSource().getText();
				var oTaskInboxModel = this.oTaskInboxModel;
				var oPaginationModel = this.oPaginationModel;
				var selectedTabKey = oTaskInboxModel.getProperty("/selectedFilterTab");
				if (selectedTabKey === "openTask") {
					oPaginationModel.setProperty("/openTaskPagination/currentPage", selectedPage);
					this.getInboxData(selectedPage, "", "OPEN ", "openTask", "openTaskCount", "openTaskPagination");
				} else if (selectedTabKey === "myTask") {
					oPaginationModel.setProperty("/myTaskPagination/currentPage", selectedPage);
					this.getInboxData(selectedPage, "", "MYTASK", "myTask", "myTaskCount", "myTaskPagination");
				} else if (selectedTabKey === "draft") {
					oPaginationModel.setProperty("/draftedTaskPagination/currentPage", selectedPage);
					this.getInboxData(selectedPage, "", "DRAFT", "draftTask", "draftCount", "draftedTaskPagination");
				}
			},

			onNextButtonClick: function (oEvent) {
				var oTaskInboxModel = this.oTaskInboxModel;
				var oPaginationModel = this.oPaginationModel;
				var currentPage, selectedPage;
				var selectedTabKey = oTaskInboxModel.getProperty("/selectedFilterTab");
				currentPage = oPaginationModel.getProperty("/pagination/currentPage");
				selectedPage = parseInt(currentPage) + 1;
				oPaginationModel.setProperty("/pagination/currentPage", selectedPage);
				if (selectedTabKey === "GROUP_TASK") {
					oPaginationModel.setProperty("/openTaskPagination/currentPage", selectedPage);
					this.getAllGroupTasks(selectedPage);
				} else if (selectedTabKey === "MY_TASK") {
					oPaginationModel.setProperty("/myTaskPagination/currentPage", selectedPage);
					this.getAllMyTasks(selectedPage);
				} else {
					oPaginationModel.setProperty("/draftedTaskPagination/currentPage", selectedPage);
					this.getAllTasks(selectedPage);
				}
			},

			onPrevButtonClick: function (oEvent) {
				var oTaskInboxModel = this.oTaskInboxModel;
				var oPaginationModel = this.oPaginationModel;
				var currentPage, selectedPage;
				var selectedTabKey = oTaskInboxModel.getProperty("/selectedFilterTab");
				currentPage = oPaginationModel.getProperty("/pagination/currentPage");
				selectedPage = parseInt(currentPage) - 1;
				oPaginationModel.setProperty("/pagination/currentPage", selectedPage);
				if (selectedTabKey === "GROUP_TASK") {
					oPaginationModel.setProperty("/openTaskPagination/currentPage", selectedPage);
					this.getAllGroupTasks(selectedPage);
				} else if (selectedTabKey === "MY_TASK") {
					oPaginationModel.setProperty("/myTaskPagination/currentPage", selectedPage);
					this.getAllMyTasks(selectedPage);
				} else {
					oPaginationModel.setProperty("/draftedTaskPagination/currentPage", selectedPage);
					this.getAllTasks(selectedPage);
				}
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

			getInboxData: function (pageNo, scroll, taskStatus, object, count, paginationObject) {
				var loggedinUserGroup = this.oUserDetailModel.getProperty("/loggedinUserGroup"),
					oVisibilityModel = this.oVisibilityModel;
				oVisibilityModel.setProperty("/Inbox", {});
				var oTaskInboxModel = this.oTaskInboxModel,
					sUrl = "/menabevdev/invoiceHeader/inboxMultiple",
					that = this;
				var oPayload = jQuery.extend(true, {}, oTaskInboxModel.getProperty("/filterParams"));
				if (oPayload.createdAtFrom) {
					oPayload.createdAtFrom = new Date(oPayload.createdAtFrom).getTime();
				}
				if (oPayload.createdAtTo) {
					oPayload.createdAtTo = new Date(oPayload.createdAtTo).getTime();
				}
				if (oPayload.dueDateFrom) {
					oPayload.dueDateFrom = new Date(oPayload.dueDateFrom).getTime();
				}
				if (oPayload.dueDateTo) {
					oPayload.dueDateTo = new Date(oPayload.dueDateTo).getTime();
				}
				oPayload.userId = this.oUserDetailModel.getProperty("/loggedInUserMail");
				oPayload.indexNum = pageNo;
				oPayload.count = 10;
				oPayload.myTask = taskStatus;
				oPayload.roleOfUser = this.oUserDetailModel.getProperty("/loggedinUserGroup");
				var oServiceModel = new sap.ui.model.json.JSONModel();
				var oHeader = {
					"Content-Type": "application/scim+json"
				};
				oServiceModel.loadData(sUrl, JSON.stringify(oPayload), true, "POST", false, false, oHeader);
				oServiceModel.attachRequestCompleted(function (oEvent) {
					var data = oEvent.getSource().getData();
					var arr = [];
					if (data.body.count) {
						oTaskInboxModel.setProperty("/" + count, data.body.count);
						oTaskInboxModel.setProperty("/" + object, data.body.listOfTasks);
						that.pagination(data.body.count, paginationObject, pageNo);
					} else {

					}
				});

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
					this.getInboxData(1, "", "OPEN ", "openTask", "openTaskCount", "openTaskPagination");
				} else if (key == "myTask") {
					this.getInboxData(1, "", "MYTASK", "myTask", "myTaskCount", "myTaskPagination");
				} else if (key == "draft") {
					this.getInboxData(1, "", "DRAFT", "draftTask", "draftCount", "draftedTaskPagination");
				}
			},

			SearchInboxData: function (oEvent) {
				this.getInboxData(1, "", "OPEN ", "openTask", "openTaskCount", "openTaskPagination");
				this.getInboxData(1, "", "MYTASK", "myTask", "myTaskCount", "myTaskPagination");
				this.getInboxData(1, "", "DRAFT", "draftTask", "draftCount", "draftedTaskPagination");
			},
			clearFilter: function () {
				var oTaskInboxModel = this.oTaskInboxModel;
				oTaskInboxModel.setProperty("/filterParams", {});
				oTaskInboxModel.setProperty("/filterParams/validationStatus", []);
				oTaskInboxModel.setProperty("/filterParams/assignedTo", []);
				oTaskInboxModel.setProperty("/filterParams/vendorId", []);
				oTaskInboxModel.setProperty("/filterParams/invoiceType", []);
				this.getInboxData(1, "", "OPEN ", "openTask", "openTaskCount", "openTaskPagination");
				this.getInboxData(1, "", "MYTASK", "myTask", "myTaskCount", "myTaskPagination");
				this.getInboxData(1, "", "DRAFT", "draftTask", "draftCount", "draftedTaskPagination");
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
								this.getInboxData("", "", "OPEN ", "openTask", "openTaskCount", "openTaskPagination");
								this.getInboxData("", "", "MYTASK", "myTask", "myTaskCount", "myTaskPagination");
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