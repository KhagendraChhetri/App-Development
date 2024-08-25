package com.example.tripify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment {

    private EditText editAddress, editPostcode, editName, editPhone;
    private Button btnUpdate;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private TravelManager travelManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_edit_profile, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        travelManager = new TravelManager();

        editAddress = view.findViewById(R.id.editAddress);
        editName = view.findViewById(R.id.editFullName);
        editPostcode = view.findViewById(R.id.editPostcode);
        editPhone = view.findViewById(R.id.editPhone);
        btnUpdate = view.findViewById(R.id.btnUpdate);

        if (user != null && user.getEmail() != null) {
            loadUserProfileByEmail(user.getEmail());
            Log.d("profile", "clicked the view  "+ user.getEmail());

        } else {
            Toast.makeText(getActivity(), "No user logged in xx", Toast.LENGTH_SHORT).show();
        }

        btnUpdate.setOnClickListener(v -> updateProfile());

        return view;
    }

    private void loadUserProfileByEmail(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query Firestore for user data based on email
        db.collection("users").whereEqualTo("email", email).limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Assuming we take the first document of the results
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                        // Retrieve each field and use a fallback of an empty string if the field is not present
                        String address = documentSnapshot.getString("address") != null ? documentSnapshot.getString("address") : "";
                        String postcode = documentSnapshot.getString("postcode") != null ? documentSnapshot.getString("postcode") : "";
                        String phone = documentSnapshot.getString("phone") != null ? documentSnapshot.getString("phone") : "";
                        String name = documentSnapshot.getString("fullName") != null ? documentSnapshot.getString("fullName") : "";

                        // Set the values to the EditText fields
                        editAddress.setText(address);
                        editPostcode.setText(postcode);
                        editPhone.setText(phone);
                        editName.setText(name); // Make sure editFullName is properly initialized
                        Log.d("profile", "clicked the view = "+address+""+postcode+""+phone+""+name);

                        // Log to help debugging
                        Log.d("UserProfile", "Profile loaded: " + name + ", " + email + ", " + address + ", " + postcode + ", " + phone);
                    } else {
                        // If no document is found, clear all fields
                        clearFields();
                        Log.d("UserProfile", "No profile found for email: " + email);
                        Toast.makeText(getActivity(), "Profile not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Log and show error if the query fails
                    Log.e("UserProfile", "Error loading profile: " + e.getMessage(), e);
                    Toast.makeText(getActivity(), "Error loading profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    clearFields();
                });
    }

    private void clearFields() {
        // Clear all fields if no data is found or an error occurs
        editAddress.setText("");
        editPostcode.setText("");
        editPhone.setText("");
        editName.setText(""); // Assuming you have this field initialized and accessible
    }



    private void updateProfile() {
        String address = editAddress.getText().toString();
        String postcode = editPostcode.getText().toString();
        String phone = editPhone.getText().toString();
        String fullName = editName.getText().toString();

        Map<String, Object> updates = new HashMap<>();
        updates.put("address", address);
        updates.put("postcode", postcode);
        updates.put("phone", phone);
        updates.put("fullName", fullName);

        if (user != null) {
            travelManager.updateUserProfile(user.getUid(), updates);
            Toast.makeText(getActivity(), "Updated sucessfully", Toast.LENGTH_SHORT).show();
            // Create an intent to navigate to the Dashboard activity
            Intent intent = new Intent(getActivity(), Dashboard.class);
            // Optionally add flags to clear the back stack
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            // Finish the current fragment's activity to avoid coming back
            getActivity().finish();


        } else {
            Log.e("EditProfileFragment", "No authenticated user found.");
            Toast.makeText(getActivity(), "No authenticated user found.", Toast.LENGTH_SHORT).show();
        }
    }


}
