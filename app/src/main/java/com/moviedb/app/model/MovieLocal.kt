package com.moviedb.app.model

data class MovieLocal(
    val id: String,            // например, из CSV поля Const (tt...); если нет — сгенерируй из title+year
    val title: String,
    val year: Int?,
    val rating: Double?,
    val genres: List<String>
)

