package com.shopping;

import java.util.Objects;

public class Product {
    private String id;
    private String name;
    private double price;
    private int stock;

    public Product(String id, String name, double price, int stock) {
        validateId(id);
        validateName(name);
        validatePrice(price);
        validateStock(stock);
        
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    private void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID is required");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
    }

    private void validatePrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
    }

    private void validateStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Stock must be non-negative");
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        validatePrice(price);
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        validateStock(stock);
        this.stock = stock;
    }

    public boolean isInStock() {
        return stock > 0;
    }

    public boolean hasStock(int quantity) {
        return stock >= quantity;
    }

    public void reduceStock(int quantity) {
        if (stock < quantity) {
            throw new IllegalStateException("Insufficient stock");
        }
        stock -= quantity;
    }

    public void addStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be non-negative");
        }
        stock += quantity;
    }

    public double getTotalPrice(int quantity) {
        return price * quantity;
    }

    public boolean isValid() {
        return id != null && !id.trim().isEmpty() &&
               name != null && !name.trim().isEmpty() &&
               price > 0 && stock >= 0;
    }

    public Product copy() {
        return new Product(id, name, price, stock);
    }

    public String toJson() {
        return String.format("{\"id\":\"%s\",\"name\":\"%s\",\"price\":%.2f,\"stock\":%d}", 
                           id, name, price, stock);
    }

    public static Product fromJson(String json) {
        // Simple JSON parsing for demonstration
        // In a real application, you would use a proper JSON library
        String cleanJson = json.replaceAll("[{}\"]", "");
        String[] parts = cleanJson.split(",");
        
        String id = null, name = null;
        double price = 0.0;
        int stock = 0;
        
        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                
                switch (key) {
                    case "id":
                        id = value;
                        break;
                    case "name":
                        name = value;
                        break;
                    case "price":
                        price = Double.parseDouble(value);
                        break;
                    case "stock":
                        stock = Integer.parseInt(value);
                        break;
                }
            }
        }
        
        return new Product(id, name, price, stock);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                '}';
    }
} 