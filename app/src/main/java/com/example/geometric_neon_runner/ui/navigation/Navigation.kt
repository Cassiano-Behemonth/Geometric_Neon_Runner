package com.example.geometric_neon_runner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.geometric_neon_runner.data.repository.AuthRepository
import com.example.geometric_neon_runner.data.repository.ScoreRepository
import com.example.geometric_neon_runner.ui.screens.*
import com.example.geometric_neon_runner.ui.viewmodels.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Menu : Screen("menu")

    object Game : Screen("game/{mode}") {
        fun createRoute(mode: String) = "game/$mode"
    }

    object GameOver : Screen("gameover/{score}/{time}/{mode}") {
        fun createRoute(score: Int, time: Int, mode: String) = "gameover/$score/$time/$mode"
    }

    object Ranking : Screen("ranking/{mode}") {
        fun createRoute(mode: String = "NORMAL") = "ranking/$mode"
    }
}

@Composable
fun AppNavigation(
        navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current

    // Create repositories (only once)
    val authRepository = AuthRepository(context)
    val scoreRepository = ScoreRepository(context)

    val startDestination = if (authRepository.isUserLoggedIn()) {
        Screen.Menu.route
    } else {
        Screen.Login.route
    }

    NavHost(
            navController = navController,
            startDestination = startDestination
    ) {
        // Login Screen
        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = viewModel(
                    factory = ViewModelFactory { LoginViewModel(authRepository) }
            )

            LoginScreen(
                    navController = navController,
                    viewModel = viewModel
            )
        }

        // Register Screen
        composable(Screen.Register.route) {
            val viewModel: RegisterViewModel = viewModel(
                    factory = ViewModelFactory { RegisterViewModel(authRepository) }
            )

            RegisterScreen(
                    navController = navController,
                    viewModel = viewModel
            )
        }

        // Menu Screen
        composable(Screen.Menu.route) {
            val viewModel: MenuViewModel = viewModel(
                    factory = ViewModelFactory { MenuViewModel(authRepository, scoreRepository) }
            )

            MenuScreen(
                    viewModel = viewModel,
                    onPlayClicked = { navController.navigate(Screen.Game.createRoute("NORMAL")) },
                    onRankingClicked = { navController.navigate(Screen.Ranking.createRoute("NORMAL")) },
                    onProfileClicked = { /* TODO: Profile screen */ },
                    onExitClicked = {
                        viewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Menu.route) { inclusive = true }
                        }
                    }
            )
        }

        // Game Screen
        composable(
                route = Screen.Game.route,
                arguments = listOf(
                        navArgument("mode") { type = NavType.StringType }
                )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "NORMAL"
            val viewModel: GameViewModel = viewModel(
                    factory = ViewModelFactory { GameViewModel(scoreRepository, authRepository) }
            )

            GameScreen(
                    navController = navController,
                    viewModel = viewModel,
                    mode = mode
            )
        }

        // GameOver Screen
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
            val mode = backStackEntry.arguments?.getString("mode") ?: "NORMAL"

            val viewModel: GameOverViewModel = viewModel(
                    factory = ViewModelFactory { GameOverViewModel(scoreRepository) }
            )

            GameOverScreen(
                    navController = navController,
                    score = score,
                    timeSeconds = time,
                    mode = mode
            )
        }

        // Ranking Screen
        composable(
                route = Screen.Ranking.route,
                arguments = listOf(
                        navArgument("mode") {
                            type = NavType.StringType
                            defaultValue = "NORMAL"
                        }
                )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "NORMAL"
            val viewModel: RankingViewModel = viewModel(
                    factory = ViewModelFactory { RankingViewModel(scoreRepository, authRepository) }
            )

            RankingScreen(
                    navController = navController,
                    vm = viewModel,
                    initialMode = mode
            )
        }
    }
}

class ViewModelFactory<T>(private val creator: () -> T) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return creator() as T
    }
}