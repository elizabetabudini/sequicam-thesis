package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

import model.AXISCamera;
import model.Camera;
import model.Position;
import model.SV3CCamera;
import model.Zone;

public class ConfigurationProperties {

    private static String fileProperties = "configCameras.properties";

    public static synchronized ArrayList<Camera> getProperties() throws NumberFormatException, IOException{
        ArrayList<Camera> list = new ArrayList<Camera>();
        Properties props = new Properties();
        String path = System.getProperty("user.home")+ System.getProperty("file.separator") + "Sequicam";
        try {
        	File file = new File(path + System.getProperty("file.separator") + fileProperties);
        	file.createNewFile();
			props.load(new FileInputStream(file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			//IOEx
		}
        
        JsonParser parser = new JsonParser();
        File f=new File(path + System.getProperty("file.separator") + fileProperties);
        if(f.length()!=0) {
        	JsonArray jsonArray = (JsonArray) parser.parse(new FileReader(path + System.getProperty("file.separator") + fileProperties));

            Camera cam= null;

    		if (jsonArray != null) {
    			int len = jsonArray.size();
    			for (int i = 0; i < len; i++) {
    				JsonObject ob=jsonArray.get(i).getAsJsonObject();
    				Boolean ptz=ob.get("ptz").getAsBoolean();
    				Position pos= new Position(ob.get("x").getAsDouble() ,ob.get("y").getAsDouble(),ob.get("z").getAsDouble() );
    				Zone zone = new Zone(ob.get("zone").getAsString(), ob.get("zoneid").getAsInt());
    				if(ptz) {
    					cam= new AXISCamera(ob.get("ip").getAsString(), zone, pos, ob.get("orientation").getAsDouble(), true );
    				} else {
    					cam= new SV3CCamera(ob.get("ip").getAsString(), zone, pos, ob.get("orientation").getAsDouble(), false );

    				}
    				list.add(cam);
    				
    			}
    		}
        }
        

		return list;
    }

    public static synchronized void setProperties(List<Camera> list) throws IOException {

    	
		JsonWriter  writer = new JsonWriter(new FileWriter(System.getProperty("user.home") + System.getProperty("file.separator") + fileProperties));
		writer.setIndent("  ");
		if(!list.isEmpty()) {
			writer.beginArray();
			for (Camera camera : list) {
				writer.beginObject();
				writer.name("ip").value(camera.getIP());
				writer.name("zone").value(camera.getZone().getName());
				writer.name("zoneid").value(camera.getZone().getId());
				writer.name("orientation").value(camera.getDiff());
				writer.name("ptz").value(camera.isPtz());
				writer.name("x").value(camera.getPos().getX());
				writer.name("y").value(camera.getPos().getY());
				writer.name("z").value(camera.getPos().getZ());
				writer.endObject();
			}
			writer.endArray();
			 writer.close();
		}
		

    	
//    	
//    	Properties properties = new Properties();
//        properties.load(new FileInputStream(System.getProperty("user.home") + "/" + fileProperties));
//        properties.setProperty(key[0], value[0]);
//        properties.setProperty(key[1], value[1]);
//        properties.setProperty(key[2], value[2]);
//        properties.setProperty(key[3], value[3]);
//        properties.store(new FileOutputStream(System.getProperty("user.home") + "/" + fileProperties, false), null);
    }
}