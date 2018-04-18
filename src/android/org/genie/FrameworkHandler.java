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

import java.util.List;
import java.util.Map;

public class FrameworkHandler {

    private static final String TYPE_GET_FRAMEWORK_DETAILS = "getFrameworkDetails";
    private static final String TYPE_GET_CATEGORY_DATA = "getCategoryData";

    private static Framework framework = null;

    public static void handle(JSONArray args, final CallbackContext callbackContext) {
        try {
            String type = args.getString(0);
            if (type.equals(TYPE_GET_FRAMEWORK_DETAILS)) {
                getFrameworkDetails(callbackContext);
            } else {
                if (type.equals(TYPE_GET_CATEGORY_DATA)) {
                    getCategoryData(args, callbackContext);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void getCategoryData(JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (framework == null) {
            FrameworkDetailsRequest.Builder builder = new FrameworkDetailsRequest.Builder();
            builder.defaultFrameworkDetails();
            GenieResponse<Framework> genieResponse = GenieService.getService().getFrameworkService().getFrameworkDetails(builder.build());
            if (!genieResponse.getStatus()) {
                callbackContext.error(GsonUtil.toJson(genieResponse));
                return;
            }

            framework = genieResponse.getResult();
        }

        final String requestJson = args.getString(1);
        Map<String, String> requestMap = GsonUtil.fromJson(requestJson, Map.class);
        String currentCategory = requestMap.get("currentCategory");
        String prevCategory;
        String selectedCode;
        if (requestMap.containsKey("prevCategory") && requestMap.containsKey("selectedCode")) {
            prevCategory = requestMap.get("prevCategory");
            selectedCode = requestMap.get("selectedCode");
        }

        Object categoryData = null;
        Map<String, Object> frameworkMap = GsonUtil.fromJson(framework.getFramework(), Map.class);
        if (frameworkMap.containsKey("categories")) {
            List<Map> categories = (List<Map>) frameworkMap.get("categories");
            for (Map category : categories) {
                if (currentCategory.equals(category.get("code"))) {
                    categoryData = category.get("terms");

                }
            }
        }

        callbackContext.success(GsonUtil.toJson(categoryData));
    }

    private static void getFrameworkDetails(final CallbackContext callbackContext) {
        FrameworkDetailsRequest.Builder frameworkDetailsRequest = new FrameworkDetailsRequest.Builder();
        frameworkDetailsRequest.defaultFrameworkDetails();

        GenieService.getAsyncService().getFrameworkService().getFrameworkDetails(frameworkDetailsRequest.build(), new IResponseHandler<Framework>() {
            @Override
            public void onSuccess(GenieResponse<Framework> genieResponse) {
                framework = genieResponse.getResult();
                callbackContext.success(GsonUtil.toJson(genieResponse));
            }

            @Override
            public void onError(GenieResponse<Framework> genieResponse) {
                callbackContext.error(GsonUtil.toJson(genieResponse));
            }
        });
    }
}
