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
import java.util.LinkedList;
import java.util.List;

import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttQoS;
import ui.GUIListenerFunctions;

/***
 * Author : Jorge Merino jm2210@cam.ac.uk
 * Goal: Reroute the sensors readings and upload them to the Real-Time Data Platform deployed in DIAL server.
 * Method: Use MQTT to create a server/broker in the that publishes the sensors' readings
 * Creation Date: 20/02/2020
 * Last Update: 20/05/2020
***/


public class DataPlatformManager {

	private MqttClient mqttcli;
	private MqttClientOptions mqttcliopt;
	private MqttServer mqttsrv;
	private MqttServerOptions mqttsrvopt;
	private List<MqttEndpoint> mqttendpoints;
	public DataPlatformManager(boolean isServer, boolean encrypted) {
		if(isServer) {
			mqttendpoints = new LinkedList<MqttEndpoint>();
			DataPlatformManagerServer();			
		}else if(encrypted) DataPlatformManagerEncrypted();
		else {
			//DataPlatFormManagerUnencrypted();
			Client cli = new Client();
			try {
				cli.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
	}
	
	private void DataPlatformManagerServer() {
		GUIListenerFunctions.print("Creating MQTT Server. Basic Username + Password method");
		Vertx vertx = Vertx.vertx();
		mqttsrvopt = new MqttServerOptions();
		mqttsrvopt.setPort(1883);
		mqttsrvopt.setAcceptBacklog(100);
				  
		
		mqttsrv = MqttServer.create(vertx, mqttsrvopt);
		mqttsrv.endpointHandler(endpoint -> {
		  GUIListenerFunctions.print("MQTT client [" + endpoint.clientIdentifier() + "] request to connect, clean session = " + endpoint.isCleanSession());
		  if (endpoint.auth() != null) {
			  GUIListenerFunctions.print("[username = " + endpoint.auth().getUsername() + ", password = " + endpoint.auth().getPassword() + "]");
			  if(!endpoint.auth().getUsername().equals("DIALServerDataPlatform") || !endpoint.auth().getPassword().equals("theWindows2020")) endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD);
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

	private void DataPlatFormManagerUnencrypted() {
		GUIListenerFunctions.print("Connecting without certificate. Basic Username + Password method");
		Vertx vertx = Vertx.vertx();
		String clientId = "DIALServerDataPlatform";
		String password = "theWindows2020";
		
		mqttcliopt = new MqttClientOptions();
		mqttcliopt.setAutoKeepAlive(true);
		mqttcliopt.setClientId(clientId);
		mqttcliopt.setPassword(password);
		mqttcli = MqttClient.create(vertx, mqttcliopt);
		mqttcli.publishHandler( s -> {
			try {
				String message = new String(s.payload().getBytes(), "UTF-8");
				GUIListenerFunctions.print("Receive message with content: " + message + " from topic " + s.topicName());
				GUIListenerFunctions.print("Message Published to MQTTBrocker: " + message);
			} catch (UnsupportedEncodingException e) {
				System.out.println("Error while handling the message: ");
				e.printStackTrace();
			}
    	});
		
        connect(1883, "129.169.50.112");
		//connect(1883, "localhost");
	}
	
	private void DataPlatformManagerEncrypted() {
		GUIListenerFunctions.print("Connecting with certificate. Encryption method");
		Vertx vertx = Vertx.vertx();
		String clientId = "DIALServerDataPlatform";
		String password = "theWindows2020";
		
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
				
		mqttcli = MqttClient.create(vertx, mqttcliopt);
        connect(8883, "129.169.50.112");	
	}
	
	private void connect(int port, String host){
		mqttcli.connect(port, host, s -> {
			GUIListenerFunctions.print("HERE");
			if (s.succeeded()) {
				GUIListenerFunctions.print("Connection : " + s.toString());
				fakeReading("TESTVALUE");
			} else {
				GUIListenerFunctions.print("Connection : " + s.cause().getMessage());
			}
			mqttcli.subscribe("#", 0);
			fakeReading("TEST 2");
	    });
		
    }

    private void publish(String topic, String message){
        if(mqttcli!=null) {
        	publishCli(topic, message);
        }else if (mqttsrv!=null) {
        	publishSrv(topic, message);
        }
    	
    }
    
    private void publishSrv(String topic, String message) {
    	for(int i=0; i<mqttendpoints.size(); i++) {
    		MqttEndpoint endpoint = mqttendpoints.get(i); 
    		endpoint.publish(topic, Buffer.buffer(message), MqttQoS.AT_MOST_ONCE, false, false, res -> {
            	GUIListenerFunctions.print("Message Published to MQTTendpoint " + endpoint.clientIdentifier() +  ": " + message);
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
    
    private void publishCli(String topic, String message) {
    	if(mqttcli.isConnected()) {
        	mqttcli.publish(topic, Buffer.buffer(message), MqttQoS.AT_MOST_ONCE, false, false);
        } else {
        	GUIListenerFunctions.print("Not connected to MQTTBroker: Data could not be sent");
        }
    }
    
    public void InsertReading(String timestamp, String description, String sensorid, String signalstrength, String value){
        String message = "{" + 
            "\"timestamp\":" + "\"" + timestamp + "\"," +
            "\"sensor_id\":" + "\"" + sensorid + "\"," +
            "\"signalStrength\":" + "\"" + signalstrength + "\"," +
            "\"value\":" + "\"" + value + "\"," +
            "\"type\":" + "\"" + description + "\"" +
            "}";
        String topic = "dialServer/devices/windows/up";
        //if (description.Equals("Humidity")) topic = "dialServer/devices/humidity/up";
        
        publish(topic, message);
    }
    
    public void fakeReading(String value) {
		InsertReading("yyyy-MM-dd HH:mm", "TypeTest", "ID01234", "100", value);
	}

}
