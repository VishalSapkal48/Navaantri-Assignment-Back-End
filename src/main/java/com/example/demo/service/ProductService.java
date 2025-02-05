package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.opencsv.CSVReader;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);


    @Autowired
    private ProductRepository productRepository;

    
    // Save a product
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Delete product by ID
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // Import products from CSV file

public void importProducts(MultipartFile file) throws Exception {
    try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
        List<Product> products = reader.readAll().stream()
            .skip(1) // Skip header
            .filter(fields -> fields.length >= 2) // Ensure there are at least 2 columns
            .map(fields -> {
                try {
                    Product product = new Product();
                    product.setName(fields[0].trim()); // Assuming Name is the first column
                    // Parse the price safely
                    try {
                        product.setPrice(Double.parseDouble(fields[1].trim())); // Assuming Price is the second column
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Invalid price format in line: " + String.join(",", fields), e);
                    }
                    return product;
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing line: " + String.join(",", fields), e);
                }
            })
            .collect(Collectors.toList());

        // Save all products to the repository
        productRepository.saveAll(products);
    } catch (Exception e) {
        throw new RuntimeException("Error processing file", e);
    }
}


    // Export products to CSV file
    public void exportProducts(Writer writer) throws IOException {
        List<Product> products = productRepository.findAll();
        writer.write("ID,Name,Price\n");
        for (Product product : products) {
            writer.write(product.getId() + "," + product.getName() + "," + product.getPrice() + "\n");
        }
        writer.flush();  // Fix this line
    }

}
