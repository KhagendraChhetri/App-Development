package com.example.tripify;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.List;

public class GoogleMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private String locationName;
    private PlacesClient placesClient;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        Bundle args = getArguments();
        if (args != null) {
            locationName = args.getString("locationName");
        }

        // Initialize Places SDK
        Places.initialize(requireContext(), getString(R.string.places_api_key));
        placesClient = Places.createClient(requireContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Display the location based on the provided locationName
        displayLocation();
    }

    private void displayLocation() {
        // Use Geocoding to get the LatLng for the provided locationName
        Geocoder geocoder = new Geocoder(requireContext());
        LatLng locationLatLng;
        try {
            List<Address> addressList = geocoder.getFromLocationName(locationName, 1);
            if (!addressList.isEmpty()) {
                Address address = addressList.get(0);
                locationLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(locationLatLng).title(locationName));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 15));
                Log.d("Attractions", " display main attractions");

                // Fetch nearby places

            } else {
                // Handle case when no address found for the location name
                Toast.makeText(requireContext(), "No location found for the provided name", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            // Handle Geocoder errors
            Toast.makeText(requireContext(), "Error while geocoding: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }





}