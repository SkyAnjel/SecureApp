package com.example.client;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenManager {

    // Название SharedPreferences, где будет храниться токен
    private static final String PREF_NAME = "secure_prefs";

    // Ключ для токена, который будет сохранён в SharedPreferences
    private static final String TOKEN_KEY = "access_token";

    // Метод для получения зашифрованных SharedPreferences
    private static SharedPreferences getEncryptedPrefs(Context context) throws GeneralSecurityException, IOException {
        // Создание MasterKey, который будет использоваться для шифрования и дешифрования данных
        MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM) // Используем AES256 GCM для ключа
                .build();

        // Возвращаем зашифрованные SharedPreferences с заданной схемой шифрования ключа и значений
        return EncryptedSharedPreferences.create(
                context, // Контекст
                PREF_NAME, // Имя файла SharedPreferences
                masterKey, // MasterKey для шифрования
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, // Схема шифрования ключа
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM // Схема шифрования значений
        );
    }

    // Метод для сохранения токена в SharedPreferences
    public static void saveToken(Context context, String token) {
        try {
            // Получаем зашифрованные SharedPreferences
            SharedPreferences prefs = getEncryptedPrefs(context);
            // Сохраняем токен в SharedPreferences, используя ключ TOKEN_KEY
            prefs.edit().putString(TOKEN_KEY, token).apply();
        } catch (Exception e) {
            e.printStackTrace(); // Логируем исключение, если что-то пошло не так
        }
    }

    // Метод для получения токена из SharedPreferences
    public static String getToken(Context context) {
        try {
            // Получаем зашифрованные SharedPreferences
            SharedPreferences prefs = getEncryptedPrefs(context);
            // Возвращаем токен по ключу TOKEN_KEY или null, если токен не найден
            return prefs.getString(TOKEN_KEY, null);
        } catch (Exception e) {
            e.printStackTrace(); // Логируем исключение, если что-то пошло не так
            return null; // Возвращаем null в случае ошибки
        }
    }

    // Метод для очистки токена из SharedPreferences
    public static void clearToken(Context context) {
        try {
            // Получаем зашифрованные SharedPreferences
            SharedPreferences prefs = getEncryptedPrefs(context);
            // Удаляем токен, если он существует
            prefs.edit().remove(TOKEN_KEY).apply();
        } catch (Exception e) {
            e.printStackTrace(); // Логируем исключение, если что-то пошло не так
        }
    }
}
