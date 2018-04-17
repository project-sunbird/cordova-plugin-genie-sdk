package org.genie;

import org.apache.cordova.CallbackContext;
import org.ekstep.genieservices.GenieService;
import org.ekstep.genieservices.commons.IResponseHandler;
import org.ekstep.genieservices.commons.bean.Framework;
import org.ekstep.genieservices.commons.bean.FrameworkDetailsRequest;
import org.ekstep.genieservices.commons.bean.GenieResponse;
import org.ekstep.genieservices.commons.utils.GsonUtil;
import org.json.JSONArray;
import org.json.JSONException;

public class FrameworkHandler {

    private static final String TYPE_GET_FRAMEWORK_DETAILS = "getFrameworkDetails";

    public static void handle(JSONArray args, final CallbackContext callbackContext) {
        try {
            String type = args.getString(0);
            if (type.equals(TYPE_GET_FRAMEWORK_DETAILS)) {
                getFrameworkDetails(callbackContext);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void getFrameworkDetails(final CallbackContext callbackContext) throws JSONException {
        FrameworkDetailsRequest.Builder frameworkDetailsRequest = new FrameworkDetailsRequest.Builder();
        frameworkDetailsRequest.defaultFrameworkDetails();

        GenieService.getAsyncService().getFrameworkService().getFrameworkDetails(frameworkDetailsRequest.build(), new IResponseHandler<Framework>() {
            @Override
            public void onSuccess(GenieResponse<Framework> genieResponse) {
                callbackContext.success(GsonUtil.toJson(genieResponse));
            }

            @Override
            public void onError(GenieResponse<Framework> genieResponse) {
                callbackContext.error(GsonUtil.toJson(genieResponse));
            }
        });
    }
}
