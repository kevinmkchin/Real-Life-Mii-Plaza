package com.example.rl_mii_plaza.Face;

import com.example.rl_mii_plaza.ConfidenceResponse;
import com.google.gson.Gson;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FaceRecognition {

    public Face detectFaceId(String url) {
        Gson gson = new Gson();
        OkHttpClient okHttpClient = new OkHttpClient();
        String requestBodyString = "{\"url\": \"" + url + "\"}";
        RequestBody requestBody = RequestBody.create(null, requestBodyString.getBytes());
        Request request = new Request.Builder().url("https://ubcfaceverification.cognitiveservices.azure.com/face/v1.0/detect?returnFaceId=true&returnFaceLandmarks=false&recognitionModel=recognition_01&returnRecognitionModel=false&detectionModel=detection_01").
                addHeader("Content-Type", "application/json").addHeader("Ocp-Apim-Subscription-Key", "b24b58920c8e4703932909246540d0b3")
                .post(requestBody).build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            String stringResponse = response.body().string();


            String goodJson = stringResponse.substring(1, stringResponse.length() - 1);

            Face face = gson.fromJson(goodJson, Face.class);
            return face;

            // Do something with the response.
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkIfFaceMatch(Face face1, Face face2) {
        Gson gson = new Gson();
        OkHttpClient okHttpClient = new OkHttpClient();
        String face1ID = face1.faceId.toString();
        String face2ID = face2.faceId.toString();
        String requestBodyString = "{\"faceId1\": \"" + face1ID + "\"," +
                "\"faceId2\": \"" + face2ID + "\"}";
        RequestBody requestBody = RequestBody.create(null, requestBodyString.getBytes());
        Request request = new Request.Builder().url("https://ubcfaceverification.cognitiveservices.azure.com/face/v1.0/verify").
                addHeader("Content-Type", "application/json").addHeader("Ocp-Apim-Subscription-Key", "b24b58920c8e4703932909246540d0b3")
                .post(requestBody).build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            String stringResponse = response.body().string();
            ConfidenceResponse confidenceResponse = gson.fromJson(stringResponse, ConfidenceResponse.class);
            System.out.println(confidenceResponse.getConfidence());
            return confidenceResponse.getIsIdentical();

            // Do something with the response.
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, String> searchDatabase(List<Map<String, String>> entries, String url) throws NoURLFoundException, NoUserFoundException {
        Face face1 = detectFaceId(url);

        for (Map<String, String> entry : entries) {
            String urlToMatch = entry.get("url");
            Face face2 = detectFaceId(urlToMatch);

            if (urlToMatch == null || urlToMatch.isEmpty()) {
                throw new NoURLFoundException();
            }

            if (checkIfFaceMatch(face1, face2)) {
                return entry;
            }
        }
        throw new NoUserFoundException();
    }
}
