package adaptador;

public class Condicion {

	private String condicion;
	private String hoja;
	private int mes = 0;

	public Condicion() {
	}

	String getCondicion() {
		return condicion;
	}

	String getHoja() {
		return hoja;
	}

	public int getMes() {
		return mes;
	}

	public void setCondicion(String condicion) {
		this.condicion = condicion;
	}

	public void setHoja(String hoja) {
		this.hoja = hoja;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

}
