package org.genie;

import org.apache.cordova.CallbackContext;
import org.ekstep.genieservices.GenieService;
import org.ekstep.genieservices.commons.IResponseHandler;
import org.ekstep.genieservices.commons.bean.GenieResponse;
import org.ekstep.genieservices.commons.bean.Profile;
import org.ekstep.genieservices.commons.utils.CollectionUtil;
import org.ekstep.genieservices.commons.utils.GsonUtil;
import org.ekstep.genieservices.commons.utils.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Created by souvikmondal on 6/3/18.
 */

public class ProfileHandler {

    private static final String TYPE_CREATE_PROFILE = "createProfile";
    private static final String TYPE_UPDTATE_PROFILE = "updateProfile";
    private static final String TYPE_SET_CURRENT_USER = "setCurrentUser";
    private static final String TYPE_GET_CURRENT_USER = "getCurrentUser";
    private static final String TYPE_SET_CURRENT_PROFILE = "setCurrentProfile";
    private static final String TYPE_SET_ANONYMOUS_USER = "setAnonymousUser";

    public static void handle(JSONArray args, final CallbackContext callbackContext) {
        try {
            String type = args.getString(0);
            if (type.equals(TYPE_CREATE_PROFILE)) {
                createProfile(args, callbackContext);
            } else if (type.equals(TYPE_UPDTATE_PROFILE)) {
                updateProfile(args, callbackContext);
            } else if (type.equals(TYPE_SET_CURRENT_USER)) {
                setCurrentUser(args, callbackContext);
            } else if (type.equals(TYPE_GET_CURRENT_USER)) {
                getCurrentUser(callbackContext);
            } else if (type.equals(TYPE_SET_CURRENT_PROFILE)) {
                setCurrentProfile(args,callbackContext);
            } else if (type.equals(TYPE_SET_ANONYMOUS_USER)) {
                setAnonymousUser(callbackContext);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * create profile
     */
    private static void createProfile(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String requestJson = args.getString(1);
        Profile request = GsonUtil.fromJson(requestJson, Profile.class);

        GenieService.getAsyncService().getUserService().createUserProfile(request, new IResponseHandler<Profile>() {
            @Override
            public void onSuccess(GenieResponse<Profile> genieResponse) {
                callbackContext.success(GsonUtil.toJson(genieResponse.getResult()));
            }

            @Override
            public void onError(GenieResponse<Profile> genieResponse) {
                callbackContext.error(genieResponse.getError());
            }
        });
    }

    /**
     * update profile
     */
    private static void updateProfile(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String requestJson = args.getString(1);
        Profile request = GsonUtil.fromJson(requestJson, Profile.class);

        GenieService.getAsyncService().getUserService().updateUserProfile(request, new IResponseHandler<Profile>() {
            @Override
            public void onSuccess(GenieResponse<Profile> genieResponse) {
                callbackContext.success(GsonUtil.toJson(genieResponse.getResult()));
            }

            @Override
            public void onError(GenieResponse<Profile> genieResponse) {
                callbackContext.error(genieResponse.getError());
            }
        });
    }

    /**
     * set current user
     */
    private static void setCurrentUser(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        String uid = args.getString(1);
        GenieService.getAsyncService().getUserService().setCurrentUser(uid, new IResponseHandler<Void>() {
            @Override
            public void onSuccess(GenieResponse<Void> genieResponse) {
                callbackContext.success(GsonUtil.toJson(genieResponse.getResult()));
            }

            @Override
            public void onError(GenieResponse<Void> genieResponse) {
                callbackContext.error(genieResponse.getError());
            }
        });
    }

    /**
     * get current user
     */
    private static void getCurrentUser(final CallbackContext callbackContext) {
        GenieService.getAsyncService().getUserService().getCurrentUser(new IResponseHandler<Profile>() {
            @Override
            public void onSuccess(GenieResponse<Profile> genieResponse) {
                callbackContext.success(GsonUtil.toJson(genieResponse.getResult()));
            }

            @Override
            public void onError(GenieResponse<Profile> genieResponse) {
                callbackContext.error(genieResponse.getError());
            }
        });
    }

    /**
     * set current profile
     */
    private static void setCurrentProfile(JSONArray args, final CallbackContext callbackContext) throws JSONException {

        boolean isGuest = args.getBoolean(1);
        String requestJson = args.getString(2);
        Profile profile = GsonUtil.fromJson(requestJson, Profile.class);

        if (profile == null) {
            callbackContext.error("INVALID_PROFILE");
            return;
        }

        if (isGuest) {
            if (!StringUtil.isNullOrEmpty(profile.getUid())) {
                setUser(profile.getUid(), callbackContext);
            } else {
                createUser(profile, callbackContext);
            }
        } else {
            GenieService.getAsyncService().getUserService().getAllUserProfile(new IResponseHandler<List<Profile>>() {
                @Override
                public void onSuccess(GenieResponse<List<Profile>> genieResponse) {
                    boolean isUserExist = false;
                    List<Profile> profileList = genieResponse.getResult();
                    if (!CollectionUtil.isNullOrEmpty(profileList)) {

                        for (Profile p : profileList) {
                            if (p.getUid().equalsIgnoreCase(profile.getUid())) {
                                isUserExist = true;
                                setUser(profile.getUid(), callbackContext);
                                break;
                            }
                        }
                    }

                    if (!isUserExist) {
                        createUser(profile, callbackContext);
                    }
                }

                @Override
                public void onError(GenieResponse<List<Profile>> genieResponse) {
                    callbackContext.error(genieResponse.getError());
                }
            });
        }
    }

    private static void setAnonymousUser(final CallbackContext callbackContext) throws JSONException{
        GenieService.getAsyncService().getUserService().setAnonymousUser(new IResponseHandler<String>() {
            @Override
            public void onSuccess(GenieResponse<String> genieResponse) {
                callbackContext.success(genieResponse.getResult());
            }

            @Override
            public void onError(GenieResponse<String> genieResponse) {
                callbackContext.error(genieResponse.getError());
            }
        });
    }

    private static void setUser(String uid, CallbackContext callbackContext) {
        GenieService.getAsyncService().getUserService().setCurrentUser(uid, new IResponseHandler<Void>() {
            @Override
            public void onSuccess(GenieResponse<Void> genieResponse) {
                callbackContext.success(GsonUtil.toJson(genieResponse.getResult()));
            }

            @Override
            public void onError(GenieResponse<Void> genieResponse) {
                callbackContext.error(genieResponse.getError());
            }
        });
    }

    private static void createUser(Profile profile, CallbackContext callbackContext) {
        GenieService.getAsyncService().getUserService().createUserProfile(profile, new IResponseHandler<Profile>() {
            @Override
            public void onSuccess(GenieResponse<Profile> genieResponse) {
                setUser(genieResponse.getResult().getUid(), callbackContext);
            }

            @Override
            public void onError(GenieResponse<Profile> genieResponse) {
                callbackContext.error(genieResponse.getError());
            }
        });
    }

}