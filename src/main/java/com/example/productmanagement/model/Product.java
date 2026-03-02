package com.example.productmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @Column(name = "price", nullable = false)
    @NotNull(message = "Giá sản phẩm không được để trống")
    @Min(value = 1, message = "Giá sản phẩm phải lớn hơn hoặc bằng 1")
    @Max(value = 9999999, message = "Giá sản phẩm không được vượt quá 9999999")
    private Double price;

    @Column(name = "image", length = 200)
    @Size(max = 200, message = "Tên hình ảnh không được quá 200 kí tự")
    private String image;

    @Column(name = "category", length = 50)
    private String category;

    public Product() {
    }

    public Product(Long id, String code, String name, Double price, String image, String category) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.price = price;
        this.image = image;
        this.category = category;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
