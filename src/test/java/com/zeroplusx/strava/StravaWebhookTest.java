package com.zeroplusx.strava;

import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StravaWebhookTest {
  private static Gson gson = new Gson();

  @Mock
  private HttpRequest httpRequest;
  @Mock
  private HttpResponse httpResponse;

  private BufferedWriter writerOut;
  private StringWriter responseOut;

  @Before
  public void setUp() throws Exception {

    responseOut = new StringWriter();
    writerOut = new BufferedWriter(responseOut);
    when(httpResponse.getWriter()).thenReturn(writerOut);
  }

  @Test
  public void StravaWebhookPostTest() throws Exception {
    String requestJson = gson.toJson(Map.of("object_type", "activity"));
    BufferedReader bodyReader = new BufferedReader(new StringReader(requestJson));

    when(httpRequest.getMethod()).thenReturn("POST");
    when(httpRequest.getReader()).thenReturn(bodyReader);

    new StravaWebhook().service(httpRequest, httpResponse);

    writerOut.flush();
    verify(httpResponse, times(1)).setStatusCode(HttpURLConnection.HTTP_OK);
    Assert.assertEquals("EVENT_RECEIVED", responseOut.toString());
  }
}
