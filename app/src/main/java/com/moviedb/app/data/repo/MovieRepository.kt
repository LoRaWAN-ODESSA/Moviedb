package com.moviedb.app.data.repo

import android.content.Context
import com.moviedb.app.data.repo.csv.CsvParser
import com.moviedb.app.model.MovieLocal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepository(private val context: Context) {

    @Volatile
    private var cache: List<MovieLocal> = emptyList()

    /** Гарантирует, что CSV загружен в память. Возвращает размер кэша. */
    suspend fun ensureLoaded(): Int = withContext(Dispatchers.IO) {
        if (cache.isEmpty()) {
            cache = CsvParser.parse(context, "movies.csv")
        }
        cache.size
    }

    /** Все фильмы из кэша (после ensureLoaded). */
    suspend fun loadAll(): List<MovieLocal> {
        ensureLoaded()
        return cache
    }

    /** Поиск по названию и жанрам (возвращаем до 100 элементов). */
    suspend fun searchLocal(query: String): List<MovieLocal> = withContext(Dispatchers.Default) {
        ensureLoaded()
        val q = query.trim()
        if (q.isEmpty()) return@withContext emptyList<MovieLocal>()

        cache.asSequence()
            .filter { it.title.contains(q, ignoreCase = true) ||
                    it.genres.any { g -> g.contains(q, ignoreCase = true) } }
            .take(100)
            .toList()
    }

    /** Поиск по id. Если у модели id не String — сравниваем как строку. */
    suspend fun findById(movieId: String): MovieLocal? = withContext(Dispatchers.Default) {
        ensureLoaded()
        cache.firstOrNull { it.id.toString() == movieId }
    }
}
