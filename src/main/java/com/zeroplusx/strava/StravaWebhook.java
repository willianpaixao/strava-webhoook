package com.zeroplusx.strava;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.net.HttpURLConnection;
import java.util.logging.Logger;

/**
 * Main class of the webhook microsesrvice.
 * It receives an HTTP request from Strava's backend and handles it's payload,
 * processing the payload and calling the further service.
 *
 * @author Willian Paixao
 * @version 1.0
 * @since 1.0
 */
public class StravaWebhook implements HttpFunction {
  private static final Logger logger = Logger.getLogger(StravaWebhook.class.getName());
  private static final String token = System.getProperty("VERIFY_TOKEN");

  /**
   * @param httpRequest
   * @param httpResponse
   * @throws Exception
   */
  @Override
  public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
    BufferedWriter writer = httpResponse.getWriter();

    if (httpRequest.getMethod() == "GET") {
      if (httpRequest.getQueryParameters().containsKey("hub.verify_token") &&
        httpRequest.getQueryParameters().get("hub.verify_token").get(0).equals(token)) {
        if (httpRequest.getQueryParameters().get("hub.mode").get(0).equals("subscribe")) {
          String json = String.format("{\"hub.challenge\": %s}", httpRequest.getQueryParameters().get("hub.challenge").get(0));
          writer.write(json);
          logger.info("Webhook subscription created");
          httpResponse.setStatusCode(HttpURLConnection.HTTP_OK);
        }
      } else {
        logger.info("Invalid request: invalid verify_token");
        httpResponse.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
      }
    } else if (httpRequest.getMethod() == "POST") {
      Gson gson = new Gson();
      JsonObject body = gson.fromJson(httpRequest.getReader(), JsonObject.class);
      if (body != null && body.has("object_type")) {
        switch (body.get("object_type").getAsString()) {
          case "activity":
            logger.info("Object activity received");
            break;
          case "athlete":
            logger.info("Object athlete received");
            break;
          default:
            logger.info("Invalid request: invalid object_type");
            httpResponse.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
            break;
        }
        httpResponse.setStatusCode(HttpURLConnection.HTTP_OK);
        writer.write("EVENT_RECEIVED");
      }
    } else {
      logger.info("Invalid request: method not allowed");
      httpResponse.setStatusCode(HttpURLConnection.HTTP_BAD_METHOD);
    }
  }
}
