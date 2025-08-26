@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.moviedb.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.moviedb.app.model.MovieLocal

@Composable
fun LocalMoviesScreen(
    navController: NavController,
    vm: LocalMoviesViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state by vm.state.collectAsState()

    // На всякий случай – гарантируем одноразовую загрузку
    LaunchedEffect(Unit) { vm.ensureLoaded() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        TextField(
            value = state.query,
            onValueChange = vm::onQueryChange,
            label = { Text("Поиск по названию (локально)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Button(onClick = { vm.submitSearch() }) {
            Text("Искать")
        }

        Spacer(Modifier.height(12.dp))

        Text("Загружено из CSV: ${state.totalLoaded}")
        Text("Найдено: ${state.results.size}")

        if (state.loading) {
            LinearProgressIndicator(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(state.results, key = { it.id }) { movie ->
                MovieCard(movie) { navController.navigate("details/${movie.id}") }
            }
        }
    }
}

@Composable
private fun MovieCard(movie: MovieLocal, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                movie.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            val year = movie.year ?: "—"
            val rating = movie.rating?.toString() ?: "—"
            val genres = if (movie.genres.isEmpty()) "—" else movie.genres.joinToString()
            Text(
                "Год: $year • IMDb: $rating • $genres",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
