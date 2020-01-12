package com.example.rl_mii_plaza.activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
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
import java.util.Random;

public class Realtime extends AppCompatActivity {

    float x1, x2, y1, y2;

    private static final int PERMISSION_CODE = 100;
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private StorageReference mStorageRef;
    private boolean faceFound = false;
    private Uri imageURI;
    private int[] emojiArray;

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
                                                                    displayFood((String) document.get("food"));
                                                                    displayLinkedin((String) document.get("linkedin")); // might not be camelCase
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            });
                                            t.start();

                                            try {
                                                t.join();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            Log.d("BRUH", document.getId() + " => " + document.getData());
                                        }

                                        if (!faceFound) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(Realtime.this, "No Face Matched in DB", Toast.LENGTH_SHORT).show();
                                                    displayName("");
                                                    displayHobbies("");
                                                    displayPronouns("");
                                                    displaySchool("");
                                                    displayFood("");
                                                    displayLinkedin("");
                                                }
                                            });
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
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
        }

        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);
        mStorageRef = FirebaseStorage.getInstance().getReference("Images");

        setupEmoji();

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
        displayFood("");
        displaySchool("");
        displayPronouns("");
        displayLinkedin("");
    }

    public void setupEmoji() {
        emojiArray = new int[15];

        emojiArray[0] = 0x1F618;
        emojiArray[1] = 0x1F601;
        emojiArray[2] = 0x1F602;
        emojiArray[3] = 0x1F603;
        emojiArray[4] = 0x1F604;
        emojiArray[5] = 0x1F605;
        emojiArray[6] = 0x1F606;
        emojiArray[7] = 0x1F60D;
        emojiArray[8] = 0x1F60A;
        emojiArray[9] = 0x1F623;

        emojiArray[10] = 0x1F624;

        emojiArray[11] = 0x1F525;

        emojiArray[12] = 0x1F619;
        emojiArray[13] = 0x1F608;
        emojiArray[14] = 0x1F60E;

        // emojiArray[11] = 0x2728; // Sparkles
    }


    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    public void displayName(String name) {
        TextView text = findViewById(R.id.name_text);
        if (!name.equals("")) {
            int r = new Random().nextInt(16);
            int emoji = emojiArray[r];
            text.setText(getEmojiByUnicode(emoji) + " " + name);
        } else {
            text.setText("");
        }
    }

    public void displayHobbies(String hobbies) {
        TextView text = findViewById(R.id.hobby_text);
        if (!hobbies.equals("")) {
            if (hobbies.contains("soccer")) {
                text.setText(getEmojiByUnicode(0x26BD) + " " + hobbies);
            }else
            if (hobbies.contains("basketball")) {
                text.setText(getEmojiByUnicode(0x1F3C0) + " " + hobbies);
            }else
            if (hobbies.contains("shop")) {
                text.setText(getEmojiByUnicode(0x1F6CD) + " " + hobbies);
            }else
            if (hobbies.contains("gam")) {
                text.setText(getEmojiByUnicode(0x1F47E) + " " + hobbies);
            }else
            if (hobbies.contains("fencing")){
                text.setText(getEmojiByUnicode(0x1F93A) + " " + hobbies);
            }else
            if (hobbies.contains("gym") || hobbies.contains("work")) {
                text.setText(getEmojiByUnicode(0x1F3CB) + " " + hobbies);
            } else {
                text.setText(getEmojiByUnicode(0x2728) + " " + hobbies);
            }
        } else {
            text.setText("");
        }
    }

    public void displayPronouns(String pronouns) {
        TextView text = findViewById(R.id.pronoun_text);
        if (!pronouns.equals("")) {
            text.setText(pronouns);
            if (pronouns.contains("him") || pronouns.contains("he")) {
                text.setText(getEmojiByUnicode(0x1F466) + " " + pronouns);
            } else if (pronouns.contains("her") || pronouns.contains("she")) {
                text.setText(getEmojiByUnicode(0x1F469) + " " + pronouns);
            }
        } else {
            text.setText("");
        }
    }

    public void displayFood(String name) {
        TextView text = findViewById(R.id.food_text);
        if (!name.equals("")) {
            text.setText(getEmojiByUnicode(0x1F374) + " " + name);
        } else {
            text.setText("");
        }
    }

    public void displayLinkedin(String name) {
        TextView text = findViewById(R.id.linkedin);
        if (!name.equals("")) {
            text.setText(getEmojiByUnicode(0x1F4BC) + " " + name);
        } else {
            text.setText("");
        }

    }

    public void displaySchool(String school) {
        TextView text = findViewById(R.id.school_text);
        if (!school.equals("")) {
            text.setText(getEmojiByUnicode(0x1F3EB) + " " + school);
        } else {
            text.setText("");
        }
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
            ContentResolver contentResolver = getContentResolver();
            contentResolver.delete(imageURI, null, null);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
}