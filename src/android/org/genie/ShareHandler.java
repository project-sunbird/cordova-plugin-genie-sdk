package org.genie;

import android.app.Dialog;
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

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.ekstep.genieservices.GenieService;
import org.ekstep.genieservices.commons.IResponseHandler;
import org.ekstep.genieservices.commons.bean.ContentExportRequest;
import org.ekstep.genieservices.commons.bean.ContentExportResponse;
import org.ekstep.genieservices.commons.bean.GenieResponse;
import org.ekstep.genieservices.commons.bean.TelemetryExportRequest;
import org.ekstep.genieservices.commons.bean.TelemetryExportResponse;
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

    private static final String TYPE_EXPORT_ECAR = "exportEcar";
    private static final String TYPE_EXPORT_TELEMETRY = "exportTelemetry";

    public static void handle(final JSONArray args, final CordovaInterface cordova, final CallbackContext callbackContext) {

        try {
            String shareType = args.getString(0);
            switch (shareType) {
                case TYPE_EXPORT_ECAR:
                    String contentId = args.getString(1);
                    exportEcar(contentId, callbackContext);
                    break;

                case TYPE_EXPORT_TELEMETRY:
                    exportTelemetry(callbackContext);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private static void exportEcar(String contentId, final CallbackContext callbackContext) {
        List<String> ContentIds = new ArrayList<String>();
        ContentIds.add(contentId);
        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "/Ecars");
        ContentExportRequest.Builder builder = new ContentExportRequest.Builder();
        builder.exportContents(ContentIds).toFolder(String.valueOf(directory));
        GenieService.getAsyncService().getContentService().exportContent(builder.build(), new IResponseHandler<ContentExportResponse>() {
            @Override
            public void onSuccess(GenieResponse<ContentExportResponse> genieResponse) {
                ContentExportResponse contentExportResponse = genieResponse.getResult();
                String ecarPath = contentExportResponse.getExportedFilePath();
                callbackContext.success(ecarPath);
            }

            @Override
            public void onError(GenieResponse<ContentExportResponse> genieResponse) {
                callbackContext.error("failure");
            }
        });
    }

    private static void exportTelemetry(final CallbackContext callbackContext) {

        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "/Telemetry");
        TelemetryExportRequest.Builder builder = new TelemetryExportRequest.Builder();
        builder.toFolder(String.valueOf(directory));
        GenieService.getAsyncService().getTelemetryService().exportTelemetry(builder.build(), new IResponseHandler<TelemetryExportResponse>() {
            @Override
            public void onSuccess(GenieResponse<TelemetryExportResponse> genieResponse) {
                TelemetryExportResponse telemetryExportResponse = genieResponse.getResult();
                String ecarPath = telemetryExportResponse.getExportedFilePath();
                callbackContext.success(ecarPath);
            }

            @Override
            public void onError(GenieResponse<TelemetryExportResponse> genieResponse) {
                callbackContext.error("failure");
            }
        });
    }

}
