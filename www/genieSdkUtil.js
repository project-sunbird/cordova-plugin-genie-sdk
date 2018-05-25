var exec = require("cordova/exec");

var PLUGIN_NAME = 'GenieSDK';

var genieSdkUtil = {
    getDeviceID: function (success, error) {
        exec(success, error, PLUGIN_NAME, this.action(), ["getDeviceID"]);
    },

    getLocation: function (success, error) {
        exec(success, error, PLUGIN_NAME, this.action(), ["getLocation"]);
    },

    isConnected: function (success, error) {
        exec(success, error, PLUGIN_NAME, this.action(), ["isConnected"]);
    },

    isConnectedOverWifi: function (success, error) {
        exec(success, error, PLUGIN_NAME, this.action(), ["isConnectedOverWifi"]);
    },

    getBuildConfigParam: function (param,success, error) {
        exec(success, error, PLUGIN_NAME, this.action(), ["getBuildConfigParam",param]);
      },

    decode: function (encodedString, flag, success, error) {
        exec(success, error, PLUGIN_NAME, this.action(), ["decode",encodedString,flag]);
      },

    action: function () {
        return "genieSdkUtil";
    }
};

module.exports = genieSdkUtil;