package com.sylavana.lfmovies

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("results") val results: List<Movie>
)

data class Movie(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("vote_average") val rating: Double,
    @SerializedName("budget") val budget: Int,
    @SerializedName("runtime") val runtime: Int,
    @SerializedName("original_language") val original_language: String,
    @SerializedName("homepage") val homepage: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(overview)
        parcel.writeString(posterPath)
        parcel.writeString(releaseDate)
        parcel.writeDouble(rating)
        parcel.writeInt(budget)
        parcel.writeInt(runtime)
        parcel.writeString(original_language)
        parcel.writeString(homepage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Movie> {
        override fun createFromParcel(parcel: Parcel): Movie {
            return Movie(parcel)
        }

        override fun newArray(size: Int): Array<Movie?> {
            return arrayOfNulls(size)
        }
    }
}
