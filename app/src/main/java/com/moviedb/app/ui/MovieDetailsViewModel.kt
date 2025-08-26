package com.moviedb.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviedb.app.data.repo.MovieRepository
import com.moviedb.app.model.MovieLocal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DetailsUiState(
    val loading: Boolean = false,
    val movie: MovieLocal? = null
)

class MovieDetailsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = MovieRepository(app)

    private val _state = MutableStateFlow(DetailsUiState())
    val state: StateFlow<DetailsUiState> = _state

    fun load(movieId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            val m = repo.findById(movieId)
            _state.value = DetailsUiState(loading = false, movie = m)
        }
    }
}
