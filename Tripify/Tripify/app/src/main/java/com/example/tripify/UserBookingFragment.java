package com.example.tripify;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserBookingFragment extends Fragment implements BookingAdapter.OnBookingClickListener {

    private RecyclerView bookingRecyclerView;
    private BookingAdapter adapter;
    private List<Map<String, Object>> bookings;
    private List<String> bookingIds;
    private Dialog qrDialog;

    public UserBookingFragment() {
        // Required empty public constructor
    }

    public static UserBookingFragment newInstance() {
        return new UserBookingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookings = new ArrayList<>();
        bookingIds = new ArrayList<>();
        Log.d("booking", "oncreate userbookingfragment  ");

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userbooking, container, false);
        bookingRecyclerView = view.findViewById(R.id.bookingRecyclerView);
        bookingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookingAdapter(bookings, bookingIds, this);
        bookingRecyclerView.setAdapter(adapter);
        Log.d("booking", "view on create view  ");
        fetchBookings();

        return view;
    }

    @Override
    public void onBookingClick(String bookingId) {
        //Toast.makeText(getContext(), "Booking clicked: " + bookingId, Toast.LENGTH_SHORT).show();
        generateAndShowQRCode(bookingId);
    }

    private void fetchBookings() {
        Log.d("booking", "fetch booking  ");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            String userEmail = auth.getCurrentUser().getEmail(); // Ensure the userEmail is up-to-date

            // Adjusted query to include sorting by date
            db.collection("bookings")
                    .whereEqualTo("userEmail", userEmail)
                    .orderBy("date", Query.Direction.DESCENDING) // Add this to sort by date in descending order
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            bookings.clear();
                            bookingIds.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> booking = document.getData();
                                bookings.add(booking);
                                bookingIds.add(document.getId()); // Store the unique ID for each booking
                            }
                            if (bookings.isEmpty()) {
                                // Optionally handle empty booking list scenario here
                                Toast.makeText(getContext(), "No bookings found", Toast.LENGTH_SHORT).show();
                                Log.d("booking", "empty booking  ");
                            }
                            adapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                        } else {
                            // Handle failures
                            Toast.makeText(getContext(), "Error fetching bookings: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            // Handle case where user is not logged in or user data could not be retrieved
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    //qr code generator


    private void generateAndShowQRCode(String data) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 200, 200);
            Bitmap bmp = Bitmap.createBitmap(bitMatrix.getWidth(), bitMatrix.getHeight(), Bitmap.Config.RGB_565);
            for (int x = 0; x < bitMatrix.getWidth(); x++) {
                for (int y = 0; y < bitMatrix.getHeight(); y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            Log.d("QRCode", "value"+ data);

            showQRCodeDialog(bmp,data);
        } catch (WriterException e) {
            Log.e("QRCode", "Error generating QR code", e);
            Toast.makeText(getContext(), "Error generating QR code: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showQRCodeDialog(Bitmap qrCodeBitmap, String bid) {

        dismissDialog();
        qrDialog = new Dialog(getContext());
        qrDialog.setContentView(R.layout.qr_code_dialog);
        ImageView qrImage = qrDialog.findViewById(R.id.qrCodeImageView);
        qrImage.setImageBitmap(qrCodeBitmap);
        Button close = qrDialog.findViewById(R.id.btnclosed);
        TextView details = qrDialog.findViewById(R.id.qrdetails);
        Log.d("QRCode", "value of bid "+ bid);

        details.setText("Booking Id:\n" + bid);
        close.setOnClickListener(v -> qrDialog.dismiss());
        qrDialog.show();
    }

    private void dismissDialog() {
        if (qrDialog != null && qrDialog.isShowing()) {
            qrDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissDialog();  // Ensure dialog is dismissed to avoid leaks
    }



}
