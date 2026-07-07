package com.parapharmacie.controller;

import com.parapharmacie.dto.AuthRequest;
import com.parapharmacie.dto.AuthResponse;
import com.parapharmacie.dto.RegisterRequest;
import com.parapharmacie.repository.UserRepository;
import com.parapharmacie.security.JwtService;
import com.parapharmacie.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthController(UserService userService, UserRepository userRepository, AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService, JwtService jwtService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @GetMapping("/auth/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/auth/register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest("", "", ""));
        return "auth/register";
    }

    @PostMapping("/auth/register")
    public String register(@Valid @ModelAttribute RegisterRequest registerRequest, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            userService.register(registerRequest);
            return "redirect:/auth/login?registered";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "auth/register";
        }
    }

    @PostMapping("/api/auth/register")
    @ResponseBody
    public AuthResponse apiRegister(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return apiLogin(new AuthRequest(request.email(), request.password()));
    }

    @PostMapping("/api/auth/login")
    @ResponseBody
    public AuthResponse apiLogin(@Valid @RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        var details = userDetailsService.loadUserByUsername(request.email());
        var token = jwtService.generateToken(details);
        var user = userRepository.findByEmail(request.email()).orElseThrow();
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }
}
