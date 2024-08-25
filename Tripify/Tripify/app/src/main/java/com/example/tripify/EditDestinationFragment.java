package com.example.tripify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditDestinationFragment extends Fragment {

    private EditText nameEditText, descriptionEditText, imageURLEditText, locationEditText, priceEditText;
    private Button updateButton;
    private FirebaseFirestore db;
    private String destinationId;  // This will be set when creating an instance of the fragment

    public static EditDestinationFragment newInstance(String destinationId) {
        EditDestinationFragment fragment = new EditDestinationFragment();
        Bundle args = new Bundle();
        args.putString("destinationId", destinationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            destinationId = getArguments().getString("destinationId");
        }
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_destination, container, false);
        initializeForm(view);
        loadDestinationDetails();
        return view;
    }

    private void initializeForm(View view) {
        nameEditText = view.findViewById(R.id.nameEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        imageURLEditText = view.findViewById(R.id.imageURLEditText);
        locationEditText = view.findViewById(R.id.locationEditText);
        priceEditText = view.findViewById(R.id.priceEditText);
        updateButton = view.findViewById(R.id.updateButton);

        updateButton.setOnClickListener(v -> updateDestination());
    }

    private void loadDestinationDetails() {
        db.collection("destinations").document(destinationId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        nameEditText.setText(documentSnapshot.getString("name"));
                        descriptionEditText.setText(documentSnapshot.getString("description"));
                        imageURLEditText.setText(documentSnapshot.getString("imageURL"));
                        locationEditText.setText(documentSnapshot.getString("location"));
                        priceEditText.setText(documentSnapshot.getString("price"));
                    } else {
                        Toast.makeText(getActivity(), "Destination not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error loading destination", Toast.LENGTH_SHORT).show());
    }

    private void updateDestination() {
        String name = nameEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String imageURL = imageURLEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String price = priceEditText.getText().toString();

        if (name.isEmpty() || description.isEmpty() || imageURL.isEmpty() || location.isEmpty() || price.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {
        db.collection("destinations").document(destinationId)
                .update("name", name, "description", description, "imageURL", imageURL, "location", location, "price", price)
                .addOnSuccessListener(aVoid -> {
                    if (getContext() != null) {
                    Toast.makeText(getActivity(), "Destination updated successfully", Toast.LENGTH_SHORT).show();
                    navigateToAdminDestination();} else {}
                })                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error updating destination", Toast.LENGTH_SHORT).show());}
    }

    private void navigateToAdminDestination() {
        // Get the FragmentManager
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // Replace the current fragment with a new instance of admin_destination
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentdestination, admin_destination.newInstance())
                .setReorderingAllowed(true)
                .addToBackStack(null)  // Adjust this as per your back stack management strategy
                .commit();
    }

}
