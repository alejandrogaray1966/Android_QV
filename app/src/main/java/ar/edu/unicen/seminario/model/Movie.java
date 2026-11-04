package ar.edu.unicen.seminario.model;

/**
 * Modelo de una película.
 * Esta clase representa la información que se mostrará
 * en cada Card del RecyclerView.
 */

public class Movie {

    private final int id;
    private final String title;
    private final String imageUrl;

    public Movie(int id, String title, String imageUrl) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
