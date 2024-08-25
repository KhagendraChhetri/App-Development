package com.example.tripify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private TravelManager travelManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        fAuth = FirebaseAuth.getInstance();

        // Initialize Firebase App Check
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());

        travelManager = new TravelManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    private void checkUserStatus() {
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser == null) {
            // No user is signed in, redirect to Login activity
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish(); // This call is used to close the MainActivity once it's no longer visible.
        } else {
            // User is signed in, redirect to Dashboard activity
            Intent intent = new Intent(MainActivity.this, Dashboard.class);
            startActivity(intent);
            finish(); // This call is used to close the MainActivity once it's no longer visible.
        }
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "notification_channel";
            CharSequence channelName = "Booking Confirmation";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }
}