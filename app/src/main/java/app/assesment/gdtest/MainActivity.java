package app.assesment.gdtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;


import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.UUID;


import app.assesment.gdtest.models.MyPosition;
import app.assesment.gdtest.presenter.MapPresenterImp;
import io.realm.Realm;
import io.realm.RealmConfiguration;


public class MainActivity extends Activity {

    private GoogleMap map;
    private MapPresenterImp presenter;
    private LocationManager locationManager;
    private ArrayList<LatLng> listPosition = new ArrayList<LatLng>();
    private BroadcastReceiver receiver;
    private MyService myService;
    private boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MapPresenterImp();

        // Realm Initialize
        RealmConfiguration config = new RealmConfiguration.Builder(getApplicationContext()).build();
        Realm.setDefaultConfiguration(config);


        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.e("LOKASI", "receiver " + action);
                if (action.equalsIgnoreCase("PROVIDER_INTENT_SERVICE'")){
                    Toast.makeText(MainActivity.this,"service reset" , Toast.LENGTH_SHORT).show();

                    startMyService();
                }

                if (action.equalsIgnoreCase("LOCATION_INTENT_SERVICE")) {

                    String latLong = intent.getStringExtra("LatLng");
                    if (!TextUtils.isEmpty(latLong)) {
                        Toast.makeText(MainActivity.this,"position:" + latLong , Toast.LENGTH_SHORT).show();
                        Log.e("LOKASI", "on receive" + latLong);
                        LatLng latlong = new LatLng(Double.parseDouble(latLong.split(",")[0]), Double.parseDouble(latLong.split(",")[1]));
                        if (!listPosition.contains(latlong)) {
                            listPosition.add(latlong);
                        }
                        presenter.setMarker(map, latlong);
                        presenter.drawPolyLine(map, listPosition);
                    }
                }
            }
        };



        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.moveCamera(CameraUpdateFactory.zoomTo(15));
                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        presenter.setMarker(map, latLng);
                    }
                });


                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showAlertGPS();
                }else{
                    startMyService();
                }
            }
        });

    }

    private void showAlertGPS() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 10);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                startMyService();
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("LOCATION_INTENT_SERVICE");
        filter.addAction("PROVIDER_INTENT_SERVICE");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        startMyService();
    }

    private void startMyService(){
        Intent intent = new Intent(this, MyService.class);
        intent.setAction("startListening");
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

//    private void stopMyServices(){
//        unbindService(mServiceConnection);
//        Intent intent = new Intent(this, MyService.class);
//        intent.setAction("stopListening");
//        stopService(intent);
//    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(mServiceConnection);
            isBound = false;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder myBinder = (MyService.MyBinder) service;
            myService = myBinder.getInstance();
            isBound = true;
        }
    };
}
