package excel;

public class ExcelConfig {

	private String nombreArchivo;
	private String nombreTab;
	private String ruta;

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public String getNombreTab() {
		return nombreTab;
	}

	public String getRuta() {
		return ruta;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public void setNombreTab(String nombreTab) {
		this.nombreTab = nombreTab;
	}

	public void setRuta(String ruta) {
		this.ruta = ruta;
	}
}
