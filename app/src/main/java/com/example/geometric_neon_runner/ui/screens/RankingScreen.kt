package com.example.geometric_neon_runner.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.geometric_neon_runner.ui.theme.DarkBackground
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


data class RankingItemModel(val username: String, val score: Int, val timeSeconds: Int)

class RankingViewModel {
    private val _normal = MutableStateFlow(List(50) { i -> RankingItemModel("User${i+1}", (5000 - i*50), 60 + i) })
    val normal: StateFlow<List<RankingItemModel>> = _normal

    private val _hard = MutableStateFlow(List(30) { i -> RankingItemModel("HardUser${i+1}", (3000 - i*40), 70 + i) })
    val hard: StateFlow<List<RankingItemModel>> = _hard

    private val _extreme = MutableStateFlow(List(20) { i -> RankingItemModel("Extreme${i+1}", (1500 - i*30), 90 + i) })
    val extreme: StateFlow<List<RankingItemModel>> = _extreme

    val currentUsername: StateFlow<String> = MutableStateFlow("PlayerOne")
}

@Composable
fun RankingScreen(
        navController: NavController,
        initialMode: String = "Normal",
        vm: RankingViewModel = viewModel() as RankingViewModel
) {
    var selectedTab by remember { mutableStateOf(initialMode) }
    val normal by vm.normal.collectAsState(initial = emptyList())
    val hard by vm.hard.collectAsState(initial = emptyList())
    val extreme by vm.extreme.collectAsState(initial = emptyList())
    val currentUser by vm.currentUsername.collectAsState(initial = "")

    val items = when (selectedTab) {
        "Normal" -> normal
        "Hard" -> hard
        "Extreme" -> extreme
        else -> normal
    }

    Column(
            modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground)
                    .padding(12.dp)
    ) {
        TabRow(selectedTabIndex = when (selectedTab) {
            "Normal" -> 0
            "Hard" -> 1
            "Extreme" -> 2
            else -> 0
        }) {
            Tab(selected = selectedTab == "Normal", onClick = { selectedTab = "Normal" }, text = { Text("Normal") })
            Tab(selected = selectedTab == "Hard", onClick = { selectedTab = "Hard" }, text = { Text("Hard") })
            Tab(selected = selectedTab == "Extreme", onClick = { selectedTab = "Extreme" }, text = { Text("Extreme") })
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(items) { index, item ->
                RankingItem(
                        position = index + 1,
                        model = item,
                        isCurrentUser = item.username == currentUser,
                        modifier = Modifier
                                .fillMaxWidth()
                                .clickable {

                                }
                )
            }
        }
    }
}

@Composable
fun RankingItem(position: Int, model: RankingItemModel, isCurrentUser: Boolean, modifier: Modifier = Modifier) {
    Card(modifier = modifier.padding(6.dp)) {
        Row(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "#$position", style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(64.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = model.username, style = MaterialTheme.typography.titleSmall)
                Text(text = "Time: ${model.timeSeconds}s", style = MaterialTheme.typography.bodySmall)
            }
            Text(text = "${model.score}", style = MaterialTheme.typography.titleMedium, color = if (isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground)
        }
    }
}
