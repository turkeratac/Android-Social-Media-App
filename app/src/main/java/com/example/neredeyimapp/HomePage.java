package com.example.neredeyimapp;

import static com.example.neredeyimapp.MainActivity.activeUserName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.neredeyimapp.Model.Post;
import com.example.neredeyimapp.ViewHolder.postViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class HomePage extends AppCompatActivity{

    FirebaseDatabase database;
    DatabaseReference post;
    FirebaseStorage storage;
    StorageReference resimYolu;
    static String activeUsername;
    FirebaseRecyclerAdapter<Post, postViewHolder> adapter;
    RecyclerView recycler_post;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.myAccountUploadImage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        try {
            activeUsername = getIntent().getStringExtra("ACTIVE_USERNAME");
            database = FirebaseDatabase.getInstance();
            post = database.getReference("posts");
            storage = FirebaseStorage.getInstance();
            resimYolu = storage.getReference();
            recycler_post = findViewById(R.id.recycler_posts);
            recycler_post.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            recycler_post.setLayoutManager(layoutManager);
        postYukle();
        }
        catch (Exception ex){
            Log.e("hata", ex.getMessage());
        }

        // Logout butonuna tıklama işlemi
        ImageButton buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Oturumu kapatır ve ana ekrana yönlendirir
                Intent intent = new Intent(HomePage.this, MainActivity.class);
                startActivity(intent);
                finish(); // Aktiviteyi kapat
            }
        });

        // Home sayfasına yönlendirme
        ImageButton buttonHomePage = findViewById(R.id.buttonHome);
        buttonHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activeUserIntent = new Intent(HomePage.this, HomePage.class);
                activeUserIntent.putExtra("ACTIVE_USERNAME", activeUserName);
                startActivity(activeUserIntent);
            }
        });

        // CreatePost sayfasına yönlendirme
        ImageButton buttonCreatePost = findViewById(R.id.buttonCreatePost);
        buttonCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activeUserIntent = new Intent(HomePage.this, CreatePost.class);
                activeUserIntent.putExtra("ACTIVE_USERNAME", activeUserName);
                startActivity(activeUserIntent);
            }
        });

        // MyAccount sayfasına yönlendirme
        ImageButton buttonMyAccount = findViewById(R.id.buttonMyAccount);
        buttonMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activeUserIntent = new Intent(HomePage.this, MyAccount.class);
                activeUserIntent.putExtra("ACTIVE_USERNAME", activeUserName);
                startActivity(activeUserIntent);
            }
        });


    }

    // Firebase deki postları gösterme
    private void postYukle() {
            FirebaseRecyclerOptions<Post> secenekler = new FirebaseRecyclerOptions.Builder<Post>()
                    .setQuery(post, Post.class).build();
            adapter = new FirebaseRecyclerAdapter<Post, postViewHolder>(secenekler) {
                @Override
                protected void onBindViewHolder(@NonNull postViewHolder postlarViewHolder, int i, @NonNull Post postlar) {
                    Picasso.with(getBaseContext()).load(postlar.getUserImage()).into(postlarViewHolder.postViewHolderUserImage);
                    postlarViewHolder.postViewHolderUserName.setText(postlar.getUsername());
                    Picasso.with(getBaseContext()).load(postlar.getPostImage()).into(postlarViewHolder.postViewHolderImage);
                    postlarViewHolder.postViewHolderDescription.setText(postlar.getDescription());
                    postlarViewHolder.postViewHolderLikeCount.setText(String.valueOf(postlar.getLikeCount()) + " Kişi Beğendi");
                    postlarViewHolder.postViewHolderTime.setText(postlar.getTimestamp());
                }
                @NonNull
                @Override
                public postViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.post, parent, false);

                    return new postViewHolder(itemView);
                }
            };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recycler_post.setAdapter(adapter);
    }

}
