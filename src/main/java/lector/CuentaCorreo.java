package lector;

public class CuentaCorreo {

	String direccion;
	String password;
	String servidor;

	public CuentaCorreo(String direc, String passw, String servi) {
		this.direccion = direc;
		this.password = passw;
		this.servidor = servi;
	}

	public String getDireccion() {
		return direccion;
	}

	public String getPassword() {
		return password;
	}

	public String getServidor() {
		return servidor;
	}

	@SuppressWarnings("unused")
	public void info() throws LectorException {
		System.out.println("direccion: " + direccion);
		System.out.println("password: " + password);
		System.out.println("servidor: " + servidor);
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setServidor(String servidor) {
		this.servidor = servidor;
	}

}
