package persistence;

/*
 * Copyright 2016 Red Hat Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;

import java.nio.charset.Charset;

/**
 * An example of using the MQTT client
 */
public class Client extends AbstractVerticle {

  private static final String MQTT_TOPIC = "monnit/sensors";
  private static final String MQTT_MESSAGE = "Hello Vert.x MQTT Client";
  private static final String BROKER_HOST = "129.169.50.112";
  private static final int BROKER_PORT = 1883;


  @Override
  public void start() throws Exception {
    MqttClientOptions options = new MqttClientOptions().setKeepAliveTimeSeconds(2);
    options.setClientId("monnit-bridge");
    options.setPassword("1234z");
    Vertx vertx = Vertx.vertx();
    MqttClient client = MqttClient.create(vertx, options);

    // handler will be called when we have a message in topic we subscribing for
    client.publishHandler(publish -> {
      System.out.println("Just received message on [" + publish.topicName() 
      + "] payload [" + publish.payload().toString(Charset.defaultCharset()) 
      + "] with QoS [" + publish.qosLevel() + "]");
    });

    // handle response on subscribe request
    client.subscribeCompletionHandler(h -> {
      System.out.println("Receive SUBACK from server with granted QoS : " + h.grantedQoSLevels());

      // let's publish a message to the subscribed topic
      client.publish(
        MQTT_TOPIC,
        Buffer.buffer(MQTT_MESSAGE),
        MqttQoS.AT_MOST_ONCE,
        false,
        false,
        s -> System.out.println("Publish sent to a server"));

      // unsubscribe from receiving messages for earlier subscribed topic
      vertx.setTimer(5000, l -> client.unsubscribe(MQTT_TOPIC));
    });

    // handle response on unsubscribe request
    client.unsubscribeCompletionHandler(h -> {
      System.out.println("Receive UNSUBACK from server");
      vertx.setTimer(5000, l ->
        // disconnect for server
        client.disconnect(d -> System.out.println("Disconnected form server"))
      );
    });

    // connect to a server
    client.connect(BROKER_PORT, BROKER_HOST, ch -> {
      if (ch.succeeded()) {
        System.out.println("Connected to a server");
        client.subscribe(MQTT_TOPIC, 0);
      } else {
        System.out.println("Failed to connect to a server");
        System.out.println(ch.cause());
      }
    });
  }
}
