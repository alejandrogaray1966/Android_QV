package ar.edu.unicen.seminario.network

import retrofit2.Call
import retrofit2.http.GET

interface TmdbApiService {

    @GET("movie/popular")
    fun getPopularMovies(): Call<String>

}