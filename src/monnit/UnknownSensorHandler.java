package monnit;

import com.monnit.mine.MonnitMineAPI.iUnknownSensorHandler;

import ui.GUIListenerFunctions;

public class UnknownSensorHandler implements iUnknownSensorHandler {

    @Override
    public void ProcessUnknownSensor(long SensorID) {
//        eSensorApplication esensortype = type;
        try {
//            GUIListenerFunctions.print("Unregistered Type:" + esensortype.toString() + ", SensorID: " + SensorID);
            GUIListenerFunctions.print("Unregistered SensorID: " + SensorID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}