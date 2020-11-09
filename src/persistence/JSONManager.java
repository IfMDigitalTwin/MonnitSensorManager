package persistence;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ui.GUIListenerFunctions;

public class JSONManager {

	public JSONManager() {
		// TODO Auto-generated constructor stub
	}
	
	public static JSONObject readJSON(String source) throws NullPointerException{
		JSONParser parser = new JSONParser();
		JSONObject data=null;
        try (Reader reader = new FileReader(source)) {
            data = (JSONObject) parser.parse(reader);
           // System.out.print(data);
        } catch (IOException e) {
            //e.printStackTrace();
        	GUIListenerFunctions.print("Database Configuration file not found in: " + source );
        } catch (ParseException e) {
            //e.printStackTrace();
        	GUIListenerFunctions.print("Database Configuration file contents wrong format. Please check: " + source );
        }
        if(data == null) throw new NullPointerException("Configuratio data could not be accessed");
        return data;
	}

	
	public static void writeJSON(JSONObject jsonobj, String destination){
        FileWriter file;
        try {
        	file = new FileWriter(destination);
            file.write(jsonobj.toJSONString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.print(mapjson);
        
       /**
        * method 2
        StringWriter out = new StringWriter();
        try {
        	mapjson.writeJSONString(out);
        } catch (IOException e1) {
        	e1.printStackTrace();
        }
  
        String jsonText = out.toString();
        System.out.print(jsonText);
		*/
	}
	
	
}
