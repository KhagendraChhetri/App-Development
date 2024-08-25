package com.example.tripify;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class Login extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth fAuth;
    private TravelManager travelManager;
    EditText mEmail, mPassword;
    Button mLoginBtn, mCreateBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        travelManager = new TravelManager();


        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        fAuth = FirebaseAuth.getInstance();

        // Google Sign-In button
        ImageButton googleSignInButton = findViewById(R.id.googleSignInBtn);
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());

        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.loginBtn);
        mCreateBtn = findViewById(R.id.createText);
        onclicklisteners();

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is Required.");
                    return;
                }

                if (password.length() < 6) {
                    mPassword.setError("Password Must be >= 6 Characters");
                    return;
                }

                // Authenticate the user
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Login.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //add reset password
        Button resetPasswordBtn = findViewById(R.id.btnReset);
        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetPasswordDialog();
            }
        });
    }

    private void signInWithGoogle() {
        Log.d("signin", "clicked  ");

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("signin", "authenticated");

                        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                        if (account != null) {
                            String userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                            String name = account.getDisplayName();
                            String email = account.getEmail();

                            // Debug log to check data is correct
                            Log.d("GoogleLogin", "UserID: " + userId + ", Name: " + name + ", Email: " + email);

                            if (travelManager != null) {
                                // Check if user data needs updating before saving
                                travelManager.checkUserExists(email, exists -> {
                                    if (!exists) {
                                        // User does not exist, add their details
                                        travelManager.addUserDetails(userId, name, email);
                                        navigateToDashboard();
                                        Log.d("GoogleLogin", "User data added to database");
                                    } else {
                                        Log.d("GoogleLogin", "User already exists, skipped adding to database");
                                        // Navigate to Dashboard
                                        navigateToDashboard();
                                    }

                                });
                            } else {
                                Log.e("GoogleLogin", "TravelManager is not initialized");
                                Toast.makeText(Login.this, "Internal error: TravelManager not setup", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Log.e("GoogleLogin", "Authentication failed", task.getException());
                        Toast.makeText(Login.this, "Google sign in authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(Login.this, Dashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();  // Ensure this activity is closed after navigation
    }

    private void onclicklisteners() {
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
            }
        });
    }

    private void showResetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");
        builder.setMessage("Please input your Email");


        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = input.getText().toString().trim();
                resetPassword(email);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void resetPassword(String email) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            return;
        }

        fAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showResetEmailSentDialog(email);
                            Toast.makeText(Login.this, "If you have your account registered with us then the reset email will be sent to " + email, Toast.LENGTH_SHORT).show();
                        } else {
                            showResetEmailFailedDialog(task.getException().getMessage());
                            Toast.makeText(Login.this, "Failed to send password reset email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showResetEmailSentDialog(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");
        builder.setMessage("If you have your account registered with us then the reset email will be sent to " + email);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showResetEmailFailedDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");
        builder.setMessage("Failed to send password reset email: " + errorMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


}