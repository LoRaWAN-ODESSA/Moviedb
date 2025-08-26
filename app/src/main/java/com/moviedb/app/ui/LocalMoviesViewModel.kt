package com.moviedb.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviedb.app.data.repo.MovieRepository
import com.moviedb.app.model.MovieLocal
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LocalUiState(
    val query: String = "",
    val totalLoaded: Int = 0,
    val results: List<MovieLocal> = emptyList(),
    val loading: Boolean = false,
)

class LocalMoviesViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = MovieRepository(app.applicationContext)

    private val _state = MutableStateFlow(LocalUiState())
    val state: StateFlow<LocalUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        // Предзагрузка CSV + показ счётчика
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            val count = repo.ensureLoaded()
            _state.update { it.copy(loading = false, totalLoaded = count) }
        }
    }

    /** Вызови один раз из UI (LaunchedEffect(Unit)) если хочешь гарантированно подтянуть CSV. */
    fun ensureLoaded() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            val count = repo.ensureLoaded()
            _state.update { it.copy(loading = false, totalLoaded = count) }
        }
    }

    /** Обновление строки поиска с дебаунсом. */
    fun onQueryChange(q: String) {
        _state.update { it.copy(query = q) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(250) // debounce
            launchSearch(q)
        }
    }

    /** Нажатие на кнопку “Искать” — запускает поиск без задержки. */
    fun submitSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            launchSearch(state.value.query)
        }
    }

    /** Внутренний запуск поиска. */
    private suspend fun launchSearch(q: String) {
        if (q.isBlank()) {
            _state.update { it.copy(results = emptyList()) }
            return
        }
        _state.update { it.copy(loading = true) }
        val found = repo.searchLocal(q)
        _state.update { it.copy(loading = false, results = found) }
    }
}
