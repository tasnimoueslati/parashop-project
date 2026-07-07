package com.parapharmacie.service;

import com.parapharmacie.model.CartItem;
import com.parapharmacie.model.User;
import com.parapharmacie.repository.CartItemRepository;
import com.parapharmacie.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public List<CartItem> getCart(User user) {
        return cartItemRepository.findByUser(user);
    }

    public void addToCart(User user, Long productId, int quantity) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable."));
        if (quantity < 1) {
            throw new IllegalArgumentException("La quantite doit etre positive.");
        }
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Stock insuffisant.");
        }

        var item = cartItemRepository.findByUserAndProduct(user, product).orElseGet(() -> {
            var newItem = new CartItem();
            newItem.setUser(user);
            newItem.setProduct(product);
            newItem.setQuantity(0);
            return newItem;
        });
        item.setQuantity(item.getQuantity() + quantity);
        cartItemRepository.save(item);
    }

    public void updateQuantity(User user, Long itemId, int quantity) {
        var item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable."));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Article non autorise.");
        }
        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }

    public void remove(User user, Long itemId) {
        updateQuantity(user, itemId, 0);
    }

    public BigDecimal getTotal(User user) {
        return getCart(user).stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void clear(User user) {
        cartItemRepository.deleteByUser(user);
    }
}
