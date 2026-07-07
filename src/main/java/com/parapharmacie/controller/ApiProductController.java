package com.parapharmacie.controller;

import com.parapharmacie.model.Product;
import com.parapharmacie.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ApiProductController {
    private final ProductService productService;

    public ApiProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> list(@RequestParam(required = false) String q) {
        return productService.findAll(q);
    }

    @GetMapping("/{id}")
    public Product details(@PathVariable Long id) {
        return productService.getById(id);
    }
}
