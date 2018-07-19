var exec = require('cordova/exec');

var PLUGIN_NAME = 'GenieSDK';

var group = {

  createGroup: function (requestJson, success, error) {
    exec(success, error, PLUGIN_NAME, this.action(), ["createGroup", requestJson]);
  },

  updateGroup: function (requestJson, success, error) {
    exec(success, error, PLUGIN_NAME, this.action(), ["updateGroup", requestJson]);
  },

  deleteGroup: function (requestJson, success, error) {
    exec(success, error, PLUGIN_NAME, this.action(), ["deleteGroup", requestJson]);
  },

  getAllGroup: function (success, error) {
    exec(success, error, PLUGIN_NAME, this.action(), ["getAllGroup"]);
  },

  setCurrentGroup: function (requestJson, success, error) {
    exec(success, error, PLUGIN_NAME, this.action(), ["setCurrentGroup", requestJson]);
  },

  getCurrentGroup: function (success, error) {
    exec(success, error, PLUGIN_NAME, this.action(), ["getCurrentGroup"]);
  },

  addUpdateProfilesToGroup: function (success, error) {
    exec(success, error, PLUGIN_NAME, this.action(), ["addUpdateProfilesToGroup"]);
  },

  action: function () {
    return "group";
  }

};

module.exports = group;