package de.htw.saarland.gamedev.nap.data.generic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class KeyValueFile {
	private static final String COMMENT_BEGIN = "#";
	private static final String ASSIGNMENT = "=";
	
	private File file;
	private Map<String, String> keyValues;
	
	public KeyValueFile(String filename) throws FileNotFoundException {
		if (filename == null || filename.trim().length() == 0) {
			throw new IllegalArgumentException();
		}
		
		file = new File(filename);
		
		if (!file.exists()) {
			throw new FileNotFoundException(filename);
		}
		
		if (!file.canRead() || !file.isFile()) {
			throw new IllegalAccessError("File cannot be read or is not a valid file");
		}
		
		keyValues = new HashMap<>();
	}
	
	public void load() throws IOException {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			
			try {
				String line = null;
				
				while ((line = br.readLine()) != null) {
					if (!line.startsWith(COMMENT_BEGIN)) { // not a coment
						line = line.replace("\t", "").replace(" ", ""); //filter white spaces
						String[] splits = line.split(ASSIGNMENT);
						
						if (splits.length > 2) {
							throw new FileCorruptException();
						}
						else {
							keyValues.put(splits[0], splits[1]);
						}
					}
				}
			}
			finally {
				br.close();
			}
		} catch (FileNotFoundException e) {
			// catched before might not happen (only if the class is constructed, the file deleted and then load called
		}
	}
	
	public String getValue(String key) {
		return keyValues.get(key);
	}
	
	public int getValueInt(String key) {
		return Integer.parseInt(getValue(key));
	}
	
	public float getValueFloat(String key) {
		return Float.parseFloat(getValue(key));
	}
}
