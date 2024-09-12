package com.example.neredeyimapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neredeyimapp.HomePage;
import com.example.neredeyimapp.Model.Post;
import com.example.neredeyimapp.Model.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.lang.ref.Reference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 71;
    FirebaseStorage storage;
    StorageReference filePath;
    Uri saveUri;
    String imageNameRef = UUID.randomUUID().toString();
    FirebaseDatabase database;
    DatabaseReference user;
    boolean tcHasEntered = false;
    boolean iForgotMyPass = false;
    static String activeUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase'i başlat
        FirebaseApp.initializeApp(this);
        // Firebase Storage bağlantısı
        storage = FirebaseStorage.getInstance();
        filePath = storage.getReference();
        // Veritabanı referansını al
        database = FirebaseDatabase.getInstance();
        user = database.getReference("Users");

        if (database == null || user == null) {
            Toast.makeText(this, "Firebase bağlantısı oluşturulamadı", Toast.LENGTH_SHORT).show();
        }

        Button buttonForgotPassword = findViewById(R.id.buttonForgotPassword);
        Button buttonRegister = findViewById(R.id.buttonRegister);
        EditText editTextFullName = findViewById(R.id.editTextFullName);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        EditText editTextTCKimlik = findViewById(R.id.editTextTCKimlik);
        Button buttonLogin = findViewById(R.id.buttonLogin);

        // Resim yükle butonuna tıklama işlemi
        Button buttonUploadPhoto = findViewById(R.id.buttonUploadPhoto);
        buttonUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveUri != null) {
                    uploadPhoto();
                } else {
                    Toast.makeText(MainActivity.this, "Lütfen bir resim seçin.", Toast.LENGTH_SHORT).show();
                    choosePhoto();
                }
            }
        });

        // Kayıt Ol butonuna tıklama işlemi
        buttonRegister.setOnClickListener(v -> {
            editTextTCKimlik.setVisibility(View.VISIBLE); // TC Kimlik Numarası alanını göster
            buttonUploadPhoto.setVisibility(View.VISIBLE); // Resim yükleme butonunu göster


            // TC Kimlik numarası için filtreleme ayarı
            editTextTCKimlik.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11), new InputFilter.AllCaps(), new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    if (source.toString().matches("[0-9]+")) {
                        tcHasEntered = true;
                        return source;
                    } else {
                        Toast.makeText(MainActivity.this, "TC Kimlik numarası 11 haneli bir sayı olmalıdır.", Toast.LENGTH_SHORT).show();
                        return "";
                    }
                }
            }});

            if (tcHasEntered)
            {
                uploadPhoto();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Giriş Yap butonuna tıklama işlemi
        buttonLogin.setOnClickListener(v -> {
            String userName = editTextFullName.getText().toString().trim();
            String userPassword = editTextPassword.getText().toString().trim();
            String userID = editTextTCKimlik.getText().toString().trim();

            // Kullanıcı adı ve şifre boş değilse işlem yap
            if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userPassword)) {
                // Firebase veritabanından kullanıcıyı kontrol et
                user.orderByChild("userName").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Kullanıcı veritabanında bulunursa
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User user = snapshot.getValue(User.class);
                                // Kullanıcının şifresi doğruysa giriş yap
                                if (user != null && user.getUserPassword().equals(userPassword))
                                {
                                    activeUserName = user.getUserName();
                                    Intent activeUserIntent = new Intent(MainActivity.this, CreatePost.class);
                                    activeUserIntent.putExtra("ACTIVE_USERNAME", activeUserName);
                                    startActivity(activeUserIntent);
                                    return;
                                }
                                else if(user != null && user.getUserID().equals(userID))
                                {
                                    Intent intent = new Intent(MainActivity.this, HomePage.class);
                                    startActivity(intent);
                                    return;
                                }
                            }
                            // Şifre yanlış ise hata mesajı göster
                            Toast.makeText(MainActivity.this, "Şifre yanlış, lütfen tekrar deneyin", Toast.LENGTH_SHORT).show();
                        } else {
                            // Kullanıcı adı veritabanında bulunamadı ise hata mesajı göster
                            Toast.makeText(MainActivity.this, "Kullanıcı adı bulunamadı, lütfen kaydolun", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Veritabanı erişiminde hata olursa hata mesajı göster
                        Toast.makeText(MainActivity.this, "Veritabanı erişimi iptal edildi", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Kullanıcı adı veya şifre boş ise hata mesajı göster
                Toast.makeText(MainActivity.this, "Kullanıcı adı ve şifre gereklidir", Toast.LENGTH_SHORT).show();
            }
        });

        // Şifremi Unuttum butonuna tıklama işlemi
        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iForgotMyPass = true;
                editTextTCKimlik.setVisibility(View.VISIBLE); // TC Kimlik Numarası alanını göster
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            saveUri = data.getData();

            try {
                // Seçilen fotoğrafı ImageView'e yükle
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), saveUri);
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void choosePhoto() {
        // Galeri intent'i oluştur
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

        EditText editTextFullName = findViewById(R.id.editTextFullName);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        EditText editTextTCKimlik = findViewById(R.id.editTextTCKimlik);

        String userID = editTextTCKimlik.getText().toString();
        String userName = editTextFullName.getText().toString();
        String userPassword = editTextPassword.getText().toString();

        // Resim için benzersiz bir isim oluşturur
        String imageName = UUID.randomUUID().toString();

        // Resmin yükleneceği yol
        StorageReference imageRef = filePath.child("images/" + imageName);

        // Firebase Storage'a resmi yükleme
        imageRef.putFile(saveUri)
                .addOnSuccessListener(taskSnapshot -> {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Resim başarıyla yüklendi.", Toast.LENGTH_SHORT).show();

                    // Yüklenen resmin URL'sini alır ve Post nesnesini oluşturur (resmin URL'si uri.toString() tir)
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // User nesnesini oluşturur
                        User newUser = new User(userID, userName, userPassword, uri.toString());
                        // Veritabanına yeni kullanıcıyı ekler
                        user.push().setValue(newUser);
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e("hatafirebase", e.getMessage());
                    Toast.makeText(MainActivity.this, "Resim yüklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnProgressListener(snapshot -> {
                    // Resmin yüklenmesi sırasında ilerlemeyi gösterir
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressDialog.setMessage("Yükleniyor " + (int)progress + "%");
                });
    }

}
