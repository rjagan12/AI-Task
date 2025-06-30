package com.shopping;

import java.util.*;
import java.util.stream.Collectors;

public class ShoppingCart {
    private String id;
    private List<CartItem> items;
    private double discountRate;

    public ShoppingCart() {
        this.id = UUID.randomUUID().toString();
        this.items = new ArrayList<>();
        this.discountRate = 0.0;
    }

    public String getId() {
        return id;
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    public double getTotal() {
        double subtotal = getSubtotal();
        return subtotal * (1.0 - discountRate);
    }

    public int getItemCount() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public void addItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product is required");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (!product.hasStock(quantity)) {
            throw new IllegalStateException("Insufficient stock");
        }

        Optional<CartItem> existingItem = items.stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            if (!product.hasStock(newQuantity)) {
                throw new IllegalStateException("Insufficient stock");
            }
            item.setQuantity(newQuantity);
        } else {
            items.add(new CartItem(product, quantity));
        }
    }

    public void removeItem(String productId, int quantity) {
        if (items.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        CartItem item = items.stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found in cart"));

        if (item.getQuantity() < quantity) {
            throw new IllegalStateException("Cannot remove more items than in cart");
        }

        if (item.getQuantity() == quantity) {
            items.remove(item);
        } else {
            item.setQuantity(item.getQuantity() - quantity);
        }
    }

    public void updateQuantity(String productId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        CartItem item = items.stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found in cart"));

        if (quantity == 0) {
            items.remove(item);
        } else {
            if (!item.getProduct().hasStock(quantity)) {
                throw new IllegalStateException("Insufficient stock");
            }
            item.setQuantity(quantity);
        }
    }

    public void clear() {
        items.clear();
        discountRate = 0.0;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public Optional<CartItem> getItem(String productId) {
        return items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
    }

    public double getSubtotal() {
        return items.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }

    public double calculateTax(double taxRate) {
        return getSubtotal() * taxRate;
    }

    public double getTotalWithTax(double taxRate) {
        return getSubtotal() + calculateTax(taxRate);
    }

    public boolean canCheckout() {
        return !items.isEmpty();
    }

    public boolean validateStock() {
        return items.stream()
                .allMatch(item -> item.getProduct().hasStock(item.getQuantity()));
    }

    public void applyDiscount(double discountRate) {
        if (discountRate < 0 || discountRate > 1) {
            throw new IllegalArgumentException("Discount rate must be between 0 and 1");
        }
        this.discountRate = discountRate;
    }

    public CartSummary getSummary() {
        int uniqueItems = items.size();
        int totalItems = getItemCount();
        double subtotal = getSubtotal();
        return new CartSummary(uniqueItems, totalItems, subtotal);
    }

    public boolean containsProduct(String productId) {
        return items.stream()
                .anyMatch(item -> item.getProduct().getId().equals(productId));
    }

    public int getProductQuantity(String productId) {
        return items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":\"").append(id).append("\",");
        json.append("\"items\":[");
        
        String itemsJson = items.stream()
                .map(item -> String.format("{\"product\":%s,\"quantity\":%d}", 
                                         item.getProduct().toJson(), item.getQuantity()))
                .collect(Collectors.joining(","));
        
        json.append(itemsJson);
        json.append("],");
        json.append("\"total\":").append(getTotal()).append(",");
        json.append("\"itemCount\":").append(getItemCount());
        json.append("}");
        
        return json.toString();
    }

    public static ShoppingCart fromJson(String json) {
        // Simple JSON parsing for demonstration
        // In a real application, you would use a proper JSON library
        ShoppingCart cart = new ShoppingCart();
        
        // Extract cart ID
        if (json.contains("\"id\":")) {
            int start = json.indexOf("\"id\":\"") + 6;
            int end = json.indexOf("\"", start);
            cart.id = json.substring(start, end);
        }
        
        // Extract items (simplified parsing)
        if (json.contains("\"items\":")) {
            // This is a simplified implementation
            // In practice, you would use a proper JSON parser
            String itemsSection = json.substring(json.indexOf("\"items\":[") + 9);
            itemsSection = itemsSection.substring(0, itemsSection.indexOf("]"));
            
            // Parse items (simplified)
            if (!itemsSection.trim().isEmpty()) {
                String[] itemStrings = itemsSection.split("\\},\\{");
                for (String itemString : itemStrings) {
                    // Simplified item parsing
                    if (itemString.contains("\"product\":")) {
                        // Extract product JSON and quantity
                        int productStart = itemString.indexOf("\"product\":{") + 11;
                        int productEnd = itemString.indexOf("}", productStart) + 1;
                        String productJson = itemString.substring(productStart, productEnd);
                        
                        int quantityStart = itemString.indexOf("\"quantity\":") + 12;
                        int quantityEnd = itemString.indexOf(",", quantityStart);
                        if (quantityEnd == -1) quantityEnd = itemString.indexOf("}");
                        int quantity = Integer.parseInt(itemString.substring(quantityStart, quantityEnd));
                        
                        Product product = Product.fromJson(productJson);
                        cart.addItem(product, quantity);
                    }
                }
            }
        }
        
        return cart;
    }
} 