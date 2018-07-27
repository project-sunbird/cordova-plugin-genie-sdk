package org.genie;

import org.apache.cordova.CallbackContext;
import org.ekstep.genieservices.GenieService;
import org.ekstep.genieservices.commons.IResponseHandler;
import org.ekstep.genieservices.commons.bean.Framework;
import org.ekstep.genieservices.commons.bean.FrameworkDetailsRequest;
import org.ekstep.genieservices.commons.bean.GenieResponse;
import org.ekstep.genieservices.commons.utils.GsonUtil;
import org.ekstep.genieservices.commons.utils.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FrameworkHandler {

    private static final String TYPE_GET_FRAMEWORK_DETAILS = "getFrameworkDetails";
    private static final String TYPE_PERSIST_FRAMEWORK_DETAILS = "persistFrameworkDetails";
    private static final String TYPE_GET_CATEGORY_DATA = "getCategoryData";

    public static void handle(JSONArray args, final CallbackContext callbackContext) {
        try {
            String type = args.getString(0);

            if (type.equals(TYPE_GET_FRAMEWORK_DETAILS)) {
                getFrameworkDetails(args, callbackContext);
            } else if (TYPE_PERSIST_FRAMEWORK_DETAILS.equals(type)) {
                persistFrameworkDetails(args, callbackContext);
            } 

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void getFrameworkDetails(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        final String requestJson = args.getString(1);

        FrameworkDetailsRequest.Builder frameworkDetailsRequest = GsonUtil.fromJson(requestJson, FrameworkDetailsRequest.Builder.class);
        FrameworkDetailsRequest request = frameworkDetailsRequest.build();
        frameworkDetailsRequest.forFramework(request.getFrameworkId());
        GenieService.getAsyncService().getFrameworkService().getFrameworkDetails(frameworkDetailsRequest.build(), new IResponseHandler<Framework>() {
            @Override
            public void onSuccess(GenieResponse<Framework> genieResponse) {
                callbackContext.success(genieResponse.getResult().getFramework());
            }

            @Override
            public void onError(GenieResponse<Framework> genieResponse) {
                callbackContext.error(GsonUtil.toJson(genieResponse));
            }
        });
    }

    private static void persistFrameworkDetails(JSONArray args, CallbackContext callbackContext) throws JSONException {
        final String requestJson = args.getString(1);

        GenieService.getAsyncService().getFrameworkService().persistFrameworkDetails(requestJson, new IResponseHandler<Void>() {
            @Override
            public void onSuccess(GenieResponse<Void> genieResponse) {
                // callbackContext.success(GsonUtil.toJson(genieResponse));
            }

            @Override
            public void onError(GenieResponse<Void> genieResponse) {
                // callbackContext.error(GsonUtil.toJson(genieResponse));
            }
        });
    }

}