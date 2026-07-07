package com.parapharmacie.controller;

import com.parapharmacie.dto.AccountResponse;
import com.parapharmacie.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public class ApiAccountController {
    private final UserService userService;

    public ApiAccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public AccountResponse me(Authentication authentication) {
        var user = userService.getByEmail(authentication.getName());
        return new AccountResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name());
    }
}
