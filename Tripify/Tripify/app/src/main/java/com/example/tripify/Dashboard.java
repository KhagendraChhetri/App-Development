package com.example.tripify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button btnLogout;
    private Button btnEditProfile;
    private FirebaseAuth auth;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon, ticketicon;
    LinearLayout contentView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        user.reload();

        if (user != null) {
            user.reload();
            user.reload();
            if (user.isEmailVerified()) {
                // Log for debugging: "User email is not verified."
                Log.d("verify", "User is  verified: " + user.isEmailVerified());
            } else {

                blockDashboardAccess();
                Log.d("verify", "User is not verified: " + user.isEmailVerified());
            }
        } else {
            Log.e("verify", "User object is null, cannot check verification status.");
        }


        menuIcon = findViewById(R.id.menu_icon);
        ticketicon = findViewById(R.id.ticket_icon);
        contentView = findViewById(R.id.content);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        setupNavigationDrawer();

        loadDefaultFragment();


        if (getIntent().hasExtra("fragmentToLoad") && "UserBookingFragment".equals(getIntent().getStringExtra("fragmentToLoad"))) {
            loadUserBookingFragment();
        }
        ticketicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("booking", "clicked the view  ");
                navigationView.setCheckedItem(R.id.nav_booking);

                // Create a new instance of MyBookings fragment
                Fragment UserBookingFragment = new UserBookingFragment();

                // Use FragmentManager and FragmentTransaction to add/replace the fragment in the container
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentdestination, UserBookingFragment)
                        .addToBackStack(null)  // Optional: adds the transaction to the back stack
                        .commit();
            }
        });
    }

    private void loadDefaultFragment() {
        if (!getIntent().hasExtra("fragmentToLoad")) {
            if (user != null && user.getEmail() != null && user.getEmail().equals("chhetrikhagendra96@gmail.com")) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentdestination, new Admin_dashboard())
                        .commit();
                ticketicon.setVisibility(View.GONE);
                navigationView.setCheckedItem(R.id.nav_admin_dashboard);
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentdestination, new fragment_destinations())
                        .commit();
                navigationView.setCheckedItem(R.id.nav_destination);
            }
        }}

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d("navbar", "Item selected: " + item.getItemId());
        int id = item.getItemId();
        Log.d("navbar", "clicked  with ID: " + id);

        if (id == R.id.nav_destination) {
            Fragment fragment_destinations = new fragment_destinations();

            // Use FragmentManager and FragmentTransaction to add/replace the fragment in the container
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentdestination, fragment_destinations)
                    .addToBackStack(null)  // Optional: adds the transaction to the back stack
                    .commit();

        } else if (id == R.id.nav_profile) {
            Fragment EditProfile = new EditProfileFragment();

            // Use FragmentManager and FragmentTransaction to add/replace the fragment in the container
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentdestination, EditProfile)
                    .addToBackStack(null)  // Optional: adds the transaction to the back stack
                    .commit();
        } else if (id == R.id.nav_booking) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.fragmentdestination, UserBookingFragment.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("name") // Name can be null
                    .commit();
        }else if (id == R.id.nav_settings) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.fragmentdestination, setting.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("name") // Name can be null
                    .commit();
        }else if (id == R.id.nav_budgetCalculator) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.fragmentdestination, budgetCalculatorFragment.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("name") // Name can be null
                    .commit();
        } else if (id == R.id.nav_contact_us) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.fragmentdestination, aboutFragment.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("name") // Name can be null
                    .commit();
        }else if (id == R.id.nav_currencyConverter) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.fragmentdestination, CurrencyConverterFragment.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("name") // Name can be null
                    .commit();
        }else if (id == R.id.nav_logout) {
            // Handle logout
            logout();
            startActivity(new Intent(Dashboard.this, Login.class));
            finish();
        } else if (id == R.id.nav_exit) {
            exitApp();
        }
        else if (id == R.id.nav_admin_dashboard) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.fragmentdestination, Admin_dashboard.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("name") // Name can be null
                    .commit();
        }else if (id == R.id.nav_destination_manager) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.fragmentdestination, admin_destination.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("name") // Name can be null
                    .commit();
        }else if (id == R.id.nav_ticket_manager) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.fragmentdestination, TicketFragment.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("name") // Name can be null
                    .commit();
        }else if (id == R.id.nav_ticket_scanner) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.fragmentdestination, TicketScannerFragment.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("name") // Name can be null
                    .commit();
        }
        else if (id == R.id.nav_favourite) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.fragmentdestination, favouriteFragment.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("name") // Name can be null
                    .commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, Login.class));
        finish();
    }

    private void exitApp() {
        finishAffinity();  // Close all activities
        System.exit(0);
    }


    private void setupNavigationDrawer() {
        // Clear the existing menu items
        Menu menu = navigationView.getMenu();
        menu.clear();
        menu.close();
        // Inflate the menu from the user navigation XML file

         if(user.getEmail().equals("chhetrikhagendra96@gmail.com")) {
            navigationView.inflateMenu(R.menu.admin_menu);
            menu.close();

        } else {
            navigationView.inflateMenu(R.menu.userlogged_menu);
            menu.close();

        }

        //navigationView.inflateMenu(R.menu.userlogged_menu);
        navigationView.bringToFront();

        // Set the item selection listener
        navigationView.setNavigationItemSelectedListener(this);

        // Set the default checked item

        // Set the click listener for the menu icon
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query Firestore for user data based on email
        db.collection("users").whereEqualTo("email", user.getEmail()).limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Assuming we take the first document of the results
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                        // Retrieve each field and use a fallback of an empty string if the field is not present
                        String name = documentSnapshot.getString("fullName") != null ? documentSnapshot.getString("fullName") : "";

                        View headerView = navigationView.getHeaderView(0);
                        TextView emailis = headerView.findViewById(R.id.nav_email);
                        TextView usernameis = headerView.findViewById(R.id.nav_username);
                        usernameis.setText(name);
                        emailis.setText(user.getEmail());
                        Log.d("Navhead", "get data "+ name+" "+ user.getEmail());


                     ///navigationView.setCheckedItem(R.id.nav_destination);

    } });}

    private void loadUserBookingFragment() {
        // Create an instance of UserBookingFragment
        Fragment userBookingFragment = new UserBookingFragment();
        // Begin the transaction
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragmentdestination, userBookingFragment, null) // Make sure R.id.fragmentdestination is correct
                .setReorderingAllowed(true)
                .addToBackStack(null) // Optional: specify a name for the back stack state, or use null
                .commit();
    }


    private void blockDashboardAccess() {

        Intent intent = new Intent(this, verify_email.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        }
    public void setCheckedItemFromFragment(int menuItemId) {
        if (navigationView != null) {
            navigationView.setCheckedItem(menuItemId);
        }
    }


}


