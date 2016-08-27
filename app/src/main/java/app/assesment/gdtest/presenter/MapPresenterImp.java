package app.assesment.gdtest.presenter;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

/**
 * Created by MacBookPro on 8/24/16.
 */
public class MapPresenterImp implements MapPresenter {
    @Override
    public void setMarker(GoogleMap map, LatLng position) {
        map.clear();
        Marker marker = map.addMarker(new MarkerOptions().position(position));
        marker.setTitle("#Current-Position");
        marker.showInfoWindow();
        map.moveCamera(CameraUpdateFactory.newLatLng(position));
        map.animateCamera(CameraUpdateFactory.zoomTo(14));
    }

    @Override
    public void drawPolyLine(GoogleMap map, List<LatLng> listPoint) {
        PolylineOptions line = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        line.addAll(listPoint);
        map.addPolyline(line);
    }
}
