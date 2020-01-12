package com.example.rl_mii_plaza.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rl_mii_plaza.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {

    float x1, x2, y1, y2;

    LinearLayout linearLayout;

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    Button captureBtn, uploadBtn, returnBtn;

    ImageView imgView;
    TextView test;
    EditText name, hobbies, food, pronouns, school, linkedin;

    String fireURL = "";
    List<String> documentIds;
    List<Map<String, String>> fireMaps = new ArrayList<>();

    Uri image_uri;

    private StorageReference mStorageRef;
    private FirebaseFirestore firestore;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_info);

        linearLayout = findViewById(R.id.infoLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();


        mStorageRef = FirebaseStorage.getInstance().getReference("Images");
        firestore = FirebaseFirestore.getInstance(); //returns objects of firestore

        // initialize capture button and image view
        captureBtn = findViewById(R.id.capture_image);
        uploadBtn = findViewById(R.id.upload);
        returnBtn = findViewById(R.id.returnHome);
        imgView = findViewById(R.id.image_view);
        test = findViewById(R.id.test);
        initializeEditTexts();

        // capture button click
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, PERMISSION_CODE);
                    } else {
                        //permission already granted
                        openCamera();
                    }
                } else {
                    //system os < marshmallow
                    openCamera();
                }
            }
        });

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileUploader();
            }
        });


        ScrollView scrollView = findViewById(R.id.peenis);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        y2 = event.getY();
                        if ((x1 - x2) > 150) {
                            Intent i = new Intent(InfoActivity.this, HomeActivity.class);
                            startActivity(i);
                        }
                        break;
                }

                return false;
            }
        });

    }


    private void initializeEditTexts() {
        name = findViewById(R.id.name);
        hobbies = findViewById(R.id.hobbies);
        food = findViewById(R.id.food);
        pronouns = findViewById(R.id.pronouns);
        school = findViewById(R.id.school);
        linkedin = findViewById(R.id.linkedin);
    }

    private void collectEditTextContent() {
        String nameContent = name.getText().toString();
        String hobbiesContent = hobbies.getText().toString();
        String foodContent = food.getText().toString();
        String pronounsContent = pronouns.getText().toString();
        String schoolContent = school.getText().toString();
        String linkContent = linkedin.getText().toString();

        if (nameContent.equals("")) {
            Toast.makeText(InfoActivity.this, "Please fill out the name field", Toast.LENGTH_LONG).show();
        } else {
            Map<String, String> person = new HashMap<>();
            try {
                person.put("url", fireURL);
                person.put("name", nameContent);
                person.put("hobbies", hobbiesContent);
                person.put("food", foodContent);
                person.put("pronouns", pronounsContent);
                person.put("school", schoolContent);
                person.put("linkedin", linkContent);
                firestore.collection("users").document(System.currentTimeMillis() + "")
                        .set(person).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(InfoActivity.this, "Uploaded JSONArray", Toast.LENGTH_LONG).show();
                        firestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    List<String> list = new ArrayList();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        list.add(document.getId());
                                    }
                                    Log.d("tag", list.toString());
                                    documentIds = list;
                                    getDocumentData();
                                } else {
                                    Log.d("tag", "error getting documents", task.getException());
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InfoActivity.this, "Did not uploaded JSONArray", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void getDocumentData() {
        CollectionReference collection = firestore.collection("users");
        for (int i = 0; i < documentIds.size(); i++) {
            collection.document(documentIds.get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            fireMaps.add((Map) document.getData());
                            Log.d("tag", "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d("tag", "No such document");
                        }
                    } else {
                        Log.d("tag", "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    public List<Map<String, String>> getFireMaps() {
        return fireMaps;
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void fileUploader() {
        final StorageReference ref = mStorageRef.child(System.currentTimeMillis() + "." + getExtension(image_uri));

        ref.putFile(image_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(InfoActivity.this, "Image uploaded successfully", Toast.LENGTH_LONG).show();
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //test.setText(uri.toString());
                                fireURL = uri.toString();
                                collectEditTextContent();
                                //test.setText(fireURL);

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(InfoActivity.this, "Error with FireBase Storage", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    //handling permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //this method is called, when user presses Allow or Deny from Permission Request Popup
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission from popup was granted
                    openCamera();
                } else {
                    // permission from popup was denied
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //called when image was capture from camera
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //set the image captured to our ImageView
            imgView.setImageURI(image_uri);
            test.setText(image_uri.toString());
        }
    }
}
