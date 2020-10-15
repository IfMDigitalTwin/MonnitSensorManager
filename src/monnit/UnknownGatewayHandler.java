package monnit;

import com.monnit.mine.MonnitMineAPI.enums.eGatewayType;

import ui.GUIListenerFunctions;

import com.monnit.mine.MonnitMineAPI.iUnknownGatewayHandler;

public class UnknownGatewayHandler implements iUnknownGatewayHandler {

    @Override
    public void ProcessUnknownGateway(long GatewayID, eGatewayType type) {
        eGatewayType egatewaytype = type;
        try {
            GUIListenerFunctions.print("Unregistered Type:" + egatewaytype.toString() + ", GatewayID: " + GatewayID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ProcessUnknownGateway(int arg0) {
        // TODO Auto-generated method stub

    }
}
