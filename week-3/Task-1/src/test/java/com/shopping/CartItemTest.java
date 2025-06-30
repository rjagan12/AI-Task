package com.shopping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        product = new Product("1", "Laptop", 999.99, 10);
        cartItem = new CartItem(product, 2);
    }

    @Test
    @DisplayName("Should create cart item with product and quantity")
    void shouldCreateCartItemWithProductAndQuantity() {
        assertEquals(product, cartItem.getProduct());
        assertEquals(2, cartItem.getQuantity());
    }

    @Test
    @DisplayName("Should calculate total price for cart item")
    void shouldCalculateTotalPriceForCartItem() {
        double total = cartItem.getTotalPrice();
        assertEquals(999.99 * 2, total, 0.001);
    }

    @Test
    @DisplayName("Should update quantity")
    void shouldUpdateQuantity() {
        cartItem.setQuantity(5);
        assertEquals(5, cartItem.getQuantity());
    }

    @Test
    @DisplayName("Should throw exception when setting negative quantity")
    void shouldThrowExceptionWhenSettingNegativeQuantity() {
        assertThrows(IllegalArgumentException.class, () -> {
            cartItem.setQuantity(-1);
        });
    }

    @Test
    @DisplayName("Should increase quantity")
    void shouldIncreaseQuantity() {
        cartItem.increaseQuantity(3);
        assertEquals(5, cartItem.getQuantity());
    }

    @Test
    @DisplayName("Should decrease quantity")
    void shouldDecreaseQuantity() {
        cartItem.decreaseQuantity(1);
        assertEquals(1, cartItem.getQuantity());
    }

    @Test
    @DisplayName("Should throw exception when decreasing below zero")
    void shouldThrowExceptionWhenDecreasingBelowZero() {
        assertThrows(IllegalStateException.class, () -> {
            cartItem.decreaseQuantity(3);
        });
    }

    @Test
    @DisplayName("Should be equal to cart item with same product")
    void shouldBeEqualToCartItemWithSameProduct() {
        CartItem cartItem2 = new CartItem(product, 3);
        assertEquals(cartItem, cartItem2);
    }

    @Test
    @DisplayName("Should not be equal to cart item with different product")
    void shouldNotBeEqualToCartItemWithDifferentProduct() {
        Product product2 = new Product("2", "Mouse", 29.99, 50);
        CartItem cartItem2 = new CartItem(product2, 2);
        assertNotEquals(cartItem, cartItem2);
    }

    @Test
    @DisplayName("Should have consistent hashCode")
    void shouldHaveConsistentHashCode() {
        CartItem cartItem2 = new CartItem(product, 3);
        assertEquals(cartItem.hashCode(), cartItem2.hashCode());
    }

    @Test
    @DisplayName("Should return meaningful toString")
    void shouldReturnMeaningfulToString() {
        String toString = cartItem.toString();
        assertTrue(toString.contains("CartItem"));
        assertTrue(toString.contains("product="));
        assertTrue(toString.contains("quantity=2"));
    }
} 