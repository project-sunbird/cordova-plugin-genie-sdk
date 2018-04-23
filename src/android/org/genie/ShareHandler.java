package org.genie;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.sunbird.app.BuildConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by souvikmondal on 23/4/18.
 */

public class ShareHandler {

    private static final String TYPE_SHARE_CONTENT = "content";

    public static void handle(final JSONArray args, final CordovaInterface cordova) {

        try {
            String shareType = args.getString(1);
            switch (shareType) {
                case TYPE_SHARE_CONTENT:
                    String content = args.getString(2);
                    String contentType = args.getString(3);
                    String identifier = args.getString(4);
                    String type = args.getString(5);
                    shareContentThroughIntent(content, contentType, identifier, type, cordova);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

//    public void logShareContentSuccessEvent(String type, String contentType, String identifier) {
//        Map<String, Object> vals = new HashMap<>();
//        vals.put(TelemetryConstant.CONTENT_TYPE, contentType);
//        if (type.equals("COURSES")) {
//            TelemetryHandler.saveTelemetry(TelemetryBuilder.buildInteractEvent(InteractionType.TOUCH, TelemetryAction.SHARE_COURSE_SUCCESS, TelemetryPageId.COURSE_DETAIL, getContextEnviroment(), vals, identifier, ObjectType.CONTENT, sharePkgVersion, Util.getCorrelationList()));
//        } else if (type.equals("LIBRARY")) {
//            TelemetryHandler.saveTelemetry(TelemetryBuilder.buildInteractEvent(InteractionType.TOUCH, TelemetryAction.SHARE_LIBRARY_SUCCESS, TelemetryPageId.CONTENT_DETAIL, getContextEnviroment(), vals, identifier, ObjectType.CONTENT, sharePkgVersion, Util.getCorrelationList()));
//        }
//    }
//
//    public void logShareContentInitiateEvent(String type, String contentType, String identifier, String pkgVersion) {
//        sharePkgVersion = pkgVersion;
//        Map<String, Object> vals = new HashMap<>();
//        vals.put(TelemetryConstant.CONTENT_TYPE, contentType);
//        if (type.equals("COURSES")) {
//            TelemetryHandler.saveTelemetry(TelemetryBuilder.buildInteractEvent(InteractionType.TOUCH, TelemetryAction.SHARE_COURSE_INITIATED, TelemetryPageId.COURSE_DETAIL, getContextEnviroment(), vals, identifier, ObjectType.CONTENT, pkgVersion, Util.getCorrelationList()));
//        } else if (type.equals("LIBRARY")) {
//            TelemetryHandler.saveTelemetry(TelemetryBuilder.buildInteractEvent(InteractionType.TOUCH, TelemetryAction.SHARE_LIBRARY_INITIATED, TelemetryPageId.CONTENT_DETAIL, getContextEnviroment(), vals, identifier, ObjectType.CONTENT, pkgVersion, Util.getCorrelationList()));
//        }
//    }


    private static boolean appInstalledOrNot(String uri, CordovaInterface cordova) {
        PackageManager pm = cordova.getActivity().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }


    private static void shareContentThroughIntent(final String content, final String contentType, final String identifier, final String type, final CordovaInterface cordova) {
        cordova.getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {

                    LinearLayout linearLayout = new LinearLayout(cordova.getActivity());

                    View.OnClickListener onclickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            logShareContentSuccessEvent(type, contentType, identifier);
                            if (view instanceof ImageView) {
                                ImageView imageV = (ImageView) view;
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                if (contentType.equals("file")) {
                                    Log.d("APP HANDLED TASK", imageV.getTag().toString());
                                    String contentName = content.substring(content.lastIndexOf("/") + 1);
                                    File file = new File(new File(Environment.getExternalStorageDirectory(), "Ecars/tmp/"), contentName);
                                    String authorities = BuildConfig.APPLICATION_ID + ".fileprovider";
                                    Uri contentUri = FileProvider.getUriForFile(cordova.getActivity(), authorities, file);

                                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                                    shareIntent.setType("application/zip");
//                                    logShareClickEvent("FILE");
                                } else if (contentType.equals("text")) {
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, content);
                                    shareIntent.setType("text/plain");
//                                    logShareClickEvent("LINK");
                                }

                                boolean isAppInstalled = appInstalledOrNot((String) imageV.getTag(), cordova);

                                if (isAppInstalled) {
                                    shareIntent.setPackage((String) imageV.getTag());
                                    shareIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                    cordova.getActivity().startActivity(shareIntent);

                                } else {
                                    try {
                                        cordova.getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + (String) imageV.getTag())));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        cordova.getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + (String) imageV.getTag())));
                                    }
                                }

                            } else {
                                Log.d("APPLINKSHAREINTENTS", "NOT AN INSTANCE OF IMAGE VIEW");
                            }

                        }
                    };

                    List<String> packages = new ArrayList<>();

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);

                    if (contentType.equals("file")) {
                        File file = new File(content);
                        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(content));
                        sendIntent.setType("application/zip");
                    } else if (contentType.equals("text")) {
                        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
                        sendIntent.setType("text/plain");
                    }

                    List<ResolveInfo> resolveInfoList = cordova.getActivity().getPackageManager()
                            .queryIntentActivities(sendIntent, 0);

                    ArrayList<String> appPackagesList = new ArrayList<>();
                    for (ResolveInfo resolveInfo : resolveInfoList) {
                        if (!appPackagesList.contains(resolveInfo.activityInfo.packageName)) {
                            if (!resolveInfo.activityInfo.packageName.contains("com.google.android.inputmethod"))
                                appPackagesList.add(resolveInfo.activityInfo.packageName);
                        }
                    }


                    PackageManager pm = cordova.getActivity().getApplicationContext().getPackageManager();
                    ApplicationInfo ai;
                    String applicationName;

                    TextView[] textView = new TextView[appPackagesList.size()];
                    ImageView[] imageView = new ImageView[appPackagesList.size()];
                    LinearLayout[] container = new LinearLayout[appPackagesList.size()];
                    int layoutHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, cordova.getActivity().getResources().getDisplayMetrics());
                    int layoutWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, cordova.getActivity().getResources().getDisplayMetrics());

                    ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(layoutWidth, layoutHeight);
                    LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    containerParams.setMargins(0, 0, 20, 0);

                    int i = 0;

                    for (String packageName : appPackagesList) {
                        container[i] = new LinearLayout(cordova.getActivity());
                        container[i].setOrientation(LinearLayout.VERTICAL);
                        imageView[i] = new ImageView(cordova.getActivity());
                        Drawable icon = null;

                        try {
                            ai = pm.getApplicationInfo(packageName, 0);
                            icon = pm.getApplicationIcon(packageName);

                        } catch (final PackageManager.NameNotFoundException e) {
                            ai = null;
                        }

                        applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
                        Log.d("APP PACKAGE " + contentType, packageName);

                        imageView[i].setImageDrawable(icon);
                        imageView[i].setLayoutParams(params);
                        imageView[i].setOnClickListener(onclickListener);
                        imageView[i].setTag(packageName);

                        textView[i] = new TextView(cordova.getActivity());
                        textView[i].setText(applicationName);
                        textView[i].setGravity(Gravity.CENTER_HORIZONTAL);
                        textView[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);

                        container[i].addView(imageView[i]);
                        container[i].addView(textView[i]);
                        container[i].setGravity(Gravity.CENTER_HORIZONTAL);
                        container[i].setLayoutParams(containerParams);

                        linearLayout.addView(container[i]);

                        i++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
