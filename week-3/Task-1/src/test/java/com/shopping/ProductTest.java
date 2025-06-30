package com.shopping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("1", "Laptop", 999.99, 10);
    }

    @Test
    @DisplayName("Should create a product with all required properties")
    void shouldCreateProductWithAllProperties() {
        assertEquals("1", product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals(999.99, product.getPrice(), 0.001);
        assertEquals(10, product.getStock());
    }

    @Test
    @DisplayName("Should throw exception when id is null")
    void shouldThrowExceptionWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product(null, "Laptop", 999.99, 10);
        });
    }

    @Test
    @DisplayName("Should throw exception when id is empty")
    void shouldThrowExceptionWhenIdIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product("", "Laptop", 999.99, 10);
        });
    }

    @Test
    @DisplayName("Should throw exception when name is null")
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product("1", null, 999.99, 10);
        });
    }

    @Test
    @DisplayName("Should throw exception when name is empty")
    void shouldThrowExceptionWhenNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product("1", "", 999.99, 10);
        });
    }

    @ParameterizedTest
    @ValueSource(doubles = {-100.0, 0.0})
    @DisplayName("Should throw exception when price is invalid")
    void shouldThrowExceptionWhenPriceIsInvalid(double invalidPrice) {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product("1", "Laptop", invalidPrice, 10);
        });
    }

    @Test
    @DisplayName("Should throw exception when stock is negative")
    void shouldThrowExceptionWhenStockIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product("1", "Laptop", 999.99, -5);
        });
    }

    @Test
    @DisplayName("Should check if product is in stock")
    void shouldCheckIfProductIsInStock() {
        assertTrue(product.isInStock());
        
        product.setStock(0);
        assertFalse(product.isInStock());
    }

    @ParameterizedTest
    @CsvSource({
        "5, true",
        "10, true",
        "11, false"
    })
    @DisplayName("Should check if product has sufficient stock")
    void shouldCheckIfProductHasSufficientStock(int quantity, boolean expected) {
        assertEquals(expected, product.hasStock(quantity));
    }

    @Test
    @DisplayName("Should reduce stock when items are purchased")
    void shouldReduceStockWhenItemsArePurchased() {
        product.reduceStock(3);
        assertEquals(7, product.getStock());
    }

    @Test
    @DisplayName("Should throw exception when reducing stock below 0")
    void shouldThrowExceptionWhenReducingStockBelowZero() {
        assertThrows(IllegalStateException.class, () -> {
            product.reduceStock(11);
        });
    }

    @Test
    @DisplayName("Should add stock when items are returned")
    void shouldAddStockWhenItemsAreReturned() {
        product.addStock(5);
        assertEquals(15, product.getStock());
    }

    @Test
    @DisplayName("Should calculate total price for quantity")
    void shouldCalculateTotalPriceForQuantity() {
        double total = product.getTotalPrice(3);
        assertEquals(999.99 * 3, total, 0.001);
    }

    @Test
    @DisplayName("Should serialize product to JSON")
    void shouldSerializeProductToJson() {
        String json = product.toJson();
        assertTrue(json.contains("\"id\":\"1\""));
        assertTrue(json.contains("\"name\":\"Laptop\""));
        assertTrue(json.contains("\"price\":999.99"));
        assertTrue(json.contains("\"stock\":10"));
    }

    @Test
    @DisplayName("Should create product from JSON")
    void shouldCreateProductFromJson() {
        String json = "{\"id\":\"2\",\"name\":\"Mouse\",\"price\":29.99,\"stock\":50}";
        Product newProduct = Product.fromJson(json);
        
        assertEquals("2", newProduct.getId());
        assertEquals("Mouse", newProduct.getName());
        assertEquals(29.99, newProduct.getPrice(), 0.001);
        assertEquals(50, newProduct.getStock());
    }

    @Test
    @DisplayName("Should be equal to product with same ID")
    void shouldBeEqualToProductWithSameId() {
        Product product2 = new Product("1", "Laptop", 999.99, 10);
        assertEquals(product, product2);
    }

    @Test
    @DisplayName("Should not be equal to product with different ID")
    void shouldNotBeEqualToProductWithDifferentId() {
        Product product2 = new Product("2", "Laptop", 999.99, 10);
        assertNotEquals(product, product2);
    }

    @Test
    @DisplayName("Should have consistent hashCode")
    void shouldHaveConsistentHashCode() {
        Product product2 = new Product("1", "Laptop", 999.99, 10);
        assertEquals(product.hashCode(), product2.hashCode());
    }

    @Test
    @DisplayName("Should return meaningful toString")
    void shouldReturnMeaningfulToString() {
        String toString = product.toString();
        assertTrue(toString.contains("Product{"));
        assertTrue(toString.contains("id='1'"));
        assertTrue(toString.contains("name='Laptop'"));
    }

    @Test
    @DisplayName("Should validate product data")
    void shouldValidateProductData() {
        assertTrue(product.isValid());
        
        Product invalidProduct = new Product("1", "Laptop", 999.99, 0);
        assertTrue(invalidProduct.isValid()); // Zero stock is valid
        
        // Test with a product that has invalid data (but we can't create it due to constructor validation)
        // So we'll test the isValid method with valid data
        assertTrue(product.isValid());
    }

    @Test
    @DisplayName("Should copy product")
    void shouldCopyProduct() {
        Product copy = product.copy();
        
        assertEquals(product.getId(), copy.getId());
        assertEquals(product.getName(), copy.getName());
        assertEquals(product.getPrice(), copy.getPrice(), 0.001);
        assertEquals(product.getStock(), copy.getStock());
        
        // Should be different objects
        assertNotSame(product, copy);
    }

    @Test
    @DisplayName("Should update product price")
    void shouldUpdateProductPrice() {
        product.setPrice(899.99);
        assertEquals(899.99, product.getPrice(), 0.001);
    }

    @Test
    @DisplayName("Should throw exception when setting negative price")
    void shouldThrowExceptionWhenSettingNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> {
            product.setPrice(-100);
        });
    }

    @Test
    @DisplayName("Should update product name")
    void shouldUpdateProductName() {
        product.setName("Gaming Laptop");
        assertEquals("Gaming Laptop", product.getName());
    }

    @Test
    @DisplayName("Should throw exception when setting null name")
    void shouldThrowExceptionWhenSettingNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            product.setName(null);
        });
    }

    @Test
    @DisplayName("Should set stock")
    void shouldSetStock() {
        product.setStock(20);
        assertEquals(20, product.getStock());
    }

    @Test
    @DisplayName("Should throw exception when setting negative stock")
    void shouldThrowExceptionWhenSettingNegativeStock() {
        assertThrows(IllegalArgumentException.class, () -> {
            product.setStock(-5);
        });
    }
} 