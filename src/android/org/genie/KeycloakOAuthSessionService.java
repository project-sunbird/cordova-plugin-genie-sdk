package org.genie;

import org.apache.cordova.CallbackContext;
import org.ekstep.genieservices.GenieService;
import org.ekstep.genieservices.async.IPerformable;
import org.ekstep.genieservices.async.ThreadPool;
import org.ekstep.genieservices.auth.AbstractAuthSessionImpl;
import org.ekstep.genieservices.commons.bean.GenieResponse;
import org.ekstep.genieservices.commons.bean.Session;
import org.ekstep.genieservices.commons.utils.GsonUtil;
import org.ekstep.genieservices.commons.utils.StringUtil;
import org.ekstep.genieservices.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by swayangjit on 23/3/18.
 */

public class KeycloakOAuthSessionService extends AbstractAuthSessionImpl {

    private static String REDIRECT_BASE_URL = "https://staging.open-sunbird.org";
    private static String END_POINT = "/auth/realms/sunbird/protocol/openid-connect/token";

    private JSONArray args;
    private CallbackContext callbackContext;

    public KeycloakOAuthSessionService() {

    }

    public KeycloakOAuthSessionService(JSONArray args, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
        this.args = args;
    }

    private Map<String, String> getCreateSessionFormData(String userToken) {
        Map<String, String> requestMap = new HashMap<>();
        try {
            requestMap.put("redirect_uri", "https://" + "staging.open-sunbird.org" + "/" + "oauth2callback");
            requestMap.put("code", userToken);
            requestMap.put("grant_type", "authorization_code");
            requestMap.put("client_id", "android");

        } catch (Exception e) {

        }

        return requestMap;
    }

    private Map<String, String> getRefreshSessionFormData(String refreshToken) {
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("client_id", "android");
        requestMap.put("grant_type", "refresh_token");
        requestMap.put("refresh_token", refreshToken);
        return requestMap;
    }

    @Override
    public void createSession(String callBackUrl) {

        ThreadPool.getInstance().execute(new IPerformable<Map<String, Object>>() {
            @Override
            public GenieResponse<Map<String, Object>> perform() {
                try {
                    callbackContext.success(invokeAPI(getCreateSessionFormData(args.getString(1))));
                } catch (Exception e) {
//                    callbackContext.error(error);
                }
                return null;
            }
        }, null);
    }

    @Override
    public void refreshSession(String refreshToken) {

        ThreadPool.getInstance().execute(new IPerformable<Map<String, Object>>() {
            @Override
            public GenieResponse<Map<String, Object>> perform() {
                try {
                    String response=invokeAPI(getRefreshSessionFormData(refreshToken));
                    if(!StringUtil.isNullOrEmpty(response)){
                        Session session = GsonUtil.fromJson(response, Session.class);
                        GenieService.getService().getAuthSession().startSession(session);
                    }
                    else{
                        EventBus.postEvent("LOGOUT");
                    }

                } catch (Exception e) {
                    Map<String,String> error=new HashMap<>();
                    error.put("error","logout");
                    callbackContext.error(GsonUtil.toJson(error));
                }
                return null;
            }
        }, null);
    }

    private String invokeAPI(Map<String,String> formData) throws IOException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.connectTimeout(10, TimeUnit.SECONDS);
        OkHttpClient httpClient = builder.build();
        Request request = new Request.Builder()
                .url(REDIRECT_BASE_URL + END_POINT)
                .post(createRequestBody(formData))
                .build();
        Response response = httpClient.newCall(request).execute();
        if(response.isSuccessful()){
            return response.body().string();
        }
        else{
            return null;
        }
    }

    private RequestBody createRequestBody(Map<String, String> formData) {
        FormBody.Builder builder = new FormBody.Builder();
        Iterator<String> iterator = formData.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = formData.get(key);
            builder.add(key, value);
        }
        return builder.build();
    }
}
