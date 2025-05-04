package com.example.server.controller;

import com.example.server.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AuthController {

    // Обработчик POST-запроса на эндпоинт "/login"
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        // Извлекаем параметры "username" и "password" из тела запроса
        String username = request.get("username");
        String password = request.get("password");

        // Проверка на соответствие логина и пароля (например, для теста)
        if ("admin".equals(username) && "admin".equals(password)) {
            // Если логин и пароль правильные, генерируем токен
            String token = JwtUtil.generateToken(username);

            // Возвращаем токен с кодом 200 (успех)
            return ResponseEntity.ok(Map.of("access_token", token));
        } else {
            // Если логин или пароль неверные, возвращаем ошибку 401 (Unauthorized)
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}