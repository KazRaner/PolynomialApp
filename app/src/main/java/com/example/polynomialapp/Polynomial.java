package com.example.polynomialapp;

import java.util.List;

public class Polynomial {
    private int degree;
    private List<Double> coefficients;

    public Polynomial() {
        // Default constructor required for Firebase
    }

    public Polynomial(int degree, List<Double> coefficients) {
        this.degree = degree;
        this.coefficients = coefficients;
    }

    // Getters and setters
    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public List<Double> getCoefficients() {
        return coefficients;
    }

    public void setCoefficients(List<Double> coefficients) {
        this.coefficients = coefficients;
    }

}