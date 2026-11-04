package ar.edu.unicen.seminario.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import ar.edu.unicen.seminario.network.dto.TmdbResponse
import retrofit2.http.Path
import ar.edu.unicen.seminario.network.dto.MovieDetailResponse

interface TmdbApiService {

    @GET("movie/popular")
    fun getPopularMovies(
        @Header("Authorization") authorization: String
    ): Call<TmdbResponse>

    @GET("trending/movie/week")
    fun getTrendingMovies(
        @Header("Authorization") authorization: String
    ): Call<TmdbResponse>

    @GET("movie/upcoming")
    fun getUpcomingMovies(
        @Header("Authorization") authorization: String
    ): Call<TmdbResponse>

    @GET("movie/{id}")
    fun getMovieDetail(
        @Path("id") movieId: Int,
        @Header("Authorization") authorization: String
    ): Call<MovieDetailResponse>

}