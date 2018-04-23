var exec = require('cordova/exec');

var PLUGIN_NAME = 'GenieSDK';

var share = {
    
  shareContent: function (content, contentType, identifier, type) {
    exec(null, null, PLUGIN_NAME, this.action(), ["content", content, contentType, identifier, type]);
  },

  action: function () {
      return "share";
  }
};

module.exports = share;

