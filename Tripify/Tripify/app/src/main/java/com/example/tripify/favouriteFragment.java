package com.example.tripify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class favouriteFragment extends Fragment implements DestinationAdapter.OnDestinationClickListener {

    private RecyclerView destinationRecyclerView;
    private DestinationAdapter adapter;
    private List<Map<String, Object>> destinations;
    private List<String> destinationIds;
    private TextView error;



    public favouriteFragment() {
        // Required empty public constructor
    }

    public static favouriteFragment newInstance() {
        return new favouriteFragment();
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
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        error = view.findViewById(R.id.textViewEmpty);

        setupRecyclerView(view);
        fetchDestinations();
// Ensure your fragment is attached to its host activity
        if (getActivity() instanceof Dashboard) {
            ((Dashboard) getActivity()).setCheckedItemFromFragment(R.id.nav_favourite);
        }
        return view;
    }

    private void setupRecyclerView(View view) {
        destinationRecyclerView = view.findViewById(R.id.destinationRecyclerView);
        destinationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DestinationAdapter(destinations, destinationIds, this);
        destinationRecyclerView.setAdapter(adapter);
    }



    private void fetchDestinations() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("favorites")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {


                        destinations.clear();
                        destinationIds.clear();
                        int x= 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the destination ID from the favorites document
                            String destinationId = document.getString("destinationId");

                            // Fetch the destination details using the destination ID
                            fetchDestinationDetails(destinationId);

                            x++;
                        }
                        if (x==0) {
                            error.setVisibility(View.VISIBLE);
                            destinationRecyclerView.setVisibility(View.GONE);
                        } else {
                            error.setVisibility(View.GONE);
                            destinationRecyclerView.setVisibility(View.VISIBLE);
                        }
                        // Show or hide the TextView based on whether the list is empty

                    } else {

                        Toast.makeText(getContext(), "Error fetching favorite destinations: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void fetchDestinationDetails(String destinationId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("destinations").document(destinationId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        // Add destination details to the list
                        destinations.add(document.getData());
                        destinationIds.add(document.getId());
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Destination details not found for ID: " + destinationId, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch destination details for ID: " + destinationId, Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestinationClick(String destinationId) {
        openFragment(SingleDestination.newInstance(destinationId));
    }

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentdestination, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
