var exec = require("cordova/exec");

var PLUGIN_NAME = 'GenieSDK';

var framework = {
    getFrameworkDetails: function(requestJson, success, error) {
        exec(success, error, PLUGIN_NAME, this.action(), ["getFrameworkDetails", requestJson]);
    }
};

module.exports = framework;