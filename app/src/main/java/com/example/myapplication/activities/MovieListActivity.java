package com.example.myapplication;

import android.content.Intent;
import com.example.myapplication.activities.BookingActivity;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<Movie> movieList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        recyclerView = findViewById(R.id.rv_movies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MovieAdapter(movieList, movie -> {
            Intent i = new Intent(this, BookingActivity.class);
            i.putExtra("movieId", movie.getId());
            i.putExtra("movieTitle", movie.getTitle());
            startActivity(i);
        });
        recyclerView.setAdapter(adapter);

        loadMovies();
    }

    private void loadMovies() {
        FirebaseFirestore.getInstance()
                .collection("movie")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("FIRESTORE", "Lỗi: " + error.getMessage());
                        return;
                    }
                    if (snapshots == null) {
                        Log.e("FIRESTORE", "Snapshots null");
                        return;
                    }
                    Log.d("FIRESTORE", "Số phim: " + snapshots.size());
                    movieList.clear();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        Movie movie = doc.toObject(Movie.class);
                        if (movie != null) {
                            movie.setId(doc.getId());
                            movieList.add(movie);
                            Log.d("FIRESTORE", "Phim: " + movie.getTitle());
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}