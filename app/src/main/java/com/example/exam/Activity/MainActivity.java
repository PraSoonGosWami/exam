package com.example.exam.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.asksira.bsimagepicker.Utils;
import com.example.exam.R;
import com.example.exam.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.security.SecureRandom;

public class MainActivity extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener {

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseUser user;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        button = findViewById(R.id.button);

        button.setOnClickListener(v->{
            imagePicker();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //performs logout
    public void logout() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setMessage("Are you sure you want logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                })
                .setNegativeButton("No", (dialog, id) -> {
                    dialog.dismiss();
                });
        builder.create().show();


    }

    //opens image picker dialog
    public void imagePicker() {
        BSImagePicker picker = new BSImagePicker.Builder("com.invaderx.firebasetrigger.fileprovider")
                .setMaximumDisplayingImages(24)
                .setSpanCount(3)
                .setGridSpacing(Utils.dp2px(2))
                .setPeekHeight(Utils.dp2px(360))
                .setTag("A request ID")
                .build();
        picker.show(getSupportFragmentManager(), "picker");

    }

    //returns file path
    @Override
    public void onSingleImageSelected(Uri uri, String tag) {

        uploadImage(uri);



    }

    //uploads image and calls add product
    public void uploadImage(Uri link) {
        ProgressDialog progressDialog = new ProgressDialog(this,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setTitle("Getting things ready");
        progressDialog.show();
        //if (uPid.isEmpty())
           // uPid = generatePid();
        storageReference.child(user.getUid()).child("p")
                .putFile(link)
                .addOnSuccessListener(taskSnapshot -> {
                    progressDialog.setMessage("Getting things done...");
                    storageReference.child(user.getUid()).child("p").getDownloadUrl().addOnSuccessListener(uri1 -> {
                        Uri downloadUri = uri1;
                        if (downloadUri != null) {
                            //enter here
                            Toast.makeText(this, "" + downloadUri, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }


                        //else
                            //showSnackbar("Technical Issue\tTry again");

                    });

                })
                .addOnFailureListener(exception -> {
                    progressDialog.dismiss();
                    //showSnackbar(exception.getMessage());

                })
                .addOnProgressListener(taskSnapshot -> {
                    //calculating progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    //displaying percentage in progress dialog
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                });
    }

    //genares random secured pid
    public String generatePid() {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

}
