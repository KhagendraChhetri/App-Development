package com.example.tripify;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {
    EditText regFullName, regEmail, regPassword, regConfirmPassword;
    Button btnRegister, btnGoToLogin;
    FirebaseAuth fAuth;
    private TravelManager travelManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regFullName = findViewById(R.id.regFullName);
        regEmail = findViewById(R.id.regEmail);
        regPassword = findViewById(R.id.regPassword);
        regConfirmPassword = findViewById(R.id.regConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);
        fAuth = FirebaseAuth.getInstance();
        travelManager = new TravelManager();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = regEmail.getText().toString().trim();
                String password = regPassword.getText().toString().trim();
                String confirmPassword = regConfirmPassword.getText().toString().trim();
                String name = regFullName.getText().toString().trim();


                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(Register.this, "All fields are required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(Register.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(Register.this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show();
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        emailverrification();
                        Toast.makeText(Register.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        String userId = fAuth.getCurrentUser().getUid();
                        Log.d("user","the added detail"+name+" "+email+" "+ userId);
                        travelManager.addUserDetails(userId, name, email); // Trigger method
                        sendRegistrationConfirmationNotification(email);
                        Intent intent = new Intent(Register.this, Dashboard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(Register.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });
    }

    void sendRegistrationConfirmationNotification(String userName) {
        Context context = this;  // Use activity context

        // Intent to start an activity, perhaps the MainActivity after registration
        Intent intent = new Intent(context, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // PendingIntent that launches the intent
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Use the existing notification channel ID
        String channelId = "notification_channel";

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("Registration Successful")
                .setContentText("Welcome, " + userName + "! Your registration is complete.")
                .setSmallIcon(R.drawable.logos)  // Make sure you have this icon in your drawable resources
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);  // Auto-cancel the notification when it is clicked

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, builder.build());  // Use a unique ID for registration notifications
    }

    private void emailverrification(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("verrification", "Email sent.");
                        }
                    }
                });
    }
}
