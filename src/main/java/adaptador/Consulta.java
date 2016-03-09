package adaptador;

import java.util.List;

public class Consulta {

	private List<Condicion> condiciones;
	private String query;

	public Consulta(String unQuery, List<Condicion> unasCondiciones) {
		this.condiciones = unasCondiciones;
		this.query = unQuery;
	}

	public List<Condicion> getCondiciones() {
		return condiciones;
	}

	public String getQuery() {
		return query;
	}
}
