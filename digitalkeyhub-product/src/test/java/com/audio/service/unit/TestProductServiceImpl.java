package com.audio.service.unit;

import com.audio.cache.CacheProductDto;
import com.audio.cache.ProductCacheService;
import com.audio.dto.request.ProductServiceUpdateRequest;
import com.audio.dto.response.ProductServiceResponse;
import com.audio.entity.ProductEntity;
import com.audio.mapper.ProductMapperImpl;
import com.audio.repository.ProductRepository;
import com.audio.service.impl.MinioStorageService;
import com.audio.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ProductServiceImpl")
public class TestProductServiceImpl {
    @InjectMocks
    private ProductServiceImpl productServiceImpl;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductCacheService productCacheService;
    @Spy
    private ProductMapperImpl productMapperImpl;
    @Captor
    private ArgumentCaptor<ProductEntity> productEntityCaptor;
    @Mock
    private MinioStorageService storageService;
    private ProductEntity productEntity;
    private CacheProductDto productCacheDto;
    private ProductServiceUpdateRequest productServiceUpdateRequest;
    private ProductServiceResponse productServiceResponse;
    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final String NAME = UUID.randomUUID().toString();
    private static final String DESCRIPTION = UUID.randomUUID().toString();
    private static final BigDecimal PRICE = new BigDecimal(BigInteger.valueOf(10));
    private static final Integer QUANTITY = 25;
    private static final String SKU = UUID.randomUUID().toString();
    private static final String PHOTO = UUID.randomUUID().toString();
    private static final String DIGITAL = UUID.randomUUID().toString();
    private static final boolean ACTIVE = true;

    @BeforeEach
    void setUp() {
        productEntity = ProductEntity.builder()
                .id(PRODUCT_ID)
                .name(NAME)
                .description(DESCRIPTION)
                .price(PRICE)
                .stockQuantity(QUANTITY)
                .sku(SKU)
                .photoUrl(PHOTO)
                .digitalContent(DIGITAL)
                .isActive(ACTIVE)
                .build();

        productServiceResponse = ProductServiceResponse.builder()
                .id(PRODUCT_ID)
                .name(NAME)
                .description(DESCRIPTION)
                .price(PRICE)
                .stockQuantity(QUANTITY)
                .sku(SKU)
                .photoUrl(PHOTO)
                .isActive(ACTIVE)
                .build();

        productCacheDto = CacheProductDto.builder()
                .id(PRODUCT_ID)
                .name(NAME)
                .description(DESCRIPTION)
                .price(PRICE)
                .stockQuantity(QUANTITY)
                .sku(SKU)
                .photoUrl(PHOTO)
                .digitalContent(DIGITAL)
                .isActive(ACTIVE)
                .build();

        productServiceUpdateRequest = ProductServiceUpdateRequest.builder()
                .name(NAME)
                .description(DESCRIPTION)
                .price(PRICE)
                .stockQuantity(QUANTITY)
                .sku(SKU)
                .isActive(ACTIVE)
                .build();

    }

    @Nested
    @DisplayName("Получить продукт по id")
    class GetProductById {
        @Test
        @DisplayName("Получить продукт если он есть")
        void getProductById() {
            when(productCacheService.getById(PRODUCT_ID)).thenReturn(productCacheDto);
            ProductServiceResponse result = productServiceImpl.getProductById(PRODUCT_ID);
            assertThat(result).isNotNull();
            verify(productCacheService, times(1)).getById(PRODUCT_ID);

        }
    }

    @Nested
    @DisplayName("Получить все продукты")
    class GetAllProductsTest {
        @Test
        @DisplayName("Показать все продукты")
        void whenGetAllProducts_thenReturnsPageOfResponses() {
            ProductEntity product1 = ProductEntity.builder()
                    .id(UUID.randomUUID())
                    .name("Product 1")
                    .price(BigDecimal.valueOf(10))
                    .isActive(true)
                    .build();

            ProductEntity product2 = ProductEntity.builder()
                    .id(UUID.randomUUID())
                    .name("Product 2")
                    .price(BigDecimal.valueOf(20))
                    .isActive(true)
                    .build();

            List<ProductEntity> products = List.of(product1, product2);
            Pageable pageable = PageRequest.of(0, 10);

            Page<ProductEntity> page = new PageImpl<>(products, pageable, products.size());

            when(productRepository.findAll(pageable)).thenReturn(page);

            Page<ProductServiceResponse> result = productServiceImpl.getAllProducts(pageable);


            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getContent())
                    .extracting(ProductServiceResponse::name)
                    .containsExactlyInAnyOrder("Product 1", "Product 2");


