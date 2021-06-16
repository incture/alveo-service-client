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
				var StaticDataModel = this.getOwnerComponent().getModel("StaticDataModel");
				this.StaticDataModel = StaticDataModel;
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
						oTaskInboxModel.getProperty("/filterParams", {});
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
				var oTaskInboxModel = this.oTaskInboxModel;
				var oServiceModel = new sap.ui.model.json.JSONModel();
				var oUserDetailModel = this.oUserDetailModel;
				var sUrl = "/menabevdev/codesAndtexts/get/uuId=/statusCode=/type=/language=";
				var userGroup = oUserDetailModel.getProperty("/loggedinUserGroup");
				var userMail = oUserDetailModel.getProperty("/loggedInUserMail");
				var oHeader = {
					"Content-Type": "application/scim+json"
				};
				var obj = [{
					"userId": userMail,
					"role": userGroup
				}];
				oServiceModel.loadData(sUrl, "", true, "GET", false, false, oHeader);
				oServiceModel.attachRequestCompleted(function (oEvent) {
					var data = oEvent.getSource().getData();
					oDropDownModel.setProperty("/validationStatus", data);
					if (userGroup != "IT_Admin") {
						oDropDownModel.setProperty("/taskGroupUsers", obj);
						oTaskInboxModel.setProperty("/filterParams/assignedTo", [userMail]);
					}
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
					var pages = parseInt(count / 2);
					if (pages < 1) {
						oPaginationModel.setProperty("/" + objectName + "/paginationVisible", false);
						return;
					} else {
						oPaginationModel.setProperty("/" + objectName + "/paginationVisible", true);
					}
					var remainder = count % 2;
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
				oPayload.userId = oTaskInboxModel.getProperty("/filterParams/assignedTo");
				oPayload.indexNum = pageNo;
				oPayload.pageCount = 2;
				oPayload.tab = taskStatus;
				oPayload.roleOfUser = this.oUserDetailModel.getProperty("/loggedinUserGroup");
				var oServiceModel = new sap.ui.model.json.JSONModel();
				var oHeader = {
					"Content-Type": "application/scim+json"
				};
				oServiceModel.loadData(sUrl, JSON.stringify(oPayload), true, "POST", false, false, oHeader);
				oServiceModel.attachRequestCompleted(function (oEvent) {
					var data = oEvent.getSource().getData();
					var arr = [];
					if (data.body) {
						if (!data.body.countOpenTask) {
							data.body.countOpenTask = 0;
						}
						if (!data.body.countDraft) {
							data.body.countDraft = 0;
						}
						if (!data.body.countMyTask) {
							data.body.countMyTask = 0;
						}
						oTaskInboxModel.setProperty("/openTaskCount", data.body.countOpenTask);
						oTaskInboxModel.setProperty("/myTaskCount", data.body.countMyTask);
						oTaskInboxModel.setProperty("/draftCount", data.body.countDraft);
						oTaskInboxModel.setProperty("/" + object, {});
						if (data.body.listOfTasks) {
							oTaskInboxModel.setProperty("/" + object, data.body.listOfTasks);
						}
						that.pagination(data.body.countOpenTask, "openTaskPagination", 1);
						that.pagination(data.body.countMyTask, "myTaskPagination", 1);
						that.pagination(data.body.countDraft, "draftedTaskPagination", 1);
					} else {
						oTaskInboxModel.setProperty("/openTaskCount", 0);
						oTaskInboxModel.setProperty("/myTaskCount", 0);
						oTaskInboxModel.setProperty("/draftCount", 0);
						oTaskInboxModel.setProperty("/" + object, {});
						
					}

				});

			},
			onSearch: function () {
				var oTaskInboxModel = this.oTaskInboxModel,
					taskDataFilterModelData = oTaskInboxModel.getProperty("/filterParams"),
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
				if (!oTaskInboxModel.getProperty("/requestId") &&
					!oTaskInboxModel.getProperty("/invoiceTotalFrom") &&
					!oTaskInboxModel.getProperty("/invoiceTotalTo") &&
					!oTaskInboxModel.getProperty("/dueDateFrom") &&
					!oTaskInboxModel.getProperty("/dueDateTo") &&
					!oTaskInboxModel.getProperty("/createdAtFrom") &&
					!oTaskInboxModel.getProperty("/createdAtTo") &&
					!oTaskInboxModel.getProperty("/extInvNum") &&
					!oTaskInboxModel.getProperty("/assignedTo") &&
					!oTaskInboxModel.getProperty("/vendorId") &&
					!oTaskInboxModel.getProperty("/invoiceType") &&
					!oTaskInboxModel.getProperty("/validationStatus")
				) {
					sap.m.MessageToast.show("Enter Atleast one search parameter to search");
					bError = true;
				}
				if (!bError)
					this.getInboxData();
			},
			onSelectTab: function (oEvent) {
				var oTaskInboxModel = this.oTaskInboxModel;
				var key = oTaskInboxModel.getProperty("/selectedFilterTab");
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
					"taskID": oEvent.getSource().getBindingContext("oTaskInboxModel").getObject().taskId,
					"userId": this.oUserDetailModel.getProperty("/loggedInUserMail")
				};
				if (oEvent.getSource().getTooltip() == "release")
					obj.claim = false;
				var oServiceModel = new sap.ui.model.json.JSONModel();
				var sUrl = "/menabevdev/invoiceHeader/claimOrRelease";
				var busy = new sap.m.BusyDialog();
				busy.open();
				var oHeader = {
					"Content-Type": "application/scim+json"
				};
				oServiceModel.loadData(sUrl, JSON.stringify(obj), true, "POST", false, false, oHeader);
				oServiceModel.attachRequestCompleted(function (oEvent) {
					busy.close();
					that.getInboxData(1, "", "OPEN ", "openTask", "openTaskCount", "openTaskPagination");
					that.getInboxData(1, "", "MYTASK", "myTask", "myTaskCount", "myTaskPagination");
				});

				// jQuery
				// 	.ajax({
				// 		url: "/menabevdev/invoiceHeader/claimOrRelease",
				// 		dataType: "json",
				// 		data: JSON.stringify(obj),
				// 		contentType: "application/json",
				// 		type: "POST",
				// 		success: function () {},
				// 		error: function (oError) {
				// 			if (oError.status == "201") {
				// 				sap.m.MessageToast.show(oError.responseText);

				// 			} else {
				// 				sap.m.MessageToast.show(oError.responseText);
				// 			}
				// 		}
				// 	});

			},
			onDateRangeChange: function (oEvent) {
				var filterData = this.oTaskInboxModel.getData().filterParams;
				var invDateFrom, invDateTo, dueDateFrom, dueDateTo;
				invDateFrom = filterData.createdAtFrom;
				invDateTo = filterData.createdAtTo;
				dueDateFrom = filterData.dueDateFrom;
				dueDateTo = filterData.dueDateTo;
				if (invDateFrom && invDateTo) {
					if (invDateFrom > invDateTo) {
						sap.m.MessageToast.show("Invoice From Date cannot be greater than Invoice To Date");
						if (oEvent) {
							oEvent.getSource().setValue();
						}
						return false;
					}
				}
				if (dueDateFrom && dueDateTo) {
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
				var filterData = this.oTaskInboxModel.getData().filterParams;
				var invValFrom = filterData.invoiceValueFrom,
					invValTo = filterData.invoiceValueTo;
				var oValue = oEvent.getSource().getValue();
				oValue = parseFloat(oValue);
				return oValue.toFixed(2);
				if(oValue>"99999999.99"){
					sap.m.MessageToast.show("Invoice value cannot be greater 99999999.99");
				}
				// oValue = (oValue.indexOf(".") >= 0) ? (oValue.substr(0, oValue.indexOf(".")) + oValue.substr(oValue.indexOf("."), 3)) : oValue;
				oEvent.getSource().setValue(oValue);
				if (invValFrom > invValTo) {
					sap.m.MessageToast.show("Invoice from value cannot be greater than Invoice To value");
					oEvent.getSource().setValue();
				}
			},

			onDeleteDraft: function (oEvent) {
				var that = this;
				var table = oEvent.getSource().getParent().getParent();
				var selectedItems = table.getSelectedContexts();
				var oTaskInboxModel = this.oTaskInboxModel;
				var requestId = [],
					req;
				for (var i = 0; i < selectedItems.length; i++) {
					req = oTaskInboxModel.getProperty(selectedItems[i] + "/requestId");
					requestId.push(req);
				}
				table.removeSelections();
				sap.m.MessageBox.confirm("Are you sure you want to delete selected Items?", {
					styleClass: "sapUiSizeCompact",
					actions: [sap.m.MessageBox.Action.YES, sap.m.MessageBox.Action.NO],
					onClose: function (oAction) {
						if (oAction === "YES") {
							that.triggerDeleteDraft(requestId);
						}
					}
				});
			},

			triggerDeleteDraft: function (reqId) {
				var that = this;
				var oServiceModel = new sap.ui.model.json.JSONModel();
				var sUrl = "/menabevdev/delete/";
				var busy = new sap.m.BusyDialog();
				busy.open();
				var oHeader = {
					"Content-Type": "application/scim+json"
				};
				var oPayload = {
					"requestId": reqId
				};
				oServiceModel.loadData(sUrl, JSON.stringify(oPayload), true, "DELETE", false, false, oHeader);
				oServiceModel.attachRequestCompleted(function (oEvent) {
					busy.close();
					that.getInboxData(1, "", "DRAFT", "draftTask", "draftCount");
					sap.m.MessageToast.show(oEvent.getSource().getData().message);
				});
			}
		});
	});