package com.audio.service;

import com.audio.dto.*;
import com.audio.entity.ProductEntity;
import com.audio.exception.ProductNotFoundException;
import com.audio.mapper.ProductMapper;
import com.audio.mapper.ProductMapperImpl;
import com.audio.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private FileStorageService storageService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ProductService productService;

    private final UUID productId = UUID.randomUUID();
    private final ProductEntity productEntity = new ProductEntity();
    private final ProductResponseDto responseDto = new ProductResponseDto(
            productId, "Test", "Desc", BigDecimal.TEN, 5, "SKU123", null, true, null, null);

    @Test
    void createProduct_Success() {
        // Arrange
        ProductCreateDto createDto = new ProductCreateDto(
                "Test", "Desc", BigDecimal.TEN, 5, "SKU123", true);

        when(productMapper.toEntity(createDto)).thenReturn(productEntity);
        when(productRepository.save(productEntity)).thenReturn(productEntity);
        when(productMapper.toResponseDto(productEntity)).thenReturn(responseDto);

        // Act
        ProductResponseDto result = productService.createProduct(createDto);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.id());
        verify(productRepository).save(productEntity);
    }

    @Test
    void getProductById_Success() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
        when(productMapper.toResponseDto(productEntity)).thenReturn(responseDto);

        // Act
        ProductResponseDto result = productService.getProductById(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.id());
    }

    @Test
    void getProductById_NotFound() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () ->
                productService.getProductById(productId));
    }

    @Test
    void getAllProducts_Success() {
        // Arrange
        Pageable pageable = Pageable.ofSize(10);
        Page<ProductEntity> page = new PageImpl<>(List.of(productEntity));

        when(productRepository.findAll(pageable)).thenReturn(page);
        when(productMapper.toResponseDto(productEntity)).thenReturn(responseDto);

        // Act
        Page<ProductResponseDto> result = productService.getAllProducts(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(responseDto, result.getContent().get(0));
    }

    @Test
    void updateProduct_Success() {
        // Arrange
        ProductUpdateDto updateDto = new ProductUpdateDto(
                "Updated", "New Desc", BigDecimal.valueOf(20), 10, "SKU456", false);

        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
        when(productRepository.save(productEntity)).thenReturn(productEntity);
        when(productMapper.toResponseDto(productEntity)).thenReturn(responseDto);

        // Act
        ProductResponseDto result = productService.updateProduct(productId, updateDto);

        // Assert
        assertNotNull(result);
        verify(productMapper).updateEntity(updateDto, productEntity);
    }

    @Test
    void updateProductPhoto_Success() throws Exception {
        // Arrange
        String fileName = "photo.jpg";
        String filePath = "path/to/photo.jpg";

        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);

        doReturn(filePath)
                .when(storageService)
                .uploadFile(any(MultipartFile.class), anyString());

        when(productRepository.save(productEntity)).thenReturn(productEntity);
        when(productMapper.toResponseDto(productEntity)).thenReturn(responseDto);

        // Act
        ProductResponseDto result = productService.updateProductPhoto(productId, multipartFile);

        // Assert
        assertNotNull(result);
        verify(storageService).uploadFile(any(MultipartFile.class), anyString());
    }

    @Test
    void deleteProductPhoto_Success() throws Exception {
        // Arrange
        productEntity.setPhotoUrl("existing/path.jpg");

        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
        doNothing().when(storageService).deleteFile("existing/path.jpg");
        when(productRepository.save(productEntity)).thenReturn(productEntity);
        when(productMapper.toResponseDto(productEntity)).thenReturn(responseDto);

        // Act
        ProductResponseDto result = productService.deleteProductPhoto(productId);

        // Assert
        assertNull(result.photoUrl());
        verify(storageService).deleteFile("existing/path.jpg");
    }

    @Test
    void getProductPhoto_Success() throws Exception {
        // Arrange
        String photoPath = "path/to/photo.jpg";
        byte[] photoBytes = "test photo".getBytes();
        productEntity.setPhotoUrl(photoPath);
        InputStream inputStream = new ByteArrayInputStream(photoBytes);

        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
        when(storageService.getFile(photoPath)).thenReturn(inputStream);

        // Act
        byte[] result = productService.getProductPhoto(productId);

        // Assert
        assertArrayEquals(photoBytes, result);
    }

    @Test
    void deleteProduct_WithPhoto() throws Exception {
        // Arrange
        productEntity.setPhotoUrl("path/to/photo.jpg");

        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
        doNothing().when(storageService).deleteFile("path/to/photo.jpg");

        // Act
        productService.deleteProduct(productId);

        // Assert
        verify(storageService).deleteFile("path/to/photo.jpg");
        verify(productRepository).delete(productEntity);
    }

    @Test
    void setProductActiveStatus_Success() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
        when(productRepository.save(productEntity)).thenReturn(productEntity);
        when(productMapper.toResponseDto(productEntity)).thenReturn(responseDto);

        // Act
        ProductResponseDto result = productService.setProductActiveStatus(productId, false);

        // Assert
        assertNotNull(result);
        assertFalse(productEntity.getIsActive());
    }

    @Test
    void updateStockQuantity_Success() {
        // Arrange
        productEntity.setStockQuantity(10);

        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
        when(productRepository.save(productEntity)).thenReturn(productEntity);
        when(productMapper.toResponseDto(productEntity)).thenReturn(responseDto);

        // Act
        ProductResponseDto result = productService.updateStockQuantity(productId, 5);

        // Assert
        assertNotNull(result);
        assertEquals(15, productEntity.getStockQuantity());
    }

    @Test
    void updateStockQuantity_InsufficientStock() {
        // Arrange
        productEntity.setStockQuantity(3);

        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                productService.updateStockQuantity(productId, -5));
    }
}