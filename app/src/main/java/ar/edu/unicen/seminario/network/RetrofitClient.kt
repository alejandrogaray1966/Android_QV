package ar.edu.unicen.seminario.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    companion object {

        private const val BASE_URL =
            "https://api.themoviedb.org/3/"

        val api: TmdbApiService by lazy {

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .build()
                .create(TmdbApiService::class.java)
        }
    }
}