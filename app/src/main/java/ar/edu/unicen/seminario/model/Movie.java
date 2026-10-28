package ar.edu.unicen.seminario.model;

/**
 * Modelo de una película.
 * Esta clase representa la información que se mostrará
 * en cada Card del RecyclerView.
 */

public class Movie {

    private final String title;
    private final String imageUrl;

    public Movie(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return this.title;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }
}
