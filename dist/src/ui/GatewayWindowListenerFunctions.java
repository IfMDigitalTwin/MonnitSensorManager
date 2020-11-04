package ui;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import com.monnit.mine.MonnitMineAPI.Gateway;
import com.monnit.mine.MonnitMineAPI.enums.eGatewayType;

public class GatewayWindowListenerFunctions {

    private Gateway gateway;
    private GatewayWindow gatewayWindow;
    private boolean useDHCP;
   
    public GatewayWindowListenerFunctions (Gateway gateway, GatewayWindow gatewayWindow) {
    	this.gateway = gateway;
    	this.gatewayWindow = gatewayWindow;
    	if (gateway != null && gateway.getGatewayType()==eGatewayType.Ethernet_3_0) useDHCP=true;
    }    
    

	public void UpdateGatewayButtonPressed() {
		String host = gatewayWindow.getHostField().getText();
        Integer port = 0;
        double reportInterval = 0.0;
        String WarningMessage = "";
        
        try {
        	InetAddress.getByName(host);
        	port = Integer.parseInt(gatewayWindow.getPortField().getText());
        } catch (UnknownHostException ue) {
        	GUIListenerFunctions.print("Invalid Host");
        	WarningMessage += "- Invalid Host\n";
        } catch(NumberFormatException nfe){
        	GUIListenerFunctions.print("Invalid port");
        	WarningMessage += "- Invalid Port\n";
        }
        
        try {
           	reportInterval = Double.parseDouble(gatewayWindow.getReportIntervalField().getText());
        } catch(NumberFormatException nfe) {
            GUIListenerFunctions.print("Invalid Report Interval");
            WarningMessage+="- Invalid Report Interval\n";
    	}
        
        
        String staticIP = gatewayWindow.getStaticIPField().getText();
        String subnet = gatewayWindow.getSubnetField().getText();
        String defaultRouter = gatewayWindow.getRouterField().getText();
        String primaryDNS = gatewayWindow.getDnsField().getText();
        String secondaryDNS = gatewayWindow.getDns2Field().getText();

        //Use DHCP
        if(!useDHCP) {
	        if (staticIP == "0.0.0.0"){
	            subnet = "0.0.0.0";
	            defaultRouter = "0.0.0.0";
	            primaryDNS = "0.0.0.0";
	            secondaryDNS = "0.0.0.0";
	        } else {
	        	try {
	        		InetAddress.getByName(staticIP);
	        	} catch (UnknownHostException ue) {
	            	GUIListenerFunctions.print("Invalid Static IP Address");
	            	WarningMessage += "- Invalid Static IP Address\n";
	        	}
	        	
	        	try {
	        		InetAddress.getByName(subnet);
	        	} catch (UnknownHostException ue) {
	            	GUIListenerFunctions.print("Invalid Subnet Mask");
	            	WarningMessage += "- Invalid Subnet Mask\n";
	        	}
	        	
	        	try {
	        		InetAddress.getByName(defaultRouter);
	        	} catch (UnknownHostException ue) {
	            	GUIListenerFunctions.print("Invalid Default Router");
	            	WarningMessage += "- Invalid Default Router\n";
	        	}
	        	
	        	try {
	        		InetAddress.getByName(primaryDNS);
	        	} catch (UnknownHostException ue) {
	            	GUIListenerFunctions.print("Invalid Primary DNS");
	            	WarningMessage += "- Invalid Primary DNS\n";
	        	}
	        	
	        	try {
	        		InetAddress.getByName(secondaryDNS);
	        	} catch (UnknownHostException ue) {
	            	GUIListenerFunctions.print("Invalid Primary DNS");
	            	WarningMessage += "- Invalid Secondary DNS\n";
	        	}
	        }
	        
        }
        
        if (WarningMessage.isEmpty()) {
        	gateway.UpdateServerInformation(host, port);
            gateway.UpdateReportInterval(reportInterval);
        	if(!useDHCP) gateway.UpdateGatewayIP(staticIP, subnet, defaultRouter, primaryDNS, secondaryDNS);
        	gatewayWindow.update();
        } else {
        	JOptionPane.showMessageDialog(null, "The Gateway " + gateway.getGatewayID() + " has not been updated due to the following errors:\n" + WarningMessage, "WARNING", JOptionPane.WARNING_MESSAGE);
        	WarningMessage="";
        };
        
        
    }


	public boolean getUseDHCP() {
		return useDHCP;
	}


	public void setUseDHCP(boolean useDHCP) {
		this.useDHCP = useDHCP;
	}

}
