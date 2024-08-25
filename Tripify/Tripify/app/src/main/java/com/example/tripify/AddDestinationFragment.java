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

public class AddDestinationFragment extends Fragment {

    private EditText nameEditText, descriptionEditText, imageURLEditText, locationEditText, priceEditText;
    private Button addButton;
    private TravelManager travelManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        travelManager = new TravelManager();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_destination, container, false);
        nameEditText = view.findViewById(R.id.nameEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        imageURLEditText = view.findViewById(R.id.imageURLEditText);
        locationEditText = view.findViewById(R.id.locationEditText);
        priceEditText = view.findViewById(R.id.priceEditText);
        addButton = view.findViewById(R.id.addButton);

        addButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            String imageURL = imageURLEditText.getText().toString().trim();
            String location = locationEditText.getText().toString().trim();
            String price = priceEditText.getText().toString().trim();

            if (name.isEmpty() || description.isEmpty() || imageURL.isEmpty() || location.isEmpty() || price.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                travelManager.addDestination(name, description, imageURL, location, price);
                        Toast.makeText(getActivity(), "Destination added successfully!", Toast.LENGTH_SHORT).show();
                        navigateToAdminDestination();}
            });

        return view;
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
