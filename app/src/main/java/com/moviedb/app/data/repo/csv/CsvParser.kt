package com.moviedb.app.data.repo.csv

import android.content.Context
import com.moviedb.app.model.MovieLocal
import java.io.BufferedReader
import java.io.InputStreamReader

object CsvParser {
    fun parse(context: Context, fileName: String): List<MovieLocal> {
        val movies = mutableListOf<MovieLocal>()

        val inputStream = context.assets.open(fileName)
        val reader = BufferedReader(InputStreamReader(inputStream))

        val header = reader.readLine() // пропускаем заголовок
        val cols = header.split(",")

        // Индексы колонок (по твоему CSV: Title, Year, IMDb Rating, Genres)
        val idxTitle = cols.indexOf("Title")
        val idxYear = cols.indexOf("Year")
        val idxRating = cols.indexOf("IMDb Rating")
        val idxGenres = cols.indexOf("Genres")

        reader.lineSequence().forEach { line ->
            val parts = line.split(",")

            if (parts.size > idxTitle) {
                val title = parts[idxTitle].trim('"')
                val year = parts.getOrNull(idxYear)?.toIntOrNull()
                val rating = parts.getOrNull(idxRating)?.toDoubleOrNull()
                val genres = parts.getOrNull(idxGenres)?.split(";", ",")?.map { it.trim() } ?: emptyList()

                movies.add(
                    MovieLocal(
                        id = title.hashCode().toString(),
                        title = title,
                        year = year,
                        rating = rating,
                        genres = genres
                    )
                )
            }
        }

        reader.close()
        return movies
    }
}

