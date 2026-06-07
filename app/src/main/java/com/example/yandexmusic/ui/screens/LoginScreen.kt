package com.example.yandexmusic.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yandexmusic.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onBiometricClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val user by viewModel.user.collectAsState()
    val error by viewModel.error.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(user) {
        if (user != null) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Яндекс Музыка",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Электронная почта") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        if (error != null) {
            val russianError = when {
                error!!.contains("password", ignoreCase = true) -> "Неверный пароль"
                error!!.contains("user not found", ignoreCase = true) -> "Пользователь не найден"
                error!!.contains("email address is badly formatted", ignoreCase = true) -> "Некорректный формат почты"
                error!!.contains("network error", ignoreCase = true) -> "Ошибка сети: проверьте интернет"
                error!!.contains("already in use", ignoreCase = true) -> "Эта почта уже используется"
                error!!.contains("weak-password", ignoreCase = true) -> "Слишком слабый пароль (мин. 6 символов)"
                error!!.contains("configuration", ignoreCase = true) -> "Ошибка настройки Firebase"
                else -> "Ошибка: ${error}"
            }
            Text(russianError, color = Color.Red, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        Button(
            onClick = { 
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.signIn(email, password) 
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
            } else {
                Text("Войти", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedButton(
            onClick = { 
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Заполните все поля для регистрации", Toast.LENGTH_SHORT).show()
                } else if (password.length < 6) {
                    Toast.makeText(context, "Пароль должен быть от 6 символов", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Регистрация...", Toast.LENGTH_SHORT).show()
                    viewModel.signUp(email, password) 
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
        ) {
            Text("Создать аккаунт", color = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        IconButton(
            onClick = onBiometricClick,
            modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.surface, RoundedCornerShape(32.dp))
        ) {
            Text("ID", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
        }
        Text("Быстрый вход", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
    }
}
