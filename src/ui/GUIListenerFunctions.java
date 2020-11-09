package ui;

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import com.monnit.mine.BaseApplication.Datum;
import com.monnit.mine.MonnitMineAPI.Gateway;
import com.monnit.mine.MonnitMineAPI.MineServer;
import com.monnit.mine.MonnitMineAPI.Sensor;
import com.monnit.mine.MonnitMineAPI.SensorMessage;
import com.monnit.mine.MonnitMineAPI.enums.eFirmwareGeneration;
import com.monnit.mine.MonnitMineAPI.enums.eGatewayType;
import com.monnit.mine.MonnitMineAPI.enums.eMineListenerProtocol;
import com.monnit.mine.MonnitMineAPI.enums.eSensorApplication;

import io.ExternalDeviceStorageHandler;
import monnit.GatewayMessageHandler;
import monnit.PersistGatewayHandler;
import monnit.PersistSensorHandler;
import monnit.ResponseHandler;
import monnit.SensorMessageHandler;
import monnit.UnknownGatewayHandler;
import monnit.exceptions.ExceptionHandler;
import persistence.DataPlatformManager;
import persistence.DatabaseManager;
import persistence.DatabaseManagerSQL;
import persistence.iDatabaseManager;

public class GUIListenerFunctions {
	public static int MQTT_SERVER_MODE = 0;
	public static int MQTT_CLIENT_MODE = 1;
	public static int MQTT_ENCRYPTED = 2;
	public static int MQTT_UNENCRYPTED = 3;
    private static MineServer _Server;
    private static ExternalDeviceStorageHandler _DataAccess;
    private static List<String> myIps;
    private static iDatabaseManager _dbManager;
    private static DataPlatformManager _dataPlatformManager;
    private static boolean serverMode;
    private static boolean encrypted;
    private static boolean MonnitServerStarted;
    private static InetAddress ip;
    private static int port;

    public static synchronized void print(String s) {
        MainWindow.println(s);
        System.out.println(s);
    }
    
    public static void OnLoad(List<Integer> options)
    {
    	MonnitServerStarted = false;
    	_dbManager = new DatabaseManager(iDatabaseManager.DB_JSON);
    	
    	serverMode = false;
        encrypted = false;
        for(int s: options) {
        	if(s==MQTT_SERVER_MODE) serverMode=true;
        	if(s==MQTT_CLIENT_MODE) serverMode=false;
        	if(s==MQTT_ENCRYPTED) encrypted=true;
        	if(s==MQTT_UNENCRYPTED) encrypted=false;
        }
        //_dataPlatformManager = new DataPlatformManager(serverMode, encrypted); //--NOW LOADED at OnLoad().
    	
        
        //loadIPAddresses();
        loadIPAddressesForAllAdapters();
        MainWindow.protocols = new String [eMineListenerProtocol.values().length];
        for (int p=0; p<eMineListenerProtocol.values().length; p++)
        	MainWindow.protocols[p] = eMineListenerProtocol.values()[p].name();
        Arrays.sort(MainWindow.protocols);
        
        MainWindow.gatewayTypes = new String [eGatewayType.values().length];
        for (int t=0; t<eGatewayType.values().length; t++)
        	MainWindow.gatewayTypes[t] = eGatewayType.values()[t].name();
        Arrays.sort(MainWindow.gatewayTypes);
        
        MainWindow.sensorApps= new String [eSensorApplication.values().length];
        for (int a=0; a<eSensorApplication.values().length; a++)
        	MainWindow.sensorApps[a] = eSensorApplication.values()[a].name();
        Arrays.sort(MainWindow.sensorApps);
        
        MainWindow.sensorGens = new String [eFirmwareGeneration.values().length];
        for (int g=0; g<eFirmwareGeneration.values().length; g++)
        	MainWindow.sensorGens[g] = eFirmwareGeneration.values()[g].name();
        Arrays.sort(MainWindow.sensorGens);
        
        
       try{
        	MainWindow.locations = _dbManager.getAllLocationsName().toArray(new String[0]);
        }catch(Exception e) {
        	MainWindow.locations = new String[]{"DIAL", "Lecture Theatre 1", "Lecture Theatre 2", "Seminar Room 1", "Seminar Room 2", "Seminar Room 3", "Meeting Room 1", "Meeting Room 2", "Meeting Room 3", "Meeting Room 4", "Meeting Room 5", "Robot Lab", "Workshop", "Electric Workshop", "Computer Lab", "3D Printing Lab", "Plant Room"};
        }
        Arrays.sort(MainWindow.locations);
        try{
        	MainWindow.objects = _dbManager.getAllObjectsName().toArray(new String[0]);
        }catch(Exception e) {
        	MainWindow.objects = new String[]{"Environment", "Radiator", "Window", "Pump 1", "Pump 2", "Boiler", "Pump Robotic Arm 1", "Pump Robotic Arm 2", "Pump Robotic Arm 3", "Pump Robotic Arm 4", "Pump Robotic Arm 5"};
        }
        Arrays.sort(MainWindow.objects);
    }
    
