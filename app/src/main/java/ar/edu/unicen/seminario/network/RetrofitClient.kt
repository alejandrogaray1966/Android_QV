package ar.edu.unicen.seminario.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    companion object {

        val api: TmdbApiService by lazy {

            Retrofit.Builder()
                .baseUrl(TmdbConfig.BASE_URL)
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .build()
                .create(TmdbApiService::class.java)
        }
    }
}