            verify(productRepository, times(1)).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("Отсортированный поиск продуктов")
    class searchProducts {
        @Test
        @DisplayName("Найти продукт через фильтры")
        void whenSearchProducts_thenReturnsProductServiceResponse() {
            String name = "test";
            BigDecimal minPrice = BigDecimal.valueOf(10);
            BigDecimal maxPrice = BigDecimal.valueOf(20);
            boolean active = true;
            Pageable pageable = PageRequest.of(0, 10);
            ProductEntity product1 = ProductEntity.builder()
                    .name("product1")
                    .price(BigDecimal.valueOf(50))
                    .isActive(active)
                    .build();
            ProductEntity product2 = ProductEntity.builder()
                    .name("product2")
                    .price(BigDecimal.valueOf(40))
                    .price(minPrice)
                    .isActive(active)
                    .build();
            Page<ProductEntity> page = new PageImpl<>(List.of(product1, product2), pageable, 2);
            when(productRepository.findByFilters(name, minPrice, maxPrice, active, pageable)).thenReturn(page);

            Page<ProductServiceResponse> result = productServiceImpl.searchProducts(name, minPrice, maxPrice, active, pageable);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getContent())
                    .extracting(ProductServiceResponse::name)
                    .containsExactly("product1", "product2");
            verify(productRepository, times(1)).findByFilters(name, minPrice, maxPrice, active, pageable);
        }
    }

    @Nested
    @DisplayName("Обновить продукт по id")
    class TestUpdateProductById {
        @Test
        @DisplayName("Обновить если продукт существует  ")
        void whenProductExists_thenReturnsUpdateProductServiceResponse() {
            when(productCacheService.update(PRODUCT_ID, productServiceUpdateRequest)).thenReturn(productCacheDto);
            ProductServiceResponse expected = ProductServiceResponse.builder()
                    .id(PRODUCT_ID)
                    .name(NAME)
                    .description(DESCRIPTION)
                    .price(PRICE)
                    .stockQuantity(QUANTITY)
                    .sku(SKU)
                    .photoUrl(PHOTO)
                    .isActive(ACTIVE)
                    .updatedAt(LocalDateTime.now())
                    .build();

            ProductServiceResponse result = productServiceImpl.updateProduct(PRODUCT_ID, productServiceUpdateRequest);

            assertThat(result)
                    .usingRecursiveComparison()
                    .ignoringFields("updatedAt")
                    .isEqualTo(expected);

            verify(productCacheService).update(PRODUCT_ID, productServiceUpdateRequest);
        }
    }

    @Nested
    @DisplayName("Обновить фото продукта")
    class UpdateProductPhotoTest {
        @Test
        @DisplayName("Обновить фото продукта если он существует")
        void updateProductPhoto_ProductExists_ReturnsUpdatedProduct() throws Exception {

            MockMultipartFile file = new MockMultipartFile(
                    "image",
                    "test.jpg",
                    "image/jpeg",
                    "test data".getBytes()
            );

            when(productCacheService.getById(PRODUCT_ID)).thenReturn(productCacheDto);
            when(storageService.uploadFile(any(MultipartFile.class), anyString())).thenReturn("newFilePath.jpg");
            when(productCacheService.update(eq(PRODUCT_ID), any(ProductServiceUpdateRequest.class)))
                    .thenReturn(productCacheDto);

            ProductServiceResponse result = productServiceImpl.updateProductPhoto(PRODUCT_ID, file);

            assertThat(result).isNotNull();
            verify(productCacheService).getById(PRODUCT_ID);
            verify(storageService).uploadFile(any(MultipartFile.class), anyString());
            verify(productCacheService).update(eq(PRODUCT_ID), any(ProductServiceUpdateRequest.class));
        }
    }

    @Test
    @DisplayName("Выбрасываем исключение, если произошла ошибка при обновлении фото")
    void updateAvatar_ProfileNotFound() {
        when(productCacheService.getById(PRODUCT_ID)).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> productServiceImpl.updateProductPhoto(PRODUCT_ID, mock(MultipartFile.class)));
    }
}

