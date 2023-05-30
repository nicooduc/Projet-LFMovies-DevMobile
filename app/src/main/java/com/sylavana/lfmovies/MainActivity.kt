package com.sylavana.lfmovies

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), MoviesAdapter.OnMovieClickListener  {
    private lateinit var editTextQuery: EditText
    private lateinit var buttonSearch: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var moviesAdapter: MoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextQuery = findViewById(R.id.editTextQuery)
        buttonSearch = findViewById(R.id.btnSearch)
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerViewMovies)
        moviesAdapter = MoviesAdapter(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = moviesAdapter

        buttonSearch.setOnClickListener {
            searchMovies()
        }
        moviesAdapter.setOnMovieClickListener(this)
    }

    private fun searchMovies() {
        val query = editTextQuery.text.toString().trim()
        if (query.isNotEmpty()) {
            showProgressBar()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val movieService = retrofit.create(MovieService::class.java)
            val call = movieService.searchMovies(query, "1dae3422b396a8df9bb5883b8f798ceb")

            call.enqueue(object : Callback<MovieResponse> {
                override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                    if (response.isSuccessful) {
                        val movieResponse = response.body()
                        if (movieResponse != null) {
                            val movies = movieResponse.results
                            showMovies(movies)
                        }
                    }
                    hideProgressBar()
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    hideProgressBar()
                    showError("Error occurred: ${t.message}")
                }
            })
        } else {
            showError("Please enter a search query")
        }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun showMovies(movies: List<Movie>) {
        moviesAdapter.submitList(movies)
    }

    private fun showError(message: String) {
        val errorTextView: TextView = findViewById(R.id.errorTextView)
        errorTextView.text = message
        errorTextView.visibility = View.VISIBLE
    }

    override fun onMovieClick(movie: Movie) {
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra("movie", movie as Parcelable)
        startActivity(intent)
    }
}
