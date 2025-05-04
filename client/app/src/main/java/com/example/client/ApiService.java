package com.example.client;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

// Основной класс для работы с API
public class ApiService {

    // Класс для отправки данных логина (имя пользователя и пароль)
    static class LoginRequest {
        @SerializedName("username") // Сериализация: как поле будет представлено в JSON
        String username;

        @SerializedName("password") // Сериализация пароля
        String password;

        // Конструктор класса, который принимает имя пользователя и пароль
        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    // Переменная для хранения интерфейса API
    private final AuthApi authApi;

    // Конструктор ApiService, который настраивает клиент для отправки HTTP-запросов
    public ApiService(Context context) {
        // Логгер для отслеживания HTTP-запросов и ответов
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Уровень логирования — вывод всех данных о запросах и ответах

        // Настройка OkHttpClient с логированием
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor) // Добавляем логгер в клиент
                .build();

        // Создаем объект Retrofit, который будет управлять сетевыми запросами
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // Базовый URL для API (локальный сервер для эмулятора Android)
                .client(client) // Используем настроенный клиент
                .addConverterFactory(GsonConverterFactory.create()) // Используем конвертер для работы с JSON
                .build();

        // Инициализация интерфейса для работы с API
        authApi = retrofit.create(AuthApi.class);
    }

    // Метод для логина пользователя
    public void login(String username, String password, LoginCallback callback) {
        // Создаем объект запроса с логином и паролем
        LoginRequest request = new LoginRequest(username, password);

        // Отправляем запрос через API
        authApi.login(request).enqueue(new Callback<LoginResponse>() { // Асинхронный запрос
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                // Обработка успешного ответа
                if (response.isSuccessful() && response.body() != null) {
                    // Если ответ успешный, передаем токен в callback
                    callback.onResult(true, response.body().accessToken);
                } else {
                    // В случае ошибки возвращаем false и null токен
                    callback.onResult(false, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                // Обработка ошибки запроса (например, отсутствие интернета)
                Log.e("ApiService", "Login error", t); // Логируем ошибку
                callback.onResult(false, null); // В случае ошибки вызываем callback с ошибкой
            }
        });
    }

    // Интерфейс для описания методов API, которые будут вызываться через Retrofit
    interface AuthApi {
        @POST("login") // Метод POST для запроса на endpoint "/login"
        Call<LoginResponse> login(@Body LoginRequest request); // Отправка объекта запроса (логин и пароль)
    }

    // Интерфейс для обратного вызова (callback), который будет вызываться при успешной или неудачной попытке логина
    interface LoginCallback {
        void onResult(boolean success, String token); // Возвращаем успех и токен (или null)
    }

    // Класс для описания структуры ответа с сервера (получаем токен доступа)
    static class LoginResponse {
        @SerializedName("access_token") // Поле для токена, которое будет приходить в JSON
        public String accessToken;
    }
}
