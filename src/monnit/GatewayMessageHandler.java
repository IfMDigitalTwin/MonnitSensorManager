package monnit;

import com.monnit.mine.MonnitMineAPI.GatewayMessage;
import com.monnit.mine.MonnitMineAPI.iGatewayMessageHandler;

import ui.GUIListenerFunctions;

public class GatewayMessageHandler implements iGatewayMessageHandler {

    @Override
    public void ProcessGatewayMessage(GatewayMessage gatewayMessage)
            throws Exception {
        //GUIListenerFunctions.print("____Gateway Message: " + gatewayMessage.toString());
    	System.out.println("____Gateway Message: " + gatewayMessage.toString());
    }
}