package io;

import java.util.List;
import com.monnit.mine.MonnitMineAPI.Gateway;
import com.monnit.mine.MonnitMineAPI.Sensor;
import com.monnit.mine.MonnitMineAPI.iExternalDeviceStorage;

import persistence.DataPlatformManager;
import persistence.iDatabaseManager;

import java.util.ArrayList;

public class ExternalDeviceStorageHandler implements iExternalDeviceStorage {
    
	private iDatabaseManager dbManager;
	private DataPlatformManager dataPlatformManager;
	
	public ExternalDeviceStorageHandler (iDatabaseManager _dbManager, DataPlatformManager dataPlatformManager) {
		this.dbManager = _dbManager;
		this.dataPlatformManager = dataPlatformManager;
	}
	
	
    public iDatabaseManager getDbManager() {
		return dbManager;
	}


	public void setDbManager(iDatabaseManager dbManager) {
		this.dbManager = dbManager;
	}


	public DataPlatformManager getDataPlatformManager() {
		return dataPlatformManager;
	}


	public void setDataPlatformManager(DataPlatformManager dataPlatformManager) {
		this.dataPlatformManager = dataPlatformManager;
	}


	@Override
    public Gateway FindGateway(long gatewayID) {
        return dbManager.getGateway(""+gatewayID);
    }

    @Override
    public Sensor FindSensor(long sensorID) {
        Sensor sensor = null;
        
        return sensor;
    }

    @Override
    public List<Sensor> FindSensorsByGateway(long gatewayID) {
        List<Sensor> gatewaySensors = new ArrayList<Sensor>();


        return gatewaySensors;
    }

    @Override
    public void LogException(Exception ex, String location) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public void UpdateGateway(Gateway gateway) {
    	//TODO: update gateway in db;
    	if(gateway != null) {
            long gatewayID = gateway.GatewayID;
        }
    }
    
    public void UpdateSensor(Sensor sensor) {
    	//TODO: update sensor in db;
    	if(sensor != null) {
            long sensorID = sensor.SensorID;
        }
    }
    
}