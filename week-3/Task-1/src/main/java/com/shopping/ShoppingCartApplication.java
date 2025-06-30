package com.shopping;

public class ShoppingCartApplication {
    
    public static void main(String[] args) {
        System.out.println("=== Shopping Cart TDD Demo ===\n");
        
        // Create products
        Product laptop = new Product("1", "Gaming Laptop", 1299.99, 10);
        Product mouse = new Product("2", "Wireless Mouse", 49.99, 50);
        Product keyboard = new Product("3", "Mechanical Keyboard", 199.99, 25);
        
        System.out.println("Products created:");
        System.out.println("- " + laptop.getName() + ": $" + laptop.getPrice());
        System.out.println("- " + mouse.getName() + ": $" + mouse.getPrice());
        System.out.println("- " + keyboard.getName() + ": $" + keyboard.getPrice());
        System.out.println();
        
        // Create shopping cart
        ShoppingCart cart = new ShoppingCart();
        System.out.println("Shopping cart created with ID: " + cart.getId());
        System.out.println();
        
        // Add items to cart
        System.out.println("Adding items to cart...");
        cart.addItem(laptop, 1);
        cart.addItem(mouse, 2);
        cart.addItem(keyboard, 1);
        
        System.out.println("Cart contains " + cart.getItemCount() + " items");
        System.out.println("Subtotal: $" + String.format("%.2f", cart.getSubtotal()));
        System.out.println();
        
        // Apply discount
        System.out.println("Applying 10% discount...");
        cart.applyDiscount(0.10);
        System.out.println("Total after discount: $" + String.format("%.2f", cart.getTotal()));
        System.out.println();
        
        // Calculate tax
        double taxRate = 0.08; // 8% tax
        double tax = cart.calculateTax(taxRate);
        double totalWithTax = cart.getTotalWithTax(taxRate);
        
        System.out.println("Tax calculation (" + (taxRate * 100) + "%):");
        System.out.println("- Tax amount: $" + String.format("%.2f", tax));
        System.out.println("- Total with tax: $" + String.format("%.2f", totalWithTax));
        System.out.println();
        
        // Get cart summary
        CartSummary summary = cart.getSummary();
        System.out.println("Cart Summary:");
        System.out.println("- Unique items: " + summary.getUniqueItems());
        System.out.println("- Total items: " + summary.getTotalItems());
        System.out.println("- Subtotal: $" + String.format("%.2f", summary.getSubtotal()));
        System.out.println();
        
        // Update quantities
        System.out.println("Updating mouse quantity to 3...");
        cart.updateQuantity(mouse.getId(), 3);
        System.out.println("New total: $" + String.format("%.2f", cart.getTotal()));
        System.out.println();
        
        // Remove items
        System.out.println("Removing 1 keyboard...");
        cart.removeItem(keyboard.getId(), 1);
        System.out.println("Cart now contains " + cart.getItemCount() + " items");
        System.out.println("New total: $" + String.format("%.2f", cart.getTotal()));
        System.out.println();
        
        // JSON serialization
        System.out.println("Serializing cart to JSON...");
        String json = cart.toJson();
        System.out.println("JSON: " + json.substring(0, Math.min(100, json.length())) + "...");
        System.out.println();
        
        // Deserialize from JSON
        System.out.println("Deserializing cart from JSON...");
        ShoppingCart newCart = ShoppingCart.fromJson(json);
        System.out.println("New cart ID: " + newCart.getId());
        System.out.println("New cart total: $" + String.format("%.2f", newCart.getTotal()));
        System.out.println();
        
        // Stock validation
        System.out.println("Stock validation:");
        System.out.println("- Cart validates stock: " + cart.validateStock());
        System.out.println("- Can checkout: " + cart.canCheckout());
        System.out.println();
        
        // Clear cart
        System.out.println("Clearing cart...");
        cart.clear();
        System.out.println("- Cart is empty: " + cart.isEmpty());
        System.out.println("- Can checkout: " + cart.canCheckout());
        System.out.println();
        
        System.out.println("=== Demo completed successfully! ===");
    }
} 