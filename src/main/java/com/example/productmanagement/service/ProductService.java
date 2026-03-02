package com.example.productmanagement.service;

import com.example.productmanagement.model.Product;
import com.example.productmanagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Lưu vào thư mục uploads trong thư mục home của user
    private Path getUploadPath() throws IOException {
        Path homePath = Paths.get(System.getProperty("user.home"));
        Path uploadPath = homePath.resolve("product-uploads");
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        return uploadPath;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String originalFilename = imageFile.getOriginalFilename();
            if (originalFilename != null && originalFilename.length() > 200) {
                throw new IOException("Tên hình ảnh không được quá 200 kí tự");
            }
            
            String filename = UUID.randomUUID().toString() + "_" + originalFilename;
            Path uploadPath = getUploadPath();
            Path filePath = uploadPath.resolve(filename);
            
            // Copy file thủ công
            try (InputStream inputStream = imageFile.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
            
            product.setImage(filename);
        }
        
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product product, MultipartFile imageFile) throws IOException {
        Optional<Product> existingProduct = productRepository.findById(id);
        
        if (existingProduct.isPresent()) {
            Product updatedProduct = existingProduct.get();
            updatedProduct.setCode(product.getCode());
            updatedProduct.setName(product.getName());
            updatedProduct.setPrice(product.getPrice());
            updatedProduct.setCategory(product.getCategory());
            
            if (imageFile != null && !imageFile.isEmpty()) {
                String originalFilename = imageFile.getOriginalFilename();
                if (originalFilename != null && originalFilename.length() > 200) {
                    throw new IOException("Tên hình ảnh không được quá 200 kí tự");
                }
                
                // Delete old image if exists
                if (updatedProduct.getImage() != null) {
                    Path oldImagePath = getUploadPath().resolve(updatedProduct.getImage());
                    Files.deleteIfExists(oldImagePath);
                }
                
                String filename = UUID.randomUUID().toString() + "_" + originalFilename;
                Path uploadPath = getUploadPath();
                Path filePath = uploadPath.resolve(filename);
                
                // Copy file thủ công
                try (InputStream inputStream = imageFile.getInputStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
                
                updatedProduct.setImage(filename);
            }
            
            return productRepository.save(updatedProduct);
        }
        
        return null;
    }

    public void deleteProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        
        if (product.isPresent()) {
            // Delete image file if exists
            if (product.get().getImage() != null) {
                try {
                    Path imagePath = getUploadPath().resolve(product.get().getImage());
                    Files.deleteIfExists(imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            productRepository.deleteById(id);
        }
    }
}
