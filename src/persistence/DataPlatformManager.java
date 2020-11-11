package persistence;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.PfxOptions;
import io.vertx.mqtt.MqttClientOptions;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttServerOptions;

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
	private static final String brokerIP = "localhost";
	public static final int unencrypted_port = 1883;
	public static final int encrypted_port = 8883;
	public static final String MQTT_TOPIC = "csn/";
	public static final String MQTT_HOST = "localhost";
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
    public void InsertReading(String monnit_sensor_id, String monnit_ts, String monnit_sensor_type, String monnit_signalstrength, String monnit_voltage, String monnit_value, String monnit_gw,
    		String dataconnector, String monnit_sensormgr_ts, String acp_location, String acp_object){
    	
        String acp_id = "monnit-"+monnit_sensor_type+"-"+monnit_sensor_id;
        String acp_ts = (!monnit_ts.equals("")?monnit_ts:monnit_sensormgr_ts);
        
    	String message = "{" + 
            "\"monnit_ts\":" + "\"" + monnit_ts + "\"," +
            "\"monnit_sensor_id\":" + "\"" + monnit_sensor_id + "\"," +
            "\"monnit_signalstrength\":" + "\"" + monnit_signalstrength + "\"," +
            "\"monnit_signalvoltage\":" + "\"" + monnit_voltage + "\"," +
            "\"monnit_gw\":" + "\"" + monnit_gw + "\"," +
            "\"monnit_value\":" + "\"" + monnit_value + "\"," +
            "\"monnit_sensor_type\":" + "\"" + monnit_sensor_type + "\"," +
            "\"monnit_sensormgr_ts\":" + "\"" + monnit_sensormgr_ts + "\"," +
            "\"data_connector\":" + "\"" + dataconnector + "\"," +
            "\"acp_id\":" + "\"" + acp_id +  "\"," +
            "\"acp_ts\":" + "\"" + acp_ts +  "\"," +
            "\"acp_ifc_location\":" + "\"" + acp_location +  "\"," +
            "\"acp_ifc_object\":" + "\"" + acp_object +  "\"" +
            "}";
        String topic = GUIListenerFunctions.getTopicPrefix() + acp_id + "/monnit";
        
        publish(topic, message);
    }
    
    public void fakeReading(String value, String ip) {
		InsertReading("FAKE", "yyyy-MM-dd HH:mm", "TypeTest", "100", "5", value, "nogw", "yyyy-MM-dd HH:mm", ip, "NOWHERE", "THEVOID");
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
