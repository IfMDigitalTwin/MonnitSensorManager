package persistence;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.monnit.mine.MonnitMineAPI.Gateway;
import com.monnit.mine.MonnitMineAPI.Sensor;
import com.monnit.mine.MonnitMineAPI.enums.eFirmwareGeneration;
import com.monnit.mine.MonnitMineAPI.enums.eGatewayType;
import com.monnit.mine.MonnitMineAPI.enums.eSensorApplication;

import ui.GUIListenerFunctions;
import ui.MainWindow;

public class DatabaseManagerSQL implements iDatabaseManager{
	private static String TBL_LOCATION = "Location";
	private static String TBL_OBJECT = "RTSD-Object";
	private static String TBL_GATEWAY = "RTSD-Gateway";
    private static String TBL_SENSOR = "RTSD-Sensor";
    private static String TBL_READING = "RTSD-Reading";
    private static String TBL_OCCUPANCY = "Occupancy";
    private static String TBL_MATCHING = "Matching_Table";
    private static String TBL_FDD_DEMO = "FDD_demo";
    private static String TBL_BMS_REFERENCE_DATA = "BMS-Reference-Data";
    private static String TBL_BMS_DATA = "BMS-Data";
    private static String TBL_BMS_DATA_2 = "BMS-Data2";
    
    private MySQLManager mysqlClient;
    
	public DatabaseManagerSQL() {
		mysqlClient = new MySQLManager();
	}
	
	public Gateway getGateway(String gatewayId) {
		Gateway gw = null;
		List<HashMap<String, String>> gwlist = mysqlClient.selectByIdentifier(TBL_GATEWAY, "GatewayId", ""+gatewayId);
		List<Gateway> gateways = new LinkedList<Gateway>();
		for(HashMap<String, String> gateway: gwlist) {
			String id=gateway.get("GatewayId");
			String type = gateway.get("Type");
			gw = gatewayValidator(id, type);
			if (gw!=null) gateways.add(gw);
		}
		return gw;
	}
	
	public List<Gateway> getAllGateways(){
		List<Gateway> gateways = new LinkedList<Gateway>();
		List<HashMap<String, String>> gwlist = mysqlClient.selectAll(TBL_GATEWAY);
		for(HashMap<String, String> gateway: gwlist) {
			String id=gateway.get("GatewayId");
			String type = gateway.get("Type");
			Gateway gw = gatewayValidator(id, type);
			if (gw!=null) gateways.add(gw);
		}
		return gateways;
	}
	
	private Gateway gatewayValidator(String gatewayID, String gatewayType) {
		Gateway gw = null;
		try {
			long id = Long.parseLong(gatewayID);
			eGatewayType type = eGatewayType.valueOf(gatewayType);
			if (type!=null) 
				gw = new Gateway (id, type);
		} catch (Exception e) {
			GUIListenerFunctions.print("Incorrect Gateway Id found in the data base: " + gatewayID);
			//System.err.println("Incorrect Gateway Id found in the data base: " + gatewayID);
			gw=null;
		}
		return gw;
	}
	
	public void insertGateway(String gatewayId, long locationId, String type) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timestamp = sdf.format(calendar.getTime());
		
		String [] colarray = {"GatewayId", "Timestamp", "LocationId", "Type"};
		String [] valarray = {gatewayId, timestamp, ""+locationId, type};
		List<String> columns = Arrays.asList(colarray);
		List<String> values = Arrays.asList(valarray);
		
