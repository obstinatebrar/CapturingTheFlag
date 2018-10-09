package com.example.surinderkahlon.mapdemo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    FirebaseDatabase database;
    DatabaseReference root;
    String key;
    TextView textView;
    Button flagButton;
    String playerName;
    String playerTeam;
    PolylineOptions options;
    ArrayList<LatLng> vertices;

    private ChildEventListener flagEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        database = FirebaseDatabase.getInstance();
        root = database.getReference();
        Intent intent = getIntent();
        playerName = intent.getStringExtra("name");
        playerTeam = intent.getStringExtra("team");
        key = intent.getStringExtra("key");
        Log.d("intentttttdataaaa", key + "," + playerName + "," + playerTeam);
        textView = (TextView) findViewById(R.id.textView);
        flagButton = (Button) findViewById(R.id.flagButton);
        flagButton.setVisibility(View.INVISIBLE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // user did NOT give permission
            Log.d("surinderbhago", "no permission granted, requesting p now...");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 5);


        } else {

            // mMap.setMyLocationEnabled(true);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {


                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                //  User user = new User(latitude,longitude);
                Player player = new Player(playerName, playerTeam, latitude, longitude);
                //  Log.d("kahlonn", String.valueOf(location));
                // String key = root.child("Players").push().getKey();
                // Log.d("kahlonn", key);
                //  key = root.child("Players").push().getKey();
                root.child("Players").child(playerTeam).child(key).setValue(player);
                //String key = root.child("Players").push().getKey();
                Log.d("keyyyy", key);
                Log.d("surinderbhago", String.valueOf(latitude));
                System.out.print(latitude);


            }


        }
        attachLocationListener();
        flagFoundListener();
    }

    Double latt;
    Double longg;

    private void attachLocationListener() {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                double lat = location.getLatitude();
                double longi = location.getLongitude();
                // User user = new User(latitude,longitude);
                final Player player = new Player(playerName, playerTeam, lat, longi);
                String team = player.playerTeam;
                Log.d("kahlonn", String.valueOf(location));
                ;
                // String key = root.child("Players").push().getKey();
                Log.d("Node ID", key);
                root.child("Players").child(playerTeam).child(key).setValue(player);
                Location playerLocation = new Location("");
                playerLocation.setLatitude(lat);
                playerLocation.setLongitude(longi);
                if (playerLocation != null) {
                    PolyUtil.containsLocation(new LatLng(playerLocation.getLatitude(), playerLocation.getLongitude()), vertices, false);
                }
                if (PolyUtil.containsLocation(new LatLng(playerLocation.getLatitude(), playerLocation.getLongitude()), vertices, false)) {

                } else {
                    Toast.makeText(MapsActivity.this, "You are outside the play area.\nYou are in the prison: ", Toast.LENGTH_LONG).show();
                    //goToPrison();

                }


                root.child("Flags").child(playerTeam).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        Log.d("flaggfora", String.valueOf(dataSnapshot.child("A")));
                        // DataSnapshot data = dataSnapshot.child("A");
                        Player playr = dataSnapshot.getValue(Player.class);
                        latt = playr.latitude;
                        longg = playr.longitude;
                        Location loc1 = new Location("");
                        loc1.setLatitude(latt);
                        loc1.setLongitude(longg);

                        Log.d("Flag latitude", String.valueOf(latt));
                        float distanceInMeters = loc1.distanceTo(location);
                        if (distanceInMeters < 25) {
                            flagButton.setVisibility(View.VISIBLE);
                        }
                        textView.setText("FlagA is " + distanceInMeters + " meters away from you...Keep trying..");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }


    //code to send notification to all players after getting flag
    public void flagFoundButtonPressed(View v) {
        root.child("Flags").child(playerTeam).child("flagFoundStatus").setValue(playerTeam);

    }

    private void flagFoundListener() {
        if (flagEventListener == null) {

            flagEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Flag p = dataSnapshot.getValue(Flag.class);
                    Log.d("aaaaaaa", String.valueOf(dataSnapshot));
                    String st = p.flagFoundStatus;
                    AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity


                            .this).create();
                    alertDialog.setTitle("GAME IS OVER/FLAG IS FOUND BY TEAM" + st);
                    alertDialog.setMessage("Congratulations to Team" + st + " members");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();


                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            root.child("Flags").addChildEventListener(flagEventListener);
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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // user did NOT give permission
            Log.d("surinderbhago", "no permission granted, requesting p now...");
            // ActivityCompat.requestPermissions(this, new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION}, 5);


        } else {
            Log.d("surinderbhago", "Yup, I have permission");
            mMap.setMyLocationEnabled(true);
            // System.out.print(mMap.g);
            try {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Double latitude = location.getLatitude();
                Double longitude = location.getLongitude();
                LatLng myLocation = new LatLng(latitude, longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                mMap.addMarker(new MarkerOptions().position(myLocation).title(playerName + "/" + playerTeam));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
                addMarkers();
            } catch (Exception e) {
                Log.d("error", String.valueOf(e));
            }
        }


    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 5: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "GO GO GO!", Toast.LENGTH_SHORT).show();
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //  mMap.setMyLocationEnabled(true);
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            Location location1 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            Double latitude = location1.getLatitude();
                            Double longitude = location1.getLongitude();
                            LatLng myLocation = new LatLng(latitude, longitude);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
                        }
                    }
                } else {
                    // permission was denied!
                    Toast.makeText(this, "Sorry, person clicked DENY", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    private void addMarkers() {
        if (mMap != null) {

            LatLng brampton = new LatLng(43.7744671,-79.3360452);
            LatLng missisauga = new LatLng(43.7747073,-79.3348436);
            LatLng vaughan = new LatLng(43.7732611,-79.3343983);
            //LatLng missisauga = new LatLng(43.7745353, -79.3364998);
            //LatLng vaughan = new LatLng(43.7731098,	-79.3353518);


            LatLng torontoCity = new LatLng(43.7730365,-79.3354336);
            LatLng torontoPublic = new LatLng(43.7744671,-79.3360452);
            LatLng vaughn2 = new LatLng(43.7732611,-79.3343983);



            List<LatLng> list = new ArrayList<>();
            list.add(torontoPublic);
            list.add(missisauga);
            list.add(vaughan);
            list.add(torontoCity);
            list.add(brampton);
            list.add(torontoPublic);
            list.add(vaughn2);

            vertices = new ArrayList<>();
            options = new PolylineOptions().width(2).color(Color.BLUE).geodesic(true);


            PolygonOptions rectOptions = new PolygonOptions()
                    .add(torontoPublic,
                            missisauga,
                            vaughan,torontoCity,brampton, torontoPublic,vaughn2);

            Polygon polygon = mMap.addPolygon(rectOptions);
            polygon.setFillColor(Color.LTGRAY);
            for (int z = 0; z < list.size(); z++) {
                LatLng point = list.get(z);
                vertices.add(point);
                options.add(point);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(torontoCity));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(missisauga, 17.0f));//10
        }
    }

    public void goToPrison() {
        Intent intent1 = new Intent(this, PrisonActivity.class);
        startActivity(intent1);
    }
}