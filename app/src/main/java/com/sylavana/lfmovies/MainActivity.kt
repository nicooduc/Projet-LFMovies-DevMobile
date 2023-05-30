package com.sylavana.lfmovies

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var movieApiService: MovieApiService
    private val apiKey = "1dae3422b396a8df9bb5883b8f798ceb"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialiser Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/") // enlever /3/ ???
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Créer une instance de MovieApiService
        movieApiService = retrofit.create(MovieApiService::class.java)

        // Appeler la méthode de recherche de films avec une chaîne de recherche
        searchMovies("Avengers")
    }

    private fun searchMovies(query: String) {
        val call = movieApiService.searchMovies(query, apiKey)
        call.enqueue(object : Callback<MovieSearchResponse> {
            override fun onResponse(
                call: Call<MovieSearchResponse>,
                response: Response<MovieSearchResponse>
            ) {
                if (response.isSuccessful) {
                    val movieSearchResponse = response.body()
                    val movies = movieSearchResponse?.results
                    // Traitez les résultats de recherche (affichage de la liste, etc.)
                } else {
                    // Gérez les erreurs de l'appel API
                }
            }

            override fun onFailure(call: Call<MovieSearchResponse>, t: Throwable) {
                // Gérez les erreurs de l'appel réseau
            }
        })
    }

    // Autres méthodes et fonctions de l'activité
}