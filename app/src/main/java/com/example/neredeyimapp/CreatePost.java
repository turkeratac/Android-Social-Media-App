package com.example.neredeyimapp;

import static com.example.neredeyimapp.MainActivity.activeUserName;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.neredeyimapp.Model.Post;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class CreatePost extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 71;
    FirebaseStorage storage;
    StorageReference filePath;
    Uri saveUri;
    static String activeUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.myAccountUploadImage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase Storage bağlantısı
        storage = FirebaseStorage.getInstance();
        filePath = storage.getReference();

        activeUsername = getIntent().getStringExtra("ACTIVE_USERNAME");
        // Kullanıcı adını ilgili alanda gösterir
        TextView textViewActiveUser = findViewById(R.id.postUserNameTextView);
        textViewActiveUser.setText(activeUsername);

        // Logout butonuna tıklama işlemi
        ImageButton buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Oturumu kapatır ve ana ekrana yönlendirir
                Intent intent = new Intent(CreatePost.this, MainActivity.class);
                startActivity(intent);
                finish(); // Aktiviteyi kapat
            }
        });

        // Home sayfasına yönlendirme
        ImageButton buttonHomePage = findViewById(R.id.buttonHome);
        buttonHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activeUserIntent = new Intent(CreatePost.this, HomePage.class);
                activeUserIntent.putExtra("ACTIVE_USERNAME", activeUserName);
                startActivity(activeUserIntent);
            }
        });

        // CreatePost sayfasına yönlendirme
        ImageButton buttonCreatePost = findViewById(R.id.buttonCreatePost);
        buttonCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activeUserIntent = new Intent(CreatePost.this, CreatePost.class);
                activeUserIntent.putExtra("ACTIVE_USERNAME", activeUserName);
                startActivity(activeUserIntent);
            }
        });

        // MyAccount sayfasına yönlendirme
        ImageButton buttonMyAccount = findViewById(R.id.buttonMyAccount);
        buttonMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activeUserIntent = new Intent(CreatePost.this, MyAccount.class);
                activeUserIntent.putExtra("ACTIVE_USERNAME", activeUserName);
                startActivity(activeUserIntent);
            }
        });

        // Resim seçme
        Button showImageButton = findViewById(R.id.createPostSelectImageButton);
        showImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                      choosePhoto();
            }
        });
        // Resim yükleme
        Button uploadImageButton = findViewById(R.id.createPostSharePostButton);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                      uploadPhoto();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            saveUri = data.getData();

            try {
                // Seçilen fotoğrafı ImageView'e yükleme
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), saveUri);
                ImageView imageView = findViewById(R.id.postSelectedImageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void choosePhoto() {
        // Galeri için intent oluşturur
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Resim Seç"), PICK_IMAGE_REQUEST);
    }
    private void uploadPhoto() {
        long currentTimeMillis = System.currentTimeMillis();

        // SimpleDateFormat ile zaman formatı belirlenir
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        // ProgressDialog oluştur ve göster (işlemin devam ettiğini belirtir)
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Resim yükleniyor...");
        progressDialog.show();

        // Resim için benzersiz bir isim oluşturur
        String imageName = UUID.randomUUID().toString();

        // Resmin yükleneceği yol
        StorageReference imageRef = filePath.child("images/" + imageName);

        String activeUsername = getIntent().getStringExtra("ACTIVE_USERNAME");
        EditText postDescriptionEditText = findViewById(R.id.postInputTextEditText);
        String postDescription = postDescriptionEditText.getText().toString();
        String timestamp = sdf.format(new Date(currentTimeMillis));
        String userIcon = "https://cdn-icons-png.flaticon.com/512/1144/1144760.png";
        int likeCount = 1;

        // Firebase Storage'a resmi yükleme
        imageRef.putFile(saveUri)
                .addOnSuccessListener(taskSnapshot -> {
                    progressDialog.dismiss();
                    Toast.makeText(CreatePost.this, "Resim başarıyla yüklendi.", Toast.LENGTH_SHORT).show();

                    // Yüklenen resmin URL'sini alır ve Post nesnesini oluşturur (resmin URL'si uri.toString() tir)
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Post post = new Post(userIcon, activeUsername, uri.toString(), postDescription, likeCount, timestamp);

                        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
                        String postId = postsRef.push().getKey();

                        postsRef.child(postId).setValue(post)
                                .addOnSuccessListener(aVoid -> {
                                    // Başarılı bir şekilde kaydedildi
                                    Toast.makeText(CreatePost.this, "Post başarıyla oluşturuldu.", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Hata oluştu
                                    Toast.makeText(CreatePost.this, "Post oluşturulurken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e("hatafirebase", e.getMessage());
                    Toast.makeText(CreatePost.this, "Resim yüklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnProgressListener(snapshot -> {
                    // Resmin yüklenmesi sırasında ilerlemeyi gösterir
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressDialog.setMessage("Yükleniyor " + (int)progress + "%");
                });
    }
}
