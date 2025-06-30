package com.shopping;

public class CartSummary {
    private int uniqueItems;
    private int totalItems;
    private double subtotal;

    public CartSummary(int uniqueItems, int totalItems, double subtotal) {
        this.uniqueItems = uniqueItems;
        this.totalItems = totalItems;
        this.subtotal = subtotal;
    }

    public int getUniqueItems() {
        return uniqueItems;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public double getSubtotal() {
        return subtotal;
    }

    @Override
    public String toString() {
        return "CartSummary{" +
                "uniqueItems=" + uniqueItems +
                ", totalItems=" + totalItems +
                ", subtotal=" + subtotal +
                '}';
    }
} 