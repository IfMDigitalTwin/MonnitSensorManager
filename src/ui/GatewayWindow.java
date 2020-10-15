package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import com.monnit.mine.MonnitMineAPI.Gateway;
import com.monnit.mine.MonnitMineAPI.enums.eGatewayType;

import java.awt.*;

public class GatewayWindow {

    private JFrame frame;
    private JPanel panel; //Only thing directly inside the frame
    
    private JPanel informationPanel;
    private JLabel gatewayIDLabel;
    private JLabel gatewayTypeLabel;
    private JLabel gatewayFirmwareLabel;
    private JLabel gatewayRadioFirmwareLabel;
    
    
    private JPanel configurationPanel;
    
    private JLabel hostLabel;
    private JTextField hostField;
    private JLabel portLabel;
    private JTextField portField;
    private JLabel reportIntervalLabel;
    private JTextField reportIntervalField;
    
    private JPanel dhcpPanel;
    private JCheckBox dhcpCheckBox;
    private JLabel staticIPLabel;
    private JTextField staticIPField;
    private JLabel subnetLabel;
    private JTextField subnetField;
    private JLabel routerLabel;
    private JTextField routerField;
    private JLabel dnsLabel;
    private JTextField dnsField;
    private JLabel dns2Label;
    private JTextField dns2Field;
 
    private JButton UpdateGatewayButton;
    private JCheckBox pendingUpdateCheckBox;
    
    private GridBagConstraints constraints;

    private GatewayWindowListenerFunctions gatewayListener;
    
    public static String version = "0.1";
    
    private Gateway gateway;
    
    
    public GatewayWindow (Gateway gateway) {
    	super();
    	if(gateway==null) {
    		GUIListenerFunctions.print("Unknown Gateway");
    		dispose();
    	} else {
    		this.gateway = gateway;
    		this.gatewayListener = new GatewayWindowListenerFunctions(gateway, this);
        	create();
    	}
    }
    
    private void create() {
        try {
            frame = new JFrame("Gateway Info " + version);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            panel = new JPanel(new GridBagLayout());
            constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            constraints.weightx = 1;
            constraints.weighty = 1;
            constraints.gridwidth = 4;
            frame.add(panel);

            createInfo();
            createConfig();
            
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
	        //constraints.gridheight = 4;
	        constraints.insets = new Insets(10, 20, 20, 150);
	        panel.add(informationPanel, constraints);

	        resetConstraints();
	        
	        gatewayIDLabel = new JLabel("GatewayID: " + gateway.getGatewayID());
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 4;
            gatewayIDLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
            constraints.insets = new Insets(10, 10, 0, 10);
            informationPanel.add(gatewayIDLabel, constraints);

            gatewayTypeLabel = new JLabel(gateway.getGatewayType().name());
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 4;
            gatewayTypeLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
            constraints.insets = new Insets(10, 20, 0, 20);
            informationPanel.add(gatewayTypeLabel, constraints);
            
            gatewayFirmwareLabel = new JLabel("Gateway Firmware: " + gateway.getGatewayFirmwareVersion());
            constraints.gridx = 0;
            constraints.gridy = 2;
            constraints.gridwidth = 4;
            constraints.insets = new Insets(10, 20, 0, 20);
            informationPanel.add(gatewayFirmwareLabel, constraints);
            
            
            gatewayRadioFirmwareLabel = new JLabel("Radio Firmware: " + gateway.getRadioFirmwareVersion());
            constraints.gridx = 0;
            constraints.gridy = 3;
            constraints.gridwidth = 4;
            constraints.insets = new Insets(10, 20, 0, 20);
            informationPanel.add(gatewayRadioFirmwareLabel, constraints);
            
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
        constraints.fill = GridBagConstraints.BOTH;
   	}
   	
    private void createConfig() {
	    try {
    		configurationPanel = new JPanel(new GridBagLayout());
	        constraints = new GridBagConstraints();
	        constraints.fill = GridBagConstraints.BOTH;
	        constraints.weightx = 1;
	        constraints.weighty = 1;
	        constraints.gridx = 0;
	        constraints.gridy = 5;
	        constraints.gridwidth = 4;
	        constraints.insets = new Insets(10, 20, 20, 20);
	        configurationPanel.setBorder(BorderFactory.createTitledBorder("Configuration"));
	        panel.add(configurationPanel,constraints);
	        
	        resetConstraints();
	        
	        hostLabel = new JLabel("Host");
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 1;
            constraints.insets = new Insets(10, 10, 10, 10);
            configurationPanel.add(hostLabel, constraints);
            
            hostField = new JTextField(gateway.getServerHostAddress());            
            constraints.gridx = 1;
            constraints.gridy = 0;
            constraints.gridwidth = 3;
            constraints.insets = new Insets(10, 0, 10, 10);
            configurationPanel.add(hostField, constraints);
            
            portLabel = new JLabel("Port");
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 1;
            constraints.insets = new Insets(10, 10, 10, 10);
            configurationPanel.add(portLabel, constraints);
            
            portField = new JTextField(""+gateway.getPort());
            constraints.gridx = 1;
            constraints.gridy = 1;
            constraints.gridwidth = 3;
            constraints.insets = new Insets(10, 0, 10, 10);
            configurationPanel.add(portField, constraints);
	        
	        reportIntervalLabel = new JLabel("Report Interval (min)");
            constraints.gridx = 0;
            constraints.gridy = 2;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = new Insets(10, 10, 10, 10);
            configurationPanel.add(reportIntervalLabel, constraints);
            
            reportIntervalField = new JTextField(""+gateway.getReportInterval());
            reportIntervalField.setToolTipText("This is how often the gateway will pass data to the Server collecting sensor data");
            constraints.gridx = 1;
            constraints.gridy = 2;
            constraints.gridwidth = 3;
            constraints.insets = new Insets(10, 0, 10, 10);
            configurationPanel.add(reportIntervalField, constraints);
            
            dhcpOptions();
            
            UpdateGatewayButton = new JButton("Update");            
            UpdateGatewayButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	try {
                        gatewayListener.UpdateGatewayButtonPressed();
                    } catch (Exception e1) {
                        GUIListenerFunctions.print(e1.toString());
                    }
	            }
	        });
	        constraints.gridx = 0;
	        constraints.gridy = 10;
	        constraints.gridwidth = 3;
	        constraints.gridheight = 1;
	        constraints.insets = new Insets(10, 10, 10, 0);
	        configurationPanel.add(UpdateGatewayButton, constraints);
	        
