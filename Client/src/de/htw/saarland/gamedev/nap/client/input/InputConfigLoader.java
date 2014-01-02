package de.htw.saarland.gamedev.nap.client.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class InputConfigLoader {
	
	private File configFile;
	
	private int skill1Key;
	private int skill2Key;
	private int skill3Key;
	private int jumpKey;
	private int leftKey;
	private int rightKey;
	private int downKey;
	private int captureKey;
	
	public InputConfigLoader(File configFile) {
		if (configFile == null || !configFile.exists() || !configFile.isFile()) {
			throw new IllegalArgumentException();
		}
		
		this.configFile = configFile;
	}
	
	public void load() throws IOException, NumberFormatException {
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(configFile)));
			
			String in = br.readLine();
			leftKey = Integer.parseInt(in);
			
			in = br.readLine();
			rightKey = Integer.parseInt(in);
			
			in = br.readLine();
			downKey = Integer.parseInt(in);
			
			in = br.readLine();
			jumpKey = Integer.parseInt(in);
			
			in = br.readLine();
			captureKey = Integer.parseInt(in);
			
			in = br.readLine();
			skill1Key = Integer.parseInt(in);
			
			in = br.readLine();
			skill2Key = Integer.parseInt(in);
			
			in = br.readLine();
			skill3Key = Integer.parseInt(in);
		}
		catch (FileNotFoundException e) {
			//cannot happen checked before
		}
		finally {
			br.close();
		}
		
	}

	public int getSkill1Key() {
		return skill1Key;
	}

	public int getSkill2Key() {
		return skill2Key;
	}

	public int getSkill3Key() {
		return skill3Key;
	}

	public int getJumpKey() {
		return jumpKey;
	}

	public int getLeftKey() {
		return leftKey;
	}

	public int getRightKey() {
		return rightKey;
	}

	public int getDownKey() {
		return downKey;
	}

	public int getCaptureKey() {
		return captureKey;
	}
}
