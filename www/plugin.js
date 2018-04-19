
var telemetry = require("./telemetry");
var content = require("./content");
var auth = require("./auth");
var event = require("./event");
var downloadService = require("./downloadService");
var profile = require("./profile");
var course = require("./course");
var userProfile = require("./userprofile");
var framework = require("./framework")
var pageAssemble = require("./pageAssemble");
var permission = require("./permission");
var announcement = require("./announcement");
var preferences = require('./preferences');
var genieSdkUtil = require("./genieSdkUtil")

var GenieSDK = {
  telemetry: telemetry,
  content: content,
  auth: auth,
  event: event,
  downloadService: downloadService,
  profile: profile,
  course: course,
  userProfile: userProfile,
  framework: framework,
  pageAssemble: pageAssemble,
  permission: permission,
  announcement: announcement,
  preferences: preferences,
  genieSdkUtil: genieSdkUtil
};


module.exports = GenieSDK;
