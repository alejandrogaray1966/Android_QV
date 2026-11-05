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
import android.widget.EditText
import android.text.Editable
import android.text.TextWatcher
import ar.edu.unicen.seminario.network.TmdbConfig

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
    private var allMovies = mutableListOf<Movie>()

    // Variable que referencia al CODE de Error en el GET.
    private lateinit var tvErrorDetail: TextView

    // Variables que referencia al Error de not found movies.
    private lateinit var layoutNoResults: LinearLayout
    private lateinit var tvNoResults: TextView

    // Variable que referencia a la PAGE  and CATEGORY and LOADING oF PAGE.
    private var currentPage = 1
    private var currentCategory = "popular"
    private var isLoadingPage = false

    // Inicializa la actividad.
    override fun onCreate(savedInstanceState: Bundle?) {

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

        // Variable que referencia al EditText.
        val etBuscar = findViewById<EditText>(R.id.etBuscar)
        etBuscar.clearFocus()

        // Variable que referencia al CODE de Error en el GET.
        tvErrorDetail = findViewById(R.id.tvErrorDetail)

        // Variables que referencia al Error de not found movies.
        layoutNoResults = findViewById(R.id.layoutNoResults)
        tvNoResults = findViewById(R.id.tvNoResults)

        // =====================================================
        // DATOS MOCK
        // =====================================================
        /*
        val movies = mutableListOf(
            Movie(1, "Deadpool", ""),
            Movie(2, "Dune", ""),
            Movie(3, "Gladiator II", ""),
            Movie(4, "The Batman", "")
        )
        */

        // =====================================================
        // RECYCLERVIEW
        // =====================================================

        layoutLoading = findViewById(R.id.layoutLoading)
        layoutError = findViewById(R.id.layoutError)
        recyclerView = findViewById(R.id.rvMovies)
        val movies = mutableListOf<Movie>()
        movieAdapter = MovieAdapter(movies)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = movieAdapter

        recyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    super.onScrolled(
                        recyclerView,
                        dx,
                        dy
                    )
                    if (etBuscar.text.toString().isNotBlank()) {
                        return
                    }
                    val layoutManager =
                        recyclerView.layoutManager as LinearLayoutManager
                    val lastVisibleItem =
                        layoutManager.findLastVisibleItemPosition()
                    val totalItems =
                        layoutManager.itemCount
                    if (dy > 0 && lastVisibleItem >= totalItems - 5) {
                        if (!isLoadingPage) {
                            isLoadingPage = true
                            currentPage++
                            when (currentCategory) {
                                "popular" -> loadPopularMovies()
                                "trending" -> loadTrendingMovies()
                                "upcoming" -> loadUpcomingMovies()
                            }
                        }
                    }
                }
            }
        )

        // =====================================================
        // BÚSQUEDA Y FILTRADO DE PELÍCULAS
        // =====================================================

        etBuscar.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }
                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    val texto = s.toString()
                    val filteredMovies =
                        allMovies.filter {
                            it.title.contains(
                                texto,
                                ignoreCase = true
                            )
                        }
                    movieAdapter.updateMovies(filteredMovies)
                    if (
                        filteredMovies.isEmpty() && texto.isNotBlank()
                    ) {
                        recyclerView.visibility = View.GONE
                        tvNoResults.text = getString(
                                R.string.no_movies_found,
                                texto
                            )
                        layoutNoResults.visibility = View.VISIBLE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        layoutNoResults.visibility = View.GONE
                    }
                }
                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )

        // =====================================================
        // EVENTOS DE LOS CHIPS
        // =====================================================

        chipPopulares.setOnClickListener {
            currentCategory = "popular"
            resetMovies()
            etBuscar.setText("")
            etBuscar.clearFocus()
            tvTitulo.text = getString(R.string.popular_movies)
            loadPopularMovies()
        }

        chipTendencias.setOnClickListener {
            currentCategory = "trending"
            resetMovies()
            etBuscar.setText("")
            etBuscar.clearFocus()
            tvTitulo.text = getString(R.string.trending_movies)
            loadTrendingMovies()
        }

        chipEstrenos.setOnClickListener {
            currentCategory = "upcoming"
            resetMovies()
            etBuscar.setText("")
            etBuscar.clearFocus()
            tvTitulo.text = getString(R.string.upcoming_movies)
            loadUpcomingMovies()
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
        // showError("Error Test")
        loadPopularMovies()

    }


    /**
     * Reset the movies when press on chip.
     */
    private fun resetMovies() {
        currentPage = 1
        allMovies.clear()
        movieAdapter.updateMovies(emptyList())
        recyclerView.scrollToPosition(0)
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
    private fun showError(message: String) {
        layoutLoading.visibility = View.GONE
        recyclerView.visibility = View.GONE
        layoutError.visibility = View.VISIBLE
        tvErrorDetail.text = message
    }

    /**
     * Function que llama al GET populares.
     */
    private fun loadPopularMovies() {
        loadMovies(
            RetrofitClient.api.getPopularMovies(TmdbConfig.TOKEN, currentPage)
        )
    }

    /**
     * Function que llama al GET Tendencias.
     */
    private fun loadTrendingMovies() {
        loadMovies(
            RetrofitClient.api.getTrendingMovies(TmdbConfig.TOKEN, currentPage)
        )
    }

    /**
     * Function que llama al GET Estrenos.
     */
    private fun loadUpcomingMovies() {
        loadMovies(
            RetrofitClient.api.getUpcomingMovies(TmdbConfig.TOKEN, currentPage)
        )
    }

    /**
     * Function of test.
     */
    private fun loadMovies(
        call: Call<TmdbResponse>
    ) {
        showLoading()
        call.enqueue(object : Callback<TmdbResponse> {

            override fun onResponse(
                call: Call<TmdbResponse>,
                response: Response<TmdbResponse>
            ) {
                isLoadingPage = false
                if (response.isSuccessful) {
                    val tmdbMovies =
                        response.body()?.results ?: emptyList()
                    val movies = tmdbMovies.map {
                        Movie(
                            it.id,
                            it.title,
                            it.posterPath
                        )
                    }
                    allMovies.addAll(movies)
                    movieAdapter.addMovies(movies)
                    showContent()
                } else {
                    println("ERROR CODE: ${response.code()}")
                    println("ERROR BODY: ${response.errorBody()?.string()}")
                    when (response.code()) {
                        401 -> showError("Error 401 - No autorizado")
                        404 -> showError("Error 404 - Recurso no encontrado")
                        500 -> showError("Error 500 - Error interno del servidor")
                        else -> showError("Error HTTP ${response.code()}")
                    }
                }
            }

            override fun onFailure(
                call: Call<TmdbResponse>,
                t: Throwable
            ) {
                isLoadingPage = false
                println("FAILURE: ${t.message}")
                t.printStackTrace()
                showError("Sin conexión a Internet")
            }
        })
    }

}