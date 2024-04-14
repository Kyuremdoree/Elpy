package uqac.dim.elpy;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.health.connect.datatypes.ExerciseRoute;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uqac.dim.elpy.utilitaire.MarkerInfo;

public class PingMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private FrameLayout mapLayout;

    private Marker actualMarker;

    private boolean isPinging = false;

    private Map<Marker, MarkerInfo> markerMap;

    FusedLocationProviderClient fusedLocationProviderClient;

    Location lastKnownLocation;

    private LatLng defaultLocation;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping_map);
        markerMap = new HashMap<>();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        defaultLocation = new LatLng(48.419008, -71.052621);
        mapLayout = (FrameLayout) findViewById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = findViewById(R.id.add_marker);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPinging = true;
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        updateLocationUI();

        getDeviceLocation();

        map.moveCamera(CameraUpdateFactory
                .newLatLngZoom(defaultLocation, 12));
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                if (isPinging) {
                    Marker marker = map.addMarker(new MarkerOptions().position(latLng));
                    afficheInfo(marker);

                    isPinging = false;
                }
            }
        });
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoContents(@NonNull Marker marker) {
                return null;
            }

            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                View infoWindow = getLayoutInflater().inflate(R.layout.activity_info_marker, null);

                TextView nom = infoWindow.findViewById(R.id.show_name);
                TextView description = infoWindow.findViewById(R.id.show_description);

                Button button = infoWindow.findViewById(R.id.supprime_button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        marker.remove();
                        markerMap.remove(marker);
                    }
                });

                MarkerInfo markerInfo = markerMap.get(marker);
                if (markerInfo != null) {
                    nom.setText(markerInfo.getNom());
                    description.setText(markerInfo.getDescription());
                }
                return infoWindow;
            }
        });
    }

    public void afficheInfo(Marker marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PingMapActivity.this);
        builder.setTitle("Info");

        View tmpLayout = getLayoutInflater().inflate(R.layout.activity_write_marker, null);
        builder.setView(tmpLayout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText nameEditText = tmpLayout.findViewById(R.id.name);
                EditText commentEditText = tmpLayout.findViewById(R.id.comment);

                MarkerInfo info = new MarkerInfo(nameEditText.getText().toString(), commentEditText.getText().toString(), marker);
                markerMap.put(marker, info);
                marker.showInfoWindow();
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                marker.remove();
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
            }
        } catch (SecurityException e) {
            Log.i("PingMap", "Catch :" + e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), 12));
                        }
                        else {
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, 12));
                        }
                    } else {
                        Log.i("DIM", "Current location is null. Using defaults.");
                        Log.i("DIM", "Exception: %s" + task.getException());
                        map.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, 12));
                        map.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("PingMap","Exception: %s" + e.getMessage());
        }

    }
}
