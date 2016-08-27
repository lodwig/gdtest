package app.assesment.gdtest.helpers;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import app.assesment.gdtest.presenter.MapPresenterImp;


public class MyLocationListener implements LocationListener {

    private static MapPresenterImp presenter;
    private ArrayList<LatLng> listLatLng;
    private GoogleMap map;
    public MyLocationListener(GoogleMap map, MapPresenterImp presenter, ArrayList<LatLng> listLatLng){
        this.presenter = presenter;
        this.listLatLng = listLatLng;
        this.map = map;
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        listLatLng.add(position);
        presenter.setMarker(map, position);
        presenter.drawPolyLine(map, listLatLng);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e("LOKASI", provider + ":" + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.e("LOKASI", provider + ":enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e("LOKASI", provider + ":disabled");
    }

}
