package com.example.rl_mii_plaza.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rl_mii_plaza.Face.FaceRecognition;
import com.example.rl_mii_plaza.R;
import com.example.rl_mii_plaza.systems.CameraPreview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayOutputStream;

public class Realtime extends AppCompatActivity {

    float x1, x2, y1, y2;

    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private StorageReference mStorageRef;
    private boolean faceFound = false;
    private Uri imageURI;

    private String faceUrl = null;

    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Log.d("BRUH", faceUrl);
            if (faceUrl != null) {
                final FaceRecognition recognizer = new FaceRecognition();
                final Face newFace = recognizer.detectFaceId(faceUrl);
                if (newFace != null) {
//                    if (recognizer.checkIfFaceMatch(newFace, newFace)) {
//                        mCamera.startPreview();
//                        Log.d("myTag", "it works");
//                    }
                    faceFound = false;

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("users")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        synchronized (this) {
                                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                                Thread t = new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Face dbFace = recognizer.detectFaceId((String) document.get("url"));
                                                        if (dbFace != null) {
                                                            if (recognizer.checkIfFaceMatch(newFace, dbFace)) {
                                                                faceFound = true;
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        displayName((String) document.get("name"));
                                                                        displayHobbies((String) document.get("hobbies"));
                                                                        displayPronouns((String) document.get("pronouns"));
                                                                        displaySchool((String) document.get("school"));
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }
                                                });
                                                t.start();
                                                Log.d("BRUH", document.getId() + " => " + document.getData());
                                            }
                                        }

                                    } else {
                                        Log.d("BRUH", "Error getting documents: ", task.getException());
                                    }
                                }
                            });

                    mCamera.startPreview();

                } else {
                    mCamera.startPreview();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Realtime.this, "No Face Detected", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.d("myTag", "no face in picture");
                }
            }
            synchronized (this) {
                if (!faceFound) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(Realtime.this, "No Face Matched in DB", Toast.LENGTH_SHORT).show();
                        }
                    });
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

        displayName("");
        displayHobbies("");
        displaySchool("");
        displayPronouns("");

    }

    public void displayName(String name) {
        TextView text = findViewById(R.id.name_text);
        text.setText(name);
    }

    public void displayHobbies(String hobbies) {
        TextView text = findViewById(R.id.hobby_text);
        text.setText(hobbies);
    }

    public void displayPronouns(String pronouns) {
        TextView text = findViewById(R.id.pronoun_text);
        text.setText(pronouns);
    }

    public void displaySchool(String school) {
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
            Matrix matrix = new Matrix();

            matrix.postRotate(90);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            imageURI = getImageUri(getApplicationContext(), rotatedBitmap);
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

    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if ((x2 - x1) > 150) {
                    Intent i = new Intent(Realtime.this, HomeActivity.class);
                    startActivity(i);
                }
                break;
        }
        return false;
    }
}