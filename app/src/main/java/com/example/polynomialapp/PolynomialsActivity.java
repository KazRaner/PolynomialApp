package com.example.polynomialapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PolynomialsActivity extends AppCompatActivity {

    private RecyclerView rvPolynomials;
    private PolynomialAdapter polynomialAdapter;

    private FirebaseFirestore db;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polynomials);

        rvPolynomials = findViewById(R.id.rvPolynomials);
        rvPolynomials.setLayoutManager(new LinearLayoutManager(this));

        polynomialAdapter = new PolynomialAdapter(new ArrayList<>());
        rvPolynomials.setAdapter(polynomialAdapter);
        etSearch = findViewById(R.id.etSearch);
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchQuery = etSearch.getText().toString().trim();
                searchPolynomials(searchQuery);
                return true;
            }
            return false;
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Perform search as the text changes
                String searchQuery = s.toString().trim();
                searchPolynomials(searchQuery);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });

        db = FirebaseFirestore.getInstance();

        fetchPolynomials();
    }

    private void fetchPolynomials() {
        db.collection("polynomials")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Polynomial> polynomials = new ArrayList<>();
                    List<String> documentIds = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Polynomial polynomial = document.toObject(Polynomial.class);
                        polynomials.add(polynomial);
                        documentIds.add(document.getId());
                    }
                    polynomialAdapter.setPolynomials(polynomials, documentIds);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PolynomialsActivity.this, "Error fetching polynomials", Toast.LENGTH_SHORT).show();
                    Log.e("PolynomialsActivity", "Error fetching polynomials", e);
                });
    }

    private void searchPolynomials(String query) {
        if (TextUtils.isEmpty(query)) {
            fetchPolynomials();
        } else {
            try {
                int degree = Integer.parseInt(query);
                db.collection("polynomials")
                        .whereEqualTo("degree", degree)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            List<Polynomial> polynomials = new ArrayList<>();
                            List<String> documentIds = new ArrayList<>();
                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                Polynomial polynomial = document.toObject(Polynomial.class);
                                polynomials.add(polynomial);
                                documentIds.add(document.getId());
                            }
                            polynomialAdapter.setPolynomials(polynomials, documentIds);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(PolynomialsActivity.this, "Error searching polynomials", Toast.LENGTH_SHORT).show();
                            Log.e("PolynomialsActivity", "Error searching polynomials", e);
                        });
            } catch (NumberFormatException e) {
                // Handle invalid search query
                Toast.makeText(PolynomialsActivity.this, "Invalid search query", Toast.LENGTH_SHORT).show();
                Log.e("PolynomialsActivity", "Invalid search query", e);
            }
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        fetchPolynomials();
    }
}