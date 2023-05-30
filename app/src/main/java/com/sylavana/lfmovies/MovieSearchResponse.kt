package com.sylavana.lfmovies

import com.google.gson.annotations.SerializedName

data class MovieSearchResponse(
    @SerializedName("results")
    val results: List<Movie>
)
