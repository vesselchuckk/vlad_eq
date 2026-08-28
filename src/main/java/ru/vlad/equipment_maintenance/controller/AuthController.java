package ru.vlad.equipment_maintenance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.vlad.equipment_maintenance.dto.AuthRequest;
import ru.vlad.equipment_maintenance.dto.JwtResponse;
import ru.vlad.equipment_maintenance.entity.Role;
import ru.vlad.equipment_maintenance.entity.User;
import ru.vlad.equipment_maintenance.repository.UserRepository;
import ru.vlad.equipment_maintenance.security.JwtCore;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtCore jwtCore;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody AuthRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Ошибка: Пользователь с таким логином уже существует!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        if ("ADMIN".equalsIgnoreCase(registerRequest.getRole())) {
            user.setRole(Role.ROLE_ADMIN);
        } else {
            user.setRole(Role.ROLE_USER);
        }

        userRepository.save(user);
        return ResponseEntity.ok("Пользователь успешно зарегистрирован!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest loginRequest) {
        // Проводим стандартную проверку логина и пароля через Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // Сохраняем пользователя в контексте авторизации
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Генерируем валидный JWT токен на основе объекта аутентификации
        String token = jwtCore.generateToken(authentication);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}