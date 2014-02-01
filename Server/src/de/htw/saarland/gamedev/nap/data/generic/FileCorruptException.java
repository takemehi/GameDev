package de.htw.saarland.gamedev.nap.data.generic;

import java.io.IOException;

public class FileCorruptException extends IOException {

	public FileCorruptException() {
	}

	public FileCorruptException(String msg) {
		super(msg);
	}

	public FileCorruptException(Throwable t) {
		super(t);
	}

	public FileCorruptException(String msg, Throwable t) {
		super(msg, t);
	}

}
