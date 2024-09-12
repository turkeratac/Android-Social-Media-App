package com.example.neredeyimapp;

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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.neredeyimapp.Model.Post;
import com.example.neredeyimapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class MyAccount extends AppCompatActivity {
    StorageReference filePath;
    private static final int PICK_IMAGE_REQUEST = 71;
    String activeUsername;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri saveUri;
    DatabaseReference user;
    static String yeniResimUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_account);

        ViewPager2 viewPager = (ViewPager2) findViewById(R.id.viewPagerMyAccount);
        SimpleFragmentPagerAdapter adapter = new
                SimpleFragmentPagerAdapter(getSupportFragmentManager(),getLifecycle());
        viewPager.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.myAccountUploadImage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = FirebaseDatabase.getInstance();
        user = database.getReference("Users");

        storage = FirebaseStorage.getInstance();
        filePath = storage.getReference();

        activeUsername = getIntent().getStringExtra("ACTIVE_USERNAME");

        TextView textViewUsername = findViewById(R.id.myAccountUsernameTextView);
        textViewUsername.setText(activeUsername);
        ImageView myAccountImageView = findViewById(R.id.myAccountImageView);
        EditText myAccountIdEditText = findViewById(R.id.myAccountIdEditText);
        EditText myAccountPasswordEditText = findViewById(R.id.myAccountPasswordEditText);

        // Logout butonuna tıklama işlemi
        ImageButton buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Oturumu kapatır ve ana ekrana yönlendirir
                Intent intent = new Intent(MyAccount.this, MainActivity.class);
                startActivity(intent);
                finish(); // Aktiviteyi kapat
            }
        });

        // Home sayfasına yönlendirme
        ImageButton buttonHomePage = findViewById(R.id.buttonHome);
        buttonHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccount.this, HomePage.class);
                startActivity(intent);
            }
        });

        // CreatePost sayfasına yönlendirme
        ImageButton buttonCreatePost = findViewById(R.id.buttonCreatePost);
        buttonCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccount.this, CreatePost.class);
                startActivity(intent);
            }
        });


        // MyAccount sayfasına yönlendirme
        ImageButton buttonMyAccount = findViewById(R.id.buttonMyAccount);
        buttonMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccount.this, MyAccount.class);
                startActivity(intent);
            }
        });

        // Ek Özellikler sayfasına yönlendirme
        Button buttonFeatures = findViewById(R.id.myAccountFeaturesButton);
        buttonFeatures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccount.this, AddFeatures.class);
                startActivity(intent);
            }
        });


        // Resim seçme işlemi
        Button showImageButton = findViewById(R.id.myAccountUploadImageButton);
        showImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoto();
            }
        });

        // Bilgilerimi güncelle butonuna basınca
        Button updateMyInfoButton = findViewById(R.id.myAccountUpdateButton);
        updateMyInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredTcNo = myAccountIdEditText.getText().toString();
                String yeniSifre = myAccountPasswordEditText.getText().toString();
                String enteredUsername = textViewUsername.getText().toString();

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
                userRef.orderByChild("userID").equalTo(enteredTcNo).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                User user = userSnapshot.getValue(User.class);

                                if (user != null && user.getUserID().equals(enteredTcNo) && user.getUserName().equals(enteredUsername))
                                {
                                    // Girilen ve oluşan değerleri logcat de kontrol et
                                    Log.d("DEBUG", "Entered TC No: " + enteredTcNo);
                                    Log.d("DEBUG", "entered user name: " + enteredUsername);
                                    Log.d("DEBUG", "yeni sifre: " + yeniSifre);

                                    // Fotoğrafı yükle
                                    uploadPhoto();

                                    // Kullanıcı bilgilerini güncelle
                                    user.setUserPassword(yeniSifre);
                                    user.setImageRefNo(yeniResimUrl);

                                    // Güncellemeyi gerçekleştiriyor
                                    userSnapshot.getRef().setValue(user);

                                    Toast.makeText(getApplicationContext(), "Bilgiler başarıyla güncellendi", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    // Kullanıcı adı ve TC numarası eşleşmedi, uyarı mesajı göster
                                    Toast.makeText(getApplicationContext(), "Girilen TC numarası farklı bir kullanıcıya ait", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        else {
                            // Veritabanında girilen TC numarasına sahip bir kullanıcı bulunamadı, uyarı mesajı göster
                            Toast.makeText(getApplicationContext(), "Girilen TC numarasına sahip bir kullanıcı bulunamadı", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Veritabanı erişiminde hata oluştuğunda yapılacaklar
                        Log.e("TAG", "Veritabanı erişiminde hata oluştu: " + databaseError.getMessage());
                    }
                });
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            saveUri = data.getData();
            try {
                // Seçilen fotoğrafı ImageView'e yükle
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), saveUri);
                ImageView imageView = findViewById(R.id.myAccountImageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void choosePhoto() {
        // Galeri için intent oluştur
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Resim Seç"), PICK_IMAGE_REQUEST);
    }
    private void uploadPhoto() {
        // ProgressDialog oluştur ve göster (işlemin devam ettiğini belirtir)
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Resim yükleniyor...");
        progressDialog.show();

        String imageName = UUID.randomUUID().toString();

        // Resmin yükleneceği yol
        StorageReference imageRef = filePath.child("images/" + imageName);

        // Firebase Storage'a resmi yükleme
        imageRef.putFile(saveUri)
                .addOnSuccessListener(taskSnapshot -> {
                    progressDialog.dismiss();
                    Toast.makeText(MyAccount.this, "Resim başarıyla yüklendi.", Toast.LENGTH_SHORT).show();

                    // Yüklenen resmin URL'sini alır ve Post nesnesini oluşturur (resmin URL'si uri.toString() tir)
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        yeniResimUrl = uri.toString();
                        Log.d("DEBUG", "yeni resim url: " + yeniResimUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e("hatafirebase", e.getMessage());
                    Toast.makeText(MyAccount.this, "Resim yüklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnProgressListener(snapshot -> {
                    // Resmin yüklenmesi sırasında ilerlemeyi gösterir
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressDialog.setMessage("Yükleniyor " + (int)progress + "%");
                });
    }

}
