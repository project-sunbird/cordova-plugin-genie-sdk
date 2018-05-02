var exec = require('cordova/exec');

var PLUGIN_NAME = 'GenieSDK';

var profile = {

  createProfile: function (requestJson, success, error) {
    exec(success, error, PLUGIN_NAME, this.action(), ["createProfile", requestJson]);
  },

  updateProfile: function (requestJson, success, error) {
    exec(success, error, PLUGIN_NAME, this.action(), ["updateProfile", requestJson]);
  },

  setCurrentUser: function (requestJson, success, error) {
    exec(success, error, PLUGIN_NAME, this.action(), ["setCurrentUser", requestJson]);
  },

  getCurrentUser: function (success, error) {
    exec(success, error, PLUGIN_NAME, this.action(), ["getCurrentUser"]);
  },

  setCurrentProfile: function (isGuestMode,requestJson, success, error) {
    exec(success, error, PLUGIN_NAME, this.action(), ["setCurrentProfile", isGuestMode,requestJson]);
  },

  setAnonymousUser: function (success, error) {
    exec(success, error, PLUGIN_NAME, this.action(), ["setAnonymousUser"]);
  },
  

  action: function () {
    return "profile";
  }

};

module.exports = profile;