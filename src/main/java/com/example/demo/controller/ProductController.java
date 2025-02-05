package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import com.opencsv.CSVReader;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")

public class ProductController {
	private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

	//private static final Logger logger = LoggerFactory.getLogger(.class);


    @Autowired
    private ProductService productService;

    // Save a product
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.saveProduct(product));
    }

    // Get all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // Delete product by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Import from CSV
    @PostMapping("/upload")
    public ResponseEntity<String> importCSV(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                logger.error("No file uploaded");
                return ResponseEntity.badRequest().body("Please upload a CSV file.");
            }
            productService.importProducts(file);
            logger.info("CSV file uploaded successfully!");
            return ResponseEntity.ok("CSV file uploaded successfully!");
        } catch (Exception e) {
            logger.error("Error processing CSV file: ", e);
            return ResponseEntity.badRequest().body("Error processing CSV file: " + e.getMessage());
        }
    }


    // Export to CSV
    @GetMapping("/export")
    public void exportCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=products.csv");
        PrintWriter writer = response.getWriter();
        productService.exportProducts(writer);
    }
}
