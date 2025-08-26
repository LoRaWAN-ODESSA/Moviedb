@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.moviedb.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons

@Composable
fun MovieDetailsScreen(
    movieId: String,
    onBack: () -> Unit,
    vm: MovieDetailsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state by vm.state.collectAsState()

    // загрузим фильм по id при входе на экран
    LaunchedEffect(movieId) {
        vm.load(movieId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(state.movie?.title ?: "Детали фильма") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (state.loading) {
                LinearProgressIndicator(Modifier.fillMaxSize().height(4.dp))
                Spacer(Modifier.height(16.dp))
            }

            val m = state.movie
            if (m != null) {
                Text(text = m.title, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                Text(text = "ID: ${m.id}")
                Text(text = "Год: ${m.year ?: "—"}")
                Text(text = "IMDb: ${m.rating ?: "—"}")
                val genres = if (m.genres.isEmpty()) "—" else m.genres.joinToString()
                Text(text = "Жанры: $genres")
            } else if (!state.loading) {
                Text(
                    text = "Фильм не найден",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
