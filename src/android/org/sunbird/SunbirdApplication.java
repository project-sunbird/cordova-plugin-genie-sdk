package org.sunbird;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import org.ekstep.genieservices.GenieService;
import org.ekstep.genieservices.commons.ILocationInfo;
import org.ekstep.genieservices.commons.IResponseHandler;
import org.ekstep.genieservices.commons.bean.GenieResponse;
import org.ekstep.genieservices.commons.bean.telemetry.DeviceSpecification;
import org.ekstep.genieservices.commons.bean.telemetry.Interrupt;
import org.ekstep.genieservices.commons.bean.telemetry.Start;
import org.ekstep.genieservices.commons.bean.telemetry.Telemetry;
import org.ekstep.genieservices.utils.DeviceSpec;
import org.genie.SDKParams;

import java.util.Locale;


/**
 * Created by swayangjit on 21/4/18.
 */
public class SunbirdApplication extends Application implements ForegroundService.OnForegroundChangeListener{
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(ForegroundService.getInstance());
        ForegroundService.getInstance().registerListener(this);
        SDKParams.setParams();
        GenieService.init(this, "org.sunbird.app");
        saveTelemetry(buildStartEvent(this));

    }

    @Override
    public void onSwitchForeground() {
        Interrupt interrupt = new Interrupt.Builder().environment("home").type("background").pageId("").build();
        saveTelemetry(interrupt);
    }

    @Override
    public void onSwitchBackground() {
        Interrupt resume = new Interrupt.Builder().environment("home").type("resume").pageId("").build();
        saveTelemetry(resume);
    }

    private void saveTelemetry(Telemetry telemetry){
        GenieService.getAsyncService().getTelemetryService().saveTelemetry(telemetry, new IResponseHandler<Void>() {
            @Override
            public void onSuccess(GenieResponse<Void> genieResponse) {

            }

            @Override
            public void onError(GenieResponse<Void> genieResponse) {

            }
        });
    }

    public  Start buildStartEvent(Context context) {

        DeviceSpecification deviceSpec = new DeviceSpecification();
        deviceSpec.setOs("Android " + org.ekstep.genieservices.utils.DeviceSpec.getOSVersion());
        deviceSpec.setMake(org.ekstep.genieservices.utils.DeviceSpec.getDeviceName());
        deviceSpec.setId(org.ekstep.genieservices.utils.DeviceSpec.getAndroidId(context));

        String internalMemory = bytesToHuman(DeviceSpec.getTotalInternalMemorySize());
        if (!TextUtils.isEmpty(internalMemory)) {
            deviceSpec.setIdisk(Double.valueOf(internalMemory));
        }

        String externalMemory = bytesToHuman(DeviceSpec.getTotalExternalMemorySize());
        if (!TextUtils.isEmpty(externalMemory)) {
            deviceSpec.setEdisk(Double.valueOf(externalMemory));
        }

        String screenSize = DeviceSpec.getScreenInfoinInch(context);
        if (!TextUtils.isEmpty(screenSize)) {
            deviceSpec.setScrn(Double.valueOf(screenSize));
        }

        String[] cameraInfo = org.ekstep.genieservices.utils.DeviceSpec.getCameraInfo(context);
        String camera = "";
        if (cameraInfo != null) {
            camera = TextUtils.join(",", cameraInfo);
        }
        deviceSpec.setCamera(camera);

        deviceSpec.setCpu(org.ekstep.genieservices.utils.DeviceSpec.getCpuInfo());
        deviceSpec.setSims(-1);

        ILocationInfo locationInfo = GenieService.getService().getLocationInfo();

        Start start = new Start.Builder()
                .environment("home")
                .deviceSpecification(deviceSpec)
                .loc(locationInfo.getLocation())
                .type("app")
                .build();

        return start;
    }

    public  String bytesToHuman(long size) {
        long Kb = 1 * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size < Kb) return floatForm(size) + "";
        if (size >= Kb && size < Mb) return floatForm((double) size / Kb) + "";
        if (size >= Mb && size < Gb) return floatForm((double) size / Mb) + "";
        if (size >= Gb && size < Tb) return floatForm((double) size / Gb) + "";
        if (size >= Tb && size < Pb) return floatForm((double) size / Tb) + "";
        if (size >= Pb && size < Eb) return floatForm((double) size / Pb) + "";
        if (size >= Eb) return floatForm((double) size / Eb) + "";

        return "0.00";
    }

    public  String floatForm(double d) {
        return String.format(Locale.US, "%.2f", d);
    }
}
