package org.genie;

import org.apache.cordova.CallbackContext;
import org.ekstep.genieservices.GenieService;
import org.ekstep.genieservices.utils.BuildConfigUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.sunbird.app.BuildConfig;

public class GenieSdkUtilHandler {

    private static final String TYPE_GET_DEVICE_ID = "getDeviceID";
    private static final String TYPE_GET_LOCATION = "getLocation";
    private static final String TYPE_IS_CONNECTED = "isConnected";
    private static final String TYPE_IS_CONNECTED_OVER_WIFI = "isConnectedOverWifi";
    private static final String TYPE_GET_BUILD_CONFIG_PARAM = "getBuildConfigParam";

    public static void handle(JSONArray args, final CallbackContext callbackContext) {
        try {
            String type = args.getString(0);

            if (type.equals(TYPE_GET_DEVICE_ID)) {
                getDeviceID(callbackContext);
            } else if (type.equals(TYPE_GET_LOCATION)) {
                getLocation(callbackContext);
            } else if (type.equals(TYPE_IS_CONNECTED)) {
                isConnected(callbackContext);
            } else if (type.equals(TYPE_IS_CONNECTED_OVER_WIFI)) {
                isConnectedOverWifi(callbackContext);
            } else if (type.equals(TYPE_GET_BUILD_CONFIG_PARAM)) {
                getBuildConfigParam(args, callbackContext);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void getDeviceID(CallbackContext callbackContext) {
        String did = GenieService.getService().getDeviceInfo().getDeviceID();
        callbackContext.success(did);
    }

    private static void getLocation(CallbackContext callbackContext) {
        String location = GenieService.getService().getLocationInfo().getLocation();
        callbackContext.success(location);
    }

    private static void isConnected(CallbackContext callbackContext) {
        boolean isConnected = GenieService.getService().getConnectionInfo().isConnected();
        callbackContext.success(Boolean.toString(isConnected));
    }

    private static void isConnectedOverWifi(CallbackContext callbackContext) {
        boolean isConnectedOverWifi = GenieService.getService().getConnectionInfo().isConnectedOverWifi();
        callbackContext.success(Boolean.toString(isConnectedOverWifi));
    }

    private static void getBuildConfigParam(JSONArray args, CallbackContext callbackContext) throws JSONException {
        String param = args.getString(1);
        String value = BuildConfigUtil.getBuildConfigValue(BuildConfig.APPLICATION_ID, param).toString();
        callbackContext.success(value);
    }
}
