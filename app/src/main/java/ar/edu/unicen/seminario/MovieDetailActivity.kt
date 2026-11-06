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
import android.widget.ImageView
import com.bumptech.glide.Glide


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

        val ivPoster = findViewById<ImageView>(R.id.ivPoster)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvOverview = findViewById<TextView>(R.id.tvOverview)
        val tvRating = findViewById<TextView>(R.id.tvRating)
        val tvGenres = findViewById<TextView>(R.id.tvGenres)

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
            TmdbConfig.TOKEN,
            TmdbConfig.getLanguage()
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
                    movie?.let {
                        Glide.with(this@MovieDetailActivity)
                            .load(
                                TmdbConfig.IMAGE_BASE_URL +
                                        it.posterPath
                            )
                            .into(ivPoster)
                        tvTitle.text = it.title
                        tvOverview.text = it.overview
                        tvRating.text = "⭐ %.1f".format(it.voteAverage)
                        tvGenres.text = it.genres.joinToString(", ") { genre ->
                            genre.name
                        }
                    }
                } else {
                    tvTitle.text = "Error"
                    tvOverview.text = "No fue posible obtener los datos (${response.code()})"
                }
            }

            override fun onFailure(
                call: Call<MovieDetailResponse>,
                t: Throwable
            ) {
                tvTitle.text = "Error"
                tvOverview.text = t.message ?: "Error desconocido"
            }
        })
    }
}