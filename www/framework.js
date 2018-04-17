var exec = require("cordova/exec");

var PLUGIN_NAME = 'GenieSDK';

var framework = {
    getFrameworkDetails: function(requestJson, success, error) {
        exec(success, error, PLUGIN_NAME, this.action(), ["getFrameworkDetails", requestJson]);
    },

    getBoards: function(requestJson, success, error) {
        exec(success, error, PLUGIN_NAME, this.action(), ["getBoards", requestJson]);
    }
};

module.exports = framework;