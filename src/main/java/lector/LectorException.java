package lector;

public class LectorException extends Throwable {

	@SuppressWarnings("unused")
	public LectorException(String message) {
		super(message);
	}

	public LectorException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