    public static List<String> getIPAddresses() {
    	return myIps;
    }
    
    @SuppressWarnings("unused")
	private static void loadIPAddresses() {
    	
		try {
			InetAddress[] allMyIps = InetAddress.getAllByName(InetAddress.getLocalHost().getCanonicalHostName());
		
	        List<String> newIps = new ArrayList<String>();
	        for (int i = 0; i < allMyIps.length; i++) {
	        	InetAddress netAddress = allMyIps[i];
	        	if(netAddress.isSiteLocalAddress()) {
	        		newIps.add(netAddress.getHostAddress());
	            }
     		}
	        myIps = newIps;
		} catch (Exception e) {
			print("IPs not loaded. Error: " + e.getMessage());
		}
    }

    
    private static void loadIPAddressesForAllAdapters() {
    	
		try {
			List<String> newIps = new ArrayList<String>();
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()){
            	NetworkInterface adapter = interfaces.nextElement();
            	if (adapter.isUp()) {
            		Enumeration<InetAddress> addresses = adapter.getInetAddresses();
            		while(addresses.hasMoreElements()) {
            			InetAddress ip = addresses.nextElement(); 
           				newIps.add(ip.getHostAddress());
            		}
            			
                }
            }
	        myIps = newIps;
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    

    public static void startButtonPressed() throws Exception {
        if(MonnitServerStarted) {
        	MainWindow.println("------> Monnit Server is already running!");
        	return;
        }
    	port = getPort();
        if (port < 1) {
            MainWindow.println("Invalid Port Number.");
            return;
        }
        eMineListenerProtocol Protocol;
        try {
            Protocol = eMineListenerProtocol.valueOf(MainWindow.protocolDropdown.getSelectedItem().toString());
        } catch (Exception e) {
            print("Invalid Protocol");
            return;
        }

        ip = InetAddress.getByName(MainWindow.ipDropdown.getSelectedItem().toString());
        //stringToByteIP(gui.ipDropdown.getSelectedItem().toString()));

        _dataPlatformManager = new DataPlatformManager(serverMode, encrypted);
        _DataAccess = new ExternalDeviceStorageHandler(_dbManager, _dataPlatformManager);
        _Server = new MineServer(Protocol, ip, port);
 
        _Server.StartServer();
        print("========================[  SERVER STARTED  ]========================");
        print("Constructed Server (" + _Server + "). Monnit version: " + MineServer.getVersion());

        if (_Server.addGatewayDataProcessingHandler(new GatewayMessageHandler())) {
            print("Added GatewayHandler.");
        } else {
            print("Failed to add handler.");
        }
        if (_Server.addSensorDataProcessingHandler(new SensorMessageHandler())) {
            print("Added SensorHandler.");
        } else {
            print("Failed to add handler.");
        }
        if (_Server.addExceptionProcessingHandler(new ExceptionHandler())) {
            print("Added ExceptionHandler.");
        } else {
            print("Failed to add handler.");
        }
        
        if (_Server.addPersistSensorHandler(new PersistSensorHandler(_DataAccess))) {
            print("Added PersistSensorHandler.");
        } else {
            print("Failed to add handler.");
        }
        if (_Server.addPersistGatewayHandler(new PersistGatewayHandler(_DataAccess))) {
            print("Added PersistGatewayHandler.");
        } else {
            print("Failed to add handler.");
        }
        if (_Server.addUnknownGatewayHandler(new UnknownGatewayHandler())) {
            print("Added UnknownGatewayHandler.");
        } else {
            print("Failed to add handler.");
        }
        
        ResponseHandler responseHandler = new ResponseHandler();
        _Server.addGatewayResponseHandler(responseHandler);

        
        List<Gateway> registeredGateways = _dbManager.getAllGateways();
        for (Gateway gw : registeredGateways) {
        	registerGateway (gw.getGatewayID(), gw.getGatewayType().toString());
        	List<Sensor> registeredSensors = _dbManager.getGatewaySensors(""+gw.getGatewayID());
            for (Sensor s : registeredSensors) {
            	registerSensor(s.getSensorID(), s.getFirmwareVersion(), s.getMonnitApplication().toString(), gw.getGatewayID());
            }
        }
                
        print("Finished all inits");
        MonnitServerStarted = true;
        MainWindow.startListenButton.setText("Stop");
        getTopicPrefix();
    }
    
    public static void stopButtonPressed() throws Exception {
        if(!MonnitServerStarted) {
        	MainWindow.println("------> Monnit Server is NOT running!");
        	return;
        }
        if(_Server!=null) {
        	_Server.StopServer();
        }
        if(_dataPlatformManager!=null) {
        	_dataPlatformManager.stop();
        }
        MonnitServerStarted = false;
        MainWindow.startListenButton.setText("Start");
        print("========================[  SERVER STOPPED  ]========================");
    }

    private static int getPort() {
        try {
            return Integer.parseInt(MainWindow.portField.getText());
        } catch (Exception e) {
            return -1;
        }
    }

    public static Sensor FindSensorBySensorID(long sid) throws InterruptedException {
        Sensor sens = _Server.FindSensor(sid);
        return sens;
    }

    public static byte[] stringToByteIP(String ip) {
        byte[] bip = new byte[4];
        String[] iparray = ip.split("\\.");
        for (int i = 0; i < bip.length; i++) {
            bip[i] = (byte) Integer.parseInt(iparray[i]);
        }
        return bip;
    }
	

	public static void registerGatewayButtonPressed() throws Exception {
		long GatewayID = ValidateGateway();
		String GatewayType = MainWindow.gatewayTypeDropdown.getSelectedItem().toString();
		registerGateway(GatewayID, GatewayType);
	}
	
	public static void registerGateway(long GatewayID, String Type) {
		if (GatewayID > 0){
            try{
                eGatewayType GatewayType = eGatewayType.valueOf(Type);
                
				// The values are just example values, be sure to change them according to what you need
                Gateway MineGateway = new Gateway(GatewayID, GatewayType, "3.3.1.5", "2.5.2.1", "127.0.0.1", getPort());
                /*
                print("Default Router IP: " + MineGateway.DefaultRouterIP + " - " + MineGateway.getDefaultRouterIP());
                print("Gateway IP: " + MineGateway.GatewayIP + " - " + MineGateway.getGatewayIP());
                print("Gateway port: " + MineGateway.Port + " - " + MineGateway.getPort());
                print("Server Host Address: " + MineGateway.ServerHostAddress + " - " + MineGateway.getServerHostAddress());
                print("Server Host Address 2: " + MineGateway.ServerHostAddress2 + " - " + MineGateway.getServerHostAddress());
                */
                _Server.RegisterGateway(MineGateway);
                print("GatewayID " + GatewayID + " has been registered");
                long locationId = _dbManager.getLocationId((String) MainWindow.gatewayLocationDropdown.getSelectedItem());
                _dbManager.insertGateway(""+GatewayID, locationId, GatewayType.toString());
            } catch(Exception ex) {
                print("Problem registering the gateway: " + ex.getMessage());
            }
		}
		
	}
	
	private static long ValidateGateway()
    {
        if (_Server == null)
        {
            MainWindow.println("Server not started");
            return 0;
        }

        long GatewayID;
        try{
        	GatewayID = Long.parseLong(MainWindow.gatewayIDField.getText());
        }catch(Exception e)
        {
            MainWindow.println("Invalid GatewayID");
            return 0;
        }

        return GatewayID;
    }
    
	public static void findGatewayButtonPressed() throws Exception {
		GatewayWindow gatewayForm;
		/*
		// FOR TESTING ONLY
		gatewayForm = new GatewayWindow(new Gateway(1234, eGatewayType.Ethernet_3_0, "3.3.1.5", "2.5.2.1", "127.0.0.1", getPort()));
        gatewayForm.show();
        */
		long GatewayID = ValidateGateway();
        if (GatewayID > 0)
        {
            Gateway GatewayObj = _Server.FindGateway(GatewayID);
            if (GatewayObj == null){
                print("GatewayID " + GatewayID + " has not been registered");
            }else{
            	gatewayForm = new GatewayWindow(GatewayObj);
                gatewayForm.show();
            }
        }
		
	}
	
	public static void removeGatewayButtonPressed() throws Exception {
		long GatewayID = ValidateGateway();
        if (GatewayID > 0)
        {
            if (_Server.FindGateway(GatewayID) == null)
            {
                print("Gateway " + GatewayID + " has not been registered");
            }
            else
            {
                _Server.RemoveGateway(GatewayID);
                print("GatewayID " + GatewayID + " has been removed");
                _dbManager.deleteGateway(""+GatewayID);
            }
        }
		
	}
	
	public static void reformGatewayButtonPressed() throws Exception {
		long GatewayID = ValidateGateway();
        if (GatewayID > 0)
		{
			Gateway GatewayObj = _Server.FindGateway(GatewayID);
			if (GatewayObj != null)
			{
				GatewayObj.ReformNetwork();
				print("Reform pending");
			}
			else
			{
				print("Gateway does not exist.");
			}
		}
	}
	
	public static void point2iMonnitGatewayButtonPressed() throws Exception {
		long GatewayID = ValidateGateway();
        if (GatewayID > 0)
		{
			Gateway GatewayObj = _Server.FindGateway(GatewayID);
			if (GatewayObj != null)
			{
				GatewayObj.ServerHostAddress = "u1.sensorsgateway.com";
				GatewayObj.Port = 3000;
				GatewayObj.IsDirty = true;
				print("Pointing Gateway " + GatewayID + " to " + GatewayObj.ServerHostAddress + ":" + GatewayObj.Port);
				print("Update pending");
			}
			else
			{
				print("Gateway does not exist");
			}
		}
	}

	public static void registerSensorButtonPressed() throws Exception {
		Integer SensorID = ValidateSensor();
        long GatewayID = ValidateGateway();
        String generation = MainWindow.sensorGenDropdown.getSelectedItem().toString();
        String application = MainWindow.sensorAppDropdown.getSelectedItem().toString();
        registerSensor (SensorID, generation, application, GatewayID);
	}
	
	public static void registerSensor (long SensorID, String generation, String application, long GatewayID) {
		 if (SensorID > 0){
			 try{
				eFirmwareGeneration firmwaregen;
				switch (generation){
				case "Alta":
					firmwaregen = eFirmwareGeneration.Alta;
					break;
				case "Wifi":
					firmwaregen = eFirmwareGeneration.Wifi;
					break;
				case "Commercial":
					firmwaregen = eFirmwareGeneration.Commercial;
					break;
				default:
					firmwaregen = eFirmwareGeneration.Commercial;
					break;
				}
				
				eSensorApplication SensorApplication;
				SensorApplication = eSensorApplication.valueOf(application);
				
				Sensor MineSensor = new Sensor(SensorID, SensorApplication, "2.3.0.0", firmwaregen);
				_Server.RegisterSensor(GatewayID, MineSensor);
				print("SensorID " + SensorID + " has been registered to Gateway " + GatewayID);
				long locationId = _dbManager.getLocationId((String) MainWindow.sensorLocationDropdown.getSelectedItem());
				long objectId = _dbManager.getObjectId((String) MainWindow.objectDropdown.getSelectedItem());
				_dbManager.insertSensor(""+SensorID, SensorApplication.toString(), ""+GatewayID, locationId, objectId, getUnitForSensorApp(SensorApplication));
			 } catch(Exception ex) {
				 print("Sensor registering failed: " + ex.getMessage());
	         }
		 }
	}
	
	private static String getUnitForSensorApp(eSensorApplication sensorApplication) {
		String res = "";
		switch (sensorApplication) {
	        case Temperature:
	            res = "\u00b0C";
	            break;
	        case Humidity:
	            res = "rH (%)";
	            break;
	        case Activity_Number:
	            res ="Count";
	            break;
	        case Open_Closed:
	            res ="Boolean";
	            break;
	        default:
	            res= "";
		}
		return res;
	}

	private static Integer ValidateSensor()
    {
        if (_Server == null)
        {
            MainWindow.println("Server not started");
            return 0;
        }

        Integer SensorID;
        try{
        	SensorID = Integer.parseInt(MainWindow.sensorIDField.getText());
        }catch(Exception e)
        {
            MainWindow.println("Invalid SensorID");
            return 0;
        }

        return SensorID;
    }
	
	public static void findSensorButtonPressed() throws Exception {
		SensorWindow sensorForm;
		
		/* 
		// FOR TESTING ONLY
		sensorForm = new SensorWindow(new Sensor(123, eSensorApplication.AC_Voltage_500, "2.3.0.0", eFirmwareGeneration.Wifi));
        sensorForm.show();
        */
        
        Integer SensorID = ValidateSensor();
        if (SensorID > 0)
        {
            Sensor SensorObj = _Server.FindSensor(SensorID);
            if (SensorObj == null) {
                print("SensorID " + SensorID + " has not been registered");
            } else{
            	sensorForm = new SensorWindow(SensorObj);
                sensorForm.show();
            }
        }
	}

	public static void removeSensorButtonPressed() throws Exception {
		Integer SensorID = ValidateSensor();
        if (SensorID > 0)
        {
            if (_Server.FindSensor(SensorID) == null)
            {
                MainWindow.println("SensorID " + SensorID + " has not been registered");
            }
            else
            {
                _Server.RemoveSensor(SensorID);
                print("SensorID " + SensorID + " has been removed");
                _dbManager.deleteSensor(SensorID.toString());
            }
        }
	}
	
	public static void insertReadings (List<SensorMessage> sensorMessageList) {
		String date = "";
		String sensorid = "";
		String acp_location = "";
		String acp_object = "";
		String monnit_gw = "";
		for (SensorMessage msg : sensorMessageList) {
            Sensor sens = null;
			try {
				//GUIListenerFunctions.print(msg.toString());
				System.out.println(msg.toString());
				sensorid = ""+msg.SensorID;
				sens = FindSensorBySensorID(msg.SensorID);
				if(sens!=null) msg.ProfileID = sens.MonnitApplication.Value();
				Calendar msgcal = msg.getMessageDate();
				if (msgcal!=null) date = ""+msgcal.getTimeInMillis()*1000L;
				acp_location = "" + _dbManager.getSensorLocation(sensorid);
	    		acp_object = "" + _dbManager.getSensorObject(sensorid);
	    		monnit_gw = _dbManager.getSensorGateway(sensorid);
	            Calendar calendarSM = Calendar.getInstance();
	    		String monnit_sensormgr_ts = ""+calendarSM.getTimeInMillis()*1000L;
	    		String dataconnector = ip.getHostAddress().toString();
	            for(Datum d: msg.getData()) {
	            	_dbManager.insertReading(sensorid, date, d.Description, ""+msg.getSignalStrength(), ""+msg.getVoltage(), d.Data.toString(), monnit_gw, dataconnector, monnit_sensormgr_ts, acp_location, acp_object);
	            	// Reroutes the message to the Real-Time Data Platform in the DIAL Server.
	            	_dataPlatformManager.InsertReading(sensorid, date, d.Description, ""+msg.getSignalStrength(), ""+msg.getVoltage(), d.Data.toString(), monnit_gw, dataconnector, monnit_sensormgr_ts, acp_location, acp_object);
	            }
			} catch (InterruptedException | NullPointerException | IllegalArgumentException e) {
				System.err.println("---===========================================================================---");
				System.err.println("Something (Controlled) wrong with: "+ msg.toString() + " -> "+ e.getMessage());
				System.err.println("Sensor"+ msg.SensorID + " is sending data, but maybe not registered?");
				//e.printStackTrace();
				System.err.println("---===========================================================================---");
			} catch (Exception e) {
				System.err.println("---===========================================================================---");
				System.err.println("Something wrong with: "+ msg.toString() + " -> "+ e.getMessage());
				System.err.println("Check whether "+ msg.SensorID + " is of type -> " + sens.MonnitApplication.name());
				//e.printStackTrace();
				System.err.println("---===========================================================================---");
			}
		}
	}
	
	public static MineServer getServer() {
		return _Server;
	}
	
	public static void fakeReading() {
		_dataPlatformManager.fakeReading("TEST_VALUE_FROM_GUI", ip.getHostAddress().toString());
	}

	public static boolean isMonnitServerStarted() {
		return MonnitServerStarted;
	}
	
	public static String getTopicPrefix() {
		String topic = MainWindow.topicField.getText();
		if(!topic.startsWith("csn/")) topic = "csn/"+topic;
		return DataPlatformManager.topicValidation(topic);
	}
}
