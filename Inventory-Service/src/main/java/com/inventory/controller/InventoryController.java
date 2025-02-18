package com.inventory.controller;

import com.inventory.entity.Product;
import com.inventory.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/")
public class InventoryController {


    private ProductService productService;

    public InventoryController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("create")
    public ResponseEntity<String> addProduct(@RequestBody List<Product> productList){
        return ResponseEntity.ok(productService.addProduct(productList));
    }

    @GetMapping("{productId}")
    public ResponseEntity<Product> getDetail(@PathVariable Long productId){
        return ResponseEntity.ok(productService.getDetail(productId));
    }

    @PutMapping("{productId}")
    public ResponseEntity<String> updateProduct(@RequestBody List<Product> productList){
        return ResponseEntity.ok(productService.updateProduct(productList));
    }


}
