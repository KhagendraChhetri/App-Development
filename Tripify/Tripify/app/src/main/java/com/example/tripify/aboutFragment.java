package com.example.tripify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class aboutFragment extends Fragment {

    ImageButton map;
    LinearLayout call,email;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        String locationName= "Waterside Campus, University Dr, Northampton NN1 5PH";
         map = view.findViewById(R.id.mapButton);
         call = view.findViewById(R.id.call);
         email = view.findViewById(R.id.email);
        final Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale);

        map.startAnimation(animation);


        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use ACTION_SEND instead of ACTION_SENDTO
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822"); // Ensures only email apps are shown

                String[] recipients = {"chhetrikhagendra96@gmail.com"};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Tripify - Hello");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");

                // Check if any app can handle the email intent
                if (emailIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(Intent.createChooser(emailIntent, "Choose an email client:"));
                } else {
                    Toast.makeText(getActivity(), "No email app installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:+447430000000")); // Ensure it's properly formatted

                if (callIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(callIntent);
                } else {
                    Toast.makeText(getActivity(), "No dialer app installed.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View view) {
                // Ensure the location name is not null or empty
                if (locationName != null && !locationName.isEmpty()) {
                    // Encode the location name to handle spaces/special characters
                    Uri uri = Uri.parse("geo:0,0?q=" + Uri.encode(locationName));
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                    mapIntent.setPackage("com.google.android.apps.maps");

                    // Check if there is an application that can handle the map intent
                    if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(mapIntent);
                    } else {
                        // Fallback to a web URL if the geo URI fails
                        mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(locationName)));
                        startActivity(mapIntent);  // Start the fallback intent
                    }
                } else {
                    Toast.makeText(getActivity(), "Location not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (getActivity() instanceof Dashboard) {
            ((Dashboard) getActivity()).setCheckedItemFromFragment(R.id.nav_contact_us);
        }
        return view;
    }




}