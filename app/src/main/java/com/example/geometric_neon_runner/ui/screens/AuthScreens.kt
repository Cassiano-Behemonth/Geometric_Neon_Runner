package com.example.geometric_neon_runner.ui.screens

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.geometric_neon_runner.ui.components.NeonButton
import com.example.geometric_neon_runner.ui.components.NeonTextField
import com.example.geometric_neon_runner.ui.components.LoadingIndicator
import com.example.geometric_neon_runner.ui.navigation.Screen
import com.example.geometric_neon_runner.ui.theme.NeonTunnelTheme
import com.example.geometric_neon_runner.ui.viewmodels.LoginViewModel
import com.example.geometric_neon_runner.ui.viewmodels.RegisterViewModel
import com.example.geometric_neon_runner.utils.Result

@Composable
fun LoginScreen(
        navController: NavController,
        viewModel: LoginViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginState by viewModel.loginState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val isEmailValid = remember(email) {
        email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    val isPasswordValid = remember(password) {
        password.isEmpty() || password.length >= 6
    }

    // Handle login success
    LaunchedEffect(loginState) {
        if (loginState is Result.Success) {
            navController.navigate(Screen.Menu.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

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
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    NeonTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email",
                            isError = !isEmailValid,
                            modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    NeonTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            isError = !isPasswordValid,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isLoading) {
                        LoadingIndicator(
                                modifier = Modifier.size(48.dp)
                        )
                    } else {
                        NeonButton(
                                text = "LOGIN",
                                onClick = {
                                    if (email.isNotEmpty() && password.isNotEmpty()) {
                                        viewModel.login(email, password)
                                    }
                                },
                                enabled = email.isNotEmpty() && password.isNotEmpty() && isEmailValid && isPasswordValid,
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                            onClick = { navController.navigate(Screen.Register.route) }
                    ) {
                        Text(
                                text = "Don't have an account? Register",
                                color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Show error if any
                    if (loginState is Result.Error) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                                text = (loginState as Result.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(
        navController: NavController,
        viewModel: RegisterViewModel
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val registerState by viewModel.registerState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val isUsernameValid = remember(username) {
        username.isEmpty() || username.length >= 3
    }
    val isEmailValid = remember(email) {
        email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    val isPasswordValid = remember(password) {
        password.isEmpty() || password.length >= 6
    }

    // Handle register success
    LaunchedEffect(registerState) {
        if (registerState is Result.Success) {
            navController.navigate(Screen.Menu.route) {
                popUpTo(Screen.Register.route) { inclusive = true }
            }
        }
    }

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
                            text = "CREATE ACCOUNT",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    NeonTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = "Username",
                            isError = !isUsernameValid,
                            modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    NeonTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email",
                            isError = !isEmailValid,
                            modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    NeonTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            isError = !isPasswordValid,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isLoading) {
                        LoadingIndicator(
                                modifier = Modifier.size(48.dp)
                        )
                    } else {
                        NeonButton(
                                text = "REGISTER",
                                onClick = {
                                    if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                                        viewModel.register(email, password, username)
                                    }
                                },
                                enabled = username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
                                        && isUsernameValid && isEmailValid && isPasswordValid,
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                            onClick = { navController.navigate(Screen.Login.route) }
                    ) {
                        Text(
                                text = "Already have an account? Login",
                                color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Show error if any
                    if (registerState is Result.Error) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                                text = (registerState as Result.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}