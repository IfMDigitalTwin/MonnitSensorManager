package persistence;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import ui.GUIListenerFunctions;

public class MQTTClient /*extends AbstractVerticle */{
    JsonObject config; // JSON config object given in instantiation
    String FEED_ID;    // which is checked and parsed into these values
    String USERNAME;
    String PASSWORD;
    Integer PORT;
    String HOST;
    String TOPIC;

    MqttClient client = null; // MqttFeed object will be null if config bad

    boolean watchdog_running = false; // set 'true' to avoid multiple instances

    long watchdog_timer; // id of timer - so we can reset to longer interval

    long watchdog_count = 0;

    int watchdog_period = 10000; // time (s) between status checks

    int WATCHDOG_MAX = 10; // when disconnected, check WATCHDOG_MAX times before doubling period

    public MQTTClient() {

        MqttClientOptions client_options = new MqttClientOptions();

        USERNAME = "dtsensormanager";
        PASSWORD = "dtsensormanager";
        FEED_ID = "DTSensorManager";
        PORT = 1883;
        HOST = "localhost";
        TOPIC = "csn/pi-monnit/sensors/default";

        client_options.setPassword(PASSWORD);
        client_options.setUsername(USERNAME);
        // Initialize and connect the MQTT client
        init(client_options);
    }
    
    

    private void init(MqttClientOptions client_options)
    {
        // *********************************
        // NOW CREATE THE VERTX MQTT CLIENT
        // *********************************
        client = MqttClient.create(Vertx.vertx(), client_options);

        // catch exceptions buried in netty
        client.exceptionHandler( e -> {
            System.err.println(FEED_ID+
                       ": MQTT exception" );
        });

        // ***************************************************
        // REGISTER MQTT CLOSE HANDLER
        // ***************************************************
        client.closeHandler( Void -> {
            System.err.println(FEED_ID+
                       ": MQTT server closed connection" );

        }); // end closeHandler

        // ***************************************************
        // REGISTER MQTT SUBSCRIBE CALLBACK
        // ***************************************************
        client.publishHandler( mqtt_data -> {
            System.out.println(FEED_ID+": MQTT data received");
            try {
                //process_feed(mqtt_data, config);
            	
            }
            catch (Exception e)
            {
                System.err.println(FEED_ID+
                           ": MQTT process_feed exception");
                System.err.println(e.getMessage());
                return;
            }
        }); // end publishHandler

        // ***************************************************
        // CONNECT TO MQTT SERVER
        // ***************************************************
        //
        //connect();
        //
        // Starting the watchdog
        // This will automatically connect the first time if we're not connected
        //
        start_watchdog(watchdog_period); // if watchdog already running, this will do nothing


        System.out.println(FEED_ID+": MQTT handler started");
    }
    
    

    // ***************************************************
    // CONNECT TO MQTT SERVER
    // ***************************************************
    private void connect()
    {
        System.err.println(FEED_ID+
                   ": MQTT connecting to "+HOST+":"+PORT.toString());

        // Connect to the MQTT server
        // Note we will only start the watchdog AFTER the first connect attempt, good or bad, to avoid race.
	        client.connect(PORT, HOST, connect_response -> {
	            if (connect_response.succeeded())
	            {
	                System.out.println(FEED_ID+": MQTT connected");
	
	                // subscribe to the MQTT topic, e.g. '+/devices/+/up' for TTN device uplink data
	                subscribe();
	            }
	            else
	            {
	                System.err.println(FEED_ID+": MQTT connect FAILED");
	            }
	        }); // end connect
    }

    // *************************
    // SUBSCRIBE TO MQTT TOPIC
    // *************************
    private void subscribe()
    {
         System.out.println(FEED_ID+": MQTT subscribing");

         client.subscribe(TOPIC, 0); // Subscribe with QoS ZERO
    }

    // *************************
    // START WATCHDOG TIMER
    // *************************
    private void start_watchdog(int period)
    {
        System.out.println(FEED_ID+
                ": MQTT start_watchdog period="+Integer.toString(period)+
                ", running="+watchdog_running);

        if (!watchdog_running)
        {
            watchdog_running = true;

            System.out.println(FEED_ID+": MQTT starting watchdog");

            // send periodic "system_status" messages
            watchdog_timer = Vertx.vertx().setPeriodic(period, id -> { watchdog();  });
        }
    }

    // *************************
    // WATCHDOG CHECKS
    // wakes up on timer
    // *************************
    private void watchdog()
    {
        System.err.println(FEED_ID+
                ": MQTT watchdog period="+Integer.toString(watchdog_period)+
                ", "+(client.isConnected() ? "connected" : "DISCONNECTED"));

        if (!client.isConnected())
        {

            System.err.println(FEED_ID+
                ": MQTT watchdog calling connect()");

            // re-connect
            try {
            	connect();
            }catch (Exception e) {
            	System.err.println ("Could not connect...Test whether a MQTT Broker is started");
            }
        }
    }
    
    public void publish (String topic, String message) {
    	if(client.isConnected()) {
        	client.publish(topic, Buffer.buffer(message), MqttQoS.AT_MOST_ONCE, false, false, s -> System.out.println("Publish "+ s + ", sent to a server"));
        } else {
        	//GUIListenerFunctions.print("++++++ Not connected to MQTTBroker: Data could not be sent");
        	System.err.println("++++++ Not connected to MQTTBroker: Data could not be sent");
        }
    }
}
