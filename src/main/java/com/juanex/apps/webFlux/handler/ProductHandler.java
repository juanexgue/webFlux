package com.juanex.apps.webFlux.handler;

import com.juanex.apps.webFlux.dto.ProductDTO;
import com.juanex.apps.webFlux.entity.Product;
import com.juanex.apps.webFlux.service.ProductService;
import com.juanex.apps.webFlux.validation.ObjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductHandler {

    private final ProductService productService;
    private final ObjectValidator objectValidator;

    public Mono<ServerResponse> getAll(ServerRequest request) {
        Flux<Product> products = productService.getAll();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(products, Product.class);
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        int id = Integer.valueOf(request.pathVariable("id"));
        Mono<Product> product = productService.getById(id);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(product, Product.class);
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<ProductDTO> productMono = request.bodyToMono(ProductDTO.class).doOnNext(objectValidator::validate);
        return productMono.flatMap(productDTO -> {
            Mono<Product> savedProduct = productService.save(productDTO);
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(savedProduct, Product.class);

        });
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        int id = Integer.valueOf(request.pathVariable("id"));
        Mono<ProductDTO> productMono = request.bodyToMono(ProductDTO.class).doOnNext(objectValidator::validate);
        return productMono.flatMap(productDTO -> {
            Mono<Product> updatedProduct = productService.update(id, productDTO);
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(updatedProduct, Product.class);
        });
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        int id = Integer.valueOf(request.pathVariable("id"));
        Mono<Void> deletedProduct = productService.delete(id);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(deletedProduct, Void.class);
    }

}
