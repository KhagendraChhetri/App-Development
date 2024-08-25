package com.example.tripify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
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
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.HashMap;

public class fragmentBooking extends Fragment {

    private static final String ARG_DESTINATION_ID = "destination_id";
    private static final String ARG_COUNT = "count";

    private String destinationId;
    private int ticketCount;
    private double totalPrice; // To store the total price calculated

    private TextView txtDestinationInfo;
    private TextView txtUserInfo;
    private TextView txtTotalPrice;
    private Button btnConfirm;
    private PaymentSheet paymentSheet;
    private String paymentIntentClientSecret;
    public fragmentBooking() {
        // Required empty public constructor
    }

    public static fragmentBooking newInstance(String destinationId, int ticketCount) {
        fragmentBooking fragment = new fragmentBooking();
        Bundle args = new Bundle();
        args.putString(ARG_DESTINATION_ID, destinationId);
        args.putInt(ARG_COUNT, ticketCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            destinationId = getArguments().getString(ARG_DESTINATION_ID);
            ticketCount = getArguments().getInt(ARG_COUNT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);
        txtDestinationInfo = view.findViewById(R.id.txt_destination_info);
        txtUserInfo = view.findViewById(R.id.txt_user_info);
        txtTotalPrice = view.findViewById(R.id.txt_total_price);
        btnConfirm = view.findViewById(R.id.button_checkout);

        displayUserInfo();

        loadDestinationDetails();


       PaymentConfiguration.init(getContext(), "pk_test_51P8jXCP5LXLR5LbEOPeo88XSDCPtf5xkuXuTNfFe4IfWAyxNj9m7N0UmIBhHrNorSInuOvsDIruTpoXDiUZ4vj3u0023WGeDMV");
        setupPaymentSheet();
      // btnConfirm.setOnClickListener(v -> presentPaymentSheet());
        btnConfirm.setOnClickListener(v -> confirmBooking());

        return view;
    }

    private void loadDestinationDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("destinations").document(destinationId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String destinationName = document.getString("name");
                        String description = document.getString("description");
                        double price = Double.parseDouble(document.getString("price"));

                        txtDestinationInfo.setText(String.format("Destination: %s\nDescription: %s\nPrice: $%.2f\nTickets: %d",
                                destinationName, description, price, ticketCount));

                        totalPrice = ticketCount * price;
                        txtTotalPrice.setText(String.format("Total Price: $%.2f", totalPrice));
                    } else {
                        txtDestinationInfo.setText("No destination data available.");
                    }
                })
                .addOnFailureListener(e -> txtDestinationInfo.setText("Failed to load destination data. Error: " + e.getMessage()));
    }

    private void displayUserInfo() {
        Log.d("displayUserInfo", "create displayUserInfo");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d("displayUserInfo", "user not null");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(document -> {
                        String name = document.getString("fullName");
                        String email = document.getString("email");
                        Log.d("displayUserInfo", "user not null value"+user.getUid()+ email);

                        txtUserInfo.setText(String.format("User Name: %s\nEmail: %s", name, email));
                    })
                    .addOnFailureListener(e -> txtUserInfo.setText("Failed to load user data. Error: " + e.getMessage()));
        } else {
            txtUserInfo.setText("No user data available.");
        }
    }
    private void bookingconfirmpage() {

        Fragment bookingconfirmation = new bookingconfirmation();

        // Obtain the FragmentManager from the current Fragment
        FragmentManager fragmentManager = getParentFragmentManager(); // or getFragmentManager() for older API levels

        // Begin a transaction and replace the BookingFragment with ConfirmationFragment
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentdestination, bookingconfirmation); // Ensure you have a container in your layout
        fragmentTransaction.addToBackStack(null); // Optional: Adds the transaction to the back stack
        fragmentTransaction.commit();
    }

    private void confirmBooking() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            String email = user.getEmail();  // Get the current user's email

            // First fetch the destination details to ensure they are up to date
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("destinations").document(destinationId)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String destinationName = document.getString("name");
                            String priceString = document.getString("price");

                            try {
                                double price = Double.parseDouble(priceString);
                                double totalBookingPrice = ticketCount * price;

                                // Proceed to add the booking using the TravelManager
                                TravelManager travelManager = new TravelManager();
                                travelManager.addBooking(destinationId, destinationName, email, ticketCount, totalBookingPrice);
                                Toast.makeText(getContext(), "Booking confirmed!", Toast.LENGTH_LONG).show();
                                sendBookingConfirmationNotification(destinationName, ticketCount, totalBookingPrice);
                                bookingconfirmpage();
                            } catch (NumberFormatException e) {
                                Toast.makeText(getContext(), "Error in price format", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Destination data not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to load destination details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "You must be logged in to confirm a booking.", Toast.LENGTH_LONG).show();
        }
    }

    private void sendBookingConfirmationNotification(String destinationName, int ticketCount, double totalBookingPrice) {
        Context context = getActivity();  // Get the activity context from the fragment

        // Intent to start the DashboardActivity
        Intent intent = new Intent(context, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("fragmentToLoad", "UserBookingFragment");  // DashboardActivity needs to recognize this

        // Correct PendingIntent creation to support Android 12 and above
        int pendingIntentFlag;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntentFlag = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;  // Use FLAG_IMMUTABLE as recommended
        } else {
            pendingIntentFlag = PendingIntent.FLAG_UPDATE_CURRENT;  // Existing apps targeting lower than Android 12 will continue to work
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingIntentFlag);

        // Notification channel ID and NotificationManager initialization
        String channelId = "notification_channel";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Booking Notifications", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for booking confirmations");
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("Booking Confirmed")
                .setContentText("Your booking to " + destinationName + " for " + ticketCount + " tickets at $" + totalBookingPrice + " has been confirmed.")
                .setSmallIcon(R.drawable.logos)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);  // Auto-cancel the notification when it is clicked

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }


