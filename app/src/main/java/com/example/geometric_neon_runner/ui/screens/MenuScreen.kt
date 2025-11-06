package com.example.geometric_neon_runner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Scoreboard
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.geometric_neon_runner.ui.color.DarkBackground
import com.example.geometric_neon_runner.ui.components.NeonButton
import com.example.geometric_neon_runner.ui.theme.NeonTunnelTheme


@Composable
fun MenuScreen(
    onPlayClicked: () -> Unit,
    onRankingClicked: () -> Unit,
    onProfileClicked: () -> Unit,
    onExitClicked: () -> Unit
) {
    // Simulação de dados do jogador (seria obtido do ViewModel)
    val currentUsername = remember { "Runner_ZX81" }
    val bestScore = remember { 128456 }

    NeonTunnelTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Text(
                text = "NEON TUNNEL",
                style = MaterialTheme.typography.displayLarge.copy(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "MAIN MENU",
                style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.padding(bottom = 48.dp)
            )

            PlayerStatus(username = currentUsername, bestScore = bestScore)

            Spacer(modifier = Modifier.height(48.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                MenuButton(
                    text = "P L A Y",
                    onClick = onPlayClicked,
                    icon = Icons.Default.Star,
                    neonColor = MaterialTheme.colorScheme.primary
                )


                MenuButton(
                    text = "RANKING",
                    onClick = onRankingClicked,
                    icon = Icons.Default.Scoreboard,
                    neonColor = MaterialTheme.colorScheme.secondary
                )

                MenuButton(
                    text = "PROFILE",
                    onClick = onProfileClicked,
                    icon = Icons.Default.Person,
                    neonColor = MaterialTheme.colorScheme.tertiary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // SAIR
                MenuButton(
                    text = "LOGOUT",
                    onClick = onExitClicked,
                    icon = Icons.Default.ExitToApp,
                    neonColor = Color.Gray.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun PlayerStatus(username: String, bestScore: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "RUNNER: $username",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "BEST SCORE: $bestScore",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}


@Composable
private fun MenuButton(
    text: String,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    neonColor: Color
) {
    NeonButton(
        onClick = onClick,
        text = "", // Usamos o Box para colocar o ícone e o texto
        neonColor = neonColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = neonColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = neonColor
            )
        }
    }
}