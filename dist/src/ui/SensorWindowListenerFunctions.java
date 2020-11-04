package ui;

import javax.swing.JOptionPane;

import com.monnit.mine.MonnitMineAPI.Sensor;

public class SensorWindowListenerFunctions {

    private Sensor sensor;
    private SensorWindow sensorWindow;
    
    public SensorWindowListenerFunctions (Sensor sensor, SensorWindow sensorWindow) {
    	this.sensor = sensor;
    	this.sensorWindow = sensorWindow;

    }

	public void Update1SensorButtonPressed() {
		double ReportInterval = 0.0;
        int TransmitIntervalLink = 0;
        String WarningMessage = "";
        
        try {
        	ReportInterval = Double.parseDouble(sensorWindow.getReportIntervalField().getText());
        	if(ReportInterval <= 0.0) ReportInterval = 1.0;
        	else if (ReportInterval < 0.17) ReportInterval = 0.17;
        	else if (ReportInterval >= 720.0) ReportInterval = 720.0;
        } catch(NumberFormatException nfe){
        	GUIListenerFunctions.print("Invalid Report Interval");
        	WarningMessage += "- Invalid Report Interval\n";
        }
        
        try {
        	TransmitIntervalLink = Integer.parseInt(sensorWindow.getLinkIntervalField().getText());
        	if (TransmitIntervalLink <= 0) TransmitIntervalLink = 120;
        	else if(TransmitIntervalLink > 720) TransmitIntervalLink = 720;
        } catch(NumberFormatException nfe){
        	GUIListenerFunctions.print("Invalid Link Interval");
        	WarningMessage += "- Invalid Link Interval\n";
        }
        
        if(WarningMessage.isEmpty()) {
        	sensor.UpdateReportInterval(ReportInterval); //sensor.setReportInterval(ReportInterval);
        	sensor.setTransmitIntervalLink(TransmitIntervalLink);
        } else {
        	JOptionPane.showMessageDialog(null, "The Sensor " + sensor.getSensorID() + " has not been updated due to the following errors:\n" + WarningMessage, "WARNING", JOptionPane.WARNING_MESSAGE);
        	WarningMessage="";
        }
        sensorWindow.update();
        
	}

	public void Update2SensorButtonPressed() {
		int Recovery = 0;
        String WarningMessage = "";
        
        try {
        	Recovery = Integer.parseInt(sensorWindow.getRecoveryField().getText());
        } catch(NumberFormatException nfe){
        	GUIListenerFunctions.print("Invalid Recovery times");
        	WarningMessage += "- Invalid Recovery times\n";
        }
        
        if(WarningMessage.isEmpty()) {
        	sensor.setRecovery(Recovery);
        } else {
        	String helpMessage = "\nRemember: Number of messages sensor sends without acknowledgment from gateway before entering LINK MODE. Accepted values: between 0 (= infinite) and 10";
        	JOptionPane.showMessageDialog(null, "The Sensor " + sensor.getSensorID() + " has not been updated due to the following errors:\n" + WarningMessage + helpMessage, "WARNING", JOptionPane.WARNING_MESSAGE);
        	WarningMessage="";
        }
        sensorWindow.update();
	}
	
	/***
	 * How often (in minutes) the sensor attempts to re-link with a gateway once it goes into link mode.
	 * It transforms the original transmitIntervalLink value to minutes.
	 * TransmitIntevalLink Values below 12 are interpreted as hours. Values above 12 are interpreted as minutes. The max value allowed in this field is 720. if a value of 0 is given the sensor will default to a re-link interval of 2 hours
	 * @param transmitIntervalLink
	 * @return transmitIntervalLink in minutes
	 */
	public int getTransmitIntervalLinkInMinutes() {
		int tilmin=sensor.getTransmitIntervalLink();
		if (tilmin < 12) tilmin *=60;
		return tilmin;
	}

}
