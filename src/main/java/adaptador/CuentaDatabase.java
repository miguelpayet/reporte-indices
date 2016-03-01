package adaptador;

public class CuentaDatabase {

	String host;
	String password;
	String service;
	String usuario;

	public CuentaDatabase() {
	}

	@SuppressWarnings("unused")
	public String getHost() {
		return host;
	}

	public String getPassword() {
		return password;
	}

	@SuppressWarnings("unused")
	public String getService() {
		return service;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setService(String service) {
		this.service = service;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
}
