package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;

import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Value("${product.image.path}")
    private String imagePath;

    @GetMapping
    public ResponseEntity<Page<Product>> getProducts(
            @RequestParam int page, @RequestParam int size) {
        Page<Product> products = productService.getPaginatedProducts(page, size);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }


    @GetMapping("/image/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        try {
            Path filePath = Paths.get(imagePath).resolve(imageName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
