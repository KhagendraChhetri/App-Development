package com.example.tripify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.text.DecimalFormat;

public class budgetCalculatorFragment extends Fragment {
    private EditText editTextNoOfPeople, editTextFlights, editTextAccommodation, editTextMeals, editTextActivities, editTextMiscellaneous;
    private TextView textViewTotalCost, textViewPerPersonCost, textViewPerDayCost;
    private Button buttonCalculate;

    public budgetCalculatorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget_calculator, container, false);

        // Initialize all the EditTexts
        editTextNoOfPeople = view.findViewById(R.id.editTextNoOfPeople);
        editTextFlights = view.findViewById(R.id.editTextFlights);
        editTextAccommodation = view.findViewById(R.id.editTextAccommodation);
        editTextMeals = view.findViewById(R.id.editTextMeals);
        editTextActivities = view.findViewById(R.id.editTextActivities);
        editTextMiscellaneous = view.findViewById(R.id.editTextMiscellaneous);

        // Initialize the TextViews for displaying results
        textViewTotalCost = view.findViewById(R.id.textViewTotalCost);
        textViewPerPersonCost = view.findViewById(R.id.textViewPerPersonCost);
        textViewPerDayCost = view.findViewById(R.id.textViewPerDayCost);

        // Initialize the Button and set its onClick listener
        buttonCalculate = view.findViewById(R.id.buttonCalculate);
        buttonCalculate.setOnClickListener(v -> calculateTotal());

        return view;
    }

    private void calculateTotal() {
        // Check if any input field is empty
        if (isFieldEmpty(editTextNoOfPeople) || isFieldEmpty(editTextFlights) ||
                isFieldEmpty(editTextAccommodation) || isFieldEmpty(editTextMeals) ||
                isFieldEmpty(editTextActivities) || isFieldEmpty(editTextMiscellaneous)) {

            // Clear all result text views
            textViewTotalCost.setText("Please input all values");
            textViewPerPersonCost.setText("");
            textViewPerDayCost.setText("");

            // Show an error message
            Toast.makeText(getContext(), "Please input all values", Toast.LENGTH_SHORT).show();
            return; // Exit early since we can't proceed with calculations
        }

        double numPeople = parseDouble(editTextNoOfPeople.getText().toString());
        double days = 1; // Add an EditText or a method to input days if needed

        double flights = parseDouble(editTextFlights.getText().toString());
        double accommodation = parseDouble(editTextAccommodation.getText().toString()) * numPeople * days;
        double meals = parseDouble(editTextMeals.getText().toString()) * numPeople * days;
        double activities = parseDouble(editTextActivities.getText().toString()) * numPeople * days;
        double miscellaneous = parseDouble(editTextMiscellaneous.getText().toString());

        double total = flights + accommodation + meals + activities + miscellaneous;
        double perPersonTotal = total / numPeople;
        double perDayPerPersonTotal = (accommodation + meals + activities + miscellaneous) / numPeople;

        DecimalFormat df = new DecimalFormat("0.00");
        textViewTotalCost.setText("Total Cost: $" + df.format(total));
        textViewPerPersonCost.setText("Cost per Person: $" + df.format(perPersonTotal));
        textViewPerDayCost.setText("Travel Cost per Day per Person: $" + df.format(perDayPerPersonTotal));
    }

    // Utility method to check if a field is empty
    private boolean isFieldEmpty(EditText editText) {
        String text = editText.getText().toString().trim();
        return text.isEmpty();
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return 0.0;  // Return 0 if the input is not a valid double
        }
    }
}
