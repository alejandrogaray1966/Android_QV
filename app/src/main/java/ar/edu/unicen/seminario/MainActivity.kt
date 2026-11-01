package ar.edu.unicen.seminario

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import com.google.android.material.chip.Chip
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unicen.seminario.adapter.MovieAdapter
import ar.edu.unicen.seminario.model.Movie
import android.view.View
import android.widget.LinearLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ar.edu.unicen.seminario.network.RetrofitClient
import ar.edu.unicen.seminario.network.dto.TmdbResponse

/**
 * MainActivity es la pantalla principal de la aplicación.
 *
 * Aquí se inicializa la interfaz y se configuran los márgenes
 * para que la vista se adapte a las barras del sistema.
 *
 * @constructor Crea una actividad principal.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Método que se ejecuta al crear la actividad.
     */

    // =====================================================
    // COMPONENTES DE LA UI
    // =====================================================

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutLoading: LinearLayout
    private lateinit var layoutError: LinearLayout
    private lateinit var movieAdapter: MovieAdapter
    private val token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJlOWUyYzIwNDRhNWU1MzBkN2M4N2ExY2ZlYzQ5ZDlhMCIsIm5iZiI6MTc5MzA3NDMxMS40NjEsInN1YiI6IjZhZTAyNDg3NWZmNTYwOGMyMmVlYWM4MiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.b4X9fTOJ5EIH01AoY2XxaPAGvSE4NkXIVOwXTYUutCs"

    override fun onCreate(savedInstanceState: Bundle?) {

        // Inicializa la actividad.
        // Este método se ejecuta al abrir la pantalla.
        super.onCreate(savedInstanceState)

        // Configura la pantalla para utilizar todo el espacio disponible,
        // Include las áreas cercanas a las barras del sistema.
        enableEdgeToEdge()

        // Asocia esta actividad con el archivo activity_main.xml.
        setContentView(R.layout.activity_main)

        // =====================================================
        // REFERENCIAS A LOS COMPONENTES DE LA INTERFAZ
        // =====================================================

        // Título que indica la categoría de películas seleccionada.
        val tvTitulo = findViewById<TextView>(R.id.tvTituloPeliculas)

        // Chips utilizados para seleccionar el tipo de películas.
        val chipPopulares = findViewById<Chip>(R.id.chipPopulares)
        val chipTendencias = findViewById<Chip>(R.id.chipTendencias)
        val chipEstrenos = findViewById<Chip>(R.id.chipEstrenos)

        // =====================================================
        // DATOS MOCK
        // =====================================================
        val movies = listOf(
            Movie("Deadpool", ""),
            Movie("Dune", ""),
            Movie("Gladiator II", ""),
            Movie("The Batman", "")
        )

        // =====================================================
        // RECYCLERVIEW
        // =====================================================

        layoutLoading = findViewById(R.id.layoutLoading)
        layoutError = findViewById(R.id.layoutError)
        recyclerView = findViewById(R.id.rvMovies)
        movieAdapter = MovieAdapter(movies)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = movieAdapter

        // =====================================================
        // EVENTOS DE LOS CHIPS
        // =====================================================

        // Al pulsar "Populares" se actualiza el título.
        chipPopulares.setOnClickListener {
            tvTitulo.text = getString(R.string.popular_movies)
        }

        // Al pulsar "Tendencias" se actualiza el título.
        chipTendencias.setOnClickListener {
            tvTitulo.text = getString(R.string.trending_movies)
        }

        // Al pulsar "Estrenos" se actualiza el título.
        chipEstrenos.setOnClickListener {
            tvTitulo.text = getString(R.string.upcoming_movies)
        }

        // =====================================================
        // AJUSTE DE MÁRGENES DEL SISTEMA
        // =====================================================

        // Adapta la interfaz a las barras superior e inferior
        // del dispositivo para evitar superposiciones.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            // Devuelve los insets aplicados.
            insets
        }

        /**
         *  Test View
         */
        // showLoading()
        // showContent()
        // showError()
        testTmdbConnection()

    }

    /**
     * Muestra el estado de carga.
     */
    private fun showLoading() {
        layoutLoading.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        layoutError.visibility = View.GONE
    }

    /**
     * Muestra el contenido.
     */
    private fun showContent() {
        layoutLoading.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        layoutError.visibility = View.GONE
    }

    /**
     * Muestra el error.
     */
    private fun showError() {
        layoutLoading.visibility = View.GONE
        recyclerView.visibility = View.GONE
        layoutError.visibility = View.VISIBLE
    }

    /**
     * Function of test.
     */
    private fun testTmdbConnection() {
        showLoading()
        RetrofitClient.api
            .getPopularMovies(token)
            .enqueue(object : Callback<TmdbResponse> {
                override fun onResponse(
                    call: Call<TmdbResponse>,
                    response: Response<TmdbResponse>
                ) {
                    if (response.isSuccessful) {
                        val tmdbMovies =
                            response.body()?.results ?: emptyList()
                        val movies = tmdbMovies.map {
                            Movie(
                                it.title,
                                it.posterPath
                            )
                        }
                        movieAdapter.updateMovies(movies)
                        showContent()
                    } else {
                        println("ERROR CODE: ${response.code()}")
                        println("ERROR BODY: ${response.errorBody()?.string()}")
                        showError()
                    }
                }
                override fun onFailure(
                    call: Call<TmdbResponse>,
                    t: Throwable
                ) {
                    println("FAILURE: ${t.message}")
                    t.printStackTrace()
                    showError()
                }
            })
    }

}