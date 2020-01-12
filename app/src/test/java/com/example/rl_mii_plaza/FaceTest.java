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
        Face face1 = recognizer.detectFaceId("https://i.imgur.com/vIgCmx4.jpg");
        Face face2 = recognizer.detectFaceId("https://i.imgur.com/7GAhN9m.png");

        System.out.println(recognizer.checkIfFaceMatch(face1, face2));
    }
}
