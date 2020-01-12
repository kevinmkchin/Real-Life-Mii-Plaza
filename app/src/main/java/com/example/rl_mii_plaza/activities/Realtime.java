package com.example.rl_mii_plaza.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.rl_mii_plaza.Face.FaceRecognition;
import com.example.rl_mii_plaza.R;
import com.example.rl_mii_plaza.systems.CameraPreview;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Realtime extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private StorageReference mStorageRef;
    Uri imageURI;

    private String faceUrl = null;

    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Log.d("BRUH", faceUrl);
            if (faceUrl != null) {
                FaceRecognition recognizer = new FaceRecognition();
                //Face newFace = ;
                if (recognizer.checkIfFaceMatch(recognizer.detectFaceId(faceUrl), recognizer.detectFaceId(faceUrl))) {
                    mCamera.startPreview();
                    Log.d("myTag", "it works");
                }
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime);
        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);
        mStorageRef = FirebaseStorage.getInstance().getReference("Images");

        mCamera.startPreview();

        Button captureButton = findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCamera.takePicture(null, null, mPicture);


            }
        });

        displayName("Chris Fung");
        displayHobbies("Memes");
        displaySchool("UBC");
        displayPronouns("She/Her");

    }

    public void displayName(String name){
        TextView text = findViewById(R.id.name_text);
        text.setText(name);
    }

    public void displayHobbies(String hobbies){
        TextView text = findViewById(R.id.hobby_text);
        text.setText(hobbies);
    }

    public void displayPronouns(String pronouns){
        TextView text = findViewById(R.id.pronoun_text);
        text.setText(pronouns);
    }

    public void displaySchool(String school){
        TextView text = findViewById(R.id.school_text);
        text.setText(school);
    }


    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void fileUploader() {
        final StorageReference ref = mStorageRef.child(System.currentTimeMillis() + "." + getExtension(imageURI));

        ref.putFile(imageURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        //Toast.makeText(InfoActivity.this, "Image uploaded successfully", Toast.LENGTH_LONG).show();
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                faceUrl = uri.toString();

                                new MyTask().execute();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            imageURI = getImageUri(getApplicationContext(), bitmap);
            fileUploader();

        }

    };

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.my_camera, menu); //TODO
        return true;
    }

}