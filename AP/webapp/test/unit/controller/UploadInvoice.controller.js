/*global QUnit*/

sap.ui.define([
	"com/menabev/AP/controller/UploadInvoice.controller"
], function (Controller) {
	"use strict";

	QUnit.module("UploadInvoice Controller");

	QUnit.test("I should test the UploadInvoice controller", function (assert) {
		var oAppController = new Controller();
		oAppController.onInit();
		assert.ok(oAppController);
	});

});