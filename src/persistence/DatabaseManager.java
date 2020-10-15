package persistence;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.monnit.mine.MonnitMineAPI.enums.eSensorApplication;

import ui.GUIListenerFunctions;

public class DatabaseManager {
	private static String TBL_LOCATION = "Location";
	private static String TBL_OBJECT = "RTSD-Object";
	private static String TBL_GATEWAY = "RTSD-Gateway";
    private static String TBL_SENSOR = "RTSD-Sensor";
    private static String TBL_READING = "RTSD-Reading";
    private MySQLManager mysqlClient;
    
	public DatabaseManager() {
		mysqlClient = new MySQLManager();
	}
	
	public void insertGateway(String gatewayId, int locationId, String type) {
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
	
	public void updateGateway(String gatewayId, int locationId, String type) {
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
	
	public void deleteGateway(String gatewayId) {
		mysqlClient.delete(TBL_GATEWAY, "GatewayId", gatewayId);
		GUIListenerFunctions.print("Gateway " + gatewayId + " removed.");
	}
	
	public void insertSensor(String sensorId, String description, int gatewayId, int locationId, int objectId, String unit) {
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
	
	public void updateSensor(String sensorId, String description, int gatewayId, int locationId, int objectId, String unit) {
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
	
	public void insertReading(String sensorId, String timestamp, String description, String signalStrength, String value) {
		String [] colarray = {"SensorId", "Timestamp", "Description", "SignalStrength", "Value"};
		String [] valarray = {sensorId, timestamp, description, signalStrength, value};
		List<String> columns = Arrays.asList(colarray);
		List<String> values = Arrays.asList(valarray);
		
		try {
			mysqlClient.insert(TBL_READING, columns, values);
			GUIListenerFunctions.print("Reading " + colarray + " inserted.");
		} catch (SQLException e) {
			GUIListenerFunctions.print("Reading not inserted. Error: " + e.getMessage());
		}
    }

	public int getLocationId(String locationName) {
		int location = -1;
		List<HashMap<String, String>> locationsList = mysqlClient.selectByIdentifier(TBL_LOCATION, "LocationName", locationName);
		if(locationsList.size()==1) {
			location = Integer.parseInt(locationsList.get(0).get("LocationId"));
		}
		return location;
	}

	public int getObjectId(String objectName) {
		int object = -1;
		List<HashMap<String, String>> objectsList = mysqlClient.selectByIdentifier(TBL_OBJECT, "ObjectName", objectName);
		if(objectsList.size()==1) {
			object = Integer.parseInt(objectsList.get(0).get("ObjectId"));
		}
		return object;
	}
	

}
