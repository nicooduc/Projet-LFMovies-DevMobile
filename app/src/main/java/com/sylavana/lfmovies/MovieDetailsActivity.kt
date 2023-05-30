package com.sylavana.lfmovies

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class MovieDetailsActivity : AppCompatActivity() {
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
    }
}

