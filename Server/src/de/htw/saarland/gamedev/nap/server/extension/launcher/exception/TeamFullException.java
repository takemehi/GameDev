package de.htw.saarland.gamedev.nap.server.extension.launcher.exception;

public class TeamFullException extends RuntimeException {

	public TeamFullException() {
	}

	public TeamFullException(String arg0) {
		super(arg0);
	}

	public TeamFullException(Throwable arg0) {
		super(arg0);
	}

	public TeamFullException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public TeamFullException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
