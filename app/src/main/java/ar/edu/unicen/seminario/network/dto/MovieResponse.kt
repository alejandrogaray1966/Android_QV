package ar.edu.unicen.seminario.network.dto

import com.google.gson.annotations.SerializedName

data class MovieResponse(

    val title: String,

    @SerializedName("poster_path")
    val posterPath: String

)