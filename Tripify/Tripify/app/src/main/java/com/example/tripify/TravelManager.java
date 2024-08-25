package com.example.tripify;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TravelManager {

    private static final String TAG = "TravelManager";
    private FirebaseFirestore firestore;

    public TravelManager() {
        firestore = FirebaseFirestore.getInstance();
    }


    public interface OnDestinationDataReceived {
        void onDataReceived(List<Map<String, Object>> destinations);
        void onError(Exception e);
    }

    // Adds a new destination to Firestore
    public void addDestination(String name, String description, String imageURL, String location, String price) {
        Map<String, Object> destination = new HashMap<>();
        destination.put("name", name);
        destination.put("description", description);
        destination.put("imageURL", imageURL);
        destination.put("location", location);
        destination.put("price", price);

        Log.d("Database", "Adding destination xxx...");

        firestore.collection("destinations")
                .add(destination)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Database", "Destination added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("Database", "Error adding destination", e);
                });

        Log.d("Database", "Add destination xxx.");
    }


    // Edits an existing destination
    public void editDestination(String destinationId, Map<String, Object> updates) {
        firestore.collection("destinations")
                .document(destinationId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Destination updated successfully!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating destination", e));
    }

    // Deletes a destination
    public void deleteDestination(String destinationId) {
        firestore.collection("destinations")
                .document(destinationId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Destination deleted successfully!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting destination", e));
    }

    // Books a destination

    public void addBooking(String destinationId, String destinationName, String userEmail, int ticket, double totalPrice) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = formatter.format(new Date());
        Map<String, Object> booking = new HashMap<>();
        booking.put("destinationId", destinationId);
        booking.put("destinationName", destinationName);
        booking.put("userEmail", userEmail);
        booking.put("ticket", ticket);
        booking.put("totalPrice", totalPrice);
        booking.put("date", formattedDate); // Store current time as booking date

        db.collection("bookings")
                .add(booking)
                .addOnSuccessListener(documentReference -> {
                    Log.d("TravelManager", "Booking added with ID: " + documentReference.getId());
                    // Here you can notify the user that the booking has been successfully added.
                })
                .addOnFailureListener(e -> {
                    Log.w("TravelManager", "Error adding booking", e);
                    // Here you should handle the error, perhaps notifying the user that the booking failed.
                });
    }


    // Fetches all bookings for a user
    public void fetchBookings(String userId) {
        firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.w(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
    public void addUserDetails(String userId, String fullName, String email) {
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("userId", userId);
        userDetails.put("fullName", fullName);
        userDetails.put("email", email);

        firestore.collection("users")
                .document(userId)
                .set(userDetails)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User details added successfully!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding user details", e));
    }

    public void fetchDestinations(TravelManager.OnDestinationDataReceived callback) {
        List<Map<String, Object>> destinations = new ArrayList<>();
        firestore.collection("destinations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> destination = document.getData();
                            destinations.add(destination);
                        }
                        callback.onDataReceived(destinations);
                    } else {
                        callback.onError(task.getException());
                    }
                });
    }
    public void updateUserProfile(String email, Map<String, Object> updates) {
        if (email == null || updates == null || updates.isEmpty()) {
            Log.e("UpdateProfile", "User ID is null or updates are invalid.");
            return;
        }

        firestore.collection("users").document(email)
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d("UpdateProfile", "User profile updated successfully!"))
                .addOnFailureListener(e -> Log.e("UpdateProfile", "Error updating user profile", e));
    }

    public void checkUserExists(String email, UserExistenceCallback callback) {
        Query query = firestore.collection("users").whereEqualTo("email", email);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // If the task is successful, check if any documents were found
                boolean exists = task.getResult() != null && !task.getResult().isEmpty();
                callback.onUserExistenceChecked(exists);
            } else {
                // If the task fails, log the error and return false
                Log.e("TravelManager", "Error checking user existence", task.getException());
                callback.onUserExistenceChecked(false);
            }
        });
    }

    public interface UserExistenceCallback {
        void onUserExistenceChecked(boolean exists);
    }


    public void addattraction(String name, String description, String imageURL, String destinationName
            ) {
        Map<String, Object> attraction = new HashMap<>();
        attraction.put("name", name);
        attraction.put("description", description);
        attraction.put("imageURL", imageURL);
        attraction.put("destinationName", destinationName);
        //destination.put("price", price);

        Log.d("Database", "Adding attraction xxx...");

        firestore.collection("attraction")
                .add(attraction)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Database", "attraction added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("Database", "Error adding attraction", e);
                });

        Log.d("Database", "Add destination xxx.");
    }
}




