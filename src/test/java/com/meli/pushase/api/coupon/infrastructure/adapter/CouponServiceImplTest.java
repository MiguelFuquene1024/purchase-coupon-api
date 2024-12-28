package com.meli.pushase.api.coupon.infrastructure.adapter;


import com.meli.pushase.api.coupon.application.exception.CustomException;
import com.meli.pushase.api.coupon.application.service.ProductsClient;
import com.meli.pushase.api.coupon.domain.CouponResponse;
import com.meli.pushase.api.coupon.domain.ProductClientResponse;
import com.meli.pushase.api.coupon.infrastructure.document.Coupon;
import com.meli.pushase.api.coupon.infrastructure.repository.CouponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private ProductsClient productsClient;

    @InjectMocks
    private CouponServiceImpl couponService;

    @Test
    void testGetMaxAmountItems(){
        // Datos de prueba
        String[] itemIds = {"item1", "item2"};
        double amount = 100.0;

        // Mocking la respuesta del servicio de productos
        ProductClientResponse productClientResponse = new ProductClientResponse();
        Map<String, Double> products = new HashMap<>();
        products.put("item1", 50.0);
        products.put("item2", 50.0);
        productClientResponse.setProducts(products);
        productClientResponse.setAmount(100.0);

        when(productsClient.getWebProducts(itemIds, amount)).thenReturn(Mono.just(productClientResponse));

        // Mocking el comportamiento del repositorio
        when(couponRepository.isProductNameUnique(anyString())).thenReturn(Mono.just(true));
        when(couponRepository.saveCoupon(any(Coupon.class))).thenReturn(Mono.empty());

        // Ejecutar el servicio
        Mono<CouponResponse> result = couponService.getMaxAmountItems(itemIds, amount);

        // Verificar el resultado con StepVerifier
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getAmount() == 100.0 && response.getItem_ids().length == 2)
                .verifyComplete();

        // Verificar interacciones con los mocks
        verify(productsClient).getWebProducts(itemIds, amount);
        verify(couponRepository, times(2)).isProductNameUnique(anyString());
        verify(couponRepository, times(2)).saveCoupon(any(Coupon.class));
    }
    @Test
    void testGetMaxAmountItems_shouldThrowCustomException() {
        // Datos de prueba
        String[] itemIds = {"item1", "item2"};
        double amount = 100.0;

        // Simular una excepción en el servicio de productos
        when(productsClient.getWebProducts(itemIds, amount)).thenReturn(Mono.error(new RuntimeException("Error al obtener productos")));

        // Ejecutar el servicio y verificar que lance una excepción personalizada
        Mono<CouponResponse> result = couponService.getMaxAmountItems(itemIds, amount);

        // Verificar el error
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof CustomException && throwable.getMessage().contains("Error al obtener productos"))
                .verify();

        // Verificar interacciones con los mocks
        verify(productsClient).getWebProducts(itemIds, amount);
    }
    @Test
    void testGetTop5Favorites() {
        // Mocking el comportamiento del repositorio
        Map<String, Integer> favoriteCoupons = new HashMap<>();
        favoriteCoupons.put("item1", 10);
        favoriteCoupons.put("item2", 5);

        when(couponRepository.getTop5FavoriteCoupons()).thenReturn(Mono.just(favoriteCoupons));

        // Ejecutar el servicio
        Mono<Map<String, Integer>> result = couponService.getTop5Favorites();

        // Verificar el resultado con StepVerifier
        StepVerifier.create(result)
                .expectNext(favoriteCoupons)
                .verifyComplete();

        // Verificar interacciones con el mock
        verify(couponRepository).getTop5FavoriteCoupons();
    }

}