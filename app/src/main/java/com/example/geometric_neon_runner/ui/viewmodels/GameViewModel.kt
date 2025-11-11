package com.example.geometric_neon_runner.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geometric_neon_runner.data.model.GameMode
import com.example.geometric_neon_runner.data.model.Score
import com.example.geometric_neon_runner.data.repository.AuthRepository
import com.example.geometric_neon_runner.data.repository.ScoreRepository
import com.example.geometric_neon_runner.game.GameState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class GameViewModel(
        private val scoreRepository: ScoreRepository,
        private val authRepository: AuthRepository
) : ViewModel() {


    private val _gameState = MutableStateFlow<GameState>(GameState.Playing)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()


    private val _currentScore = MutableStateFlow(0)
    val currentScore: StateFlow<Int> = _currentScore.asStateFlow()


    private val _shouldNavigateToGameOver = MutableStateFlow(false)
    val shouldNavigateToGameOver: StateFlow<Boolean> = _shouldNavigateToGameOver.asStateFlow()


    var finalScore: Int = 0
        private set
    var finalTime: Int = 0
        private set
    var mode: GameMode = GameMode.NORMAL
        private set


    fun setGameMode(gameMode: GameMode) {
        mode = gameMode
    }


    fun onGameOver(score: Int, time: Int) {
        finalScore = score
        finalTime = time
        _gameState.value = GameState.GameOver


        saveScore()


        _shouldNavigateToGameOver.value = true
    }


    fun updateScore(score: Int) {
        _currentScore.value = score
    }


    private fun saveScore() {
        viewModelScope.launch {
            try {
                val userId = authRepository.getCurrentUserId() ?: return@launch
                val username = authRepository.getCurrentUsername()

                val scoreData = Score(
                        userId = userId,
                        username = username,
                        score = finalScore,
                        timeSeconds = finalTime,
                        mode = mode.name,
                        timestamp = System.currentTimeMillis(),
                        synced = false
                )

                scoreRepository.saveScore(scoreData)
            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }


    fun pauseGame() {
        if (_gameState.value == GameState.Playing) {
            _gameState.value = GameState.Paused
        }
    }


    fun resumeGame() {
        if (_gameState.value == GameState.Paused) {
            _gameState.value = GameState.Playing
        }
    }


    fun onNavigatedToGameOver() {
        _shouldNavigateToGameOver.value = false
    }
}