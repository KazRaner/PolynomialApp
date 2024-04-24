package com.example.polynomialapp;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AddPolynomialActivity extends AppCompatActivity {

    private EditText etDegree;
    private LinearLayout coefficientsLayout;
    private Button btnSubmit;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_polynomial);

        etDegree = findViewById(R.id.etDegree);
        coefficientsLayout = findViewById(R.id.coefficientsLayout);
        btnSubmit = findViewById(R.id.btnSubmit);

        db = FirebaseFirestore.getInstance();

        etDegree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int degree = TextUtils.isEmpty(s) ? 0 : Integer.parseInt(s.toString());
                createCoefficientEditTexts(degree);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnSubmit.setOnClickListener(v -> savePolynomial());
    }

    private void createCoefficientEditTexts(int degree) {
        coefficientsLayout.removeAllViews();
        for (int i = degree; i >= 0; i--) {
            EditText et = new EditText(this);
            String baseString = "Coefficient for X";
            SpannableString span = new SpannableString(baseString + i);
            span.setSpan(new SuperscriptSpan(), baseString.length(), span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            et.setHint(span);
            int hintColor = ContextCompat.getColor(this, R.color.lightGray);
            et.setHintTextColor(hintColor);
            et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            et.setTextColor(Color.WHITE);
            coefficientsLayout.addView(et);
        }
    }

    private void savePolynomial() {
        String degreeString = etDegree.getText().toString();
        if (TextUtils.isEmpty(degreeString)) {
            Toast.makeText(AddPolynomialActivity.this, "Please enter the degree", Toast.LENGTH_SHORT).show();
            return;
        }

        int inputDegree = Integer.parseInt(degreeString);
        List<Double> coefficients = new ArrayList<>();
        for (int i = 0; i < coefficientsLayout.getChildCount(); i++) {
            EditText et = (EditText) coefficientsLayout.getChildAt(i);
            double coefficient = TextUtils

                    .isEmpty(et.getText()) ? 0 : Double.parseDouble(et.getText().toString());
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

        Polynomial polynomial = new Polynomial(actualDegree, trimmedCoefficients);

        db.collection("polynomials")
                .add(polynomial)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddPolynomialActivity.this, "Polynomial saved", Toast.LENGTH_SHORT).show();
                    etDegree.setText("");
                    coefficientsLayout.removeAllViews();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddPolynomialActivity.this, "Error saving polynomial", Toast.LENGTH_SHORT).show();
                    Log.e("AddPolynomialActivity", "Error saving polynomial", e);
                });
    }
}