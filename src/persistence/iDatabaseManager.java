package persistence;

import java.util.List;

import com.monnit.mine.MonnitMineAPI.Gateway;
import com.monnit.mine.MonnitMineAPI.Sensor;

public interface iDatabaseManager {
	public static String DB_SQL = "SQL";
	public static String DB_JSON = "JSON";
	
	
	public Gateway getGateway(String gatewayId);
	
	public List<Gateway> getAllGateways();
	
//	private Gateway gatewayValidator(String gatewayID, String gatewayType);
	
	public void insertGateway(String gatewayId, long l, String type);
	
	public void updateGateway(String gatewayId, long locationId, String type);
	
	public Sensor getSensor(String sensorId);
	
//	private Sensor sensorValidator(String sensorID, String application, String firmware);
	
	public List<Sensor> getAllSensors();
	
	public List<Sensor> getGatewaySensors(String gatewayID);
	
	
	public void deleteGateway(String gatewayId);
	
	public void insertSensor(String sensorId, String description, String gatewayId, long locationId, long objectId, String unit);
	
	public void updateSensor(String sensorId, String description, String gatewayId, long locationId, long objectId, String unit) ;
	
	public void deleteSensor(String sensorId);
	
	public void insertReading(String sensorId, String timestamp, String description, String signalStrength, String value);

	public long getLocationId(String locationName) ;

	public long getObjectId(String objectName);
	
	public List<String> getAllObjectsName();
	
	public List<String> getAllLocationsName();
}
