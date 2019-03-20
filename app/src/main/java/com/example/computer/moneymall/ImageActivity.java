package com.example.computer.moneymall;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ImageActivity extends AppCompatActivity {
   ImageView imageView;
   ImageButton imageButton;
   private StorageReference UserProfileImageRef;
    private static final int Camera_Request_Code=1;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mauth;
    String mCurrentPhotoPath;
    private String CurrentUserId;
    private DatabaseReference mRoot;
    private ProgressDialog loadingbar;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Camera_Request_Code);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        //imageView = (ImageView)findViewById(R.id.imageView2);
        //imageButton = (ImageButton)findViewById(R.id.imageButton);
       // Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       // startActivityForResult(intent,Camera_Request_Code);
        dispatchTakePictureIntent();
        mauth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        CurrentUserId = mauth.getCurrentUser().getUid();
        mRoot = FirebaseDatabase.getInstance().getReference("User").child(CurrentUserId);
        loadingbar=new ProgressDialog(this);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ImageActivity.this, "Permission allowed!"+requestCode, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(ImageActivity.this, "Permission Denied!"+requestCode, Toast.LENGTH_SHORT).show();
                }


        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Camera_Request_Code && resultCode == RESULT_OK && data!=null) {
                    loadingbar.setTitle("Set Profile Image");
                    loadingbar.setMessage("Please wait your profile image is updating...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                    //galleryAddPic();
                    Uri uri = data.getData();
                    final StorageReference filepath = UserProfileImageRef.child(uri.getLastPathSegment());
                    if (uri != null) {
                        filepath.putFile(uri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                final String profileImageUrl = task.getResult().toString();
                                                Map<String, Object> profilemap = new HashMap<>();
                                                profilemap.put("image", profileImageUrl);
                                                mRoot.child("Profile Pic").setValue(profilemap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            SendUserToActivity();
                                                            Picasso.get().load(profileImageUrl).into(imageView);
                                                            loadingbar.dismiss();
                                                            Toast.makeText(ImageActivity.this, "Profile Updated Successfully..", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            String message = task.getException().toString();
                                                            loadingbar.dismiss();
                                                            Toast.makeText(ImageActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loadingbar.dismiss();
                                        Toast.makeText(ImageActivity.this, "aaa " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }

    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    private void SendUserToActivity() {
        Intent intent = new Intent(ImageActivity.this,Tracking.class);
        startActivity(intent);
    }
}
