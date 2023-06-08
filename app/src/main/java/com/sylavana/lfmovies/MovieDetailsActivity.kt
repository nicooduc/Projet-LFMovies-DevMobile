package com.sylavana.lfmovies

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

class MovieDetailsActivity : AppCompatActivity(), MoviesAdapter.OnMovieClickListener {
    val movieService: MovieService = RetrofitClient.create()
    private lateinit var recommendedMoviesAdapter: MoviesAdapter

    override fun onMovieClick(movie: Movie) {
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra("movie", movie as Parcelable)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        // Récupérer les données du film à partir de l'intent
        val movie = intent.getParcelableExtra<Movie>("movie") as Movie

        // Récupérez l'ID du film sélectionné depuis l'intent
        val selectedMovieId = movie.id

        detailsMovies(selectedMovieId)

        // Utilisez les données du film pour afficher les détails
        val decimalFormat = DecimalFormat("0.00")
        val imageViewPoster: ImageView = findViewById(R.id.imageViewPoster)
        val textViewTitle: TextView = findViewById(R.id.textViewTitle)
        val textViewOverview: TextView = findViewById(R.id.textViewOverview)
        val textViewBudget: TextView = findViewById(R.id.textViewBudget)
        val textViewRuntime: TextView = findViewById(R.id.textViewRuntime)
//        val textViewGenres: TextView = findViewById(R.id.textViewGenres)
        val textViewLanguage: TextView = findViewById(R.id.textViewLanguage)
        val textViewReleaseDate: TextView = findViewById(R.id.textViewReleaseDate)
//        val textViewProductionCountries: TextView = findViewById(R.id.textViewProductionCountries)
        val textViewHomepage: TextView = findViewById(R.id.textViewHomepage)
        val textViewRating: TextView = findViewById(R.id.textViewRating)

        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
            .placeholder(R.drawable.placeholder_poster)
            .error(R.drawable.error_poster)
            .into(imageViewPoster)

        textViewTitle.text = movie.title
        textViewOverview.text = movie.overview
        textViewBudget.text = if (movie.budget==0) "N/A" else movie.budget.toString()
        textViewReleaseDate.text = if (movie.releaseDate.isNotBlank()) movie.releaseDate.substring(0, 4) else "N/A"
        textViewRating.text = if (movie.rating==0.0) "N/A" else decimalFormat.format(movie.rating).toString()


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

    private fun detailsMovies(movieId: Int) {
        val call = movieService.getMovieById(movieId, "1dae3422b396a8df9bb5883b8f798ceb")

        call.enqueue(object : Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                if (response.isSuccessful) {
                    val movie = response.body()
                    if (movie != null) {
                        val textViewBudget: TextView = findViewById(R.id.textViewBudget)
                        val textViewRuntime: TextView = findViewById(R.id.textViewRuntime)
//                        val textViewGenres: TextView = findViewById(R.id.textViewGenres)
                        val textViewLanguage: TextView = findViewById(R.id.textViewLanguage)
//                        val textViewProductionCountries: TextView = findViewById(R.id.textViewProductionCountries)
                        val textViewHomepage: TextView = findViewById(R.id.textViewHomepage)

                        textViewBudget.text = if (movie.budget==0) "N/A" else movie.budget.toString()
                        textViewRuntime.text = if (movie.runtime==0) "N/A" else "${movie.runtime} min"
//                        textViewGenres.text = getGenresString(movie.genres)
                        textViewLanguage.text = movie.original_language
//                        textViewProductionCountries.text = getProductionCountriesString(movie.productionCountries)
                        if (!movie.homepage.isNullOrEmpty()) {
                            textViewHomepage.text = movie.homepage
                            textViewHomepage.autoLinkMask = android.text.util.Linkify.WEB_URLS
                        } else {
                            textViewHomepage.text = "N/A"
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                // Gestion des erreurs
            }
        })
    }


    private fun showRecommendedMovies(recommendedMovies: List<Movie>) {
        recommendedMoviesAdapter.submitList(recommendedMovies)
        recommendedMoviesAdapter.setOnMovieClickListener(this)
    }
}
