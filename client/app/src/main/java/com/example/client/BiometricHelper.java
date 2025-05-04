package com.example.client;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Executor;

// Класс для работы с биометрической аутентификацией
public class BiometricHelper {

    // Интерфейс для обработки результатов биометрической аутентификации
    public interface BiometricCallback {
        void onResult(boolean success); // Функция обратного вызова для обработки результата
    }

    // Метод для запуска биометрической аутентификации
    public static void authenticate(FragmentActivity activity, BiometricCallback callback) {
        // Executor, который гарантирует, что выполнение будет происходить на главном потоке UI
        Executor executor = ContextCompat.getMainExecutor(activity);

        // Создаём колбэк, который будет обрабатывать успешную или неудачную аутентификацию
        BiometricPrompt.AuthenticationCallback authCallback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                // Если аутентификация успешна — вызываем метод обратного вызова с успехом
                callback.onResult(true);
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                // Если произошла ошибка аутентификации — вызываем метод обратного вызова с ошибкой
                callback.onResult(false);
            }

            @Override
            public void onAuthenticationFailed() {
                // Если аутентификация не прошла — вызываем метод обратного вызова с ошибкой
                callback.onResult(false);
            }
        };

        // Создаём объект BiometricPrompt для отображения диалога с биометрией
        BiometricPrompt prompt = new BiometricPrompt(activity, executor, authCallback);

        // Строим информацию для диалога с биометрией
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Аутентификация") // Заголовок диалога
                .setSubtitle("Подтвердите личность с помощью биометрии") // Подзаголовок
                .setNegativeButtonText("Отмена") // Кнопка для отмены аутентификации
                .build();

        // Запускаем аутентификацию
        prompt.authenticate(promptInfo);
    }

    // Метод для проверки доступности биометрии на устройстве
    public static boolean isBiometricAvailable(Context context) {
        // Получаем объект BiometricManager для проверки состояния биометрии
        BiometricManager manager = BiometricManager.from(context);

        // Проверяем, доступна ли биометрия (например, отпечатки пальцев или распознавание лица)
        return manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                == BiometricManager.BIOMETRIC_SUCCESS; // Возвращаем true, если биометрия доступна
    }
}