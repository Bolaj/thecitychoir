package com.portfolio.thecitychoir.controller;

import com.portfolio.thecitychoir.dto.RequestResponseDTO;
import com.portfolio.thecitychoir.entity.Product;
import com.portfolio.thecitychoir.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final ProductRepository productRepository;

    @GetMapping("/public/products")
    public ResponseEntity<Object> getAllProducts(){
        return ResponseEntity.ok(productRepository.findAll());
    }
    @PostMapping("/admin/save-product")
    public ResponseEntity<Object> saveProduct(@RequestBody RequestResponseDTO productRequest){
        Product product = new Product();
        product.setProductName(productRequest.getName());
        return ResponseEntity.ok(productRepository.save(product));
    }

    @GetMapping("/alone")
    public ResponseEntity<Object> userAlone(){
        return ResponseEntity.ok("Users Alone can access this API");
    }

    @GetMapping("/adminuser/both")
    public ResponseEntity<Object> bothAdminAndUser(){
        return ResponseEntity.ok("Both Admin and User can access this API");
    }
}
