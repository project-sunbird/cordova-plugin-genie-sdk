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
    private static final String TYPE_GET_CATEGORY_DATA = "getCategoryData";

    private static Framework framework = null;

    public static void handle(JSONArray args, final CallbackContext callbackContext) {
        try {
            String type = args.getString(0);

            if (type.equals(TYPE_GET_FRAMEWORK_DETAILS)) {
                getFrameworkDetails(args, callbackContext);
            } else if (type.equals(TYPE_GET_CATEGORY_DATA)) {
                getCategoryData(args, callbackContext);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void getFrameworkDetails(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        final String requestJson = args.getString(1);

        FrameworkDetailsRequest.Builder frameworkDetailsRequest = GsonUtil.fromJson(requestJson, FrameworkDetailsRequest.Builder.class);

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

    private static void getCategoryData(JSONArray args, CallbackContext callbackContext) throws JSONException {
        final String requestJson = args.getString(1);
        Map<String, Object> requestMap = GsonUtil.fromJson(requestJson, Map.class);
        String frameworkId = (String) requestMap.get("frameworkId");

        String cachedFrameworkId = null;
        if (framework == null) {
            Map<String, Object> cachedFrameworkMap = GsonUtil.fromJson(framework.getFramework(), Map.class);
            cachedFrameworkId = (String) cachedFrameworkMap.get("identifier");
        }

        if (cachedFrameworkId == null || !cachedFrameworkId.equals(frameworkId)) {
            FrameworkDetailsRequest.Builder builder = new FrameworkDetailsRequest.Builder();
            if (!StringUtil.isNullOrEmpty(frameworkId)) {
                builder.forFramework(frameworkId);
            } else {
                builder.defaultFrameworkDetails();
            }

            GenieResponse<Framework> genieResponse = GenieService.getService().getFrameworkService().getFrameworkDetails(builder.build());
            if (!genieResponse.getStatus()) {
                callbackContext.error(GsonUtil.toJson(genieResponse));
                return;
            }

            framework = genieResponse.getResult();
        }

        String currentCategory = (String) requestMap.get("currentCategory");


        if (requestMap.containsKey("prevCategory")
                && requestMap.containsKey("selectedCode")) {
            String prevCategory = (String) requestMap.get("prevCategory");
            ArrayList<String> selectedCodeList = (ArrayList<String>) requestMap.get("selectedCode");

            List<Map> prevCategoryData = null;
            Map<String, Object> frameworkMap = GsonUtil.fromJson(framework.getFramework(), Map.class);
            if (frameworkMap.containsKey("categories")) {
                List<Map> categories = (List<Map>) frameworkMap.get("categories");
                for (Map category : categories) {
                    if (prevCategory.equals(category.get("code"))) {
                        prevCategoryData = (List<Map>) category.get("terms");
                        break;
                    }
                }
            }

            List<Map> allAssociations = new ArrayList<>();

            if (prevCategoryData != null && prevCategoryData.size() > 0) {
                for (Map prevCategoryValue : prevCategoryData) {
                    String code = (String) prevCategoryValue.get("code");
                    if (selectedCodeList.contains(code)
                            && prevCategoryValue.containsKey("associations")) {
                        List<Map> associations = (List<Map>) prevCategoryValue.get("associations");

                        if (associations != null && associations.size() > 0) {
                            /*allAssociations.addAll(associations);*/
                            for (Map association: associations) {
                                if (association.containsKey("category")) {
                                    String categoryValue = (String) association.get("category");
                                    if (categoryValue.equalsIgnoreCase(currentCategory)) {
                                        allAssociations.add(association);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (allAssociations.size() > 0) {
                Set<Map> responseAssociations = new HashSet<>(allAssociations);
                callbackContext.success(GsonUtil.toJson(responseAssociations));
                return;
            }
        }

        Object categoryData = null;
        Map<String, Object> frameworkMap = GsonUtil.fromJson(framework.getFramework(), Map.class);
        if (frameworkMap.containsKey("categories")) {
            List<Map> categories = (List<Map>) frameworkMap.get("categories");
            for (Map category : categories) {
                if (currentCategory.equals(category.get("code"))) {
                    categoryData = category.get("terms");
                    break;
                }
            }
        }

        callbackContext.success(GsonUtil.toJson(categoryData));
    }

}