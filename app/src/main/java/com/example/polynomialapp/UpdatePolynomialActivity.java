package com.example.polynomialapp;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpdatePolynomialActivity extends AppCompatActivity {
    private EditText etDegree;
    private LinearLayout coefficientsLayout;
    private Button btnUpdate;

    private FirebaseFirestore db;
    private Polynomial polynomial;
    private String polynomialId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_polynomial);

        etDegree = findViewById(R.id.etDegree);
        coefficientsLayout = findViewById(R.id.coefficientsLayout);
        btnUpdate = findViewById(R.id.btnUpdate);

        db = FirebaseFirestore.getInstance();

        // Get the polynomial ID from the intent extras
        polynomialId = getIntent().getStringExtra("polynomialId");

        // Retrieve the polynomial from Firestore
        db.collection("polynomials").document(polynomialId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    polynomial = documentSnapshot.toObject(Polynomial.class);
                    if (polynomial != null) {
                        etDegree.setText(String.valueOf(polynomial.getDegree()));
                        createCoefficientEditTexts(polynomial.getDegree(),polynomial.getDegree(), polynomial.getCoefficients());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UpdatePolynomialActivity.this, "Error retrieving polynomial", Toast.LENGTH_SHORT).show();
                    Log.e("UpdatePolynomialActivity", "Error retrieving polynomial", e);
                });

        etDegree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                String degreeString = s.toString().trim();
                if (!TextUtils.isEmpty(degreeString)) {
                    try {
                        int newDegree = Integer.parseInt(degreeString);
                        fetchPolynomialFromDatabase(newDegree,polynomial.getDegree());
                    } catch (NumberFormatException e) {
                        // Handle invalid input, e.g., show an error message
                        Toast.makeText(UpdatePolynomialActivity.this, "Invalid degree value", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Clear the coefficient EditTexts if the degree is empty
                    coefficientsLayout.removeAllViews();
                }
            }
        });



        btnUpdate.setOnClickListener(v -> updatePolynomial());
    }
    private void fetchPolynomialFromDatabase(int newDegree,int oldDegree) {
        db.collection("polynomials").document(polynomialId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    polynomial = documentSnapshot.toObject(Polynomial.class);
                    if (polynomial != null) {
                        int currentSize = polynomial.getCoefficients().size();
                        if (newDegree >= currentSize) {
                            List<Double> updatedCoefficients = new ArrayList<>(Collections.nCopies(newDegree - currentSize + 1, 0.0));
                            updatedCoefficients.addAll(polynomial.getCoefficients());
                            polynomial.setCoefficients(updatedCoefficients);
                        } else {
                            List<Double> updatedCoefficients = polynomial.getCoefficients().subList(currentSize - newDegree - 1, currentSize);
                            polynomial.setCoefficients(updatedCoefficients);
                        }
                        createCoefficientEditTexts(newDegree,oldDegree, polynomial.getCoefficients());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UpdatePolynomialActivity.this, "Error retrieving polynomial", Toast.LENGTH_SHORT).show();
                    Log.e("UpdatePolynomialActivity", "Error retrieving polynomial", e);
                });
    }
    private void createCoefficientEditTexts(int newDegree,int oldDegree, List<Double> coefficients) {
        coefficientsLayout.removeAllViews();
        for (int i = newDegree; i >= 0; i--) {
            EditText et = new EditText(this);
            et.setHint("Coefficient for x^" + i);
            et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            if (i <= oldDegree) {
                et.setText(String.valueOf(coefficients.get(coefficients.size() - i - 1)));
            }
            et.setTextColor(Color.WHITE);
            coefficientsLayout.addView(et);
        }
    }

    private void updatePolynomial() {
        int inputDegree = Integer.parseInt(etDegree.getText().toString());
        List<Double> coefficients = new ArrayList<>();
        for (int i = inputDegree; i >= 0; i--) {
            EditText et = (EditText) coefficientsLayout.getChildAt(inputDegree - i);
            double coefficient = TextUtils.isEmpty(et.getText()) ? 0 : Double.parseDouble(et.getText().toString());
            coefficients.add(coefficient);
        }

        int actualDegree = inputDegree;
        for (int i = inputDegree; i >= 0; i--) {
            if (coefficients.get(inputDegree - i) != 0) {
                actualDegree = i;
                break;
            }
        }

        List<Double> trimmedCoefficients = coefficients.subList(inputDegree - actualDegree, inputDegree + 1);

        polynomial.setDegree(actualDegree);
        polynomial.setCoefficients(trimmedCoefficients);

        db.collection("polynomials").document(polynomialId)
                .set(polynomial)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UpdatePolynomialActivity.this, "Polynomial updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UpdatePolynomialActivity.this, "Error updating polynomial", Toast.LENGTH_SHORT).show();
                    Log.e("UpdatePolynomialActivity", "Error updating polynomial", e);
                });
    }
}