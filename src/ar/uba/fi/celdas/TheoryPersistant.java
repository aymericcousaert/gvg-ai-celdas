package ar.uba.fi.celdas;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class TheoryPersistant {
	
	public static final String FILENAME = "theories.json";
	

	public static void save(Theories theories) throws JsonIOException, IOException{
				
		try (Writer writer = new FileWriter(FILENAME)) {
		    Gson gson = new GsonBuilder().setPrettyPrinting().create();
		    gson.toJson(theories, writer);		    
		}
	}

	public static Theories load() throws FileNotFoundException{
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new FileReader(FILENAME));
		
		Type listType = new TypeToken<Theories>(){}.getType();
		
		Theories theories = gson.fromJson(reader, listType); // contains the whole reviews list
		return theories;
	}

}
