package adaptador;

public class Condicion {

	private String condicion;
	private String hoja;

	public Condicion(String unaHoja, String unaCondicion) {
		this.condicion = unaCondicion;
		this.hoja = unaHoja;
	}

	public String getCondicion() {
		return condicion;
	}

	public String getHoja() {
		return hoja;
	}

}
