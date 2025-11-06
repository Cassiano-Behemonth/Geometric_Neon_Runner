package com.example.geometric_neon_runner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.geometric_neon_runner.ui.screens.AuthScreens
import com.example.geometric_neon_runner.ui.screens.GameOverScreen
import com.example.geometric_neon_runner.ui.screens.GameScreen
import com.example.geometric_neon_runner.ui.screens.MenuScreen
import com.example.geometric_neon_runner.ui.screens.ModeSelectionScreen
import com.example.geometric_neon_runner.ui.screens.RankingScreen


sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Menu : Screen("menu")
    object ModeSelection : Screen("mode_selection")

    object Game : Screen("game/{mode}") {
        fun createRoute(mode: String) = "game/$mode"
    }
    object GameOver : Screen("game_over/{score}/{time}/{mode}") {
        fun createRoute(score: Int, time: Int, mode: String) = "game_over/$score/$time/$mode"
    }
    object Ranking : Screen("ranking/{mode}") {
        fun createRoute(mode: String) = "ranking/$mode"
    }
}


@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            AuthScreens.LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.Menu.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            AuthScreens.RegisterScreen(
                onRegisterSuccess = { navController.navigate(Screen.Menu.route) },
                onNavigateBackToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Menu.route) {
            MenuScreen(
                onPlayClicked = { navController.navigate(Screen.ModeSelection.route) },
                onRankingClicked = { navController.navigate(Screen.Ranking.createRoute("Normal")) },
                onProfileClicked = {  },
                onExitClicked = {  }
            )
        }

        composable(Screen.ModeSelection.route) {
            ModeSelectionScreen(
                onModeSelected = { mode ->
                    navController.navigate(Screen.Game.createRoute(mode))
                },
                onBackPressed = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(navArgument("mode") { type = NavType.StringType })
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "Normal"
            GameScreen(
                mode = mode,
                onGameOver = { score, time ->
                    navController.navigate(Screen.GameOver.createRoute(score, time, mode)) {
                        popUpTo(Screen.Menu.route) { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = Screen.GameOver.route,
            arguments = listOf(
                navArgument("score") { type = NavType.IntType },
                navArgument("time") { type = NavType.IntType },
                navArgument("mode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val time = backStackEntry.arguments?.getInt("time") ?: 0
            val mode = backStackEntry.arguments?.getString("mode") ?: "Normal"
            GameOverScreen(
                finalScore = score,
                timePlayed = time,
                mode = mode,
                onPlayAgain = { navController.navigate(Screen.Game.createRoute(mode)) },
                onGoToMenu = {
                    navController.popBackStack(Screen.Menu.route, inclusive = false)
                },
                onGoToRanking = {
                    navController.navigate(Screen.Ranking.createRoute(mode))
                }
            )
        }

        composable(
            route = Screen.Ranking.route,
            arguments = listOf(navArgument("mode") { type = NavType.StringType })
        ) { backStackEntry ->
            val initialMode = backStackEntry.arguments?.getString("mode") ?: "Normal"
            RankingScreen(
                initialMode = initialMode,
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}