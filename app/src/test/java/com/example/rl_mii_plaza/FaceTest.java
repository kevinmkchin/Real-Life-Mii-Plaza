package com.example.rl_mii_plaza;


import com.example.rl_mii_plaza.Face.FaceRecognition;
import com.microsoft.projectoxford.face.contract.Face;

import org.junit.Test;

public class FaceTest {

    @Test
    public void faceListTest() {

    }

    @Test
    public void addFaceTest() {



    }

    @Test
    public void detectFaceTest() {
        FaceRecognition recognizer = new FaceRecognition();
        Face face1 = recognizer.detectFaceId("https://firebasestorage.googleapis.com/v0/b/rlmiiplaza-4206b.appspot.com/o/Images%2F1578814869274.jpg?alt=media&token=fb5c75c5-15b2-4857-b2bd-cd47ef93585c");
        Face face2 = recognizer.detectFaceId("https://i.imgur.com/7GAhN9m.png");

        System.out.println(recognizer.checkIfFaceMatch(face1, face2));
    }
}