	        constraints.fill = GridBagConstraints.NONE;
	        
	        pendingUpdateCheckBox = new JCheckBox("Pending");         
            pendingUpdateCheckBox.setEnabled(false);
            update();
	        constraints.gridx = 3;
	        constraints.gridy = 10;
	        constraints.gridwidth = 1;
	        constraints.gridheight = 1;
	        constraints.insets = new Insets(10, 0, 10, 0);
	        configurationPanel.add(pendingUpdateCheckBox, constraints);
           
	        resetConstraints();
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}
    
    public void update () {
    	pendingUpdateCheckBox.setSelected(!gateway.getIsDirty());
    	
    	hostField.setText(gateway.getServerHostAddress());
        portField.setText(""+gateway.getPort());
    	reportIntervalField.setText(""+gateway.getReportInterval());
    	staticIPField.setText(gateway.getGatewayIP());
        subnetField.setText(gateway.getGatewaySubnet());
        routerField.setText(gateway.getDefaultRouterIP());
        dnsField.setText(gateway.getGatewayDNS());
        dns2Field.setText(gateway.getSecondaryDNS());
    }
    
    private void showDHCPOptions() {
    	try {
			if (dhcpCheckBox.isSelected()){
	            dhcpPanel.setVisible(false);
	            gatewayListener.setUseDHCP(true);
	            staticIPField.setText("0.0.0.0");
	            subnetField.setText(gateway.getGatewaySubnet());
	            routerField.setText(gateway.getDefaultRouterIP());
	            dnsField.setText(gateway.getGatewayDNS());
	            dns2Field.setText(gateway.getSecondaryDNS());
	        } else {
	        	dhcpPanel.setVisible(true);
	        	gatewayListener.setUseDHCP(false);
	        }
			frame.pack();
		} catch (Exception e1) {
			GUIListenerFunctions.print(e1.toString());
		}
    }
	
    private void dhcpOptions () {
    	try {
    		dhcpCheckBox = new JCheckBox("Use DHCP");
            dhcpCheckBox.setEnabled(true);
            dhcpCheckBox.setSelected(gatewayListener.getUseDHCP());
            dhcpCheckBox.addActionListener(new ActionListener() {
            	@Override
            	public void actionPerformed(ActionEvent e) {
            		showDHCPOptions();
            	}
            });
	        constraints.gridx = 0;
	        constraints.gridy = 3;
	        constraints.gridwidth = 1;
	        constraints.gridheight = 1;
	        constraints.insets = new Insets(10, 0, 10, 0);
	        configurationPanel.add(dhcpCheckBox, constraints);
	        
    		dhcpPanel = new JPanel(new GridBagLayout());
	        constraints = new GridBagConstraints();
	        constraints.fill = GridBagConstraints.BOTH;
	        constraints.weightx = 1;
	        constraints.weighty = 1;
	        constraints.gridx = 0;
	        constraints.gridy = 4;
	        constraints.gridwidth = 4;
	        configurationPanel.add(dhcpPanel,constraints);
	        
	        resetConstraints();
	        
	        constraints.fill = GridBagConstraints.BOTH;
	        
	        staticIPLabel = new JLabel("Static IP");
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 1;
            constraints.insets = new Insets(10, 10, 10, 10);
            dhcpPanel.add(staticIPLabel, constraints);
            
            staticIPField = new JTextField(gateway.getGatewayIP());
            constraints.gridx = 1;
            constraints.gridy = 0;
            constraints.gridwidth = 3;
            constraints.insets = new Insets(10, 0, 10, 10);
            dhcpPanel.add(staticIPField, constraints);
            
            subnetLabel = new JLabel("Subnet");
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 1;
            constraints.insets = new Insets(10, 10, 10, 10);
            dhcpPanel.add(subnetLabel, constraints);
            
            subnetField = new JTextField(gateway.getGatewaySubnet());
            constraints.gridx = 1;
            constraints.gridy = 1;
            constraints.gridwidth = 3;
            constraints.insets = new Insets(10, 0, 10, 10);
            dhcpPanel.add(subnetField, constraints);
            
            routerLabel = new JLabel("Default Router");
            constraints.gridx = 0;
            constraints.gridy = 2;
            constraints.gridwidth = 1;
            constraints.insets = new Insets(10, 10, 10, 10);
            dhcpPanel.add(routerLabel, constraints);
            
            routerField = new JTextField(gateway.getDefaultRouterIP());
            constraints.gridx = 1;
            constraints.gridy = 2;
            constraints.gridwidth = 3;
            constraints.insets = new Insets(10, 0, 10, 10);
            dhcpPanel.add(routerField, constraints);
            
            dnsLabel = new JLabel("Primary DNS");
            constraints.gridx = 0;
            constraints.gridy = 3;
            constraints.gridwidth = 1;
            constraints.insets = new Insets(10, 10, 10, 10);
            dhcpPanel.add(dnsLabel, constraints);
            
            dnsField = new JTextField(gateway.getGatewayDNS());
            constraints.gridx = 1;
            constraints.gridy = 3;
            constraints.gridwidth = 3;
            constraints.insets = new Insets(10, 0, 10, 10);
            dhcpPanel.add(dnsField, constraints);
            
            dns2Label = new JLabel("Secondary DNS");
            constraints.gridx = 0;
            constraints.gridy = 4;
            constraints.gridwidth = 1;
            constraints.insets = new Insets(10, 10, 10, 10);
            dhcpPanel.add(dns2Label, constraints);
            
            dns2Field = new JTextField(gateway.getSecondaryDNS());
            constraints.gridx = 1;
            constraints.gridy = 4;
            constraints.gridwidth = 3;
            constraints.insets = new Insets(10, 0, 10, 10);
            dhcpPanel.add(dns2Field, constraints);

            showDHCPOptions();
            
	        resetConstraints();
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
    	dhcpCheckBox.setVisible(gateway.GatewayType == eGatewayType.Ethernet_3_0);
    	
    }
    
    
    public JTextField getStaticIPField() {
		return staticIPField;
	}

	public void setStaticIPField(JTextField staticIPField) {
		this.staticIPField = staticIPField;
	}

	public JTextField getSubnetField() {
		return subnetField;
	}

	public void setSubnetField(JTextField subnetField) {
		this.subnetField = subnetField;
	}

	public JTextField getRouterField() {
		return routerField;
	}

	public void setRouterField(JTextField routerField) {
		this.routerField = routerField;
	}

	public JTextField getDnsField() {
		return dnsField;
	}

	public void setDnsField(JTextField dnsField) {
		this.dnsField = dnsField;
	}

	public JTextField getDns2Field() {
		return dns2Field;
	}

	public void setDns2Field(JTextField dns2Field) {
		this.dns2Field = dns2Field;
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
    
    public JTextField getHostField() {
		return hostField;
	}

	public void setHostField(JTextField hostField) {
		this.hostField = hostField;
	}

	public JTextField getPortField() {
		return portField;
	}

	public void setPortField(JTextField portField) {
		this.portField = portField;
	}

	public JTextField getReportIntervalField() {
		return reportIntervalField;
	}

	public void setReportIntervalField(JTextField reportIntervalField) {
		this.reportIntervalField = reportIntervalField;
	}
    
}
