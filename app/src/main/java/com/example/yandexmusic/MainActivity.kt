package com.example.yandexmusic

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yandexmusic.ui.Screen
import com.example.yandexmusic.ui.screens.LoginScreen
import com.example.yandexmusic.ui.screens.MainScreen
import com.example.yandexmusic.ui.theme.YandexMusicTheme
import com.example.yandexmusic.viewmodel.AuthViewModel
import com.example.yandexmusic.viewmodel.MusicViewModel

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YandexMusicTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val musicViewModel: MusicViewModel = viewModel()

                val currentUser = authViewModel.user.value
                val startDestination = if (currentUser != null) Screen.Main.route else Screen.Login.route

                NavHost(navController = navController, startDestination = startDestination) {
                    composable(Screen.Login.route) {
                        LoginScreen(
                            viewModel = authViewModel,
                            onLoginSuccess = {
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            onBiometricClick = { showBiometricPrompt() }
                        )
                    }
                    composable(Screen.Main.route) {
                        val user by authViewModel.user.collectAsState()
                        MainScreen(
                            musicViewModel = musicViewModel,
                            userId = user?.uid ?: "",
                            onLogout = {
                                authViewModel.signOut()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext, "Authentication succeeded!", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Вход в Яндекс Музыку")
            .setSubtitle("Используйте биометрические данные для входа")
            .setNegativeButtonText("Использовать пароль")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
