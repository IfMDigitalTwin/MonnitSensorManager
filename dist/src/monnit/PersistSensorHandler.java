package monnit;

import com.monnit.mine.MonnitMineAPI.Sensor;
import com.monnit.mine.MonnitMineAPI.iPersistSensorHandler;

import io.ExternalDeviceStorageHandler;
import ui.GUIListenerFunctions;

public class PersistSensorHandler implements iPersistSensorHandler {

	private ExternalDeviceStorageHandler dataAccess;
	public PersistSensorHandler (ExternalDeviceStorageHandler dataAccess) {
		this.dataAccess = dataAccess;
	}
    @Override
    public void ProcessPersistSensor(Sensor sensor) {
        GUIListenerFunctions.print("Sensor " + sensor.SensorID + " has been updated.");
        dataAccess.UpdateSensor(sensor);
    }

}
