package com.moviedb.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moviedb.app.ui.LocalMoviesScreen
import com.moviedb.app.ui.MovieDetailsScreen
import com.moviedb.app.ui.theme.MoviedbTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MoviedbTheme {
                val nav = rememberNavController()

                NavHost(
                    navController = nav,
                    startDestination = "local"
                ) {
                    composable("local") {
                        // LocalMoviesScreen ожидает navController
                        LocalMoviesScreen(navController = nav)
                    }
                    composable("details/{movieId}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("movieId") ?: ""
                        // MovieDetailsScreen НЕ принимает navController,
                        // зато требует onBack
                        MovieDetailsScreen(
                            movieId = id,
                            onBack = { nav.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

