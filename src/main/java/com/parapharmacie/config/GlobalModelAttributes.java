package com.parapharmacie.config;

import com.parapharmacie.service.CartService;
import com.parapharmacie.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private final CartService cartService;
    private final UserService userService;

    public GlobalModelAttributes(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @ModelAttribute
    public void addCartItemCount(Model model) {
        int cartItemCount = 0;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            try {
                var user = userService.getByEmail(authentication.getName());
                cartItemCount = cartService.getCart(user).stream()
                        .mapToInt(item -> item.getQuantity())
                        .sum();
            } catch (RuntimeException ignored) {
                // Utilisateur non trouve ou session invalide : on garde le compteur a 0.
            }
        }

        model.addAttribute("cartItemCount", cartItemCount);
    }
}
