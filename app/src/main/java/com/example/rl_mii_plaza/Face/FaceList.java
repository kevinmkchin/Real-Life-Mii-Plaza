//package com.example.rl_mii_plaza.Face;
//
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpPut;
//import org.apache.http.client.utils.URIBuilder;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.util.EntityUtils;
//
//import java.net.URI;
//
//public class FaceList {
//    private static final String subscriptionKey = "b24b58920c8e4703932909246540d0b3";
//    private static final String endpoint = "https://ubcfaceverification.cognitiveservices.azure.com";
//    private static final String faceListID = "face_list";
//
//    public void createFaceList() {
//        HttpClient httpclient = HttpClients.createDefault();
//        String requestBody = "{\n" +
//                "    \"name\": \"face_list\",\n" +
//                "    \"userData\": \"User-provided data attached to the face list.\",\n" +
//                "    \"recognitionModel\": \"recognition_02\"\n" +
//                "}";
//
//        try {
//            URIBuilder builder = new URIBuilder(endpoint + "/face/v1.0/facelists/" + faceListID);
//
//            URI uri = builder.build();
//            HttpPut request = new HttpPut(uri);
//            request.setHeader("Content-Type", "application/json");
//            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
//
//            // Request body
//            StringEntity reqEntity = new StringEntity(requestBody);
//            request.setEntity(reqEntity);
//
//            HttpResponse response = httpclient.execute(request);
//            HttpEntity entity = response.getEntity();
//
//            if (entity != null) {
//                System.out.println(EntityUtils.toString(entity));
//            }
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    public String addFace(String url, String userData, String targetFace) {
//        HttpClient httpclient = HttpClients.createDefault();
//        String requestBody = "{\n" +
//                "\"url\": \"" + url + "\"\n" +
//                "}";
//
//        try {
//            URIBuilder builder = new URIBuilder(endpoint + "/face/v1.0/facelists/" + faceListID + "/persistedFaces");
//
//            builder.setParameter("userData", userData);
//            builder.setParameter("targetFace", targetFace);
//            builder.setParameter("detectionModel", "detection_02");
//
//            URI uri = builder.build();
//            HttpPost request = new HttpPost(uri);
//            request.setHeader("Content-Type", "application/json");
//            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
//
//            // Request body
//            StringEntity reqEntity = new StringEntity(requestBody);
//            request.setEntity(reqEntity);
//
//            HttpResponse response = httpclient.execute(request);
//            HttpEntity entity = response.getEntity();
//
//            if (entity != null) {
//                String persistentIDJSON = EntityUtils.toString(entity);
//                System.out.println(persistentIDJSON);
//
//                JsonParser parser = new JsonParser();
//                JsonObject obj = parser.parse(persistentIDJSON).getAsJsonObject();
//
//                return obj.get("persistedFaceId").getAsString();
//            }
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        return null;
//    }
//}
//
