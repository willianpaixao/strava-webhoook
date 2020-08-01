package com.zeroplusx.strava;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.net.HttpURLConnection;
import java.util.logging.Logger;

public class StravaWebhook implements HttpFunction {
  private static final Logger logger = Logger.getLogger(StravaWebhook.class.getName());
  private static final String token = System.getProperty("VERIFY_TOKEN");
  private static final Gson gson = new Gson();

  @Override
  public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
    BufferedWriter writer = httpResponse.getWriter();

    switch (httpRequest.getMethod()) {
      case "GET":
        if (httpRequest.getQueryParameters().get("hub.mode").get(0).equals("subscribe") &&
          httpRequest.getQueryParameters().get("hub.verify_token").get(0).equals(token)) {
          String json = String.format("{\"hub.challenge\": %s}", httpRequest.getQueryParameters().get("hub.challenge").get(0));
          writer.write(json);
        }
        httpResponse.setStatusCode(HttpURLConnection.HTTP_OK);
        break;
      case "POST":
        JsonObject body = gson.fromJson(httpRequest.getReader(), JsonObject.class);
        if (body.has("object_type")) {
          httpResponse.setStatusCode(HttpURLConnection.HTTP_OK);
          writer.write("EVENT_RECEIVED");
        }
        break;
      default:
        httpResponse.setStatusCode(HttpURLConnection.HTTP_BAD_METHOD);
        break;
    }
  }
}
