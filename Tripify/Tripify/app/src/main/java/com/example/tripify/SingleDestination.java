package com.example.tripify;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleDestination extends Fragment {

    private static final String ARG_UID = "uid";
    private String destinationId;
    private ImageView imgDestination;
    private TextView txtDestinationName, txtDestinationDescription, txtPriceDestination, txtCount;
    private Button btnAddCount, btnSubCount, btnPay, btnFav;
    private ImageButton btnLocation;
    private int count = 1;
    private String locationName;
    private boolean isFavorited;

    private RecyclerView recyclerAttractions;
    private AttractionAdapter attractionAdapter;
    private final List<Map<String, Object>> attractionList = new ArrayList<>();

    public SingleDestination() {
        // Required empty public constructor
    }

    public static SingleDestination newInstance(String uid) {
        SingleDestination fragment = new SingleDestination();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            destinationId = getArguments().getString(ARG_UID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_destination, container, false);

        // Initialize UI components
        imgDestination = view.findViewById(R.id.img_destination);
        txtDestinationName = view.findViewById(R.id.txt_destination_name);
        txtDestinationDescription = view.findViewById(R.id.txt_destination_description);
        txtPriceDestination = view.findViewById(R.id.txt_price_destination);
        txtCount = view.findViewById(R.id.txt_count);
        btnAddCount = view.findViewById(R.id.btn_addCount);
        btnSubCount = view.findViewById(R.id.btn_subCount);
        btnPay = view.findViewById(R.id.btn_pay);
        btnLocation = view.findViewById(R.id.buttonLocation);
        btnFav = view.findViewById(R.id.buttonfav);

        // Set up initial count
        txtCount.setText(String.valueOf(count));

        // Initialize RecyclerView with horizontal layout
        recyclerAttractions = view.findViewById(R.id.recycler_attractions);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerAttractions.setLayoutManager(horizontalLayoutManager);
        attractionAdapter = new AttractionAdapter(attractionList);
        recyclerAttractions.setAdapter(attractionAdapter);

        // Load data and set button listeners
        loadDestinationDetails();
        checkFavoriteItem(destinationId);
        setupButtonListeners();

        return view;
    }

    private void setupButtonListeners() {
        btnAddCount.setOnClickListener(v -> {
            count++;
            txtCount.setText(String.valueOf(count));
        });

        btnSubCount.setOnClickListener(v -> {
            if (count > 1) count--;
            txtCount.setText(String.valueOf(count));
        });

        btnPay.setOnClickListener(v -> navigateToBookingFragment());

        btnLocation.setOnClickListener(v -> openMapFragment1());
        setUpFavoriteButton();
    }

    private void loadDestinationDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("destinations").document(destinationId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String imageUrl = document.getString("imageURL");
                        Picasso.get().load(imageUrl).into(imgDestination);
                        txtDestinationName.setText(document.getString("name"));
                        txtDestinationDescription.setText(document.getString("description"));
                        txtPriceDestination.setText( "$"+document.getString("price"));
                        locationName = document.getString("name");

                        loadAttractions(locationName);
                    } else {
                        txtDestinationName.setText("No data available.");
                    }
                })
                .addOnFailureListener(e -> txtDestinationName.setText("Failed to load data. Error: " + e.getMessage()));
    }

    private void loadAttractions(String destinationName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("attraction")
                .whereEqualTo("destinationName", destinationName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    attractionList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        attractionList.add(doc.getData());
                    }
                    attractionAdapter.notifyDataSetChanged();

                    // Show "no attractions" message if the list is empty
                    TextView noAttractionsText = getView().findViewById(R.id.txt_no_attractions);
                    if (attractionList.isEmpty()) {
                        noAttractionsText.setVisibility(View.VISIBLE);
                    } else {
                        noAttractionsText.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error loading attractions", e));
    }


    @SuppressLint("QueryPermissionsNeeded")
    private void openMapFragment() {
        if (locationName != null && !locationName.isEmpty()) {
            Uri uri = Uri.parse("geo:0,0?q=" + Uri.encode(locationName));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(locationName)));
                startActivity(mapIntent);
            }
        } else {
            Toast.makeText(getActivity(), "Location not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToBookingFragment() {
        Fragment bookingFragment = fragmentBooking.newInstance(destinationId, count);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentdestination, bookingFragment)
                .addToBackStack(null)
                .commit();
    }

    private void checkFavoriteItem(String destinationId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (user != null) {
            String userId = user.getUid();
            db.collection("favorites")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("destinationId", destinationId)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            btnFav.setBackgroundResource(R.drawable.baseline_favorite_24);
                            isFavorited = true;
                        } else {
                            btnFav.setBackgroundResource(R.drawable.baseline_favorite_border_24);
                            isFavorited = false;
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Check Favorite", "Failed to check favorite item. Error: " + e.getMessage()));
        }
    }

    private void setUpFavoriteButton() {
        btnFav.setOnClickListener(view -> {
            if (isFavorited) removeFromFavorites();
            else addToFavorites();
        });
    }

    private void addToFavorites() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = user != null ? user.getUid() : null;

        if (userId != null) {
            Map<String, Object> favoriteData = new HashMap<>();
            favoriteData.put("userId", userId);
            favoriteData.put("destinationId", destinationId);

            db.collection("favorites")
                    .add(favoriteData)
                    .addOnSuccessListener(documentReference -> {
                        isFavorited = true;
                        Context context = getContext(); // Get context safely
                        if (context != null) {
                            Toast.makeText(context, "Destination added to favorites", Toast.LENGTH_SHORT).show();
                        }                        btnFav.setBackgroundResource(R.drawable.baseline_favorite_24);
                    })
                    .addOnFailureListener(e -> Log.e("Add to Favorites", "Failed to add to favorites. Error: " + e.getMessage()));
        }
    }

    private void removeFromFavorites() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = user != null ? user.getUid() : null;

        if (userId != null) {
            db.collection("favorites")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("destinationId", destinationId)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            db.collection("favorites").document(document.getId()).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        isFavorited = false;
                                        Toast.makeText(getContext(), "Destination removed from favorites", Toast.LENGTH_SHORT).show();
                                        btnFav.setBackgroundResource(R.drawable.baseline_favorite_border_24);
                                    })
                                    .addOnFailureListener(e -> Log.e("Remove from Favorites", "Failed to remove from favorites. Error: " + e.getMessage()));
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Remove from Favorites", "Failed to query favorites. Error: " + e.getMessage()));
        }
    }

    private void openMapFragment1() {
        // Pass the location name to the map fragment
        Bundle bundle = new Bundle();
        bundle.putString("locationName", locationName);

        // Create and open the map fragment
        GoogleMapFragment mapFragment = new GoogleMapFragment();
        mapFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentdestination, mapFragment)
                .addToBackStack(null)
                .commit();
    }
}
