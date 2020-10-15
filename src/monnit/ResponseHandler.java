package monnit;

import com.monnit.mine.MonnitAPN.MNTEventArgs.*;
import com.monnit.mine.MonnitMineAPI.*;

import ui.GUIListenerFunctions;

public class ResponseHandler implements MineServiceGatewayResponseAccess {

    @Override
    public void ProcessNetworkStatusMessage(NetworkStatusEventArgs args)
            throws Exception {

    }

    @Override
    public void ProcessSensorStatusIndicator(SensorStatusEventArgs args)
            throws Exception {

    }

    @Override
    public void ProcessReadDatasectorResponse(ReadConfigEventArgs args)
            throws Exception {

    }

    @Override
    public void ProcessWriteDatasectorResponse(WriteConfigEventArgs args)
            throws Exception {

    }

    @Override
    public void ProcessAPPCMDResponse(ActionCtrlEventArgs args)
            throws Exception {
        GUIListenerFunctions.print(args.getMsg());
    }

    @Override
    public void ProcessBootloaderResponse(BootloaderEventArgs args)
            throws Exception {

    }

    @Override
    public void ProcessParentMsg(ParentMessageEventArgs msg) throws Exception {

    }

    @Override
    public void ProcessDataMessage(DataMessageEventArgs msg) throws Exception {

    }

    @Override
    public void ProccessFindGateway(FindGatewayEventArgs paramFindGatewayEventArgs) throws Exception {

    }

}
