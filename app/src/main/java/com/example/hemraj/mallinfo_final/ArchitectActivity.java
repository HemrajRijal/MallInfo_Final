package com.example.hemraj.mallinfo_final;

import android.location.LocationListener;

import com.example.hemraj.mallinfo_final.util.Constant;
import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.StartupConfiguration;


/**
 * Created by Hemraj on 9/26/2016.
 */

public class ArchitectActivity extends AbstractArchitectCamActivity {
    @Override
    protected StartupConfiguration.CameraPosition getCameraPosition() {
        return StartupConfiguration.CameraPosition.DEFAULT;
    }



    @Override
    public String getActivityTitle() {
        return "AR Experience";
    }

    @Override
    public String getARchitectWorldPath() {
        return "index.html";
    }

    @Override
    public ArchitectView.ArchitectUrlListener getUrlListener() {
        return null;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_architect;
    }

    @Override
    public String getWikitudeSDKLicenseKey() {
        return Constant.TRIAL_KEY;
    }

    @Override
    public int getArchitectViewId() {
        return R.id.architectView;
    }

    @Override
    public ILocationProvider getLocationProvider(LocationListener locationListener) {
        return new LocationProvider(ArchitectActivity.this, locationListener);
    }

    @Override
    public ArchitectView.SensorAccuracyChangeListener getSensorAccuracyListener() {
        return null;
    }

    @Override
    public float getInitialCullingDistanceMeters() {
        return 0;
    }
}
