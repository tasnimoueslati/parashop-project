package com.parapharmacie.controller;

import com.parapharmacie.model.OrderStatus;
import com.parapharmacie.service.CartService;
import com.parapharmacie.service.OrderService;
import com.parapharmacie.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderController {
    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;

    public OrderController(OrderService orderService, CartService cartService, UserService userService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/orders")
    public String myOrders(Authentication authentication, Model model) {
        var user = userService.getByEmail(authentication.getName());
        model.addAttribute("orders", orderService.findForUser(user));
        return "orders/list";
    }

    @GetMapping("/checkout")
    public String checkout(Authentication authentication, Model model) {
        var user = userService.getByEmail(authentication.getName());
        model.addAttribute("items", cartService.getCart(user));
        model.addAttribute("total", cartService.getTotal(user));
        return "orders/checkout";
    }

    @PostMapping("/checkout")
    public String createOrder(@RequestParam String deliveryAddress, Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            var order = orderService.createOrder(userService.getByEmail(authentication.getName()), deliveryAddress);
            redirectAttributes.addFlashAttribute("success", "Commande #" + order.getId() + " creee avec succes.");
            return "redirect:/orders";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/admin/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminOrders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/orders";
    }

    @PostMapping("/admin/orders/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        orderService.updateStatus(id, status);
        return "redirect:/admin/orders";
    }
}
