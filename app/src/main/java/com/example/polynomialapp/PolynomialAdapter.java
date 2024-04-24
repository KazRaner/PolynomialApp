package com.example.polynomialapp;

import android.content.Intent;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PolynomialAdapter extends RecyclerView.Adapter<PolynomialAdapter.PolynomialViewHolder> {

    private List<Polynomial> polynomials = new ArrayList<>();
    private List<String> documentIds = new ArrayList<>();
    private FirebaseFirestore db;

    public PolynomialAdapter(List<Polynomial> polynomials) {
        this.polynomials = polynomials;
        db = FirebaseFirestore.getInstance();
    }

    public void setPolynomials(List<Polynomial> polynomials, List<String> documentIds) {
        this.polynomials.clear();
        this.documentIds.clear();
        this.polynomials.addAll(polynomials);
        this.documentIds.addAll(documentIds);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PolynomialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_polynomial, parent, false);
        return new PolynomialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PolynomialViewHolder holder, int position) {
        Polynomial polynomial = polynomials.get(position);
        String documentId = documentIds.get(position);
        holder.tvPolynomial.setText(holder.formatPolynomial(polynomial));
        holder.bind(polynomial, documentId);
    }

    @Override
    public int getItemCount() {
        return polynomials.size();
    }

    class PolynomialViewHolder extends RecyclerView.ViewHolder {

        private TextView tvPolynomial;
        private Button btnDelete;
        private Button btnUpdate;

        public PolynomialViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPolynomial = itemView.findViewById(R.id.tvPolynomial);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
        }

        public void bind(Polynomial polynomial, String documentId) {
            tvPolynomial.setText(formatPolynomial(polynomial));

            btnUpdate.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), UpdatePolynomialActivity.class);
                intent.putExtra("polynomialId", documentId);
                itemView.getContext().startActivity(intent);
            });

            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    db.collection("polynomials").document(documentId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                polynomials.remove(position);
                                documentIds.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(itemView.getContext(), "Polynomial deleted", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(itemView.getContext(), "Error deleting polynomial", Toast.LENGTH_SHORT).show();
                                Log.e("PolynomialAdapter", "Error deleting polynomial", e);
                            });
                }
            });
        }

        private SpannableStringBuilder formatPolynomial(Polynomial polynomial) {
            SpannableStringBuilder sb = new SpannableStringBuilder();
            sb.append("f(x) = ");
            boolean isFirst = true;
            for (int i = polynomial.getDegree(); i >= 0; i--) {
                double coefficient = polynomial.getCoefficients().get(polynomial.getDegree() - i);
                if (coefficient != 0) {
                    if (sb.length() > 0 && !isFirst) {
                        sb.append(coefficient > 0 ? " + " : " - ");
                    } else if (coefficient < 0) {
                        sb.append("-");
                    }
                    if (Math.abs(coefficient) != 1 || i == 0) {
                        sb.append(String.valueOf(Math.abs(coefficient)));
                    }
                    if (i > 0) {
                        isFirst = false;
                        sb.append("x");
                        if (i > 1) {
                            String exponentString = String.valueOf(i);
                            int startIndex = sb.length();
                            sb.append(exponentString);
                            sb.setSpan(new SuperscriptSpan(), startIndex, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            sb.setSpan(new RelativeSizeSpan(0.75f), startIndex, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                }
            }
            return sb;
        }
    }
}