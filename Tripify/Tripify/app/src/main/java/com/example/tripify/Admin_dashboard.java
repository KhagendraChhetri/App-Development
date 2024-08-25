package com.example.tripify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Admin_dashboard extends Fragment {

    private ImageButton tickets, destination;
    private TextView totalDestinationsText, totalUsersText, totalBookingsText, totalAmountText, totalTicketsText;
    private FirebaseFirestore db;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public Admin_dashboard() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        // Initialize the UI elements
        tickets = view.findViewById(R.id.imageButton);
        destination = view.findViewById(R.id.imageButton2);
        totalDestinationsText = view.findViewById(R.id.totalDestinationsText);
        totalUsersText = view.findViewById(R.id.totalUsersText);
        totalBookingsText = view.findViewById(R.id.totalBookingsText);
        totalAmountText = view.findViewById(R.id.totalAmountText);
        totalTicketsText = view.findViewById(R.id.totalTicketsText);

        // Set onClick listeners
        tickets.setOnClickListener(v -> openFragment(new TicketFragment()));
        destination.setOnClickListener(v -> openFragment(new admin_destination()));

        // Fetch and update statistics
        fetchStatistics();

        return view;
    }

    private void fetchStatistics() {
        // Fetch total destinations
        db.collection("destinations").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int totalDestinations = task.getResult().size();
                totalDestinationsText.setText(String.valueOf(totalDestinations));
            }
        });

        // Fetch total users
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int totalUsers = task.getResult().size();
                totalUsersText.setText(String.valueOf(totalUsers));
            }
        });

        // Fetch bookings and calculate total amount and ticket count for this month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startOfMonth = calendar.getTime();
        String startOfMonthStr = formatter.format(startOfMonth);

        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        Date endOfMonth = calendar.getTime();
        String endOfMonthStr = formatter.format(endOfMonth);

        db.collection("bookings")
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int totalBookings = 0;
                        double totalAmount = 0.0;
                        int totalTickets = 0;

                        for (DocumentSnapshot document : task.getResult()) {
                            String dateStr = document.getString("date");

                            if (dateStr != null) {
                                try {
                                    // Parse the date string into a Date object
                                    Date bookingDate = formatter.parse(dateStr);

                                    // Check if the booking date falls within the current month
                                    if (bookingDate != null && bookingDate.after(startOfMonth) && bookingDate.before(endOfMonth)) {
                                        totalBookings++;

                                        Double amount = document.getDouble("totalPrice");
                                        if (amount != null) {
                                            totalAmount += amount;
                                        }

                                        Long tickets = document.getLong("ticket");
                                        if (tickets != null) {
                                            totalTickets += tickets.intValue();
                                        }
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        totalBookingsText.setText(String.valueOf(totalBookings));
                        totalAmountText.setText(String.format(Locale.getDefault(), "$%.2f", totalAmount));
                        totalTicketsText.setText(String.valueOf(totalTickets));
                    }
                });
    }

    private void openFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentdestination, fragment)
                .addToBackStack(null)
                .commit();
    }
}
