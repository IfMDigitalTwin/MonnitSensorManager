package persistence;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONObject;

import com.monnit.mine.MonnitMineAPI.Gateway;
import com.monnit.mine.MonnitMineAPI.Sensor;
import com.monnit.mine.MonnitMineAPI.enums.eFirmwareGeneration;
import com.monnit.mine.MonnitMineAPI.enums.eGatewayType;
import com.monnit.mine.MonnitMineAPI.enums.eSensorApplication;


import ui.GUIListenerFunctions;
import ui.MainWindow;

public class DatabaseManagerJSON implements iDatabaseManager{
	//private JSONManager jsonmgr;
	private final String PATH = "./dist/db/";
	private final String GATEWAYS_DB = "gateways.json";
	private final String SENSORS_DB = "sensors.json";
	private final String LOCATIONS_DB = "locations.json";
	private final String OBJECTS_DB = "objects.json";
    
	public DatabaseManagerJSON() {
		//jsonmgr = new JSONManager();
	}
	
	private JSONObject readGatewaysJSON() {
		return JSONManager.readJSON(PATH+GATEWAYS_DB);
	}
	
	private void saveGatewayChanges (JSONObject gateways) {
		JSONManager.writeJSON(gateways, PATH+GATEWAYS_DB);
	}
	
	private JSONObject readSensorsJSON() {
		return JSONManager.readJSON(PATH+SENSORS_DB);
	}
	
	private void saveSensorChanges (JSONObject sensors) {
		JSONManager.writeJSON(sensors, PATH+SENSORS_DB);
	}
	
	private JSONObject readLocationsJSON() {
		return JSONManager.readJSON(PATH+LOCATIONS_DB);
	}
	
	private void saveLocationsChanges (JSONObject locations) {
		JSONManager.writeJSON(locations, PATH+LOCATIONS_DB);
	}
	
	private JSONObject readObjectsJSON() {
		return JSONManager.readJSON(PATH+OBJECTS_DB);
	}
	
	private void saveObjectsChanges (JSONObject objects) {
		JSONManager.writeJSON(objects, PATH+OBJECTS_DB);
	}
	
	public Gateway getGateway(String gatewayId) {
		Gateway gw = null;
		JSONObject gateways = readGatewaysJSON();
		JSONObject gateway = (JSONObject) gateways.get(gatewayId);
		
		if(gateway!=null) {
			// inside the gateway
			String id = (String) gateway.get("gatewayId");
			String type = (String) gateway.get("type");
			gw = gatewayValidator(id, type);
		}
		return gw;
	}
	
	public List<Gateway> getAllGateways(){
		List<Gateway> gatewaysList = new LinkedList<Gateway>();
		Gateway gw = null;
		JSONObject gateways = readGatewaysJSON();
		for(Iterator iterator = gateways.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			JSONObject gateway = (JSONObject) gateways.get(key);
			// inside the gateway
			String id = (String) gateway.get("gatewayId");
			String type = (String) gateway.get("type");
			gw = gatewayValidator(id, type);
			if (gw!=null) gatewaysList.add(gw);
		}
		return gatewaysList;
	}
	
	private Gateway gatewayValidator(String gatewayId, String gatewayType) {
		Gateway gw = null;
		try {
			long gwid = Long.parseLong(gatewayId);
			eGatewayType type = eGatewayType.valueOf(gatewayType);
			if (type!=null) 
				gw = new Gateway (gwid, type);
		} catch (Exception e) {
			GUIListenerFunctions.print("Incorrect Gateway Id found in the data base: " + gatewayId);
			gw=null;
		}
		return gw;
	}
	
	@SuppressWarnings("unchecked")
	public void insertGateway(String gatewayId, long locationId, String type) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datetime = sdf.format(calendar.getTime());
		Timestamp ts = new Timestamp(calendar.getTimeInMillis());
		String timestamp = ts.toInstant().toString();
		JSONObject gateways = readGatewaysJSON();
		JSONObject gw = (JSONObject) gateways.get(gatewayId);		
		
		HashMap<String,Object> gwmap;
		if(gw==null) {
			gwmap = new HashMap<String,Object>();
		}
		else {
			gwmap = (HashMap<String, Object>) gw.clone();
		}	
		gwmap.put("gatewayId", gatewayId);
		gwmap.put("last_update", timestamp);
		gwmap.put("locationId", locationId);
		gwmap.put("type", type);
		
