sap.ui.define([
		"sap/m/VBox"
	],
	function (VBox) {
		"use strict";
		var VBoxCC = VBox.extend("com.incture.COM.controls", {
			metadata: {
				properties: {
					"key": ""
				},
				events: {
					"press": {}
				}
			},

			renderer: {}
		});

		VBoxCC.prototype.onclick = function (oEvent) {
			this.firePress();
		};

		return VBoxCC;
	});