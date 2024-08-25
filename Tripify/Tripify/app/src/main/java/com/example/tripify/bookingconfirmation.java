package com.example.tripify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class bookingconfirmation extends Fragment {

    private TextView bookingId;
    private TextView confirmationDetails;
    private Button btnOk;

    public bookingconfirmation() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookingconfirmation, container, false);

        bookingId = view.findViewById(R.id.bookingno);
        confirmationDetails = view.findViewById(R.id.confirmationDetails);
        btnOk = view.findViewById(R.id.btnOk);

        loadBookingData();
        setupButtonListener();

        return view;
    }

    private void loadBookingData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            String email = user.getEmail();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("bookings")
                    .whereEqualTo("userEmail", email)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                            String destinationName = documentSnapshot.getString("destinationName") != null ? documentSnapshot.getString("destinationName") : "";
                            String dateStr = documentSnapshot.getString("date") != null ? documentSnapshot.getString("date") : "";
                            int ticket = documentSnapshot.getLong("ticket") != null ? documentSnapshot.getLong("ticket").intValue() : 0;
                            double totalPrice = documentSnapshot.getDouble("totalPrice") != null ? documentSnapshot.getDouble("totalPrice") : 0.0;
                            String bookingid = documentSnapshot.getId();

                            // Format the date string or handle it as needed
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                Date date = dateFormat.parse(dateStr);  // Parsing the string into a Date object
                                String formattedDate = new SimpleDateFormat("MMM dd, yyyy HH:mm").format(date);

                                bookingId.setText(bookingid);
                                confirmationDetails.setText(String.format("Destination: %s\nBooking Date: %s\nPrice: $%.2f\nTickets: %d",
                                        destinationName, formattedDate, totalPrice, ticket));

                            } catch (ParseException e) {
                                Log.e("DateFormatError", "Error parsing the date: " + e.getMessage());
                                confirmationDetails.setText(String.format("Destination: %s\nBooking Date: %s\nPrice: $%.2f\nTickets: %d",
                                        destinationName, "Invalid date format", totalPrice, ticket));
                            }

                            Log.d("BookingData", "Booking loaded successfully: " + destinationName + ", " + dateStr + ", " + totalPrice + ", " + ticket+" "+bookingid);
                        } else {
                            Log.d("BookingData", "No booking found for email: " + email);
                            Toast.makeText(getActivity(), "No recent booking found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("BookingData", "Error loading booking: " + e.getMessage(), e);
                        Toast.makeText(getActivity(), "Error loading booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "You must be logged in to confirm a booking.", Toast.LENGTH_LONG).show();
        }
    }



    private void setupButtonListener() {
        btnOk.setOnClickListener(v -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), Dashboard.class);
                startActivity(intent);
                getActivity().finish(); // Optionally finish the current activity if you don't want it on the back stack
            }
        });
    }
}
