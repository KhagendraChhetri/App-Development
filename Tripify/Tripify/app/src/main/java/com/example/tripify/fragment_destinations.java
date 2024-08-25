package com.example.tripify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class fragment_destinations extends Fragment implements DestinationAdapter.OnDestinationClickListener {

    private RecyclerView destinationRecyclerView;
    private DestinationAdapter adapter;
    private List<Map<String, Object>> destinations;
    private List<String> destinationIds;
    private ImageButton btnA, btnB, btnC, btnD;
    NavigationView navigationView;


    public fragment_destinations() {
        // Required empty public constructor
    }

    public static fragment_destinations newInstance() {
        return new fragment_destinations();
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
        View view = inflater.inflate(R.layout.fragment_destinations, container, false);

        setupRecyclerView(view);
        setupImageButtons(view);
        fetchDestinations();
// Ensure your fragment is attached to its host activity
        if (getActivity() instanceof Dashboard) {
            ((Dashboard) getActivity()).setCheckedItemFromFragment(R.id.nav_destination);
        }
        return view;
    }

    private void setupRecyclerView(View view) {
        destinationRecyclerView = view.findViewById(R.id.destinationRecyclerView);
        destinationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DestinationAdapter(destinations, destinationIds, this);
        destinationRecyclerView.setAdapter(adapter);
    }

    private void setupImageButtons(View view) {
        btnA = view.findViewById(R.id.btna);
        btnB = view.findViewById(R.id.btnb);
        btnC = view.findViewById(R.id.btnc);
        btnD = view.findViewById(R.id.btnd);

        //navigationView = view.findViewById(R.id.navigation_view);

        btnA.setOnClickListener(v -> openFragment(new CurrencyConverterFragment()));


        btnB.setOnClickListener(v -> openFragment(new budgetCalculatorFragment()));
        btnC.setOnClickListener(v -> openFragment(new favouriteFragment()

        ));
        btnD.setOnClickListener(v -> openFragment(new EditProfileFragment()));
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
