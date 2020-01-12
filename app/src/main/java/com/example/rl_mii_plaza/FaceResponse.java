package com.example.rl_mii_plaza;

public class FaceResponse {
    String faceId;
    String[] faceCoordinates;

    private String getFaceId() {
        return faceId;
    }

    private String[] getFaceCoordinates() {
        return faceCoordinates;
    }
}
