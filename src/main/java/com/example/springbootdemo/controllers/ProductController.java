package com.example.springbootdemo.controllers;

import com.example.springbootdemo.models.Product;
import com.example.springbootdemo.models.ResponseObject;
import com.example.springbootdemo.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // chú thích annotation trong spring framework để đánh dấu một lớp java là controler
@RequestMapping(path = "/api/v1/Products") // định tuyến routing ánh xạ các request
public class ProductController {

    // DI = Dependency injection
    @Autowired
    private ProductRepository repository;

    @GetMapping("/getAllProducts") // method GET
    // this request is: http://localhost:8080/api/v1/Products/getAllProducts
    List<Product> getAllProduct() {
        return repository.findAll();
    }

    // luu du lieu vao database dung H2 DB = In-memory Database

    //
    @GetMapping("/{id}")
    ResponseEntity<ResponseObject> findById(@PathVariable Long id) {
        Optional<Product> foundProduct = repository.findById(id);
        return foundProduct.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Query product successfully", foundProduct)
                ) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Cannot find product with id = " + id, "")
                );

    }

    //insert new Product with POST method
    @PostMapping("/insert")
    ResponseEntity<ResponseObject> insertProduct(@RequestBody Product newProduct) {
        List<Product> foundProducts = repository.findByProductName(newProduct.getProductName().trim());
        if (!foundProducts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed", "Product name already taken", "")
            );
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Insert new product successfully", repository.save(newProduct))
        );
    }

    // update product
    @PutMapping("/{id}")
    ResponseEntity<ResponseObject> updateProduct(@RequestBody Product newProduct, @PathVariable Long id) {
        Product updateProduct = repository.findById(id)
                .map(product -> {
                   product.setProductName(newProduct.getProductName());
                   product.setPrice(newProduct.getPrice());
                   product.setYear(newProduct.getYear());
                   return repository.save(product);
                }).orElseGet(() -> {
                    newProduct.setId(id);
                    return repository.save(newProduct);
                });
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Update product successfully", updateProduct)
        );
    }

    // Delete product
    @DeleteMapping("/{id}")
    ResponseEntity<ResponseObject> deleteProduct(@PathVariable Long id) {
        Optional<Product> exists = repository.findById(id);
        if (exists.isPresent()) {
            repository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Delete product successfully", "")
            );
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObject("failed", "Cannot find product to delete", "")
        );
    }
}
