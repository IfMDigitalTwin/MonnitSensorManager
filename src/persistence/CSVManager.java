package persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

public class CSVManager {

	public CSVManager() {
		// TODO Auto-generated constructor stub
	}
	
	public static List<String[]> readCSV(String source, String separator){
		BufferedReader br = null;
		String line = "";
		List<String[]> data=new LinkedList<String[]>();
        try {
        	br = new BufferedReader(new FileReader(source));
        	//skip the headers
        	br.readLine();
        	//read the rest of the lines
        	while ((line = br.readLine()) != null)
        		data.add(line.split(separator));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
	}

	
	public static void writeCSV(List<String[]> data, String destination, String separator){
		BufferedWriter bw = null;
		String line = "";
        try {
        	//bw = new BufferedWriter(new FileWriter(destination, "UTF-8"));
        	bw = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(destination), "UTF-8"));
        	for (int i=0; i<data.size(); i++) {
        		line="";
        		for(int j=0; j<data.get(i).length;j++) {
        			line+=data.get(i)[j];
        			if(j<data.get(i).length-1) line+=separator;
        		}
        		bw.write(line);
        		bw.newLine();
        	}
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}
}
