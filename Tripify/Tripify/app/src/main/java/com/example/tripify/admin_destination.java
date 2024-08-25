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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class admin_destination extends Fragment implements DestinationAdapter.OnDestinationClickListener {

    private RecyclerView destinationRecyclerView;
    private admin_destinationAdapter adapter;
    private List<Map<String, Object>> destinations;
    private List<String> destinationIds;
    private Button addDes;

    public admin_destination() {
        // Required empty public constructor
    }

    public static admin_destination newInstance() {
        return new admin_destination();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        destinations = new ArrayList<>();
        destinationIds = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_destination, container, false);

        setupRecyclerView(view);
        fetchDestinations();
        addDes = view.findViewById(R.id.btnAddDes);
        addDes.setOnClickListener(v -> openAddDestinationFragment());


        if (getActivity() instanceof Dashboard) {
            ((Dashboard) getActivity()).setCheckedItemFromFragment(R.id.nav_destination_manager);
        }


        return view;
    }

    private void setupRecyclerView(View view) {
        destinationRecyclerView = view.findViewById(R.id.recycleViewDesAdmin);
        destinationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new admin_destinationAdapter(destinations, destinationIds, this::onDestinationClick, this::showDeleteConfirmationDialog);
        destinationRecyclerView.setAdapter(adapter);
    }

    private void fetchDestinations() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("destinations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        destinations.clear();
                        destinationIds.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            destinations.add(document.getData());
                            destinationIds.add(document.getId());
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error fetching destinations: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onDestinationClick(String destinationId) {
        openFragment(EditDestinationFragment.newInstance(destinationId));
    }

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentdestination, fragment); // Make sure the container ID is correct
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void showDeleteConfirmationDialog(String destinationId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to delete this destination?");
        builder.setPositiveButton("Delete", (dialog, which) -> deleteDestination(destinationId));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteDestination(String destinationId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("destinations").document(destinationId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Destination deleted successfully!", Toast.LENGTH_SHORT).show();
                    restartFragment();                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error deleting destination", Toast.LENGTH_SHORT).show());
    }

    public void restartFragment() {
        // Get the FragmentManager
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // Replace the current fragment with a new instance of admin_destination
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentdestination, admin_destination.newInstance())
                .setReorderingAllowed(true)
                .addToBackStack(null)  // Adjust this as per your back stack management strategy
                .commit();
    }

    private void openAddDestinationFragment() {
        AddDestinationFragment addDestinationFragment = new AddDestinationFragment(); // Assume you have such a fragment
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentdestination, addDestinationFragment); // Ensure you use the correct container ID
        fragmentTransaction.addToBackStack(null); // Optional: Add transaction to back stack
        fragmentTransaction.commit();
    }



}
