package ar.edu.unicen.seminario.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import ar.edu.unicen.seminario.R;
import ar.edu.unicen.seminario.model.Movie;
import com.bumptech.glide.Glide;

/**
 * Adapter encargado de conectar los datos con el RecyclerView.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movies;

    public MovieAdapter(List<Movie> movies) {
        this.movies = movies;
    }

    public void updateMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull MovieViewHolder holder,
            int position) {

        Movie movie = movies.get(position);

        holder.tvMovieTitle.setText(movie.getTitle());

        String imageUrl =
                "https://image.tmdb.org/t/p/w500"
                        + movie.getImageUrl();

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .into(holder.imgMovie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    /**
     * ViewHolder.
     * Mantiene referencias a las vistas de cada card.
     */
    static class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView imgMovie;
        TextView tvMovieTitle;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            imgMovie = itemView.findViewById(R.id.imgMovie);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
        }
    }
}
