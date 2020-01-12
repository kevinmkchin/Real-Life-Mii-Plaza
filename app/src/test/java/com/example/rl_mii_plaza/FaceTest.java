package com.example.rl_mii_plaza;

import com.example.rl_mii_plaza.Face.FaceList;
import com.example.rl_mii_plaza.Face.FaceRecognition;
import com.microsoft.projectoxford.face.contract.Face;

import org.junit.Test;

import java.util.UUID;

public class FaceTest {

    @Test
    public void faceListTest() {
        FaceList faceList = new FaceList();
        faceList.createFaceList();
    }

    @Test
    public void addFaceTest() {
        FaceList faceList = new FaceList();
        FaceRecognition recognizer = new FaceRecognition();
        String id1 = faceList.addFace("https://i.imgur.com/vIgCmx4.jpg", "", "");
        String id2 = faceList.addFace("https://i.imgur.com/7GAhN9m.png", "", "");

        Face face1 = new Face();
        Face face2 = new Face();

        face1.faceId = UUID.fromString(id1);
        face2.faceId = UUID.fromString(id2);

        System.out.println(recognizer.checkIfFaceMatch(face1, face2));
    }

    @Test
    public void detectFaceTest() {
        FaceRecognition recognizer = new FaceRecognition();
        Face face1 = recognizer.detectFaceId("https://i.imgur.com/vIgCmx4.jpg");
        Face face2 = recognizer.detectFaceId("https://i.imgur.com/7GAhN9m.png");

        System.out.println(recognizer.checkIfFaceMatch(face1, face2));
    }
}
