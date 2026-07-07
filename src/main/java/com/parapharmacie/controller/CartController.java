package com.parapharmacie.controller;

import com.parapharmacie.service.CartService;
import com.parapharmacie.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public String showCart(Authentication authentication, Model model) {
        var user = userService.getByEmail(authentication.getName());
        model.addAttribute("items", cartService.getCart(user));
        model.addAttribute("total", cartService.getTotal(user));
        return "cart/index";
    }

    @PostMapping("/add/{productId}")
    public String add(@PathVariable Long productId, @RequestParam(defaultValue = "1") int quantity,
                      Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            cartService.addToCart(userService.getByEmail(authentication.getName()), productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Produit ajoute au panier.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/products";
    }

    @PostMapping("/items/{itemId}")
    public String update(@PathVariable Long itemId, @RequestParam int quantity, Authentication authentication) {
        cartService.updateQuantity(userService.getByEmail(authentication.getName()), itemId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/items/{itemId}/remove")
    public String remove(@PathVariable Long itemId, Authentication authentication) {
        cartService.remove(userService.getByEmail(authentication.getName()), itemId);
        return "redirect:/cart";
    }
}
