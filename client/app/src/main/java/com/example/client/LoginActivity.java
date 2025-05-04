package com.example.client;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editUsername, editPassword; // Поля для ввода логина и пароля
    private Button btnLogin; // Кнопка для входа
    private ApiService apiService; // Интерфейс для взаимодействия с API

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Загружаем макет для LoginActivity

        // Находим элементы UI
        editUsername = findViewById(R.id.editUsername); // Поле для ввода имени пользователя
        editPassword = findViewById(R.id.editPassword); // Поле для ввода пароля
        btnLogin = findViewById(R.id.btnLogin); // Кнопка "Войти"

        // Инициализация ApiService для выполнения запросов на сервер
        apiService = new ApiService(this);

        // Обработчик клика на кнопку "Войти"
        btnLogin.setOnClickListener(view -> {
            // Получаем логин и пароль, введённые пользователем
            String username = editUsername.getText().toString();
            String password = editPassword.getText().toString();

            // Вызываем метод для логина
            login(username, password);
        });
    }

    // Метод для выполнения логина
    private void login(String username, String password) {
        // Вызываем метод apiService для отправки запроса на сервер
        apiService.login(username, password, (success, token) -> {
            // Если логин успешен
            if (success) {
                Log.d("BIO", "Запуск биометрии..."); // Логируем запуск биометрии

                // Проверяем доступность биометрии на устройстве
                if (BiometricHelper.isBiometricAvailable(this)) {
                    // Если биометрия доступна, запускаем аутентификацию
                    BiometricHelper.authenticate(this, isSuccess -> {
                        // Если аутентификация прошла успешно
                        if (isSuccess) {
                            // Сохраняем токен в менеджере токенов
                            TokenManager.saveToken(this, token);
                            // Переходим в MainActivity
                            startActivity(new Intent(this, MainActivity.class));
                            finish(); // Закрываем текущую активность
                        } else {
                            // Если биометрическая аутентификация не прошла
                            Toast.makeText(this, "Биометрическая проверка не пройдена", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Если биометрия недоступна
                    Toast.makeText(this, "Биометрия недоступна на этом устройстве", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Если ошибка при логине
                Toast.makeText(this, "Ошибка логина", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
