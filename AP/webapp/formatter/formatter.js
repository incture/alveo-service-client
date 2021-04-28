jQuery.sap.declare("com.menabev.AP.formatter.formatter");
com.menabev.AP.formatter.formatter = {

	formatDate: function (date) {
		if (!date) {
			return;
		}
		date = new Date(date);
		var dd = date.getDate();
		if (dd < 10) {
			dd = dd.toString();
			dd = "0" + dd;
		} else {
			dd = dd.toString();
		}
		var mm = date.getMonth() + 1;
		if (mm < 10) {
			mm = mm.toString();
			mm = "0" + mm;
		} else {
			mm = mm.toString();
		}
		var yyyy = date.getFullYear();
		yyyy = yyyy.toString();

		var date1 = dd + "/" + mm + "/" + yyyy;
		return date1;
	},
	
		formatDateTime: function (date) {
		if (!date) {
			return;
		}
		date = new Date(date);
		var dd = date.getDate();
		if (dd < 10) {
			dd = dd.toString();
			dd = "0" + dd;
		} else {
			dd = dd.toString();
		}
		var mm = date.getMonth() + 1;
		if (mm < 10) {
			mm = mm.toString();
			mm = "0" + mm;
		} else {
			mm = mm.toString();
		}
		var yyyy = date.getFullYear();
		yyyy = yyyy.toString();

		var tt = date.getHours();
		if (tt < 10) {
			tt = tt.toString();
			tt = "0" + tt;
		} else {
			tt = tt.toString();
		}

		var min = date.getMinutes();
		if (min < 10) {
			min = min.toString();
			min = "0" + min;
		} else {
			min = min.toString();
		}

		var date1 = dd + "/" + mm + "/" + yyyy + " " + tt + ":" + min;
		return date1;
	},
};