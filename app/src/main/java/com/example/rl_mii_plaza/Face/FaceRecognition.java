package com.example.rl_mii_plaza.Face;

import com.example.rl_mii_plaza.ConfidenceResponse;
import com.google.gson.Gson;
import com.microsoft.projectoxford.face.contract.Face;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URI;

public class FaceRecognition {

    public Face detectFaceId(String url) {
        Gson gson = new Gson();
        String requestBody = "{\n" +
                "\"url\": \"" + url + "\"\n" +
                "}";
        HttpClient httpclient = HttpClients.createDefault();

        try {
            URIBuilder builder = new URIBuilder("https://ubcfaceverification.cognitiveservices.azure.com/face/v1.0/detect");

            builder.setParameter("returnFaceId", "true");
            builder.setParameter("returnFaceLandmarks", "true");
            builder.setParameter("recognitionModel", "recognition_02");
            builder.setParameter("returnRecognitionModel", "false");
            builder.setParameter("detectionModel", "detection_02");

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", "b24b58920c8e4703932909246540d0b3");


            // Request body
            StringEntity reqEntity = new StringEntity(requestBody);
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {

                String string = EntityUtils.toString(entity);
                String subString = string.substring(1, string.length() - 1);

                return gson.fromJson(subString, Face.class);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;

    }

    public boolean checkIfFaceMatch(Face face1, Face face2) {
        HttpClient httpclient = HttpClients.createDefault();

        try {
            URIBuilder builder = new URIBuilder("https://ubcfaceverification.cognitiveservices.azure.com/face/v1.0/verify");


            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", "b24b58920c8e4703932909246540d0b3");
            String face1ID = face1.faceId.toString();
            String face2ID = face2.faceId.toString();


            // Request body
            StringEntity reqEntity = new StringEntity("{\"faceId1\": \"" + face1ID + "\"," +
                    "\"faceId2\": \"" + face2ID + "\"}");
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                Gson gson = new Gson();
                String string = EntityUtils.toString(entity);
                ConfidenceResponse confidenceResponse = gson.fromJson(string, ConfidenceResponse.class);
                System.out.println(confidenceResponse.getConfidence());
                return confidenceResponse.getIsIdentical();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
