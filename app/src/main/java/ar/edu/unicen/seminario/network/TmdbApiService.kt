package ar.edu.unicen.seminario.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import ar.edu.unicen.seminario.network.dto.TmdbResponse

interface TmdbApiService {

    @GET("movie/popular")
    fun getPopularMovies(
        @Header("Authorization") authorization: String
    ): Call<TmdbResponse>

}