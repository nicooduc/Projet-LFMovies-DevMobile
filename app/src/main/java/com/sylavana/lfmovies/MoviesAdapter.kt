package com.sylavana.lfmovies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sylavana.lfmovies.R
import java.text.DecimalFormat


class MoviesAdapter(private val listener: OnMovieClickListener) : ListAdapter<Movie, MoviesAdapter.MovieViewHolder>(MovieDiffCallback()) {
    private var onMovieClickListener: OnMovieClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)

        holder.itemView.setOnClickListener {
            onMovieClickListener?.onMovieClick(movie)
        }
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewPoster: ImageView = itemView.findViewById(R.id.imageViewPoster)
        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        private val textViewReleaseDate: TextView = itemView.findViewById(R.id.textViewReleaseDate)
        private val textViewRating: TextView = itemView.findViewById(R.id.textViewRating)

        fun bind(movie: Movie) {
            val decimalFormat = DecimalFormat("0.00")
            textViewTitle.text = movie.title
            textViewReleaseDate.text = if (movie.releaseDate.isNotBlank()) movie.releaseDate.substring(0, 4) else "N/A"
            textViewRating.text = if (movie.rating==0.0) "N/A" else decimalFormat.format(movie.rating).toString()


            Glide.with(itemView)
                .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
                .placeholder(R.drawable.placeholder_poster)
                .error(R.drawable.error_poster)
                .into(imageViewPoster)
        }

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val movie = getItem(position)
                    listener.onMovieClick(movie)
                }
            }
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    interface OnMovieClickListener {
        fun onMovieClick(movie: Movie)
    }


    fun setOnMovieClickListener(listener: OnMovieClickListener) {
        this.onMovieClickListener = listener
    }

}
