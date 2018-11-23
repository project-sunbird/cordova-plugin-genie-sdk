package org.sunbird;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.LOG;
import org.ekstep.genieservices.GenieService;
import org.ekstep.genieservices.commons.bean.enums.ContentImportStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.sunbird.deeplinks.DeepLinkNavigation;

import java.util.ArrayList;

/**
 * Created by Sunbird Team on 14-Nov-18.
 */

public class DeepLinkImp{
  private String localeSelected;
  private Intent deepLinkIntent;
  private DeepLinkNavigation mDeepLinkNavigation;
  private JSONObject mLastEvent;
  private ArrayList<CallbackContext> mHandler = new ArrayList<>();
  private static final int IMPORT_SUCCESS = 1;
  private static final int IMPORT_ERROR = 2;
  private static final int IMPORT_PROGRESS = 3;
  private static final int IMPORTING_COUNT = 4;
  private static final int IMPORT_FAILED = 5;
  private static final int NOT_COMPATIBLE = 6;
  private static final int CONTENT_EXPIRED = 7;
  private static final int ALREADY_EXIST = 8;
  private static final String TAG = "deeplinksPlugin";



  
  SplashScreen splashScr;
  public void handleIntentForDeeplinking(Activity activity, Intent intent, SplashScreen splashScreen) {
    // get the locale set by user from the mobile
    LOG.e("DeepLinkImp ",activity.toString()+" "+intent.getData());
    splashScr=splashScreen;
    localeSelected = GenieService.getService().getKeyStore().getString("sunbirdselected_language_code", "en");
    deepLinkIntent = intent;
    mDeepLinkNavigation = new DeepLinkNavigation(activity);

    mDeepLinkNavigation.validateAndHandleDeepLink(intent, new DeepLinkNavigation.IValidateDeepLink() {
      @Override
      public void validLocalDeepLink() {

        try {
          Uri intentUri = intent.getData();

          JSONObject response = new JSONObject();

          response.put("type", "contentDetails");

          if (intentUri != null) {
            response.putOpt("id", intentUri.getLastPathSegment());
            for (String key : intentUri.getQueryParameterNames()) {
              response.putOpt(key, intentUri.getQueryParameter(key));
            }
          }

          mLastEvent = response;
          splashScr.consumeEvents();
        } catch (JSONException ex) {
          Log.e("SplashScreen", ex.toString());
        }

      }

      @Override
      public void validServerDeepLink() {
        if (intent.getData() == null) {
          return;
        }

        String url = intent.getData().toString();

        String newString = url.replace("https://", "").replace("http://", "");
        String[] pair = newString.split("/");

        if (pair[1].equalsIgnoreCase("public")) {
          splashScr.launchContentDetails(url);
        } else if (pair[1].equalsIgnoreCase("dial")) {
          JSONObject jsonObject = new JSONObject();
          try {
            jsonObject.put("type", "dialcode");
            jsonObject.put("code", url);

            mLastEvent = jsonObject;
          } catch (JSONException e) {
            e.printStackTrace();
          }

          splashScr.consumeEvents();
        } else if (pair[1].equalsIgnoreCase("play")
          && (pair[2].equalsIgnoreCase("collection") || pair[2].equalsIgnoreCase("content"))) {
          splashScr.launchContentDetails(url);
        } else if (pair[1].equalsIgnoreCase("learn") && pair[2].equalsIgnoreCase("course")) {
          splashScr.launchContentDetails(url);
        }

      }

      @Override
      public void notAValidDeepLink() {
        Uri uri = intent.getData();

        if (uri == null) {
          importEcarFile(intent);
        }
        else if ((splashScr.cordova.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
          importEcarFile(intent);
        } else{
          splashScreen.importingInProgress=true;
          splashScreen.displaySplashScreen();
          splashScreen.cordova.requestPermission(splashScreen, 100, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
      }

      @Override
      public void onTagDeepLinkFound(String tagName, String description, String startDate, String endDate) {
        splashScr.consumeEvents();
      }
    });
  }

  public void onRequestPermissionResult(int requestCode, String[] permissions,
                                        int[] grantResults) throws JSONException {
    if (requestCode == 100) {
      importEcarFile(deepLinkIntent);
      deepLinkIntent = null;
    }
    else {
      importEcarFile(deepLinkIntent);
    }
  }

  protected void importEcarFile(Intent intent) {
    LOG.e("DeepLinkImp","inside import ecar "+intent.getData());
    boolean isImport = ImportExportUtil.initiateImportFile(splashScr.cordova.getActivity(), new ImportExportUtil.IImport() {
      @Override
      public void onImportSuccess() {
        String message = splashScr.getRelevantMessage(localeSelected, IMPORT_SUCCESS);
        splashScr.importStatusTextView.setText(message);
        splashScr.importingInProgress = false;

        Toast.makeText(splashScr.cordova.getActivity(), message, Toast.LENGTH_SHORT).show();
        splashScr.hide();
      }

      @Override
      public void onImportFailure(ContentImportStatus status) {
        String statusText = null;
        switch (status) {
          case NOT_COMPATIBLE:
            statusText = splashScr.getRelevantMessage(localeSelected, NOT_COMPATIBLE);
            break;
          case CONTENT_EXPIRED:
            statusText = splashScr.getRelevantMessage(localeSelected, CONTENT_EXPIRED);
            break;
          case ALREADY_EXIST:
            statusText = splashScr.getRelevantMessage(localeSelected, ALREADY_EXIST);
            break;
          default:
            statusText = splashScr.getRelevantMessage(localeSelected, IMPORT_ERROR);
            break;
        }

        splashScr.importStatusTextView.setText(statusText);
        splashScr.importingInProgress = false;
        Toast.makeText(splashScr.cordova.getActivity(), statusText, Toast.LENGTH_SHORT).show();
        splashScr.hide();
      }

      @Override
      public void onOutDatedEcarFound() {
        splashScr.importingInProgress = false;
        splashScr.hide();
      }
    }, intent, true);
    LOG.e("DeepLinkImp","inside import ecar "+ isImport);
    if (isImport) {
      splashScr.displaySplashScreen();
      splashScr.importingInProgress = true;

      String message = splashScr.getRelevantMessage(localeSelected, IMPORT_PROGRESS);
      LOG.e("DeepLinkImp ",message);
      splashScr.importStatusTextView.setText(message);
    }
  }
}