		try {
			mysqlClient.insert(TBL_GATEWAY, columns, values);
			GUIListenerFunctions.print("Gateway " + gatewayId + " inserted.");
		} catch (SQLException e) {
			GUIListenerFunctions.print("Gateway not inserted. Error: " + e.getMessage());
		}
    }
	
	public void updateGateway(String gatewayId, long locationId, String type) {
		/*TODO
		 * 
		 * Calendar calendar = Calendar.getInstance();
		 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timestamp = sdf.format(calendar.getTime());
		
		String [] colarray = {"GatewayId", "Timestamp", "LocationId", "Type"};
		String [] valarray = {gatewayId, timestamp, ""+locationId, type};
		List<String> columns = Arrays.asList(colarray);
		List<String> values = Arrays.asList(valarray);
		
		try {
			mysqlClient.insert(TBL_GATEWAY, columns, values);
			GUIListenerFunctions.print("Gateway " + gatewayId + " inserted.");
		} catch (SQLException e) {
			GUIListenerFunctions.print("Gateway not inserted. Error: " + e.getMessage());
		}
		
		*/
    }
	
	public Sensor getSensor(String sensorId) {
		Sensor s = null;
		List<HashMap<String, String>> slist = mysqlClient.selectByIdentifier(TBL_SENSOR, "SensorId", ""+sensorId);
		List<Sensor> sensors = new LinkedList<Sensor>();
		for(HashMap<String, String> sensor: slist) {
			String id=sensor.get("SensorId");
			String application = sensor.get("Application");
			String firmware = sensor.get("Firmware");
			s = sensorValidator(id, application, firmware);
			if (s!=null) sensors.add(s);
		}
		return s;
	}
	
	private Sensor sensorValidator(String sensorID, String application, String firmware) {
		Sensor s = null;
		try {
			long id = Long.parseLong(sensorID);
			eFirmwareGeneration firmwaregen;
			if(firmware!=null) {
				switch (firmware){
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
			}else firmwaregen= eFirmwareGeneration.Commercial;
			eSensorApplication SensorApplication;
			if(application.contains("+")) application = application.split("\\+")[0];
			SensorApplication = eSensorApplication.valueOf(application);
			
			s = new Sensor(id, SensorApplication, "2.3.0.0", firmwaregen);
		} catch (Exception e) {
			GUIListenerFunctions.print("Incorrect Sensor Id found in the data base: " + sensorID);
			e.printStackTrace();
			s=null;
		}
		return s;
	}
	
	public List<Sensor> getAllSensors(){
		List<Sensor> sensors= new LinkedList<Sensor>();
		List<HashMap<String, String>> slist = mysqlClient.selectAll(TBL_SENSOR);
		
		for(HashMap<String, String> sensor: slist) {
			String id=sensor.get("SensorId");
			String application = sensor.get("Application");
			String firmware = sensor.get("Firmware");
			Sensor s = sensorValidator(id, application, firmware);
			if (s!=null) sensors.add(s);
		}
		return sensors;
	}
	
	public List<Sensor> getGatewaySensors(String gatewayID){
		List<Sensor> sensors= new LinkedList<Sensor>();
		List<HashMap<String, String>> slist = mysqlClient.selectByIdentifier(TBL_SENSOR, "GatewayId", ""+gatewayID);
		
		for(HashMap<String, String> sensor: slist) {
			String id=sensor.get("SensorId");
			String application = sensor.get("Description");
			String firmware = sensor.get("Firmware");
			Sensor s = sensorValidator(id, application, firmware);
			if (s!=null) sensors.add(s);
		}
		return sensors;
	}
	
	
	public void deleteGateway(String gatewayId) {
		mysqlClient.delete(TBL_GATEWAY, "GatewayId", gatewayId);
		GUIListenerFunctions.print("Gateway " + gatewayId + " removed.");
	}
	
	public void insertSensor(String sensorId, String description, String gatewayId, long locationId, long objectId, String unit) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
		String timestamp = sdf.format(calendar.getTime());
		
		String [] colarray = {"SensorId", "Timestamp", "Description", "GatewayId", "LocationId", "ObjectId", "Unit"};
		String [] valarray = {sensorId, timestamp, description, "" + gatewayId, ""+locationId, ""+objectId, unit};
		List<String> columns = Arrays.asList(colarray);
		List<String> values = Arrays.asList(valarray);
		
		try {
			mysqlClient.insert(TBL_SENSOR, columns, values);
			GUIListenerFunctions.print("Sensor " + sensorId + " inserted.");
		} catch (SQLException e) {
			GUIListenerFunctions.print("Sensor not inserted. Error: " + e.getMessage());
		}
    }
	
	public void updateSensor(String sensorId, String description, String gatewayId, long locationId, long objectId, String unit) {
		/* TODO
		 * 
		 
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
		String timestamp = sdf.format(calendar.getTime());
		
		String [] colarray = {"SensorId", "Timestamp", "Description", "GatewayId", "LocationId", "ObjectId", "Unit"};
		String [] valarray = {sensorId, timestamp, description, "" + gatewayId, ""+locationId, ""+objectId, unit};
		List<String> columns = Arrays.asList(colarray);
		List<String> values = Arrays.asList(valarray);
		
		try {
			mysqlClient.insert(TBL_SENSOR, columns, values);
			GUIListenerFunctions.print("Sensor " + sensorId + " inserted.");
		} catch (SQLException e) {
			GUIListenerFunctions.print("Sensor not inserted. Error: " + e.getMessage());
		}
		*/
    }
	
	public void deleteSensor(String sensorId) {
		mysqlClient.delete(TBL_SENSOR, "SensorId", sensorId);
		GUIListenerFunctions.print("Sensor " + sensorId + " removed.");
	}
	
	public void insertReading(String monnit_sensor_id, String monnit_ts, String monnit_sensor_type, String monnit_signalstrength, String monnit_voltage, String monnit_value, String monnit_gw, 
			String dataconnector, String monnit_sensormgr_ts, String acp_location, String acp_object) {
		
		String [] colarray = {"monnit_sensor_id", "monnit_ts", "monnit_sensor_type", "monnit_signalstrength", "monnit_voltage", "monnit_value", "monnit_gw",
				"dataconnector", "monnit_sensormgr_ts", "acp_location", "acp_object"};
		String [] valarray = {monnit_sensor_id, monnit_ts, monnit_sensor_type, monnit_signalstrength, monnit_voltage, monnit_value, monnit_gw, dataconnector, monnit_sensormgr_ts, acp_location, acp_object};
		List<String> columns = Arrays.asList(colarray);
		List<String> values = Arrays.asList(valarray);
		
		try {
			mysqlClient.insert(TBL_READING, columns, values);
			//GUIListenerFunctions.print("Reading " + colarray + " inserted.");
			System.err.println("Reading " + colarray + " inserted.");
		} catch (SQLException e) {
			//GUIListenerFunctions.print("Reading not inserted. SQL Error: " + e.getMessage());
			System.err.println("Reading not inserted. SQL Error: " + e.getMessage());
		} catch (Exception e) {
			//GUIListenerFunctions.print("Reading not inserted. Error: " + e.getMessage());
			System.err.println("Reading not inserted. Error: " + e.getMessage());
		}
    }

	public long getLocationId(String locationName) {
		long location = -1;
		List<HashMap<String, String>> locationsList = mysqlClient.selectByIdentifier(TBL_LOCATION, "LocationName", locationName);
		if(locationsList.size()==1) {
			location = Long.parseLong(locationsList.get(0).get("LocationId"));
		}
		return location;
	}

	public long getObjectId(String objectName) {
		long object = -1;
		List<HashMap<String, String>> objectsList = mysqlClient.selectByIdentifier(TBL_OBJECT, "ObjectName", objectName);
		if(objectsList.size()==1) {
			object = Long.parseLong(objectsList.get(0).get("ObjectId"));
		}
		return object;
	}

	@Override
	public List<String> getAllObjectsName() {
		//TODO
		return null;
	}

	@Override
	public List<String> getAllLocationsName() {
		// TODO
		return null;
	}

	@Override
	public long getSensorLocation(String sensorId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getSensorObject(String sensorId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getSensorGateway(String sensorId) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
