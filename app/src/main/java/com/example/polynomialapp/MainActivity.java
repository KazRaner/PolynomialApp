package com.example.polynomialapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnAddPolynomial;
    private Button btnViewExisting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvHello = findViewById(R.id.tvHello);
        String baseString = "Hel";
        String superscriptString = "2";
        String remainingString = "o!";

        SpannableString spannableString = new SpannableString(baseString + superscriptString + remainingString);
        spannableString.setSpan(new SuperscriptSpan(), baseString.length(), baseString.length() + superscriptString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(0.75f), baseString.length(), baseString.length() + superscriptString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvHello.setText(spannableString);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        baseString = "Welcome to Polynomial Ap";
        superscriptString = "2";

        spannableString = new SpannableString(baseString + superscriptString);
        spannableString.setSpan(new SuperscriptSpan(), baseString.length(), baseString.length() + superscriptString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(0.75f), baseString.length(), baseString.length() + superscriptString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvWelcome.setText(spannableString);

        TextView tvChoose = findViewById(R.id.tvChoose);

        baseString = "Cho";
        superscriptString = "2";
        remainingString = "se an option:";

        spannableString = new SpannableString(baseString + superscriptString + remainingString);
        spannableString.setSpan(new SuperscriptSpan(), baseString.length(), baseString.length() + superscriptString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(0.75f), baseString.length(), baseString.length() + superscriptString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvChoose.setText(spannableString);

        btnAddPolynomial = findViewById(R.id.btnAddPolynomial);
        btnViewExisting = findViewById(R.id.btnViewExisting);

        btnAddPolynomial.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddPolynomialActivity.class));
        });

        btnViewExisting.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PolynomialsActivity.class));
        });
    }
}