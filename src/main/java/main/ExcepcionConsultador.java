package main;

public class ExcepcionConsultador extends Throwable {

	@SuppressWarnings("unused")
	public ExcepcionConsultador(String message) {
		super(message);
	}

	@SuppressWarnings("unused")
	public ExcepcionConsultador(String message, Throwable throwable) {
		super(message, throwable);
	}

}