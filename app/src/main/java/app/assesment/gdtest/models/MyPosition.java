package app.assesment.gdtest.models;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by MacBookPro on 8/25/16.
 */
public class MyPosition extends RealmObject {

    @PrimaryKey
    private String id;

    @SerializedName("lat")
    private Double lat;

    @SerializedName("lng")
    private Double lng;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
