function initModel() {
	var sUrl = "/sap/opu/odata/sap/ZP2P_API_EC_GL_SRV/";
	var oModel = new sap.ui.model.odata.ODataModel(sUrl, true);
	sap.ui.getCore().setModel(oModel);
}