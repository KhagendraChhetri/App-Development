package com.example.tripify;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class verify_email extends AppCompatActivity {
    private static final int DISABLE_TIME = 60000; // 60 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        TextView statusTextView = findViewById(R.id.text_view_status);
        Button resendEmailButton = findViewById(R.id.button_resend_email);
        Button restart_app = findViewById(R.id.button_verified);


        restart_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                // animation to smooth out the transition
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                // kill the current process
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        });

        resendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Disable the button to prevent multiple clicks
                resendEmailButton.setEnabled(false);

                // Change button text to indicate processing
                resendEmailButton.setText("Email Sent ");

                // Attempt to resend the verification email
                resendVerificationEmail();

                // Show a toast message immediately
                Toast.makeText(verify_email.this, "Verification email sent.", Toast.LENGTH_SHORT).show();

                // Handler to re-enable the button after 60 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resendEmailButton.setEnabled(true);
                        resendEmailButton.setText("Resend Verification Email");
                    }
                }, 60000); // 60000 milliseconds = 60 seconds
            }
        });

    }

    private void resendVerificationEmail() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("email", "Email sent.");
                        }
                    }
                });

    }

}