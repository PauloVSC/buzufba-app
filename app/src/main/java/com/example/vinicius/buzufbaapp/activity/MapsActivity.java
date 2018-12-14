package com.example.vinicius.buzufbaapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.vinicius.buzufbaapp.R;
import com.example.vinicius.buzufbaapp.config.ConfiguracaoFireBase;
import com.example.vinicius.buzufbaapp.model.LocationData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;

    private Marker currentLocationMaker;
    private LatLng currentLocationLatLong;
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;

    private Button btn_share_location;
    private FloatingActionButton btn_plus, btn_b1, btn_b2, btn_b3;
    private Animation animeOpen, animeClose, animeRotation, animeReverse;
    boolean isOpen = false;

    private int buzufbaMarkerType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        verificaLogado();

        startGettingLocations();
        mDatabase = ConfiguracaoFireBase.getFireBase();
        getMarkers();

        //Add buttons for trucks
        btn_plus = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        btn_b1 = (FloatingActionButton) findViewById(R.id.btn_buzufba1);
        btn_b2 = (FloatingActionButton) findViewById(R.id.btn_buzufba2);
        btn_b3 = (FloatingActionButton) findViewById(R.id.btn_buzufba3);
        btn_share_location = (Button) findViewById(R.id.button);

        //Add animations to show buttons
        animeOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.float_button_open);
        animeClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.float_button_close);
        animeRotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotation_clock);
        animeReverse = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotation_reverse);

        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen){

                    btn_b1.startAnimation(animeClose);
                    btn_b2.startAnimation(animeClose);
                    btn_b3.startAnimation(animeClose);
                    btn_plus.startAnimation(animeReverse);
                    btn_b1.setClickable(false);
                    btn_b2.setClickable(false);
                    btn_b3.setClickable(false);
                    isOpen = false;


                } else {

                    btn_b1.startAnimation(animeOpen);
                    btn_b2.startAnimation(animeOpen);
                    btn_b3.startAnimation(animeOpen);
                    btn_plus.startAnimation(animeRotation);
                    btn_b1.setClickable(true);
                    btn_b2.setClickable(true);
                    btn_b3.setClickable(true);
                    isOpen = true;

                }
            }
        });

        btn_b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buzufbaMarkerType != 1){
                    buzufbaMarkerType = 1;
                    btn_share_location.startAnimation(animeOpen);
                    btn_b1.setBackgroundTintList(ColorStateList.valueOf(0xFFFF0000));
                } else {
                    buzufbaMarkerType = 0;
                    btn_share_location.startAnimation(animeClose);
                    btn_b1.setBackgroundTintList(ColorStateList.valueOf(0xFF0000FF));

                }
            }
        });

        btn_b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buzufbaMarkerType != 2){
                    buzufbaMarkerType = 2;
                    btn_b1.startAnimation(animeClose);

                } else {
                    buzufbaMarkerType = 0;
                }
            }
        });

        btn_b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buzufbaMarkerType != 3){
                    buzufbaMarkerType = 3;
                    btn_b1.startAnimation(animeClose);

                } else {
                    buzufbaMarkerType = 0;
                    mDatabase.child("location").child("id").removeValue();
                }
            }
        });

    }


    private void verificaLogado(){

        firebaseAuth = ConfiguracaoFireBase.getFirebaseAuth();

        if (firebaseAuth.getCurrentUser() == null){
            Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng ondina = new LatLng(-13.002010, -38.507675);
        //mMap.addMarker(new MarkerOptions().position(ondina).title("Marker in Odina"));

        //CameraPosition cameraPosition = new CameraPosition.Builder().zoom(10).target(ondina).build();
        //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public void onLocationChanged(Location location) {
        if (currentLocationMaker != null) {
            currentLocationMaker.remove();
        }
        //Add marcador
        currentLocationLatLong = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocationLatLong);
        markerOptions.title("Localização atual");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        //
        currentLocationMaker = mMap.addMarker(markerOptions);

        //Move para nova localização
        CameraPosition cameraPosition = new CameraPosition.Builder().zoom(10).target(currentLocationLatLong).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        LocationData locationData = new LocationData(location.getLatitude(), location.getLongitude(), buzufbaMarkerType);

        mDatabase.child("location").child(String.valueOf(new Date().getTime())).setValue(locationData);

        Toast.makeText(this, "Localização atualizada", Toast.LENGTH_SHORT).show();

        mMap.clear();
        getMarkers();
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS desativado!");
        alertDialog.setMessage("Ativar GPS?");
        alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }


    private void startGettingLocations() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean canGetLocation = true;
        int ALL_PERMISSIONS_RESULT = 101;
        long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;// Distance in meters
        long MIN_TIME_BW_UPDATES = 1000 * 10;// Time in milliseconds

        ArrayList<String> permissions = new ArrayList<>();
        ArrayList<String> permissionsToRequest;

        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        //Check if GPS and Network are on, if not asks the user to turn on
        if (!isGPS && !isNetwork) {
            showSettingsAlert();
        } else {
            // check permissions

            // check permissions for later versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    canGetLocation = false;
                }
            }
        }


        //Checks if FINE LOCATION and COARSE Location were granted
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show();
            return;
        }

        //Starts requesting location updates
        if (canGetLocation) {
            if (isGPS) {
                lm.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            } else if (isNetwork) {
                // from Network Provider

                lm.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            }
        } else {
            Toast.makeText(this, "Não é possível obter a localização", Toast.LENGTH_SHORT).show();
        }
    }

    private void getMarkers(){

        Query lastQuery = mDatabase.child("location").orderByKey().limitToLast(1);

        lastQuery.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        if (dataSnapshot.getValue() != null)
                            getAllLocations((Map<String,Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private void getAllLocations(Map<String,Object> locations) {



        for (Map.Entry<String, Object> entry : locations.entrySet()){

            Date newDate = new Date(Long.valueOf(entry.getKey()));
            Map singleLocation = (Map) entry.getValue();
            LatLng latLng = new LatLng((Double) singleLocation.get("latitude"), (Double)singleLocation.get("longitude"));
            addGreenMarker(newDate, latLng);

        }


    }

    private void addGreenMarker(Date newDate, LatLng latLng) {

        SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(dt.format(newDate));
        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        if (buzufbaMarkerType == 1){
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else if (buzufbaMarkerType == 2){
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        } else if (buzufbaMarkerType == 3){
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        }

        currentLocationMaker = mMap.addMarker(markerOptions);
    }



    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        mMap.clear();
    }
}
