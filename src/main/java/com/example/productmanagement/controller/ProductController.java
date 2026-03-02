package com.example.productmanagement.controller;

import com.example.productmanagement.model.Product;
import com.example.productmanagement.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Hiển thị danh sách sản phẩm
    @GetMapping
    public String listProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "products/list";
    }

    // Hiển thị form thêm sản phẩm
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", getCategories());
        return "products/form";
    }

    // Xử lý thêm sản phẩm
    @PostMapping
    public String createProduct(@Valid @ModelAttribute("product") Product product,
                                BindingResult bindingResult,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                Model model) {
        // Validate image filename length
        if (imageFile != null && !imageFile.isEmpty()) {
            String filename = imageFile.getOriginalFilename();
            if (filename != null && filename.length() > 200) {
                bindingResult.rejectValue("image", "error.product", 
                    "Tên hình ảnh không được quá 200 kí tự");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", getCategories());
            return "products/form";
        }

        try {
            productService.saveProduct(product, imageFile);
            return "redirect:/products";
        } catch (IOException e) {
            bindingResult.rejectValue("image", "error.product", e.getMessage());
            model.addAttribute("categories", getCategories());
            return "products/form";
        }
    }

    // Hiển thị form sửa sản phẩm
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", getCategories());
        return "products/form";
    }

    // Xử lý cập nhật sản phẩm
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id,
                               @Valid @ModelAttribute("product") Product product,
                               BindingResult bindingResult,
                               @RequestParam("imageFile") MultipartFile imageFile,
                               Model model) {
        // Validate image filename length
        if (imageFile != null && !imageFile.isEmpty()) {
            String filename = imageFile.getOriginalFilename();
            if (filename != null && filename.length() > 200) {
                bindingResult.rejectValue("image", "error.product", 
                    "Tên hình ảnh không được quá 200 kí tự");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", getCategories());
            return "products/form";
        }

        try {
            productService.updateProduct(id, product, imageFile);
            return "redirect:/products";
        } catch (IOException e) {
            bindingResult.rejectValue("image", "error.product", e.getMessage());
            model.addAttribute("categories", getCategories());
            return "products/form";
        }
    }

    // Xóa sản phẩm
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        productService.deleteProduct(id);
        return "redirect:/products";
    }

    // Danh sách danh mục
    private List<String> getCategories() {
        return List.of("điện thoại", "laptop");
    }
}
