package com.example.server.controller;

import com.example.server.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class ProfileController {

    // Обработчик GET-запроса на эндпоинт "/profile"
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        // Проверка, что заголовок Authorization присутствует и начинается с "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Если заголовок отсутствует или неверный — возвращаем ошибку 401 (Missing token)
            return ResponseEntity.status(401).body("Missing token");
        }

        // Извлекаем токен из заголовка (удаляем "Bearer ")
        String token = authHeader.substring(7);

        // Проверка на валидность токена
        if (!JwtUtil.validateToken(token)) {
            // Если токен неверен, возвращаем ошибку 401 (Invalid token)
            return ResponseEntity.status(401).body("Invalid token");
        }

        // Извлекаем имя пользователя из токена (если токен валиден)
        String username = JwtUtil.getUsernameFromToken(token);

        // Возвращаем успешный ответ с данными пользователя
        return ResponseEntity.ok(Map.of(
                "username", username, // Имя пользователя
                "email", username + "@example.com" // Email (в примере генерируем email по имени)
        ));
    }
}