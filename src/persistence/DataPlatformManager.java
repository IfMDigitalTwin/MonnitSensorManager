package persistence;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.PfxOptions;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttServerOptions;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttQoS;
import ui.GUIListenerFunctions;
import ui.MainWindow;

/***
 * Author : Jorge Merino jm2210@cam.ac.uk
 * Goal: Reroute the sensors readings and upload them to the Real-Time Data Platform deployed in DIAL server.
 * Method: Use MQTT to create a server/broker in the that publishes the sensors' readings
 * Creation Date: 20/02/2020
 * Last Update: 20/05/2020
***/


public class DataPlatformManager {
	private static final String brokerIP = "localhost";
	public static final int unencrypted_port = 1883;
	public static final int encrypted_port = 8883;
	public static final String MQTT_TOPIC = "csn/";
	public static final String MQTT_HOST = "localhost";
	//private MqttClient mqttcli;
	private MQTTClient mqttclient; 
	private MqttClientOptions mqttcliopt;
	private MqttServer mqttsrv;
	private MqttServerOptions mqttsrvopt;
	private List<MqttEndpoint> mqttendpoints;
	
	
	public DataPlatformManager(boolean isServer, boolean encrypted) {
		String clientId = "dtsensormanager";
		String password = "dtsensormanager";
		if(isServer) {
			mqttendpoints = new LinkedList<MqttEndpoint>();
			DataPlatformManagerServer(clientId, password);
		}else if(encrypted) DataPlatformManagerEncrypted(clientId, password, brokerIP);
		else {
			DataPlatFormManagerUnencryptedCli(clientId, password, brokerIP);
			//DataPlatFormManagerUnencrypted(clientId, password, brokerIP);
		}	
	}
	
	private void DataPlatformManagerServer(String clientId, String password) {
		GUIListenerFunctions.print("Creating MQTT Server. Basic Username + Password method");
		Vertx vertx = Vertx.vertx();
		mqttsrvopt = new MqttServerOptions();
		mqttsrvopt.setPort(DataPlatformManager.unencrypted_port);
		mqttsrvopt.setAcceptBacklog(100);
				  
		
		mqttsrv = MqttServer.create(vertx, mqttsrvopt);
		mqttsrv.endpointHandler(endpoint -> {
		  GUIListenerFunctions.print("MQTT client [" + endpoint.clientIdentifier() + "] request to connect, clean session = " + endpoint.isCleanSession());
		  if (endpoint.auth() != null) {
			  GUIListenerFunctions.print("[username = " + endpoint.auth().getUsername() + ", password = " + endpoint.auth().getPassword() + "]");
			  if(!endpoint.auth().getUsername().equals(clientId) || !endpoint.auth().getPassword().equals(password)) endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD);
			  else {
				  /*
				  if (endpoint.will() != null) {
					  GUIListenerFunctions.print("[will topic = " + endpoint.will().getWillTopic() + " msg = " + new String(endpoint.will().getWillMessageBytes()) +
				      " QoS = " + endpoint.will().getWillQos() + " isRetain = " + endpoint.will().isWillRetain() + "]");
				  }*/

				  GUIListenerFunctions.print("[keep alive timeout = " + endpoint.keepAliveTimeSeconds() + "]");
				  endpoint.accept(false);
				  mqttendpoints.add(endpoint);
				  endpoint.disconnectHandler(v -> {
					  GUIListenerFunctions.print("Received disconnect from client");
					  if(mqttendpoints.contains(endpoint)) mqttendpoints.remove(endpoint);
				  });
			  }
		  }
		});
		
