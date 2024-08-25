package com.example.tripify;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;
import java.util.regex.Pattern;

public class TicketScannerFragment extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private CompoundBarcodeView barcodeScannerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ticket_scanner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barcodeScannerView = view.findViewById(R.id.barcodeScannerView);

        // Request camera permissions if needed
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            initializeScanner();
        }
    }

    private void initializeScanner() {
        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                barcodeScannerView.pause(); // Pause the scanner during the dialog display
                String scannedTicketId = result.getText();
                if (isValidFirestoreDocumentId(scannedTicketId)) {
                    validateAndFetchTicket(scannedTicketId);
                } else {
                    showValidationDialog("Bad Request", "The scanned QR code is not associated with a valid ticket.");
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                // Optional: implement visual feedback if needed
            }
        });
        barcodeScannerView.resume();
    }

    private boolean isValidFirestoreDocumentId(String documentId) {
        // Firestore document IDs cannot contain slashes and should follow general path naming conventions.
        // Adjust this regex to add further validation rules if required.
        return Pattern.matches("^[a-zA-Z0-9_-]+$", documentId);
    }

    private void validateAndFetchTicket(String ticketId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("bookings")
                .document(ticketId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String destinationName = document.getString("destinationName");
                            String userEmail = document.getString("userEmail");
                            int ticketCount = document.getLong("ticket").intValue();
                            double totalPrice = document.getDouble("totalPrice");

                            StringBuilder message = new StringBuilder();
                            message.append("Destination: ").append(destinationName).append("\n")
                                    .append("User Email: ").append(userEmail).append("\n")
                                    .append("Tickets: ").append(ticketCount).append("\n")
                                    .append("Total Price: $").append(totalPrice);

                            showValidationDialog("Ticket Valid", message.toString());
                        } else {
                            showValidationDialog("Ticket Invalid", "The ticket with ID " + ticketId + " is not found or invalid.");
                        }
                    } else {
                        showValidationDialog("Error", "Error checking the ticket: " + task.getException().getMessage());
                    }
                });
    }

    private void showValidationDialog(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        barcodeScannerView.resume(); // Resume the scanner when the dialog is closed
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeScanner();
            } else {
                Toast.makeText(requireContext(), "Camera permission is required to scan tickets", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeScannerView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }
}
