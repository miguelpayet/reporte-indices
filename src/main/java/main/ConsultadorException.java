package main;

public class ConsultadorException extends Exception {

	@SuppressWarnings("unused")
	public ConsultadorException(String message) {
		super(message);
	}

	@SuppressWarnings("unused")
	public ConsultadorException(String message, Throwable throwable) {
		super(message, throwable);
	}

}