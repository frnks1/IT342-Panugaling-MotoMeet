package edu.cit.panugaling.motomeet.controller;

import edu.cit.panugaling.motomeet.dto.RegisterRequest;
import edu.cit.panugaling.motomeet.model.User;
import edu.cit.panugaling.motomeet.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + " " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        if (userRepository.findByEmail(request.getEmail().trim().toLowerCase()).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("message", "Email already exists"));
        }

        User user = new User();
        user.setFirstname(request.getFirstname().trim());
        user.setLastname(request.getLastname().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> creds) {
        String email = creds.getOrDefault("email", "").trim().toLowerCase();
        String password = creds.get("password");

        return userRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .map(u -> {
                    Map<String, Object> payload = new LinkedHashMap<>();
                    payload.put("message", "Login successful");
                    payload.put("user", u.getFirstname() + " " + u.getLastname());
                    return ResponseEntity.ok(payload);
                })
                .orElse(ResponseEntity.status(401).body(Map.of("message", "Invalid credentials")));
    }
}