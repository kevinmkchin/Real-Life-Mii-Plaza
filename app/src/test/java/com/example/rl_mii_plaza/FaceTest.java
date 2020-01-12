package com.example.rl_mii_plaza;

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
import org.junit.Test;

import java.net.URI;

public class FaceTest {

    @Test
    public void faceListTest() {
        Gson gson = new Gson();

        HttpClient httpclient = HttpClients.createDefault();

        try {
            URIBuilder builder = new URIBuilder("https://ubcfaceverification.cognitiveservices.azure.com/face/v1.0/detect");

            builder.setParameter("returnFaceId", "true");
            builder.setParameter("returnFaceLandmarks", "false");
            builder.setParameter("recognitionModel", "recognition_01");
            builder.setParameter("returnRecognitionModel", "false");
            builder.setParameter("detectionModel", "detection_01");

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", "b24b58920c8e4703932909246540d0b3");


            // Request body
            StringEntity reqEntity = new StringEntity("{\"url\": \"https://i.imgur.com/TXlcJC3.png\"}");
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {

                String string = EntityUtils.toString(entity);
                String subString = string.substring(1, string.length() - 1);

                Face face = gson.fromJson(subString, Face.class);
                System.out.println(face.faceId);
                System.out.println(face);


            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