//stripe payement flow

    private void setupPaymentSheet() {
        // Setup payment sheet with required parameters
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        // Assume clientSecret is fetched from your backend and assign it here
        paymentIntentClientSecret = "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";

    }

    private void presentPaymentSheet() {
        paymentIntentClientSecret = "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";

        // You would need to fetch or have the Payment Intent client secret here
        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Tripify")
                .build();

        // Check if the client secret is not null
        if (paymentIntentClientSecret != null) {
            paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration);
        } else {
            Toast.makeText(getContext(), "Payment setup not ready", Toast.LENGTH_SHORT).show();
            Log.d("paymentIntentClientSecret", "null value");

        }
    }

    private void onPaymentSheetResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(getContext(), "Payment Success!", Toast.LENGTH_LONG).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(getContext(), "Payment Canceled", Toast.LENGTH_LONG).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast.makeText(getContext(), "Payment Failed: ", Toast.LENGTH_LONG).show();
        }
    }


    private void fetchClientSecret() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("amount", 2000);  // For example: $20.00

        FirebaseFunctions.getInstance()
                .getHttpsCallable("com.stripe.v1.payment_intent")
                .call(data)
                .addOnSuccessListener(httpsCallableResult -> {
                    // Handle successful response which includes the client secret
                    HashMap<String, String> result = (HashMap<String, String>) httpsCallableResult.getData();
                    String clientSecret = result.get("clientSecret");
                    if (clientSecret != null) {
                        // Use the client secret for your payment flow
                        Log.d("PaymentActivity", "Received client secret: " + clientSecret);
                        // Proceed to use the client secret with Stripe PaymentSheet or Elements
                    } else {
                        Log.e("PaymentActivity", "Client secret was null.");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Log.e("PaymentActivity", "Error fetching client secret: " + e.getMessage());
                });
    }
}