		gateways.put(gatewayId, gwmap);
		saveGatewayChanges(gateways);
		GUIListenerFunctions.print("[DB JSON:: Gateway "+ gatewayId +" inserted]");
    }
	
	public void updateGateway(String gatewayId, long locationId, String type) {
		insertGateway(gatewayId, locationId, type);
    }	
	
	public void deleteGateway(String gatewayId) {
		JSONObject gateways = readGatewaysJSON();
		gateways.remove(gatewayId);
		
		saveGatewayChanges(gateways);
	}
	
	public Sensor getSensor(String sensorId) {
		Sensor s = null;
		JSONObject sensors = readSensorsJSON();
		JSONObject sensor = (JSONObject) sensors.get(sensorId);
		
		if (sensor!=null) {
			// inside the sensor
			String id=(String) sensor.get("sensorId");
			String application = (String) sensor.get("application");
			String firmware = (String) sensor.get("firmware");
			s = sensorValidator(id, application, firmware);
		}
		return s;
	}
	
	public List<Sensor> getAllSensors(){
		List<Sensor> sensorsList = new LinkedList<Sensor>();
		JSONObject sensors = readSensorsJSON();
		Sensor s;
		for(Iterator iterator = sensors.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			JSONObject sensor = (JSONObject) sensors.get(key);
			// inside the sensor
			String id=(String) sensor.get("sensorId");
			String application = (String) sensor.get("application");
			String firmware = (String) sensor.get("firmware");
			s = sensorValidator(id, application, firmware);
			if (s!=null) sensorsList.add(s);
		}
		return sensorsList;
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
	
	public void insertSensor(String sensorId, String description, String gatewayId, long locationId, long objectId, String unit) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datetime = sdf.format(calendar.getTime());
		Timestamp ts = new Timestamp(calendar.getTimeInMillis());
		String timestamp = ts.toInstant().toString();
		JSONObject sensors = readSensorsJSON();
		JSONObject s = (JSONObject) sensors.get(sensorId);		
		
		HashMap<String,Object> smap;
		if(s==null) {
			smap = new HashMap<String,Object>();
		}
		else {
			smap = (HashMap<String, Object>) s.clone();
		}	
		smap.put("sensorId", sensorId);
		smap.put("last_update", timestamp);
		smap.put("description", description);
		smap.put("gatewayId", gatewayId);
		smap.put("locationId", locationId);
		smap.put("ObjectId", objectId);
		smap.put("unit", unit);

		
		sensors.put(sensorId, smap);
		saveSensorChanges(sensors);
		GUIListenerFunctions.print("[DB JSON:: Sensor "+ sensorId +" inserted]");
    }
	
	public void updateSensor(String sensorId, String description, String gatewayId, long locationId, long objectId, String unit) {
		this.insertSensor(sensorId, description, gatewayId, locationId, objectId, unit);
    }
	
	public void deleteSensor(String sensorId) {
		JSONObject sensors = readSensorsJSON();
		sensors.remove(sensorId);

		saveSensorChanges(sensors);	
	}
	
	public List<Sensor> getGatewaySensors(String gatewayID){
		List<Sensor> sensorsList = new LinkedList<Sensor>();
		JSONObject sensors = readSensorsJSON();
		
		for(Iterator iterator = sensors.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			JSONObject sensor = (JSONObject) sensors.get(key);
			// inside the sensor
			String gwid = (String) sensor.get("gatewayId");
			if (gwid.equals(gatewayID)) {
				String id=(String) sensor.get("sensorId");
				String application = (String) sensor.get("description");
				String firmware = (String) sensor.get("firmware");
				Sensor s = sensorValidator(id, application, firmware);
				if (s!=null) sensorsList.add(s);	
			}
		}
		return sensorsList;
	}
	
	public long getLocationId(String locationName) {
		long location = -1;
		JSONObject locations = readLocationsJSON();
		for(Iterator iterator = locations.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			JSONObject loc = (JSONObject) locations.get(key);
			// inside the location
			String locname = (String) loc.get("locationName");
			if (locname.equals(locationName)) {
				if(location==-1) {
					try {
						location = (long) loc.get("locationId");
					} catch(Exception e){
						System.err.println("Invalid ID for Location " + locationName + "/" + locname + " in the JSON DB: " + location + " >>> " + loc.toJSONString());
					}
				} else {
					System.err.println("Multiple IDs with the same locationName (" + locationName+ "/" + locname + ") in the JSON DB: " + location);
				}
			}
		}
		return location;
	}
	
	public List<String> getAllLocationsName(){
		List<String> locationsList = new LinkedList<String>();
		JSONObject locations = readLocationsJSON();
		for(Iterator iterator = locations.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			JSONObject location = (JSONObject) locations.get(key);
			// inside the location
			long id=(long) location.get("locationId");
			String locationName = (String) location.get("locationName");
			locationsList.add(locationName);
		}
		return locationsList;
	}

	public long getObjectId(String objectName) {
		long object = -1;
		JSONObject objects = readObjectsJSON();
		for(Iterator iterator = objects.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			JSONObject obj = (JSONObject) objects.get(key);
			// inside the objects
			String objname = (String) obj.get("objectName");
			if (objname.equals(objectName)) {
				if(object==-1) {
					try {
						object = (long) obj.get("objectId");
					} catch(Exception e){
						System.err.println("Invalid ID for Object " + objectName + " in the JSON DB: " + object);
					}
				} else {
					System.err.println("Multiple IDs with the same objectName (" + objectName + ") in the JSON DB: " + object);
				}
			}
		}
		return object;
	}
	
	public List<String> getAllObjectsName(){
		List<String> objectsList = new LinkedList<String>();
		JSONObject objects = readObjectsJSON();
		for(Iterator iterator = objects.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			JSONObject object = (JSONObject) objects.get(key);
			// inside the object
			long id=(long) object.get("objectId");
			String objectName = (String) object.get("objectName");
			objectsList.add(objectName);
		}
		return objectsList;
	}

	@Override
	public void insertReading(String sensorId, String timestamp, String description, String signalStrength,
			String value) {
		// TODO Auto-generated method stub
		// NOT STORED IN LOCAL JSON DB
	}
	

}
