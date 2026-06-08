package com.example.warehouse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository productRepository;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        productService = new ProductService(productRepository);
    }

    @Test
    @DisplayName("1. Thêm số lượng hợp lệ vào sản phẩm hiện có")
    void testAddStockSuccess() {
        ProductService.Product product = new ProductService.Product("PROD01", 10);
        when(productRepository.findById("PROD01")).thenReturn(Optional.of(product));

        int result = productService.updateStock("PROD01", 5);

        assertThat(result).isEqualTo(15);
        assertThat(product.getStockQuantity()).isEqualTo(15);
    }

    @Test
    @DisplayName("2. Trừ số lượng hợp lệ từ sản phẩm hiện có")
    void testDeductStockSuccess() {
        ProductService.Product product = new ProductService.Product("PROD01", 10);
        when(productRepository.findById("PROD01")).thenReturn(Optional.of(product));

        int result = productService.updateStock("PROD01", -4);

        assertThat(result).isEqualTo(6);
        assertThat(product.getStockQuantity()).isEqualTo(6);
    }

    @Test
    @DisplayName("3. Cố gắng trừ số lượng lớn hơn tồn kho hiện có")
    void testDeductStockMoreThanAvailable() {
        ProductService.Product product = new ProductService.Product("PROD01", 10);
        when(productRepository.findById("PROD01")).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateStock("PROD01", -15))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Resulting stock would be negative");

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("4. Cập nhật tồn kho cho sản phẩm không tồn tại")
    void testUpdateStockForNonExistingProduct() {
        when(productRepository.findById("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateStock("INVALID", 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product not found with ID: INVALID");

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("5. Kiểm tra phương thức save được gọi đúng cách khi cập nhật thành công")
    void testSaveMethodIsCalledOnSuccess() {
        ProductService.Product product = new ProductService.Product("PROD01", 10);
        when(productRepository.findById("PROD01")).thenReturn(Optional.of(product));

        productService.updateStock("PROD01", 5);

        verify(productRepository, times(1)).save(product);
    }
}