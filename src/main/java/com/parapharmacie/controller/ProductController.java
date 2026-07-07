package com.parapharmacie.controller;

import com.parapharmacie.model.Product;
import com.parapharmacie.service.FileStorageService;
import com.parapharmacie.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductController {
    private final ProductService productService;
    private final FileStorageService fileStorageService;

    public ProductController(ProductService productService, FileStorageService fileStorageService) {
        this.productService = productService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/products";
    }

    @GetMapping("/products")
    public String list(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("products", productService.findAll(q));
        model.addAttribute("q", q);
        return "products/list";
    }

    @GetMapping("/products/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getById(id));
        return "products/details";
    }

    @GetMapping("/admin/products")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminProducts(Model model) {
        model.addAttribute("products", productService.findAll(null));
        return "admin/products";
    }

    @GetMapping("/admin/products/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/product-form";
    }

    @GetMapping("/admin/products/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getById(id));
        return "admin/product-form";
    }

    @PostMapping("/admin/products")
    @PreAuthorize("hasRole('ADMIN')")
    public String save(@Valid @ModelAttribute Product product, BindingResult bindingResult,
                        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                        Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/product-form";
        }

        if (product.getId() != null && (imageFile == null || imageFile.isEmpty())) {
            // Conserver l'image existante si aucun nouveau fichier n'est fourni lors d'une modification.
            String existingImageUrl = productService.getById(product.getId()).getImageUrl();
            product.setImageUrl(existingImageUrl);
        }

        try {
            String storedImageUrl = fileStorageService.store(imageFile);
            if (storedImageUrl != null) {
                product.setImageUrl(storedImageUrl);
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            return "admin/product-form";
        }

        if (product.getImageUrl() == null || product.getImageUrl().isBlank()) {
            product.setImageUrl("/css/product-placeholder.svg");
        }

        productService.save(product);
        redirectAttributes.addFlashAttribute("success", "Produit enregistre avec succes.");
        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/admin/products";
    }
}
