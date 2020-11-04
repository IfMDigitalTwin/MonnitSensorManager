package monnit;

import java.util.List;

import com.monnit.mine.MonnitMineAPI.Gateway;
import com.monnit.mine.MonnitMineAPI.SensorMessage;
import com.monnit.mine.MonnitMineAPI.iSensorMessageHandler;

import ui.GUIListenerFunctions;

public class SensorMessageHandler implements iSensorMessageHandler {

    @Override
    public void ProcessSensorMessages(List<SensorMessage> sensorMessageList, Gateway gateway) throws Exception {
        GUIListenerFunctions.insertReadings(sensorMessageList);
    }
}