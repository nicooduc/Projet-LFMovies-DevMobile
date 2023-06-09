package com.sylavana.lfmovies

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult


class MainActivity : AppCompatActivity(), MoviesAdapter.OnMovieClickListener  {
    private lateinit var editTextQuery: EditText
    private lateinit var buttonSearch: ImageButton
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
        val buttonScanBarcode: ImageButton = findViewById(R.id.btnScanBarcode)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = moviesAdapter

        buttonSearch.setOnClickListener {
            searchMovies()
        }
            moviesAdapter.setOnMovieClickListener(this)

        buttonScanBarcode.setOnClickListener {
            startBarcodeScanner()
        }

        fetchMovieTendance()

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
                    } else {
                        Toast.makeText(applicationContext, "Failed to retrieve movie data", Toast.LENGTH_SHORT).show()
                    }
                    hideProgressBar()
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    hideProgressBar()
                    Toast.makeText(applicationContext, "Network request failed", Toast.LENGTH_SHORT).show()
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

        private fun startBarcodeScanner() {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("Scan a QR code")
            integrator.setCameraId(0) // Utiliser la caméra arrière par défaut
            integrator.setBeepEnabled(false) // Désactiver le bip lors de la détection d'un code barre
            integrator.setBarcodeImageEnabled(false) // Ne pas enregistrer d'image du code barre
            integrator.setOrientationLocked(false);
            integrator.initiateScan()
        }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && !result.contents.isNullOrEmpty()) {
            val barcode = result.contents // Récupérer la valeur du code barre
            val movieId = barcode.toIntOrNull()
            if (movieId != null) {
                fetchMovieById(movieId)
            } else {
                showError("Invalid movie ID from QR code \"${barcode}\"")
            }
        }
    }

    private fun fetchMovieById(movieId: Int) {
        showProgressBar()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val movieService = retrofit.create(MovieService::class.java)
        val call = movieService.getMovieById(movieId, "1dae3422b396a8df9bb5883b8f798ceb")

        call.enqueue(object : Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                hideProgressBar()
                if (response.isSuccessful) {
                    val movieResponse = response.body()
                    if (movieResponse != null) {
                        val movies = listOf(movieResponse)
                        showMovies(movies)
                    } else {
                        Toast.makeText(applicationContext, "No movie found for the ID", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Failed to retrieve movie data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, "Network request failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchMovieTendance() {
        showProgressBar()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val movieService = retrofit.create(MovieService::class.java)
        val call = movieService.getPopularMovies("1dae3422b396a8df9bb5883b8f798ceb")

        call.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val movieResponse = response.body()
                    if (movieResponse != null) {
                        val movies = movieResponse.results
                        showMovies(movies)
                    }
                } else {
                    Toast.makeText(applicationContext, "Failed to retrieve movie data", Toast.LENGTH_SHORT).show()
                }
                hideProgressBar()
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, "Network request failed", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
