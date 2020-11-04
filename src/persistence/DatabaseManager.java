package persistence;

import java.util.List;

import com.monnit.mine.MonnitMineAPI.Gateway;
import com.monnit.mine.MonnitMineAPI.Sensor;

public class DatabaseManager implements iDatabaseManager {
	private iDatabaseManager _dbManager;
	
	public DatabaseManager (String dbtype) {
		switch (dbtype) {
			case iDatabaseManager.DB_JSON:
				_dbManager = new DatabaseManagerJSON();
				break;
			case iDatabaseManager.DB_SQL:
				_dbManager = new DatabaseManagerSQL();
				break;
			default:
				_dbManager = new DatabaseManagerJSON();
				break;
		}
	}

	@Override
	public Gateway getGateway(String gatewayId) {
		return _dbManager.getGateway(gatewayId);
	}

	@Override
	public List<Gateway> getAllGateways() {
		return _dbManager.getAllGateways();
	}

	@Override
	public void insertGateway(String gatewayId, long locationId, String type) {
		_dbManager.insertGateway(gatewayId, locationId, type);
	}

	@Override
	public void updateGateway(String gatewayId, long locationId, String type) {
		_dbManager.updateGateway(gatewayId, locationId, type);
	}

	@Override
	public Sensor getSensor(String sensorId) {
		return _dbManager.getSensor(sensorId);
	}

	@Override
	public List<Sensor> getAllSensors() {
		return _dbManager.getAllSensors();
	}

	@Override
	public List<Sensor> getGatewaySensors(String gatewayID) {
		return _dbManager.getGatewaySensors(gatewayID);
	}

	@Override
	public void deleteGateway(String gatewayId) {
		_dbManager.deleteGateway(gatewayId);
	}

	@Override
	public void insertSensor(String sensorId, String description, String gatewayId, long locationId, long objectId,
			String unit) {
		_dbManager.insertSensor(sensorId, description, gatewayId, locationId, objectId, unit);
	}

	@Override
	public void updateSensor(String sensorId, String description, String gatewayId, long locationId, long objectId,
			String unit) {
		_dbManager.updateSensor(sensorId, description, gatewayId, locationId, objectId, unit);
	}

	@Override
	public void deleteSensor(String sensorId) {
		_dbManager.deleteSensor(sensorId);
	}

	@Override
	public void insertReading(String sensorId, String msgtimestamp, String description, String signalStrength,
			String value, String arrived_to_DTSM) {
		_dbManager.insertReading(sensorId, msgtimestamp, description, signalStrength, value, arrived_to_DTSM);
	}

	@Override
	public long getLocationId(String locationName) {
		return _dbManager.getLocationId(locationName);
	}

	@Override
	public long getObjectId(String objectName) {
		return _dbManager.getObjectId(objectName);
	}
	
	@Override
	public List<String> getAllLocationsName(){
		return _dbManager.getAllLocationsName();
	}
	
	@Override
	public List<String> getAllObjectsName(){
		return _dbManager.getAllObjectsName();
	}
}
