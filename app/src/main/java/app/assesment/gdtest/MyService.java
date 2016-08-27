package app.assesment.gdtest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.security.Provider;

public class MyService extends Service implements LocationListener {

    private LocationManager locationManager;
    private Location location;
    private static final long MIN_GPS_TIME = 60 * 1000;
    private static final long MIN_NETWORK_TIME = 5 * 60 * 1000;
    private static final float MIN_DISTANCE = 1000;
    private IBinder myBinder = new MyBinder();

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (intent != null) {
            if (intent.getAction().equals("startListening")) {
                setLocationRequestAndListener();
            } else {
                if (intent.getAction().equals("stopListening")) {
                    reset();
                    this.stopSelf();
                }
            }
        }

        return START_STICKY;

    }

    @Override
    public IBinder onBind(final Intent intent) {
        return myBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    public void onLocationChanged(final Location location) {
        this.location = location;

        Intent intent = new Intent();
        intent.setAction("LOCATION_INTENT_SERVICE");
        intent.putExtra("LatLng", location.getLatitude() + "," + location.getLongitude());
        notifyActivity(intent);

        Log.e("SERVICE", location.getProvider() + ":" + location.getLatitude() + "," + location.getLongitude());
    }

    public void onProviderDisabled(final String provider) {
        setLocationRequestAndListener();
    }

    public void onProviderEnabled(final String provider) {
        setLocationRequestAndListener();
    }

    public void onStatusChanged(final String provider, final int status, final Bundle intent) {
        switch (status) {
            case   LocationProvider.AVAILABLE:
                Log.e("Status", provider + " Available");
                break;
            case   LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.e("Status", provider + "Temporary unavailable");
                break;
            case   LocationProvider.OUT_OF_SERVICE:
                Log.e("Status", provider + "Out of services");
                break;
        }
    }

    private void setLocationRequestAndListener() {

        Criteria criteria = new Criteria();
        String  provider = locationManager.getBestProvider(criteria,true);

        Log.e("LOKASI", "setLocationRequestAndListener" + provider);
        if (!(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            // Handle if first run not get the location
            this.location = locationManager.getLastKnownLocation(provider);
            if (location != null) {

                location.setProvider(provider);
                onLocationChanged(this.location);
            }
            locationManager.requestLocationUpdates(provider, provider.equals(LocationManager.GPS_PROVIDER) ? MIN_GPS_TIME : MIN_NETWORK_TIME, MIN_DISTANCE, this);
        }
    }
    private void reset(){
        if (!(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            locationManager.removeUpdates(this);
            locationManager = null;
        }
    }

    private void notifyActivity(Intent intent){
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
    }


    public class MyBinder extends Binder {
        MyService getInstance() {
            return MyService.this;
        }
    }
}