		mqttsrv.listen(ar -> {
			if (ar.succeeded()) {
				GUIListenerFunctions.print("MQTT server is listening on port " + ar.result().actualPort());
			} else {
				GUIListenerFunctions.print("Error on starting the server: " + ar.cause().getMessage());
				ar.cause().printStackTrace();
			}
		});
	}
	
	private void DataPlatFormManagerUnencryptedCli(String clientId, String password, String brokerIP) {

		mqttclient = new MQTTClient();
		
	}
	/*
	private void DataPlatFormManagerUnencrypted(String clientId, String password, String brokerIP) {
		GUIListenerFunctions.print("Connecting as Client without certificate. Basic Username + Password method");
		/*
		Client cli = new Client();
		try {
			cli.start();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}* /
		Vertx vertx = Vertx.vertx();
		
		mqttcliopt = new MqttClientOptions();
		mqttcliopt.setClientId(clientId);
		mqttcliopt.setPassword(password);
		//mqttcliopt.setAutoKeepAlive(true);
		mqttcli = MqttClient.create(vertx, mqttcliopt);
		
		mqttcli.publishHandler( s -> {
			/*try {* /
				//String message = new String(s.payload().getBytes(), "UTF-8");
				String message = s.payload().toString(Charset.defaultCharset());
				GUIListenerFunctions.print("Message published to MQTT Broker: " + brokerIP + s.topicName() + " -> Message: " + message);
			/*} catch (UnsupportedEncodingException e) {
				System.out.println("Error while handling the message: ");
				e.printStackTrace();
			}* /
    	});
		
		// handle response on subscribe request
	    mqttcli.subscribeCompletionHandler(h -> {
	      System.out.println("Receive SUBACK from server with granted QoS : " + h.grantedQoSLevels());
	      /*
	      // let's publish a message to the subscribed topic
	      mqttcli.publish(
	        MQTT_TOPIC,
	        Buffer.buffer("Hello Vert.x MQTT Client"),
	        MqttQoS.AT_MOST_ONCE,
	        false,
	        false,
	        s -> System.out.println("Publish sent to a server"));
	       * /
	      // unsubscribe from receiving messages for earlier subscribed topic
	      // vertx.setTimer(5000, l -> mqttcli.unsubscribe(MQTT_TOPIC));
	    });

	    // handle response on unsubscribe request
	    mqttcli.unsubscribeCompletionHandler(h -> {
	      System.out.println("Receive UNSUBACK from server");
	      vertx.setTimer(5000, l ->
	        // disconnect for server
	        mqttcli.disconnect(d -> System.out.println("Disconnected form server"))
	      );
	    });
		
        connect(DataPlatformManager.unencrypted_port, brokerIP);
		//connect(DataPlatformManager.unencrypted_port, "localhost");
	}*/
	
	private void DataPlatformManagerEncrypted(String clientId, String password, String brokerIP) {
		GUIListenerFunctions.print("Connecting with certificate. Encryption method");
		Vertx vertx = Vertx.vertx();
		
		mqttcliopt = new MqttClientOptions();
		mqttcliopt.setAutoKeepAlive(true);
		mqttcliopt.setClientId(clientId);
		mqttcliopt.setPassword(password);
		mqttcliopt.setSsl(true);
		mqttcliopt.addEnabledSecureTransportProtocol("TSLv1.2");
		
		PfxOptions certificate = new PfxOptions();
		certificate.setPath("./dist/cert/certificate.pfx");
		certificate.setPassword(password);
		mqttcliopt.setPfxKeyCertOptions(certificate);
				
		//mqttcli = MqttClient.create(vertx, mqttcliopt);
        //connect(DataPlatformManager.encrypted_port, brokerIP);	
	}
	/*
	private void connect(int port, String host){
		GUIListenerFunctions.print("Connecting to MQTT broker " + brokerIP + "...");
		mqttcli.connect(port, host, s -> {
			GUIListenerFunctions.print("HERE");
			if (s.succeeded()) {
				GUIListenerFunctions.print("Connection : " + s.toString());
				//fakeReading("TESTVALUE");
			} else {
				GUIListenerFunctions.print("Connection : " + s.cause().getMessage());
			}
			mqttcli.subscribe(MQTT_TOPIC, 0);
			//fakeReading("TEST 2");
	    });
		//GUIListenerFunctions.print("Finished METHOD CONNECT. Connected: " + mqttcli.isConnected());
	}
*/
    private void publish(String topic, String message){
    	if(mqttclient!=null) {
    		mqttclient.publish(topic, message);
    	/*} else if(mqttcli!=null) {
        	publishCli(topic, message);*/
        }else if (mqttsrv!=null) {
        	publishSrv(topic, message);
        }
    }
    
    private void publishSrv(String topic, String message) {
    	for(int i=0; i<mqttendpoints.size(); i++) {
    		MqttEndpoint endpoint = mqttendpoints.get(i); 
    		endpoint.publish(topic, Buffer.buffer(message), MqttQoS.AT_MOST_ONCE, false, false, res -> {
            	GUIListenerFunctions.print("Message Published to MQTTendpoint " + endpoint.clientIdentifier() +  " -> topic <" + topic + ">, message: " +  message);
            });
			// specifing handlers for handling QoS 1 and 2
			endpoint.publishAcknowledgeHandler(messageId -> {
				GUIListenerFunctions.print("Received ack for message = " +  messageId);

			}).publishReceivedHandler(messageId -> {

			  endpoint.publishRelease(messageId);

			}).publishCompletionHandler(messageId -> {

				GUIListenerFunctions.print("Received ack for message = " +  messageId);
			});
    	}
    }
    /*
    private void publishCli(String topic, String message) {
    	if(mqttcli.isConnected()) {
        	mqttcli.publish(topic, Buffer.buffer(message), MqttQoS.AT_MOST_ONCE, false, false, s -> System.out.println("Publish sent to a server"));
        } else {
        	GUIListenerFunctions.print("Not connected to MQTTBroker: Data could not be sent");
        }
    }
    */
    public void InsertReading(String sensorid, String msgtimestamp, String type, String signalstrength, String value, String arrived_to_DTSM){
        String acp_id = "monnit-"+type+"-"+sensorid;
    	String message = "{" + 
            "\"msg_ts\":" + "\"" + msgtimestamp + "\"," +
            "\"sensor_id\":" + "\"" + sensorid + "\"," +
            "\"signalStrength\":" + "\"" + signalstrength + "\"," +
            "\"value\":" + "\"" + value + "\"," +
            "\"type\":" + "\"" + type + "\"" +
            "\"dtsm_ts\":" + "\"" + arrived_to_DTSM + "\"" +
            "\"acp_id\":" + "\"" + acp_id +  "\"" +
            "}";
        String topic = GUIListenerFunctions.getTopicPrefix() + acp_id + "/monnit";
        
        publish(topic, message);
    }
    
    public void fakeReading(String value) {
		InsertReading("ID01234", "yyyy-MM-dd HH:mm", "TypeTest", "100", value, "yyyy-MM-dd HH:mm");
	}

	public void stop() {
		if(this.mqttsrv != null) {
			this.mqttsrv.close();
			this.mqttsrv = null;
		}
		if(this.mqttclient != null) {
			mqttclient.close();
			this.mqttclient = null;
		}
	}

	public static String topicValidation(String topic) {
		String pattern = "^([a-zA-Z]\\w*/)*$";
		String validatedTopic=topic;
		if (!topic.matches(pattern)) {
			validatedTopic = MQTT_TOPIC;
			System.err.println("TOPIC PREFIX IS NOT WELL FORMED...Using default: " + MQTT_TOPIC);
		}
		return validatedTopic;
	}

}
