package monnit;

import com.monnit.mine.MonnitMineAPI.Gateway;
import com.monnit.mine.MonnitMineAPI.iPersistGatewayHandler;

import io.ExternalDeviceStorageHandler;
import persistence.DatabaseManager;
import ui.GUIListenerFunctions;

public class PersistGatewayHandler implements iPersistGatewayHandler {
	private ExternalDeviceStorageHandler dataAccess;
	
	public PersistGatewayHandler(ExternalDeviceStorageHandler dataAccess){
		this.dataAccess = dataAccess;
	}

	@Override
	public void ProcessPersistGateway(Gateway gateway) {
		if(dataAccess!=null) {
			GUIListenerFunctions.print("Gateway " + gateway.GatewayID + " has been updated");
			dataAccess.UpdateGateway(gateway);
		}
	}
}