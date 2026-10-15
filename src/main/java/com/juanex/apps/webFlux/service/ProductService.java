package com.juanex.apps.webFlux.service;

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

    public Mono<Product> save(Product product) {
        Mono<Boolean> existsName = productRepository.findByName(product.getName())
                .hasElement();
        return existsName.flatMap(exists -> exists
                ? Mono.error(new CustomException(HttpStatus.CONFLICT, MESSAGE_NAME_EXISTS))
                : productRepository.save(product));
    }

    public Mono<Product> update(int id, Product product) {
        Mono<Boolean> productId = productRepository.findById(id)
                .hasElement();
        Mono<Boolean> productName = productRepository.repeatedNameProduct(id, product.getName())
                .hasElement();
        return productId.flatMap(existsId -> existsId
                ? productName.flatMap(existsName -> existsName
                ? Mono.error(new CustomException(HttpStatus.CONFLICT, MESSAGE_NAME_EXISTS))
                : productRepository.save(new Product(id, product.getName(), product.getPrice())))
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
