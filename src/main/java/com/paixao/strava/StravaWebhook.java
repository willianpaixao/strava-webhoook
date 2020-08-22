package com.paixao.strava;

import com.google.api.core.ApiFuture;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
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

  /**
   * This function assumes the environment variables PROJECT_ID and TOPIC_ID were set on deployment.
   * Otherwise it will fail to send a message.
   *
   * @param message String to be pushed to the PubSub service
   * @throws IOException
   * @throws ExecutionException
   * @throws InterruptedException
   */
  public static void publish(String message)
    throws IOException, ExecutionException, InterruptedException {

    Publisher publisher = null;
    try {
      publisher = Publisher.newBuilder(
        TopicName.of(System.getenv("PROJECT_ID"), System.getenv("TOPIC_ID"))).build();

      ByteString data = ByteString.copyFromUtf8(message);
      PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

      ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
      logger.info("Published message ID: " + messageIdFuture.get());
    } finally {
      if (publisher != null) {
        publisher.shutdown();
        publisher.awaitTermination(1, TimeUnit.MINUTES);
      }
    }
  }

  /**
   * Parses an incoming HTTP request.
   * It expects an GET request to confirm a webhook subscription or a POST request containing Strava's user activity.
   *
   * @param httpRequest
   * @param httpResponse
   * @throws Exception
   */
  @Override
  public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
    BufferedWriter writer = httpResponse.getWriter();

    switch (httpRequest.getMethod()) {
      case "GET":
        if (httpRequest.getQueryParameters().containsKey("hub.verify_token") &&
          httpRequest.getQueryParameters().get("hub.verify_token").get(0).equals(System.getenv("VERIFY_TOKEN"))) {
          if (httpRequest.getQueryParameters().get("hub.mode").get(0).equals("subscribe")) {
            writer.write(String.format("{\"hub.challenge\": \"%s\"}", httpRequest.getQueryParameters().get("hub.challenge").get(0)));
            logger.info("Webhook subscription created");
            httpResponse.setStatusCode(HttpURLConnection.HTTP_OK);
          }
        } else {
          logger.info("Invalid request: invalid verify_token");
          httpResponse.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
        }
      case "POST":
        Gson gson = new Gson();
        JsonObject body = gson.fromJson(httpRequest.getReader(), JsonObject.class);
        if (body != null && body.has("object_type")) {
          switch (body.get("object_type").getAsString()) {
            case "activity":
              logger.info("Object activity received");
              publish(body.toString());
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
      default:
        logger.info("Invalid request: method not allowed");
        httpResponse.setStatusCode(HttpURLConnection.HTTP_BAD_METHOD);
        break;
    }
  }
}
