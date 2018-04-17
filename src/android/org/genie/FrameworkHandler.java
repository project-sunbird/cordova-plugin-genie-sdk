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
    private static final String TYPE_GET_BOARDS = "getBoards";
    private static final String CATEGORY_BOARD = "board";

    private static Framework framework = null;

    public static void handle(JSONArray args, final CallbackContext callbackContext) {
        try {
            String type = args.getString(0);
            if (type.equals(TYPE_GET_FRAMEWORK_DETAILS)) {
                getFrameworkDetails(callbackContext);
            } else {
                if (type.equals(TYPE_GET_BOARDS)) {
                    getBoards(callbackContext);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void getBoards(CallbackContext callbackContext) {
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

        List<String> boards = new ArrayList<>();
        Map<String, Object> frameworkMap = GsonUtil.fromJson(framework.getFramework(), Map.class);
        if (frameworkMap.containsKey("categories")) {
            List<Map> categories = (List<Map>) frameworkMap.get("categories");
            for (Map category : categories) {
                if (CATEGORY_BOARD.equals(category.get("code"))) {
                    List<Map> terms = (List<Map>) category.get("terms");
                    for (Map term : terms) {
                        boards.add((String) term.get("name"));
                    }
                }
            }
        }

        callbackContext.success(GsonUtil.toJson(boards));
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
