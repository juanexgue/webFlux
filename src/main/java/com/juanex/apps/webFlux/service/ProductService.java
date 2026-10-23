package com.juanex.apps.webFlux.service;

import com.juanex.apps.webFlux.dto.ProductDTO;
import com.juanex.apps.webFlux.entity.Product;
import com.juanex.apps.webFlux.exception.CustomException;
import com.juanex.apps.webFlux.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final static String MESSAGE_NF = "product not found";
    private final static String MESSAGE_NAME_EXISTS = "product name already in use";

    private final ProductRepository productRepository;

    public Flux<Product> getAll() {
        return productRepository.findAll();
    }

    public Mono<Product> getById(int id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, MESSAGE_NF)));
    }

    public Mono<Product> save(ProductDTO productDTO) {
        Mono<Boolean> existsName = productRepository.findByName(productDTO.getName())
                .hasElement();
        return existsName.flatMap(exists -> exists
                ? Mono.error(new CustomException(HttpStatus.CONFLICT, MESSAGE_NAME_EXISTS))
                : productRepository.save(Product.builder()
                        .name(productDTO.getName())
                        .price(productDTO.getPrice())
                        .build()));
    }

    public Mono<Product> update(int id, ProductDTO productDTO) {
        Mono<Boolean> productId = productRepository.findById(id)
                .hasElement();
        Mono<Boolean> productName = productRepository.repeatedNameProduct(id, productDTO.getName())
                .hasElement();
        return productId.flatMap(existsId -> existsId
                ? productName.flatMap(existsName -> existsName
                ? Mono.error(new CustomException(HttpStatus.CONFLICT, MESSAGE_NAME_EXISTS))
                : productRepository.save(Product.builder()
                        .id(id)
                        .name(productDTO.getName())
                        .price(productDTO.getPrice())
                        .build()))
                : Mono.error(new CustomException(HttpStatus.NOT_FOUND, MESSAGE_NF)));
    }

public Mono<Void> delete(int id) {
        Mono<Boolean> productId = productRepository.findById(id)
                .hasElement();
        return productId.flatMap(existsId -> existsId
                ? productRepository.deleteById(id)
                : Mono.error(new CustomException(HttpStatus.NOT_FOUND, MESSAGE_NF)));
}
}
