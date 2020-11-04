package monnit.exceptions;

import com.monnit.mine.MonnitMineAPI.iExceptionHandler;

import ui.GUIListenerFunctions;

public class ExceptionHandler implements iExceptionHandler {

    @Override
    public void LogException(Exception e) {
        //GUIListenerFunctions.print(e.toString());
    	System.out.println("Monnit exception "+ e.getMessage() + " -> ");
        e.printStackTrace();
    }
}