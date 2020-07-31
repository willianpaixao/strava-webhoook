package com.zeroplusx.strava;

import java.io.BufferedWriter;
import java.io.StringWriter;

import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StravaWebhookTest {
  @Mock private HttpRequest httpRequest;
  @Mock private HttpResponse httpResponse;

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
    when(httpRequest.getMethod()).thenReturn("POST");
    new StravaWebhook().service(httpRequest, httpResponse);

    writerOut.flush();
    Assert.assertEquals("EVENT_RECEIVED", responseOut.toString());
  }
}
