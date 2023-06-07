package com.sylavana.lfmovies

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieDetailsActivity : AppCompatActivity(), MoviesAdapter.OnMovieClickListener {
    private val movieService: MovieService by lazy { RetrofitClient.create() }
    private lateinit var recommendedMoviesAdapter: MoviesAdapter

    override fun onMovieClick(movie: Movie) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        // Récupérer les données du film à partir de l'intent
        val movie = intent.getParcelableExtra<Movie>("movie") as Movie

        // Utilisez les données du film pour afficher les détails
        val imageViewPoster: ImageView = findViewById(R.id.imageViewPoster)
        val textViewTitle: TextView = findViewById(R.id.textViewTitle)
        val textViewOverview: TextView = findViewById(R.id.textViewOverview)

        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
            .placeholder(R.drawable.placeholder_poster)
            .error(R.drawable.error_poster)
            .into(imageViewPoster)

        textViewTitle.text = movie.title
        textViewOverview.text = movie.overview

        // Récupérez l'ID du film sélectionné depuis l'intent
        val selectedMovieId = movie.id

        // Créez et configurez l'adaptateur pour les films recommandés
        recommendedMoviesAdapter = MoviesAdapter(this)
        val recyclerViewRecommendedMovies: RecyclerView = findViewById(R.id.recyclerViewRecommendedMovies)
        recyclerViewRecommendedMovies.adapter = recommendedMoviesAdapter
        recyclerViewRecommendedMovies.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Appelez la méthode pour récupérer les films recommandés
        fetchRecommendedMovies(selectedMovieId)
    }

    private fun fetchRecommendedMovies(movieId: Int) {
        val call = movieService.getRecommendedMovies(movieId, "1dae3422b396a8df9bb5883b8f798ceb")

        call.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val movieResponse = response.body()
                    if (movieResponse != null) {
                        val recommendedMovies = movieResponse.results
                        showRecommendedMovies(recommendedMovies)
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                // Gestion des erreurs
            }
        })
    }

    private fun showRecommendedMovies(recommendedMovies: List<Movie>) {
        recommendedMoviesAdapter.submitList(recommendedMovies)
    }
}
