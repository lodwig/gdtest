package app.assesment.gdtest.presenter;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by MacBookPro on 8/24/16.
 */
public interface MapPresenter {

    void setMarker(GoogleMap map, LatLng position);
    void drawPolyLine(GoogleMap map, List<LatLng> listPoint);
}
