package com.example.client;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView txtUserInfo; // Поле для отображения информации о пользователе

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Загружаем layout для MainActivity

        // Инициализация TextView, где будет отображаться информация о пользователе
        txtUserInfo = findViewById(R.id.txtUserInfo);

        // Получаем токен из TokenManager
        String token = TokenManager.getToken(this);

        // Проверяем, есть ли токен
        if (token != null) {
            // Если токен есть, делаем запрос к API для получения данных профиля
            fetchProfile(token);
        } else {
            // Если токена нет, выводим сообщение о необходимости войти заново
            txtUserInfo.setText("Токен не найден. Пожалуйста, войдите заново.");
        }
    }

    // Метод для выполнения запроса к API и получения данных профиля
    private void fetchProfile(String token) {
        // Запуск нового потока для выполнения сетевого запроса (работаем асинхронно)
        new Thread(() -> {
            try {
                // Создаем OkHttpClient для отправки запросов
                OkHttpClient client = new OkHttpClient();

                // Строим запрос для получения данных профиля
                Request request = new Request.Builder()
                        .url("http://10.0.2.2:8080/profile") // URL для запроса (локальный сервер для эмулятора)
                        .header("Authorization", "Bearer " + token) // Добавляем заголовок с токеном
                        .build();

                // Выполняем запрос и получаем ответ
                Response response = client.newCall(request).execute();

                // Проверяем, успешен ли ответ
                if (response.isSuccessful() && response.body() != null) {
                    // Если ответ успешен, парсим тело ответа (JSON)
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body); // Преобразуем строку в JSON-объект
                    String username = json.getString("username"); // Получаем имя пользователя
                    String email = json.getString("email"); // Получаем email пользователя

                    // Обновляем UI, показываем данные пользователя на экране
                    runOnUiThread(() -> txtUserInfo.setText("Пользователь: " + username + "\nEmail: " + email));
                } else {
                    // Если ошибка при получении данных, выводим сообщение об ошибке
                    runOnUiThread(() -> txtUserInfo.setText("Ошибка получения профиля"));
                }
            } catch (Exception e) {
                // Если произошла ошибка (например, ошибка соединения), выводим сообщение об ошибке
                runOnUiThread(() -> txtUserInfo.setText("Ошибка: " + e.getMessage()));
            }
        }).start(); // Запуск потока
    }
}