package excel;

public class ExcelConfig {

	private String nombreArchivo;
	private String ruta;

	public ExcelConfig() {
	}

	public ExcelConfig(String unNombreArchivo) {
		this.nombreArchivo = unNombreArchivo;
		this.ruta = "";
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public String getRuta() {
		return ruta;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public void setRuta(String ruta) {
		this.ruta = ruta;
	}
}
