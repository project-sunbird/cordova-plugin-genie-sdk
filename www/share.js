var exec = require('cordova/exec');

var PLUGIN_NAME = 'GenieSDK';

var share = {
    
  exportEcar: function (contentId, onSuccess, onError) {
    exec(onSuccess, onError, PLUGIN_NAME, this.action(), ["exportEcar", contentId]);
  },

  action: function () {
      return "share";
  }
};

module.exports = share;

