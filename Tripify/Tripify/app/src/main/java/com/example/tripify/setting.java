package com.example.tripify;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class setting extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize all language buttons
        ImageButton buttonEnglish = view.findViewById(R.id.buttonEnglish);
        ImageButton buttonSpanish = view.findViewById(R.id.buttonSpanish);
        ImageButton buttonFrench = view.findViewById(R.id.buttonFrench);
        ImageButton buttonMandarin = view.findViewById(R.id.buttonMandarin);
        ImageButton buttonHindi = view.findViewById(R.id.buttonHindi);
        ImageButton buttonArabic = view.findViewById(R.id.buttonArabic);
        ImageButton buttonPortuguese = view.findViewById(R.id.buttonPortuguese);
        ImageButton buttonBengali = view.findViewById(R.id.buttonBengali);
        ImageButton buttonRussian = view.findViewById(R.id.buttonRussian);

        // Set click listeners for each language
        buttonEnglish.setOnClickListener(v -> updateLanguage("en"));
        buttonSpanish.setOnClickListener(v -> updateLanguage("es"));
        buttonFrench.setOnClickListener(v -> updateLanguage("fr"));
        buttonMandarin.setOnClickListener(v -> updateLanguage("zh")); // Mandarin Chinese
        buttonHindi.setOnClickListener(v -> updateLanguage("hi")); // Hindi
        buttonArabic.setOnClickListener(v -> updateLanguage("ar")); // Arabic
        buttonPortuguese.setOnClickListener(v -> updateLanguage("pt")); // Portuguese
        buttonBengali.setOnClickListener(v -> updateLanguage("bn")); // Bengali
        buttonRussian.setOnClickListener(v -> updateLanguage("ru")); // Russian

        return view;
    }

    // Update the application's language
    private void updateLanguage(String language) {
        Activity activity = getActivity();
        if (activity == null) {
            Log.e("LanguageUpdate", "Activity is null, cannot update language");
            return;
        }

        Log.e("LanguageUpdate", "Started updating language to " + language);

        try {
            // Create and set the new locale
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = new Configuration(activity.getResources().getConfiguration());
            config.setLocale(locale);
            activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());

            // Inform all activities to recreate themselves to apply the new locale
            // This can be handled differently depending on the application architecture,
            // e.g., sending a broadcast to all receivers that need to refresh their UI
            activity.runOnUiThread(activity::recreate);

            Log.e("LanguageUpdate", "Language updated successfully to " + language);
        } catch (Exception e) {
            Log.e("LanguageUpdate", "Failed to update language: " + language, e);
        }
    }
}
