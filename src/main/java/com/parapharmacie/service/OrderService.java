package com.parapharmacie.service;

import com.parapharmacie.model.CustomerOrder;
import com.parapharmacie.model.OrderItem;
import com.parapharmacie.model.OrderStatus;
import com.parapharmacie.model.User;
import com.parapharmacie.repository.CartItemRepository;
import com.parapharmacie.repository.CustomerOrderRepository;
import com.parapharmacie.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {
    private final CustomerOrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public OrderService(CustomerOrderRepository orderRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CustomerOrder createOrder(User user, String deliveryAddress) {
        var cart = cartItemRepository.findByUser(user);
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Votre panier est vide.");
        }

        var order = new CustomerOrder();
        order.setUser(user);
        order.setDeliveryAddress(deliveryAddress);

        BigDecimal total = BigDecimal.ZERO;
        for (var cartItem : cart) {
            var product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Stock insuffisant pour " + product.getName());
            }

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            var orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(cartItem.getSubtotal());
            order.addItem(orderItem);

            total = total.add(cartItem.getSubtotal());
        }

        order.setTotal(total);
        var savedOrder = orderRepository.save(order);
        cartItemRepository.deleteByUser(user);
        return savedOrder;
    }

    public List<CustomerOrder> findForUser(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<CustomerOrder> findAll() {
        return orderRepository.findAll();
    }

    public CustomerOrder getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable."));
    }

    public void updateStatus(Long orderId, OrderStatus status) {
        var order = getById(orderId);
        order.setStatus(status);
        orderRepository.save(order);
    }
}
