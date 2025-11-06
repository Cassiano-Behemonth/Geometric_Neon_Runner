package com.example.geometric_neon_runner.ui.screens


import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.geometric_neon_runner.ui.components.NeonButton
import com.example.geometric_neon_runner.ui.components.NeonTextField
import com.example.geometric_neon_runner.ui.theme.NeonTunnelTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class AuthUiState(
        val loading: Boolean = false,
        val error: String? = null,
        val username: String? = null
)

class AuthViewModel {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {

    }

    fun register(username: String, email: String, password: String) {

    }
}

@Composable
fun LoginScreen(
        navController: NavController,
        authViewModel: AuthViewModel = viewModel() as AuthViewModel // substitua pelo seu ViewModel
) {
    val uiState by authViewModel.uiState.collectAsState(initial = AuthUiState())
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }
    val isEmailValid = remember(email) { Patterns.EMAIL_ADDRESS.matcher(email).matches() }
    val isPasswordValid = remember(password) { password.length >= 6 }

    NeonTunnelTheme {
        Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
        ) {
            Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
            ) {
                Column(
                        modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                            text = "NEON TUNNEL",
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                    )

                    NeonTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email",
                            isError = email.isNotBlank() && !isEmailValid,
                            keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                            ),
                            onFocusChanged = { emailFocused = it.isFocused },
                            modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    NeonTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            isPassword = true,
                            isError = password.isNotBlank() && !isPasswordValid,
                            keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                            ),
                            onFocusChanged = { passwordFocused = it.isFocused },
                            modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (uiState.loading) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    } else {
                        NeonButton(
                                text = "LOGIN",
                                onClick = {
                                    if (isEmailValid && isPasswordValid) {
                                        authViewModel.login(email, password)
                                    }
                                },
                                enabled = isEmailValid && isPasswordValid,
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = { navController.navigate("register") }) {
                        Text(text = "Don't have an account? Register", color = MaterialTheme.colorScheme.onBackground)
                    }

                    uiState.error?.let { err ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = err, color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(
        navController: NavController,
        authViewModel: AuthViewModel = viewModel() as AuthViewModel
) {
    val uiState by authViewModel.uiState.collectAsState(initial = AuthUiState())
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isUsernameValid = remember(username) { username.length >= 3 }
    val isEmailValid = remember(email) { Patterns.EMAIL_ADDRESS.matcher(email).matches() }
    val isPasswordValid = remember(password) { password.length >= 6 }

    NeonTunnelTheme {
        Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
        ) {
            Column(
                    modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                        text = "CREATE ACCOUNT",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                )

                NeonTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = "Username",
                        isError = username.isNotBlank() && !isUsernameValid,
                        modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                NeonTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        isError = email.isNotBlank() && !isEmailValid,
                        keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                NeonTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        isPassword = true,
                        isError = password.isNotBlank() && !isPasswordValid,
                        modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))

                NeonButton(
                        text = "REGISTER",
                        onClick = {
                            if (isUsernameValid && isEmailValid && isPasswordValid) {
                                authViewModel.register(username, email, password)
                                navController.navigate("menu") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        },
                        enabled = isUsernameValid && isEmailValid && isPasswordValid,
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = { navController.navigate("login") }) {
                    Text(text = "Back to Login", color = MaterialTheme.colorScheme.onBackground)
                }

                uiState.error?.let { err ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = err, color = Color.Red)
                }
            }
        }
    }
}
