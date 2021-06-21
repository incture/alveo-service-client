sap.ui.define([
		"com/menabev/AP/controller/BaseController",
		"com/menabev/AP/formatter/formatter",
	],
	/**
	 * @param {typeof sap.ui.core.mvc.Controller} Controller
	 */
	function (BaseController, formatter) {
		"use strict";

		return BaseController.extend("com.menabev.AP.controller.Inbox", {
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
				var oDataAPIModel = this.getOwnerComponent().getModel("oDataAPIModel");
				this.oDataAPIModel = oDataAPIModel;
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
						oTaskInboxModel.setProperty("/filterParams", {});
						oTaskInboxModel.setProperty("/selectedFilterTab", "openTask");
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
					this.getInboxData(selectedPage);
				} else if (selectedTabKey === "myTask") {
					oPaginationModel.setProperty("/myTaskPagination/currentPage", selectedPage);
					this.getInboxData(selectedPage);
				} else if (selectedTabKey === "draft") {
					oPaginationModel.setProperty("/draftedTaskPagination/currentPage", selectedPage);
					this.getInboxData(selectedPage);
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
					this.getInboxData(selectedPage);
				} else if (selectedTabKey === "MY_TASK") {
					oPaginationModel.setProperty("/myTaskPagination/currentPage", selectedPage);
					this.getInboxData(selectedPage);
				} else {
					oPaginationModel.setProperty("/draftedTaskPagination/currentPage", selectedPage);
					this.getInboxData(selectedPage);
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
					this.getInboxData(selectedPage);
				} else if (selectedTabKey === "MY_TASK") {
					oPaginationModel.setProperty("/myTaskPagination/currentPage", selectedPage);
					this.getInboxData(selectedPage);
				} else {
					oPaginationModel.setProperty("/draftedTaskPagination/currentPage", selectedPage);
					this.getInboxData(selectedPage);
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

			getInboxData: function (pageNo) {
				var loggedinUserGroup = this.oUserDetailModel.getProperty("/loggedinUserGroup"),
					oVisibilityModel = this.oVisibilityModel;
				oVisibilityModel.setProperty("/Inbox", {});
				var oTaskInboxModel = this.oTaskInboxModel,
					sUrl = "/menabevdev/invoiceHeader/inbox/tasks",
					that = this;
				var oPayload = jQuery.extend(true, {}, oTaskInboxModel.getProperty("/filterParams"));
				if (oPayload.invoiceDateFrom) {
					oPayload.invoiceDateFrom = new Date(oPayload.invoiceDateFrom).getTime();
				}
				if (oPayload.invoiceDateTo) {
					oPayload.invoiceDateTo = new Date(oPayload.invoiceDateTo).getTime();
				}
				if (oPayload.dueDateFrom) {
					oPayload.dueDateFrom = new Date(oPayload.dueDateFrom).getTime();
				}
				if (oPayload.dueDateTo) {
					oPayload.dueDateTo = new Date(oPayload.dueDateTo).getTime();
				}
				var skip = (pageNo - 1) * 10;
				var assignedTo = oTaskInboxModel.getProperty("/filterParams/assignedTo");
				if (!assignedTo) {
					assignedTo = [];
					assignedTo.push(this.oUserDetailModel.getProperty("/loggedInUserMail"));
				}
				var vendor = oPayload.vendorId;
				var vendorId = [];
				if (vendor) {
					vendorId.push(vendor);
				}
				oPayload.assignedTo = assignedTo;
				oPayload.skip = skip;
				oPayload.top = 2;
				oPayload.roleOfUser = this.oUserDetailModel.getProperty("/loggedinUserGroup");
				var oServiceModel = new sap.ui.model.json.JSONModel();
				var oHeader = {
					"Content-Type": "application/scim+json"
				};
				oServiceModel.loadData(sUrl, JSON.stringify(oPayload), true, "POST", false, false, oHeader);
				oServiceModel.attachRequestCompleted(function (oEvent) {
					var data = oEvent.getSource().getData();
					var arr = [];
					if (data.message == "SUCCESS") {
						if (!data.totalCount) {
							data.totalCount = 0;
						}
						if (!data.draftCount) {
							data.draftCount = 0;
						}
						oTaskInboxModel.setProperty("/openTaskCount", data.totalCount);
						// oTaskInboxModel.setProperty("/myTaskCount", data.body.countMyTask);
						oTaskInboxModel.setProperty("/draftCount", data.countDraft);
						oTaskInboxModel.setProperty("/openTask", {});
						oTaskInboxModel.setProperty("/draftTask", {});
						if (data.taskList) {
							oTaskInboxModel.setProperty("/openTask", data.taskList);
						}
						if (data.draftList) {
							oTaskInboxModel.setProperty("/draftTask", data.draftList);
						}
						that.pagination(data.totalCount, "openTaskPagination", 1);
						// that.pagination(data.body.countMyTask, "myTaskPagination", 1);
						that.pagination(data.draftCount, "draftedTaskPagination", 1);
					} else {
						sap.m.MessageBox.information(data.message, {
							styleClass: "sapUiSizeCompact",
							actions: [sap.m.MessageBox.Action.OK]
						});
						oTaskInboxModel.setProperty("/openTaskCount", 0);
						oTaskInboxModel.setProperty("/draftCount", 0);
						oTaskInboxModel.setProperty("/openTask", {});
						oTaskInboxModel.setProperty("/draftTask", {});

					}

				});

			},

			onSearch: function () {
				var oTaskInboxModel = this.oTaskInboxModel,
					taskDataFilterModelData = oTaskInboxModel.getProperty("/filterParams"),
					bError = false;
				if ((!taskDataFilterModelData.invoiceDateFrom && taskDataFilterModelData.invoiceDateTo) || (taskDataFilterModelData.invoiceDateFrom &&
						!taskDataFilterModelData.invoiceDateTo)) {
					sap.m.MessageToast.show("Fill both invoice From Date and to date to proceed");
					bError = true;
				}
				if ((!taskDataFilterModelData.dueDateFrom && taskDataFilterModelData.dueDateTo) || (taskDataFilterModelData.dueDateFrom &&
						!taskDataFilterModelData.dueDateTo)) {
					sap.m.MessageToast.show("Fill both due From Date and to date to proceed");
					bError = true;
				}
				if ((!taskDataFilterModelData.invoiceTotalFrom && taskDataFilterModelData.invoiceTotalTo) || (taskDataFilterModelData.invoiceTotalFrom &&
						taskDataFilterModelData.invoiceTotalTo)) {
					sap.m.MessageToast.show("Fill both invoice From value and to value to proceed");
					bError = true;
				}
				if (!taskDataFilterModelData.assignedTo.length) {
					sap.m.MessageToast.show("Please select atleast one assigned to user");
					bError = true;
				}
				// if (!oTaskInboxModel.getProperty("/filterParams/requestId") &&
				// 	!oTaskInboxModel.getProperty("/filterParams/invoiceTotalFrom") &&
				// 	!oTaskInboxModel.getProperty("/invoiceTotalTo") &&
				// 	!oTaskInboxModel.getProperty("/dueDateFrom") &&
				// 	!oTaskInboxModel.getProperty("/dueDateTo") &&
				// 	!oTaskInboxModel.getProperty("/invoiceDateFrom") &&
				// 	!oTaskInboxModel.getProperty("/invoiceDateTo") &&
				// 	!oTaskInboxModel.getProperty("/extInvNum") &&
				// 	!oTaskInboxModel.getProperty("/assignedTo") &&
				// 	!oTaskInboxModel.getProperty("/vendorId") &&
				// 	!oTaskInboxModel.getProperty("/invoiceType") &&
				// 	!oTaskInboxModel.getProperty("/validationStatus")
				// ) {
				// 	sap.m.MessageToast.show("Enter Atleast one search parameter to search");
				// 	bError = true;
				// }
				if (!bError)
					this.getInboxData(1);
			},

			vendorIdSelected: function (oEvent, oController) {
				var oTaskInboxModel = this.oTaskInboxModel;
				var sVendorId = oEvent.getParameter("selectedItem").getProperty("text"),
					sVendorName = oEvent.getParameter("selectedItem").getProperty("additionalText");
				oTaskInboxModel.setProperty("/filterParams/vendorName", sVendorName);
				oTaskInboxModel.setProperty("/filterParams/vendorId", sVendorId);
				oTaskInboxModel.refresh();
				// this.onHeaderChange(oEvent, oController, "isVendoIdChanged");
			},

			onSelectTab: function (oEvent) {
				var oTaskInboxModel = this.oTaskInboxModel;
				var key = oTaskInboxModel.getProperty("/selectedFilterTab");
				// if (key == "openTask") {
				this.getInboxData(1);
				// } else if (key == "myTask") {
				// 	this.getInboxData(1);
				// } else if (key == "draft") {
				// 	this.getInboxData(1);
				// }
			},

			SearchInboxData: function (oEvent) {
				this.getInboxData(1);
			},

			clearFilter: function () {
				var assignedTo = [];
				assignedTo.push(this.oUserDetailModel.getProperty("/loggedInUserMail"));
				var oTaskInboxModel = this.oTaskInboxModel;
				oTaskInboxModel.setProperty("/filterParams", {});
				oTaskInboxModel.setProperty("/filterParams/validationStatus", []);
				oTaskInboxModel.setProperty("/filterParams/assignedTo", assignedTo);
				oTaskInboxModel.setProperty("/filterParams/vendorId", []);
				oTaskInboxModel.setProperty("/filterParams/invoiceType", ["PO"]);
				oTaskInboxModel.setProperty("/filterParams/taskStatus", ["READY", "RESERVED"]);
				this.getInboxData(1);
			},

			onClaimRelease: function (oEvent) {
				var that = this;
				var oTaskInboxModel = this.oTaskInboxModel;
				var oContextObj = oEvent.getSource().getBindingContext("oTaskInboxModel").getObject();
				var sPath = oEvent.getSource().getBindingContext("oTaskInboxModel").getPath();
				var obj = {
					"userId": this.oUserDetailModel.getProperty("/loggedInUserMail"),
					"invoice": oContextObj
				};
				if (oEvent.getSource().getTooltip() == "claim") {
					obj.taskID = oContextObj.taskId;
				}
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
					var data = oEvent.getSource().getData();
					oTaskInboxModel.setProperty(sPath, data.body);
					oTaskInboxModel.refresh();
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
				var data = oEvent.getSource().getValue();
				if (!data) {
					oEvent.getSource().setDateValue(null);
				}
				var dateVal = new Date(data);
				var day = dateVal.getDate();
				if (!day) {
					sap.m.MessageToast.show("Please enter valid date");
					oEvent.getSource().setValue("");
					return;
				} else {
					oEvent.getSource().setDateValue(dateVal);
				}
				var filterData = this.oTaskInboxModel.getData().filterParams;
				var invDateFrom, invDateTo, dueDateFrom, dueDateTo;
				invDateFrom = filterData.invoiceDateFrom;
				invDateTo = filterData.invoiceDateTo;
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
				oValue = oValue.toFixed(2);
				if (oValue > 99999999.99) {
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
				var sUrl = "/menabevdev/invoiceHeader/delete";
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
					that.getInboxData(1);
					sap.m.MessageToast.show(oEvent.getSource().getData().message);
				});
			}
		});
	});