package ar.edu.unicen.seminario.network

import java.util.Locale

/**
 * Configuración centralizada de TMDB.
 *
 * Contiene valores reutilizados por toda la aplicación para acceder a la API.
 */

object TmdbConfig {

    /**
     * URL base de la API REST.
     */
    const val BASE_URL =
        "https://api.themoviedb.org/3/"

    /**
     * Token Bearer utilizado para autenticar
     * las solicitudes.
     */
    const val TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJlOWUyYzIwNDRhNWU1MzBkN2M4N2ExY2ZlYzQ5ZDlhMCIsIm5iZiI6MTc5MzA3NDMxMS40NjEsInN1YiI6IjZhZTAyNDg3NWZmNTYwOGMyMmVlYWM4MiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.b4X9fTOJ5EIH01AoY2XxaPAGvSE4NkXIVOwXTYUutCs"

    /**
     * URL base para las imágenes.
     */
    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

    /**
     * Devuelve el language que se enviará a TMDB
     * según la configuración del dispositivo.
     */
    fun getLanguage(): String {
        return when (Locale.getDefault().language) {
            "es" -> "es-ES"
            "en" -> "en-US"
            "pt" -> "pt-BR"
            "fr" -> "fr-FR"
            else -> "en-US"
        }
    }

}