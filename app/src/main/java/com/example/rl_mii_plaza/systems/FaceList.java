package com.example.rl_mii_plaza.systems;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URI;

public class FaceList {
    private static final String listName = "face_list";
    private static final String recognition = "recognition_02";
    private static final String userData = "";
    private static final String subscriptionKey = "b9214e84-7b6e-4168-8c3b-48e04f220c98";
    private static final String endpoint = "https://ubcfaceverification.cognitiveservices.azure.com/";

    public void createFaceList() {
        HttpClient httpclient = HttpClients.createDefault();
        String JSONrequest = "{\"name\": \"" + listName + "\"," +
                "\"userData:\": \"" + userData + "\"," +
                "\"recognition_02\": \"" + recognition + "\"}";

        try {
            URIBuilder builder = new URIBuilder(endpoint);

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

            // Request body
            StringEntity reqEntity = new StringEntity(JSONrequest);
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                System.out.println(EntityUtils.toString(entity));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
