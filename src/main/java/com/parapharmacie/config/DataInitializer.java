package com.parapharmacie.config;

import com.parapharmacie.model.Product;
import com.parapharmacie.model.Role;
import com.parapharmacie.model.User;
import com.parapharmacie.repository.ProductRepository;
import com.parapharmacie.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seedData(ProductRepository productRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (productRepository.count() == 0) {
                productRepository.save(product("Creme hydratante visage", "Soin quotidien pour peaux sensibles.", "Soin visage", "15.90", 25));
                productRepository.save(product("Gel nettoyant doux", "Nettoie sans dessecher la peau.", "Hygiene", "9.50", 40));
                productRepository.save(product("Ecran solaire SPF 50", "Protection solaire haute tolerance.", "Solaire", "18.90", 30));
                productRepository.save(product("Complement vitamine C", "Boite de 30 comprimes.", "Vitamines", "12.00", 50));
            }

            if (!userRepository.existsByEmail("admin@para.test")) {
                var admin = new User();
                admin.setFullName("Administrateur");
                admin.setEmail("admin@para.test");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ROLE_ADMIN);
                userRepository.save(admin);
            }
        };
    }

    private Product product(String name, String description, String category, String price, int stock) {
        var product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setCategory(category);
        product.setPrice(new BigDecimal(price));
        product.setStock(stock);
        product.setImageUrl("/css/product-placeholder.svg");
        return product;
    }
}
