package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import com.monnit.mine.MonnitMineAPI.Sensor;

import java.awt.*;

public class SensorWindow {

    private JFrame frame;
    private JPanel panel; //Only thing directly inside the frame
    
    private JPanel informationPanel;
    private JLabel sensorIDLabel;
    private JLabel sensorAppGenLabel;
    
    
    private JPanel configuration1Panel;
    private JLabel reportIntervalLabel;
    private JTextField reportIntervalField;
    private JLabel linkIntervalLabel;
    private JTextField linkIntervalField;
    private JButton Update1Button;
    
    private JPanel configuration2Panel;
    private JLabel helpLabel;
    private JLabel recoveryLabel;
    private JTextField recoveryField;
    private JButton Update2Button;
    
    private GridBagConstraints constraints;
    
    private SensorWindowListenerFunctions sensorListener;

    public static String version = "0.1";
    
    private Sensor sensor;
    
    
    public SensorWindow (Sensor sensor) {
    	super();
    	if(sensor==null) {
    		dispose();
    		GUIListenerFunctions.print("Unknown Sensor");
    	}
    	else {
	    	this.sensor = sensor;
	    	this.sensorListener = new SensorWindowListenerFunctions(sensor, this);
	    	create();
    	}
    }
    private void create() {
        try {
            frame = new JFrame("Sensor Info " + version);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            panel = new JPanel(new GridBagLayout());
            constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            constraints.weightx = 1;
            constraints.weighty = 1;
            constraints.gridwidth = 4;
            frame.add(panel);

            createInfo();
            createConfig1();
            createConfig2();
            
            frame.pack();
            frame.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

   	private void createInfo() {
        try {
        	informationPanel = new JPanel(new GridBagLayout());
	        constraints = new GridBagConstraints();
	        constraints.fill = GridBagConstraints.BOTH;
	        constraints.weightx = 1;
	        constraints.weighty = 1;
	        constraints.gridx = 0;
	        constraints.gridy = 0;
	        constraints.gridwidth = 4;
	        constraints.gridheight = 2;
	        constraints.insets = new Insets(10, 20, 20, 150);
	        panel.add(informationPanel, constraints);

	        resetConstraints();
	        
	        sensorIDLabel = new JLabel("SensorID: " + sensor.getSensorID());
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 4;
            sensorIDLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
            constraints.insets = new Insets(10, 10, 0, 10);
            informationPanel.add(sensorIDLabel, constraints);

            sensorAppGenLabel = new JLabel(sensor.getMonnitApplication().name() + " - " + sensor.getGenerationType().name());
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 4;
            sensorAppGenLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
            constraints.insets = new Insets(10, 20, 0, 20);
            informationPanel.add(sensorAppGenLabel, constraints);
            
            resetConstraints();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
   	
   	private void resetConstraints() {
   		// Reset used constraint values to Default
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, 0, 0, 0);
   	}
   	
    private void createConfig1() {
	    try {
    		configuration1Panel = new JPanel(new GridBagLayout());
	        constraints = new GridBagConstraints();
	        constraints.fill = GridBagConstraints.BOTH;
	        constraints.weightx = 1;
	        constraints.weighty = 1;
	        constraints.gridx = 0;
	        constraints.gridy = 2;
	        constraints.gridwidth = 3;
	        constraints.insets = new Insets(10, 20, 20, 20);
	        configuration1Panel.setBorder(BorderFactory.createTitledBorder("Configuration 1"));
	        panel.add(configuration1Panel,constraints);
	        
	        resetConstraints();
	        
	        constraints.fill = GridBagConstraints.BOTH;
	        reportIntervalLabel = new JLabel("Report Interval (min)");
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(10, 10, 10, 10);
            configuration1Panel.add(reportIntervalLabel, constraints);
            
            reportIntervalField = new JTextField(""+sensor.getReportInterval());
            reportIntervalField.setToolTipText("Defines in minutes the interval at which the sensor communicates with the gateway. Accepted values: between 0.017 (1 second) and 720 (12 hours). If set to 0 (default) it will be set to 1 minute");
            constraints.gridx = 1;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            constraints.insets = new Insets(10, 0, 10, 10);
            configuration1Panel.add(reportIntervalField, constraints);
            
            linkIntervalLabel = new JLabel("Link Interval (min)");
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 1;
            constraints.insets = new Insets(10, 10, 10, 10);
            configuration1Panel.add(linkIntervalLabel, constraints);
            
            linkIntervalField = new JTextField(""+sensorListener.getTransmitIntervalLinkInMinutes());
            linkIntervalField.setToolTipText("How often (in minutes) the sensor attempts to re-link with a gateway once it goes into LINK MODE. If this is set to 0 (default) it will be setup to 120 min=2 hours");
            constraints.gridx = 1;
            constraints.gridy = 1;
            constraints.gridwidth = 2;
            constraints.insets = new Insets(10, 0, 10, 10);
            configuration1Panel.add(linkIntervalField, constraints);
            
            
            Update1Button = new JButton("Update");            
            Update1Button.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	try {
                        sensorListener.Update1SensorButtonPressed();
                    } catch (Exception e1) {
                        GUIListenerFunctions.print(e1.toString());
                    }
	            }
	        });
	        constraints.gridx = 0;
	        constraints.gridy = 2;
	        constraints.gridwidth = 3;
	        constraints.gridheight = 1;
	        constraints.insets = new Insets(10, 10, 10, 10);
	        configuration1Panel.add(Update1Button, constraints);
           
	        resetConstraints();
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}
    
    private void createConfig2() {
	    try {
    		configuration2Panel = new JPanel(new GridBagLayout());
	        constraints = new GridBagConstraints();
	        constraints.fill = GridBagConstraints.BOTH;
	        constraints.weightx = 1;
	        constraints.weighty = 1;
	        constraints.gridx = 0;
	        constraints.gridy = 3;
	        constraints.gridwidth = 3;
	        constraints.insets = new Insets(10, 20, 20, 20);
	        configuration2Panel.setBorder(BorderFactory.createTitledBorder("Configuration 2: Link Mode"));
	        panel.add(configuration2Panel,constraints);
	        
	        resetConstraints();
	        
	        helpLabel = new JLabel("What is LINK MODE?");
	        String helpText ="When the batteries are inserted in a sensor, the sensor tries all of the available channels to find a gateway. This is called \"Link Mode,\" where the sensor starts on the first channel and sends a message, \"I’m number 12345, can I talk to you?\"<br><br>"
	        		+ "If it doesn’t hear anything back it assumes there is not a gateway on that channel, and tries the second channel. After it has tried all of the channels and determines there is no available gateway, it stops sending messages for two hours.<br>"
	        		+ "Each scan-cycle takes about 30 seconds, but uses over a day’s worth of battery power. In other words, if it just continued to scan continually when the gateway wasn’t powered on, it would burn out very quickly. <br><br>"
	        		+ "After two hours it starts the process over again beginning with the first channel. Similarly, if for instance the sensor was talking to the gateway on channel 15, but has multiple failed transmissions (2 heartbeats each with a report, plus two retries, and six missed communications in a row) assumes the gateway it was communicating with is no-longer available and enters the same “Link Mode”.\n" 
	        		+ "This is important because if the battery is put in the sensor before the gateway is active and ready to listen for the sensor, the sensor will miss the gateway and not scan again for another two hours. <br><br>"
	        		+ "In order to get the sensor to scan again more quickly, you must remove the batteries for at least 60 seconds. This alows the processor to completely un-power. <br>"
	        		+ "Then put the batteries back in so it will start scanning right away. If the batteries are out only for a few seconds, the processor is still running off board capacitance and doesn’t re-boot when the batteries are put back in.<br>"
	        		+ "For the best start-up experience, make sure gateways are active and powered-on before you put the batteries in your sensors, so they can boot up and find the gateway on the first scan. If batteries are in the sensors before the gateway has gone active, then it will take two hours to join the network and start to report their readings.";
	        helpLabel.setToolTipText("<html><p width=\"800\">" + helpText + "</p></html>");
	        constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 3;
            constraints.insets = new Insets(10, 10, 0, 10);
            configuration2Panel.add(helpLabel, constraints);
	        
	        recoveryLabel = new JLabel("Recovery");
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 1;
            constraints.insets = new Insets(10, 10, 0, 10);
            configuration2Panel.add(recoveryLabel, constraints);
            
            recoveryField = new JTextField(""+sensor.getRecovery());
            recoveryField.setToolTipText("Number of messages sensor sends without acknowledgment from gateway before entering LINK MODE. Accepted values: between 0 (= infinite) and 10");
            constraints.gridx = 1;
            constraints.gridy = 1;
            constraints.gridwidth = 2;
            constraints.insets = new Insets(10, 0, 10, 10);
            configuration2Panel.add(recoveryField, constraints);
            
            Update2Button = new JButton("Update");            
            Update2Button.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	try {
                        sensorListener.Update2SensorButtonPressed();
                    } catch (Exception e1) {
                        GUIListenerFunctions.print(e1.toString());
                    }
	            }
	        });
	        constraints.gridx = 0;
	        constraints.gridy = 2;
	        constraints.gridwidth = 3;
	        constraints.gridheight = 1;
	        constraints.insets = new Insets(10, 10, 10, 10);
	        configuration2Panel.add(Update2Button, constraints);
           
	        resetConstraints();
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}
    
    public void update() {
    	reportIntervalField.setText(""+sensor.getReportInterval());
    	linkIntervalField.setText(""+sensorListener.getTransmitIntervalLinkInMinutes());
    	recoveryField.setText(""+sensor.getRecovery());
    }
	
    public void show() {
		frame.setVisible(true);
	}
    
    public void hide() {
    	frame.setVisible(false);
    }
    
    private void dispose () {
    	frame.dispose();
    }
	public JTextField getReportIntervalField() {
		return reportIntervalField;
	}
	public void setReportIntervalField(JTextField reportIntervalField) {
		this.reportIntervalField = reportIntervalField;
	}
	public JTextField getLinkIntervalField() {
		return linkIntervalField;
	}
	public void setLinkIntervalField(JTextField linkIntervalField) {
		this.linkIntervalField = linkIntervalField;
	}
	public JTextField getRecoveryField() {
		return recoveryField;
	}
	public void setRecoveryField(JTextField recoveryField) {
		this.recoveryField = recoveryField;
	}
    
    
}
