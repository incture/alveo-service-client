/*eslint no-console: 0*/
"use strict";
const express = require("express");
const passport = require("passport");
const xsenv = require("@sap/xsenv");
const JWTStrategy = require("@sap/xssec").JWTStrategy;
const app = express();
const services = xsenv.getServices({
	uaa: "uaa_APWorkbench"
});

passport.use(new JWTStrategy(services.uaa));

app.use(passport.initialize());
app.use(passport.authenticate("JWT", {
	session: false
}));

app.get("/user", function (req, res, next) {
	var user = {
		"userId": req.user.id,
		"name": req.user.name,
		"emails": req.user.emails,
		"scopes": req.authInfo.scopes,
		"identity": req.authInfo.identityZone,
		"AuthInfo": req.authInfo
	};
	return res.type("application/json").status(200).json(user);
});

const port = process.env.PORT || 3000;

app.listen(port, function () {
	console.log("app listening on port " + port);
});

// var http = require("http");
// var port = process.env.PORT || 3000;

// http.createServer(function (req, res) {
//   res.writeHead(200, {"Content-Type": "text/plain"});
//   res.end("Hello World\n");
// }).listen(port);

// console.log("Server listening on port %d", port);