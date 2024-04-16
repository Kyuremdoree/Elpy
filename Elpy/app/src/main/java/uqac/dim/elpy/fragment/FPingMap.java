package uqac.dim.elpy.fragment;

import static android.Manifest.*;
import static android.Manifest.permission.*;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

import uqac.dim.elpy.PingMapActivity;
import uqac.dim.elpy.database.RoomDB;
import uqac.dim.elpy.utilitaire.MarkerEntity;
import uqac.dim.elpy.utilitaire.MarkerInfo;

import uqac.dim.elpy.R;

public class FPingMap extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private Marker actualMarker;
    private boolean isPinging = false;
    private Map<Marker, MarkerInfo> markerMap;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private LatLng defaultLocation;

    private RoomDB database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_ping_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
            }
        }
        database = RoomDB.getInstance(getContext());
        //retrieveMarkersFromDatabase();
        markerMap = new HashMap<>();
        Context context = view.getContext();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        defaultLocation = new LatLng(48.419008, -71.052621);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        FloatingActionButton fab = view.findViewById(R.id.add_marker);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPinging = true;
            }
        });
    }

    private void requestPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            explainAndRequest();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
    }

    private void explainAndRequest() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
    }




    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        updateLocationUI();
        getDeviceLocation();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

                //insertMarkerIntoDatabase(info);
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
            if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
            if (ContextCompat.checkSelfPermission(requireContext().getApplicationContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), 12));
                        } else {
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
            Log.e("PingMap", "Exception: %s" + e.getMessage());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (markerMap != null && !markerMap.isEmpty()) {
            ArrayList<Parcelable> markerInfoList = new ArrayList<>();
            for (Map.Entry<Marker, MarkerInfo> entry : markerMap.entrySet()) {
                MarkerInfo markerInfo = entry.getValue();
                markerInfoList.add(markerInfo);

            }
            outState.putParcelableArrayList("markerInfoList", markerInfoList);
        }
    }



    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<Parcelable> markerInfoList = savedInstanceState.getParcelableArrayList("markerInfoList");
            if (markerInfoList != null) {
                for (Parcelable parcelable : markerInfoList) {
                    if (parcelable instanceof MarkerInfo) {
                        MarkerInfo markerInfo = (MarkerInfo) parcelable;
                        markerMap.put(markerInfo.getMarker(), markerInfo);
                    }
                }

            }
        }
    }

    /*private void insertMarkerIntoDatabase(MarkerInfo markerInfo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                database.mainDAO().insertMarker(markerInfo.toMarkerEntity());
            }
        }).start();
    }

    private void retrieveMarkersFromDatabase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MarkerEntity> markerEntities = database.mainDAO().getAllMarkers();

                for (MarkerEntity markerEntity : markerEntities) {
                    MarkerInfo markerInfo = convertToMarkerInfo(markerEntity);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            markerMap.put(markerInfo.getMarker(), markerInfo);
                        }
                    });
                }
            }
        }).start();
    }

    private MarkerInfo convertToMarkerInfo(MarkerEntity markerEntity) {
        LatLng position = new LatLng(markerEntity.latitude, markerEntity.longitude);
        MarkerOptions markerOptions = new MarkerOptions().position(position).title(markerEntity.name).snippet(markerEntity.comment);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Marker marker = map.addMarker(markerOptions);
                markerMap.put(marker, markerInfo);
            }
        });

        return new MarkerInfo(markerEntity.name, markerEntity.comment, marker);
    }

    private void runOnUiThread(Runnable action) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(action);
    }


    private void refreshMap() {
        if (map == null) return;
        map.clear();
        for (MarkerInfo markerInfo : markerMap.values()) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(markerInfo.getMarker().getPosition())
                    .title(markerInfo.getNom())
                    .snippet(markerInfo.getDescription());
            Marker marker = map.addMarker(markerOptions);
        }
    }*/


}