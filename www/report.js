var exec = require('cordova/exec');

var PLUGIN_NAME = 'GenieSDK';

var report = {

    getListOfReports: function (uids, success, error) {
        exec(success, error, PLUGIN_NAME, this.action(), ["getListOfReports", uids]);
    },

    getDetailReport: function (uids, contentId, success, error) {
        exec(success, error, PLUGIN_NAME, this.action(), ["getDetailReport", uids, contentId]);
    },

    action: function () {
        return "report";
    }

};

module.exports = report;

