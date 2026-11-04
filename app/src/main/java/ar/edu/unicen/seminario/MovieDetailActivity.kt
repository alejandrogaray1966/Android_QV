package ar.edu.unicen.seminario

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.TextView
import ar.edu.unicen.seminario.network.RetrofitClient
import ar.edu.unicen.seminario.network.dto.MovieDetailResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ar.edu.unicen.seminario.network.TmdbConfig

class MovieDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_movie_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /* Recupera el identificador enviado desde la pantalla principal.
         * Si el Intent no contiene el valor, se utilizará -1 como identificador inválido.
         */
        val movieId = intent.getIntExtra("movie_id", -1)

        val tvMovieId = findViewById<TextView>(R.id.tvMovieId)

        tvMovieId.text = "ID recibido: $movieId"

        /*
         * Cierra la Activity actual y regresa a la pantalla anterior.
         *
         * Android elimina esta Activity de la pila (Back Stack)
         * mediante finish().
         */
        val btnBack = findViewById<Button>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        /*
         * Consulta el detalle completo de la película.
         * Solamente se envía el ID entre Activities.
         * La información detallada se obtiene mediante una nueva llamada REST a TMDB.
         * Esta estrategia evita duplicar datos y mantiene desacopladas ambas pantallas.
         */
        RetrofitClient.api.getMovieDetail(
            movieId,
            TmdbConfig.TOKEN
        /*
         * La petición se ejecuta de forma asíncrona.
         *
         * Retrofit realiza la conexión en segundo plano para no bloquear la interfaz de usuario.
         */
        ).enqueue(object : Callback<MovieDetailResponse> {

            override fun onResponse(
                call: Call<MovieDetailResponse>,
                response: Response<MovieDetailResponse>
            ) {
                if (response.isSuccessful) {
                    val movie = response.body()
                    tvMovieId.text =
                        "ID: ${movie?.id}\n\n${movie?.title}"
                } else {
                    tvMovieId.text =
                        "Error al obtener el detalle (${response.code()})"
                }
            }

            override fun onFailure(
                call: Call<MovieDetailResponse>,
                t: Throwable
            ) {
                tvMovieId.text =
                    "Error: ${t.message}"
            }
        })
    }
}