package com.example.tripify;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TicketFragment extends Fragment implements TicketAdapter.OnTicketClickListener {

    private RecyclerView ticketRecyclerView;
    private TicketAdapter adapter;
    private List<Map<String, Object>> tickets;
    private List<String> ticketIds;
    private Button scanTicket;

    public TicketFragment() {
        // Required empty public constructor
    }

    public static TicketFragment newInstance() {
        return new TicketFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tickets = new ArrayList<>();
        ticketIds = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket, container, false);
        setupRecyclerView(view);
        fetchTickets();

        if (getActivity() instanceof Dashboard) {
            ((Dashboard) getActivity()).setCheckedItemFromFragment(R.id.nav_ticket_manager);
        }
        return view;
    }

    private void setupRecyclerView(View view) {
        ticketRecyclerView = view.findViewById(R.id.ticketRecyclerView);
        scanTicket=view.findViewById(R.id.btnScanTkt);
        scanTicket.setOnClickListener(v -> openScanFragment());
        ticketRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TicketAdapter(tickets, ticketIds, this);
        ticketRecyclerView.setAdapter(adapter);
    }

    private void fetchTickets() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            String userEmail = auth.getCurrentUser().getEmail(); // Ensure the userEmail is up-to-date

            db.collection("bookings")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            tickets.clear();
                            ticketIds.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> ticket = document.getData();
                                tickets.add(ticket);
                                ticketIds.add(document.getId());
                            }
                            if (tickets.isEmpty()) {
                                Toast.makeText(getContext(), "No tickets found", Toast.LENGTH_SHORT).show();
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Error fetching tickets: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTicketClick(String ticketId) {
        // This method can be used to show ticket details
        // Currently, it uses the same logic as delete for demonstration
        showDeleteConfirmationDialog(ticketId);
    }

    @Override
    public void onTicketDelete(String ticketId) {
        showDeleteConfirmationDialog(ticketId);
    }

    public void showDeleteConfirmationDialog(String ticketId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to delete this ticket?");
        builder.setPositiveButton("Delete", (dialog, which) -> deleteTicket(ticketId));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteTicket(String ticketId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("bookings").document(ticketId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Ticket deleted successfully!", Toast.LENGTH_SHORT).show();
                    fetchTickets(); // Refresh the ticket list to show updated data
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error deleting ticket", Toast.LENGTH_SHORT).show());
    }

    private void openScanFragment() {
        // Replace the current fragment with the scan fragment
        Fragment TicketScannerFragment = new TicketScannerFragment(); // Assume `ScanFragment` is your target fragment's name
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace the current fragment with the new one
        transaction.replace(R.id.fragmentdestination, TicketScannerFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
