package com.example.tripify;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class CurrencyConverterFragment extends Fragment {

    private EditText editTextAmount;
    private Spinner spinnerFromCurrency, spinnerToCurrency;
    private TextView textViewResult, textViewRate;
    private Button buttonConvert;

    public CurrencyConverterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_currency_converter, container, false);

        // Initialize UI components
        editTextAmount = view.findViewById(R.id.editTextAmount);
        spinnerFromCurrency = view.findViewById(R.id.spinnerFromCurrency);
        spinnerToCurrency = view.findViewById(R.id.spinnerToCurrency);
        textViewResult = view.findViewById(R.id.textViewResult);
        textViewRate = view.findViewById(R.id.textViewRate);
        buttonConvert = view.findViewById(R.id.buttonConvert);

        // Setup currency spinners
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFromCurrency.setAdapter(adapter);
        spinnerToCurrency.setAdapter(adapter);

        // Set button click listener
        buttonConvert.setOnClickListener(v -> {
            String fromCurrency = spinnerFromCurrency.getSelectedItem().toString();
            String toCurrency = spinnerToCurrency.getSelectedItem().toString();
            if (!editTextAmount.getText().toString().isEmpty()) {
                new CurrencyConverterTask(textViewResult, textViewRate, editTextAmount, toCurrency, getString(R.string.api_key)).execute(fromCurrency, toCurrency);
            } else {
                textViewResult.setText("Please enter an amount.");
            }
        });
        if (getActivity() instanceof Dashboard) {
            ((Dashboard) getActivity()).setCheckedItemFromFragment(R.id.nav_currencyConverter);
        }
        return view;
    }

    private static class CurrencyConverterTask extends AsyncTask<String, Void, Double[]> {
        private final WeakReference<TextView> textViewResultRef;
        private final WeakReference<TextView> textViewRateRef;
        private final WeakReference<EditText> editTextAmountRef;
        private final String toCurrency;
        private final String apiKey;

        public CurrencyConverterTask(TextView textViewResult, TextView textViewRate, EditText editTextAmount, String toCurrency, String apiKey) {
            this.textViewResultRef = new WeakReference<>(textViewResult);
            this.textViewRateRef = new WeakReference<>(textViewRate);
            this.editTextAmountRef = new WeakReference<>(editTextAmount);
            this.toCurrency = toCurrency;
            this.apiKey = apiKey;
        }

        @Override
        protected Double[] doInBackground(String... params) {
            try {
                URL url = new URL("https://api.exchangerate-api.com/v4/latest/" + params[0] + "?apiKey=" + apiKey);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                    double rate = jsonObject.getJSONObject("rates").getDouble(params[1]);
                    return new Double[]{rate};
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                return null; // Return null on error
            }
        }

        @Override
        protected void onPostExecute(Double[] result) {
            TextView resultView = textViewResultRef.get();
            TextView rateView = textViewRateRef.get();
            EditText editTextAmount = editTextAmountRef.get();

            if (result != null && resultView != null && rateView != null && editTextAmount != null) {
                try {
                    double amount = Double.parseDouble(editTextAmount.getText().toString());
                    double convertedAmount = amount * result[0];
                    rateView.setText(String.format("Rate: %.4f", result[0]));
                    resultView.setText(String.format("%.2f %s", convertedAmount, toCurrency));
                } catch (NumberFormatException e) {
                    resultView.setText("Please enter a valid number.");
                }
            } else if (resultView != null) {
                resultView.setText("Error fetching rate");
            }
        }
    }
}
