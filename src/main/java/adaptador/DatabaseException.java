package adaptador;

public class DatabaseException extends Exception {

	@SuppressWarnings("unused")
	public DatabaseException(String message) {
		super(message);
	}

	@SuppressWarnings("unused")
	public DatabaseException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
