package com.parapharmacie.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

/**
 * Redirige vers l'espace admin si l'utilisateur connecte a le role ADMIN,
 * vers la page produits sinon. Respecte la page initialement demandee
 * (ex: un admin qui tentait d'acceder a /admin/products avant d'etre connecte).
 */
public class RoleBasedAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public RoleBasedAuthenticationSuccessHandler() {
        setDefaultTargetUrl("/products");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws ServletException, IOException {
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);

        // Si aucune page protegee n'a declenche la connexion (acces direct a /auth/login),
        // on choisit la destination selon le role plutot que de forcer /products pour tout le monde.
        if (request.getSession(false) == null
                || request.getSession(false).getAttribute("SPRING_SECURITY_SAVED_REQUEST") == null) {
            setDefaultTargetUrl(isAdmin ? "/admin/products" : "/products");
        }

        setAlwaysUseDefaultTargetUrl(false);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
