package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;

import javax.swing.*;

import persistence.DataPlatformManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainWindow {

    private static final int FEED_MAX_SIZE = 40;
	public static JFrame frame;
    public static JPanel panel; //Only thing directly inside the frame
    
    public static JPanel northPanel;
    public static JLabel ipLabel;
    public static JComboBox ipDropdown;
    public static String[] ipAddresses;
    public static JLabel protocolLabel;
    public static JComboBox protocolDropdown;
    public static String[] protocols;
    public static JLabel portLabel;
    public static JTextField portField;
    public static JLabel topicLabel;
    public static JTextField topicField;
    public static JButton startListenButton;
    public static JButton clearButton;
    
    public static JPanel middleEarthPanel;
    public static JPanel gatewayPanel;
    public static JLabel gatewayIDLabel;
    public static JTextField gatewayIDField;
    public static JLabel gatewayTypeLabel;
    public static String[] gatewayTypes;
    public static JComboBox gatewayTypeDropdown;
    public static JLabel gatewayLocationLabel;
    public static String[] locations;
    public static JComboBox gatewayLocationDropdown;
    public static JButton registerGatewayButton;
    public static JButton findGatewayButton;
    public static JButton removeGatewayButton;
    public static JButton reformGatewayButton;
    public static JButton point2iMonnitGatewayButton;
    public static JPanel sensorPanel;
    public static JLabel sensorIDLabel;
    public static JTextField sensorIDField;
    public static JLabel sensorAppLabel;
    public static String[] sensorApps;
    public static JComboBox sensorAppDropdown;
    public static JLabel sensorGenLabel;
    public static String[] sensorGens;
    public static JComboBox sensorGenDropdown;
    public static JLabel sensorLocationLabel;
    public static JComboBox sensorLocationDropdown;
    public static JLabel objectLabel;
    public static String[] objects;
    public static JComboBox objectDropdown;
    public static JButton registerSensorButton;
    public static JButton findSensorButton;
    public static JButton removeSensorButton;
    
    public static JButton fakeReadingButton;
    
    
    
    public static JScrollPane outputScroll;
    public static JTextArea outputArea;
    public static GridBagConstraints constraints;

    public static String version = "1.2";
    
    private static void create(List<Integer> options) {
        try {
            frame = new JFrame("Monnit Sensor Manager " + version);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //initializing early to prevent printing exceptions
            outputArea = new JTextArea();
            GUIListenerFunctions.OnLoad(options);
            
            panel = new JPanel(new GridBagLayout());
            constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            constraints.weightx = 1;
            constraints.weighty = 1;
            constraints.gridwidth = 10;
            frame.add(panel);

            createNorth();
            createMiddleEarth();
            
            outputArea.setEditable(false);
            outputArea.setLineWrap(true);
            
            outputScroll = new JScrollPane(outputArea);
            constraints.gridx = 0;
            constraints.gridy = 10;
            constraints.gridwidth = 10;
            constraints.fill = GridBagConstraints.BOTH;
            //constraints.gridheight = 50;
            constraints.ipady = 250;
            constraints.insets = new Insets(10, 10, 10, 10);
            panel.add(outputScroll, constraints);
            //outputScroll.setPreferredSize(outputAreaDimension);
            //panel.add(outputScroll);
            
            resetConstraints();
            //createFakeReadingButton();
            
            frame.pack();
            frame.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    
    private static void createFakeReadingButton() {
    	fakeReadingButton = new JButton("Fake Reading");            
        fakeReadingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUIListenerFunctions.fakeReading();
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 11;
        constraints.gridwidth = 2;
        constraints.gridheight = 2;
        constraints.insets = new Insets(10, 10, 10, 10);
        panel.add(fakeReadingButton, constraints);
    }
    
   	private static void createNorth() {
        try {
        	northPanel = new JPanel(new GridBagLayout());
	        constraints = new GridBagConstraints();
	        constraints.fill = GridBagConstraints.BOTH;
	        constraints.weightx = 1;
	        constraints.weighty = 1;
	        constraints.gridx = 0;
	        constraints.gridy = 0;
	        constraints.gridwidth = 10;
	        constraints.gridheight = 2;
	        constraints.insets = new Insets(10, 10, 10, 10);
	        //northPanel.setBorder(BorderFactory.createTitledBorder("Server"));
	        panel.add(northPanel, constraints);

	        resetConstraints();
	        
        	ipLabel = new JLabel("IP Address");
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.insets = new Insets(10, 10, 0, 10);
            northPanel.add(ipLabel, constraints);
            
            List<String> myIps = GUIListenerFunctions.getIPAddresses();
            ipDropdown = initiateComboBox(myIps.toArray(new String[myIps.size()]));
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 2;
            constraints.insets = new Insets(0, 10, 10, 10);
            northPanel.add(ipDropdown, constraints);
               
            protocolLabel = new JLabel("Protocol");
            constraints.gridx = 2;
            constraints.gridy = 0;
            constraints.gridwidth = 1;
            constraints.insets = new Insets(10, 10, 0, 10);
            northPanel.add(protocolLabel, constraints);
            
            //protocols = new String[]{"TCPAndUDP", "TCP", "UDP"};
            protocolDropdown = initiateComboBox(protocols);
            constraints.gridx = 2;
            constraints.gridy = 1;
            constraints.gridwidth = 2;
            constraints.insets = new Insets(0, 10, 10, 10);
            northPanel.add(protocolDropdown, constraints);

            portLabel = new JLabel("Monnit Port");
            constraints.gridx = 0;
            constraints.gridy = 2;
            constraints.gridwidth = 2;
            constraints.insets = new Insets(10, 10, 0, 10);
            northPanel.add(portLabel, constraints);
            
            portField = new JTextField("3000");
            constraints.gridx = 0;
            constraints.gridy = 3;
            constraints.gridwidth = 2;
            constraints.insets = new Insets(0, 10, 10, 10);
            northPanel.add(portField, constraints);
            
            topicLabel = new JLabel("MQTT Topic Prefix");
            constraints.gridx = 4;
            constraints.gridy = 2;
            constraints.gridwidth = 4;
            constraints.insets = new Insets(10, 10, 0, 10);
            northPanel.add(topicLabel, constraints);
            
            topicField = new JTextField(DataPlatformManager.MQTT_TOPIC);
            constraints.gridx = 4;
            constraints.gridy = 3;
            constraints.gridwidth = 6;
            constraints.insets = new Insets(0, 10, 10, 10);
            northPanel.add(topicField, constraints);

            startListenButton = new JButton("Start");            
            startListenButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                    	if(GUIListenerFunctions.isMonnitServerStarted())
                    		GUIListenerFunctions.stopButtonPressed();
                    	else GUIListenerFunctions.startButtonPressed();
                    } catch (Exception e1) {
                    	if(GUIListenerFunctions.isMonnitServerStarted())  GUIListenerFunctions.print("Something wrong while stopping the server");
                    	else GUIListenerFunctions.print("Something wrong while starting the server");
                    	e1.printStackTrace();
                    }
                }
            });
            constraints.gridx = 4;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            constraints.gridheight = 2;
            constraints.insets = new Insets(10, 10, 10, 10);
            northPanel.add(startListenButton, constraints);
            
            clearButton = new JButton("Clear Output");            
            clearButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clearOutput();
                }
            });
            constraints.gridx = 6;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            constraints.gridheight = 2;
            constraints.insets = new Insets(10, 10, 10, 10);
            northPanel.add(clearButton, constraints);
            
            resetConstraints();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
   	
   	private static void resetConstraints() {
   		// Reset used constraint values to Default
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, 0, 0, 0);
   	}
   	
    private static void createMiddleEarth() {
	    try {
    		middleEarthPanel = new JPanel(new GridBagLayout());
	        constraints = new GridBagConstraints();
	        //constraints.fill = GridBagConstraints.BOTH;
	        constraints.weightx = 1;
	        constraints.weighty = 1;
	        constraints.gridx = 0;
	        constraints.gridy = 2;
	        constraints.gridwidth = 10;
	        panel.add(middleEarthPanel,constraints);
	        
	        resetConstraints();
	        
	        gatewayPanel = new JPanel(new GridBagLayout());
	        constraints = new GridBagConstraints();
	        constraints.fill = GridBagConstraints.BOTH;
	        constraints.weightx = 1;
	        constraints.weighty = 1;
	        constraints.gridx = 0;
	        constraints.gridy = 0;
	        constraints.gridwidth = 5;
	        constraints.insets = new Insets(10, 10, 10, 10);
	        gatewayPanel.setBorder(BorderFactory.createTitledBorder("Gateway"));
	        middleEarthPanel.add(gatewayPanel,constraints);
	    	
	        resetConstraints();
	        
	        // GATEWAY INFORMATION
	        gatewayIDLabel = new JLabel("Gateway ID");
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 1;
            constraints.insets = new Insets(10, 10, 0, 10);
            gatewayPanel.add(gatewayIDLabel, constraints);
            
            gatewayIDField = new JTextField("");
            constraints.gridx = 1;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            constraints.insets = new Insets(10, 10, 10, 10);
            gatewayPanel.add(gatewayIDField, constraints);
            
            gatewayTypeLabel = new JLabel("Type");
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 1;
            constraints.insets = new Insets(10, 10, 0, 10);
            gatewayPanel.add(gatewayTypeLabel, constraints);
            
            //gatewayTypes = new String[]{"USB", "Ethernet", "CGW2", "CGW3"};
            gatewayTypeDropdown = initiateComboBox(gatewayTypes);
            gatewayTypeDropdown.setSelectedIndex(Arrays.asList(gatewayTypes).indexOf("Ethernet_3_0"));
            constraints.gridx = 1;
            constraints.gridy = 1;
            constraints.gridwidth = 2;
            constraints.insets = new Insets(10, 10, 10, 10);
            gatewayPanel.add(gatewayTypeDropdown, constraints);
            
            gatewayLocationLabel = new JLabel("Location");
            constraints.gridx = 0;
            constraints.gridy = 2;
            constraints.gridwidth = 1;
            constraints.insets = new Insets(10, 10, 0, 10);
            gatewayPanel.add(gatewayLocationLabel, constraints);
            
            
            gatewayLocationDropdown = initiateComboBox(locations);
            gatewayLocationDropdown.setSelectedIndex(Arrays.asList(locations).indexOf("DIAL"));
            constraints.gridx = 1;
            constraints.gridy = 2;
            constraints.gridwidth = 2;
            constraints.insets = new Insets(10, 10, 10, 10);
            gatewayPanel.add(gatewayLocationDropdown, constraints);
	        
	    	registerGatewayButton = new JButton("Register");            
	    	registerGatewayButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	try {
                        GUIListenerFunctions.registerGatewayButtonPressed();
                    } catch (Exception e1) {
                        GUIListenerFunctions.print(e1.toString());
                    }
	            }
	        });
	        constraints.gridx = 0;
	        constraints.gridy = 3;
	        constraints.gridwidth = 5;
	        constraints.gridheight = 1;
	        constraints.insets = new Insets(10, 10, 10, 10);
	        gatewayPanel.add(registerGatewayButton, constraints);
	        
	        findGatewayButton = new JButton("Find");            
	        findGatewayButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	try {
                        GUIListenerFunctions.findGatewayButtonPressed();
                    } catch (Exception e1) {
                        GUIListenerFunctions.print(e1.toString());
                    }
	            }
	        });
	        constraints.gridx = 0;
	        constraints.gridy = 4;
	        constraints.gridwidth = 5;
	        constraints.gridheight = 1;
	        constraints.insets = new Insets(10, 10, 10, 10);
	        gatewayPanel.add(findGatewayButton, constraints);
	        
	        removeGatewayButton = new JButton("Remove");            
	        removeGatewayButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	try {
                        GUIListenerFunctions.removeGatewayButtonPressed();
                    } catch (Exception e1) {
                        GUIListenerFunctions.print(e1.toString());
                    }
	            }
	        });
	        constraints.gridx = 0;
	        constraints.gridy = 5;
	        constraints.gridwidth = 5;
	        constraints.gridheight = 1;
	        constraints.insets = new Insets(10, 10, 10, 10);
	        gatewayPanel.add(removeGatewayButton, constraints);
	        
	        reformGatewayButton = new JButton("Reform");            
	        reformGatewayButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	try {
                        GUIListenerFunctions.reformGatewayButtonPressed();
                    } catch (Exception e1) {
                        GUIListenerFunctions.print(e1.toString());
                    }
	            }
	        });
	        constraints.gridx = 0;
	        constraints.gridy = 6;
	        constraints.gridwidth = 5;
	        constraints.gridheight = 1;
	        constraints.insets = new Insets(10, 10, 10, 10);
	        gatewayPanel.add(reformGatewayButton, constraints);
	        
	        point2iMonnitGatewayButton = new JButton("Point to iMonnit");            
	        point2iMonnitGatewayButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	try {
                        GUIListenerFunctions.point2iMonnitGatewayButtonPressed();
                    } catch (Exception e1) {
                        GUIListenerFunctions.print(e1.toString());
                    }
	            }
	        });
	        constraints.gridx = 0;
	        constraints.gridy = 7;
	        constraints.gridwidth = 5;
	        constraints.gridheight = 1;
	        constraints.insets = new Insets(10, 10, 10, 10);
	        gatewayPanel.add(point2iMonnitGatewayButton, constraints);
	        
	        resetConstraints();

	        
	        // SENSOR INFORMATION
	        sensorPanel = new JPanel(new GridBagLayout());
	        constraints = new GridBagConstraints();
	        constraints.fill = GridBagConstraints.BOTH;
	        constraints.weightx = 1;
	        constraints.weighty = 1;
	        constraints.gridx = 6;
	        constraints.gridy = 0;
	        constraints.gridwidth = 5;
	        constraints.insets = new Insets(10, 10, 10, 10);
	        sensorPanel.setBorder(BorderFactory.createTitledBorder("Sensor"));
	        middleEarthPanel.add(sensorPanel,constraints);
	    	
	        resetConstraints();
	        
	        sensorIDLabel = new JLabel("Sensor ID");
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 1;
            constraints.insets = new Insets(10, 10, 0, 10);
            sensorPanel.add(sensorIDLabel, constraints);
            
            sensorIDField = new JTextField("");
            constraints.gridx = 1;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            constraints.insets = new Insets(10, 10, 10, 10);
            sensorPanel.add(sensorIDField, constraints);
	        
            sensorAppLabel = new JLabel("Application");
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 1;
            constraints.insets = new Insets(10, 10, 0, 10);
            sensorPanel.add(sensorAppLabel, constraints);
	        
            sensorAppDropdown = initiateComboBox(sensorApps);
            sensorAppDropdown.setSelectedIndex(Arrays.asList(sensorApps).indexOf("Open_Closed"));
            constraints.gridx = 1;
            constraints.gridy = 1;
            constraints.gridwidth = 2;
            constraints.insets = new Insets(10, 10, 10, 10);
            sensorPanel.add(sensorAppDropdown, constraints);
            
            sensorGenLabel = new JLabel("Generation");
            constraints.gridx = 0;
            constraints.gridy = 2;
            constraints.gridwidth = 1;
            constraints.insets = new Insets(10, 10, 0, 10);
            sensorPanel.add(sensorGenLabel, constraints);
	        
        
            sensorGenDropdown = initiateComboBox(sensorGens);
            sensorGenDropdown.setSelectedIndex(Arrays.asList(sensorGens).indexOf("Commercial"));
            constraints.gridx = 1;
            constraints.gridy = 2;
            constraints.gridwidth = 2;
            constraints.insets = new Insets(10, 10, 10, 10);
            sensorPanel.add(sensorGenDropdown, constraints);
            
            sensorLocationLabel = new JLabel("Location");
            constraints.gridx = 0;
            constraints.gridy = 3;
            constraints.gridwidth = 1;
            constraints.insets = new Insets(10, 10, 0, 10);
            sensorPanel.add(sensorLocationLabel, constraints);
            
            sensorLocationDropdown = initiateComboBox(locations);
            sensorLocationDropdown.setSelectedIndex(Arrays.asList(locations).indexOf("DIAL"));
            constraints.gridx = 1;
            constraints.gridy = 3;
            constraints.gridwidth = 2;
            constraints.insets = new Insets(10, 10, 10, 10);
            sensorPanel.add(sensorLocationDropdown, constraints);
            
            objectLabel = new JLabel("Object");
            constraints.gridx = 0;
            constraints.gridy = 4;
            constraints.gridwidth = 1;
            constraints.insets = new Insets(10, 10, 0, 10);
            sensorPanel.add(objectLabel, constraints);
	        
            
            objectDropdown = initiateComboBox(objects);
            objectDropdown.setSelectedIndex(Arrays.asList(objects).indexOf("Environment"));
            constraints.gridx = 1;
            constraints.gridy = 4;
            constraints.gridwidth = 2;
            constraints.insets = new Insets(10, 10, 10, 10);
            sensorPanel.add(objectDropdown, constraints);
            
            registerSensorButton = new JButton("Register");            
	    	registerSensorButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	try {
                        GUIListenerFunctions.registerSensorButtonPressed();
                    } catch (Exception e1) {
                        GUIListenerFunctions.print(e1.toString());
                    }
	            }
	        });
	        constraints.gridx = 0;
	        constraints.gridy = 5;
	        constraints.gridwidth = 5;
	        constraints.gridheight = 1;
	        constraints.insets = new Insets(10, 10, 10, 10);
	        sensorPanel.add(registerSensorButton, constraints);
	        
	        findSensorButton = new JButton("Find");            
	        findSensorButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	try {
                        GUIListenerFunctions.findSensorButtonPressed();
                    } catch (Exception e1) {
                        GUIListenerFunctions.print(e1.toString());
                    }
	            }
	        });
	        constraints.gridx = 0;
	        constraints.gridy = 6;
	        constraints.gridwidth = 5;
	        constraints.gridheight = 1;
	        constraints.insets = new Insets(10, 10, 10, 10);
	        sensorPanel.add(findSensorButton, constraints);
	        
	        removeSensorButton = new JButton("Remove");            
	        removeSensorButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	try {
                        GUIListenerFunctions.removeSensorButtonPressed();
                    } catch (Exception e1) {
                        GUIListenerFunctions.print(e1.toString());
                    }
	            }
	        });
	        constraints.gridx = 0;
	        constraints.gridy = 7;
	        constraints.gridwidth = 5;
	        constraints.gridheight = 1;
	        constraints.insets = new Insets(10, 10, 10, 10);
	        sensorPanel.add(removeSensorButton, constraints);
            
	        resetConstraints();
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}

    private static JComboBox initiateComboBox(String[] strings) {
        return initiateComboBox(new JComboBox(), strings);
    }

    private static JComboBox initiateComboBox(JComboBox box, String[] strings) {
        for (String s : strings) {
            box.addItem(s);
        }
        return box;
    }

    public static String getOutput(String s) {
        int maxLines = 15;
        String currentOut = outputArea.getText();
        String[] lines = currentOut.split("\n");
        if (lines.length > maxLines) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < lines.length; i++) {
                sb.append(lines[i]);
                sb.append("\n");
            }
            currentOut = sb.toString();
        }
        currentOut += s + "\n";
        return currentOut;
    }

    public static void println(String s) {
        String [] data_array = outputArea.getText().split("\n");
        String data="";
        for(int i=1; i < Math.min(data_array.length, FEED_MAX_SIZE) ; i++) {
        	// data = data_array[data_array.length-i] + "\n" + data; //reverse
        	data += "\n" + data_array[data_array.length-i];
        }
    	data = data + "\n" + s;
    	// data = s + "\n" + data; //reverse
        outputArea.setText(data);
    }

    public static void clearOutput() {
        outputArea.setText("");
    }
    
    public static void main(String[] args) {
    	List<Integer> options = new ArrayList<Integer>();
    	//for(int i=0;i<args.length;i++) System.out.println(args[i]);
    	if(args.length > 0) {
    		for(int i=0;i<args.length;i++) {
    			switch(args[i]) {
    			case "--serverMode":
    				options.add(GUIListenerFunctions.MQTT_SERVER_MODE);
    				break;
    			case "--clientMode":
    				options.add(GUIListenerFunctions.MQTT_CLIENT_MODE);
    				break;
    			case "--encrypted":
    				options.add(GUIListenerFunctions.MQTT_ENCRYPTED);
    				break;
				default:
					GUIListenerFunctions.print("Wrong option: Available options --clientMode, --serverMode, --encrypted");
					GUIListenerFunctions.print("Running with: Client Mode and Unencrypted connection");
					options.add(GUIListenerFunctions.MQTT_CLIENT_MODE);
					options.add(GUIListenerFunctions.MQTT_UNENCRYPTED);
    				break;
    			}
    		}
    	}
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    create(options);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

}
