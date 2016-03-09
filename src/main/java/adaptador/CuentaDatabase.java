package adaptador;

import config.ConfiguracionException;
import excel.Excel;
import excel.ExcelException;
import main.Consultador;
import main.ConsultadorAplicacion;
import main.ExcepcionConsultador;

import java.util.ArrayList;
import java.util.List;

public class CuentaDatabase {

	private Consulta consulta;
	private Database db;
	private String host;
	private String password;
	private String service;
	private String usuario;

	public CuentaDatabase() throws ConfiguracionException {
	}

	public void ejecutarConsultas() {
		try {
			ArrayList<Thread> threads = new ArrayList<>();
			Excel excel = new Excel(ConsultadorAplicacion.getConfiguracion().leerExcel());
			db = new Database(this);
			db.connect();
			List<Condicion> condiciones = consulta.getCondiciones();
			try {
				for (Condicion cond : condiciones) {
					ConsultadorAplicacion.getLogger().info("hoja: " + cond.getHoja());
					Consultador lector = new Consultador(db.getConnection(), consulta.getQuery(), cond.getCondicion());
					lector.setExcel(excel.getCurrentSheet(cond.getHoja()));
					Thread thread = new Thread(lector);
					threads.add(thread);
					thread.start();
				}
				for (Thread thread : threads) {
					try {
						thread.join();
					} catch (InterruptedException e) {
						ConsultadorAplicacion.getLogger().info(String.format("Thread interrumpido: %s", e.getMessage()));
					}
				}
			} finally {
				ConsultadorAplicacion.getLogger().info("uniendo threads");
				ConsultadorAplicacion.getLogger().info("grabar excel");
				excel.grabar();
			}
		} catch (ConfiguracionException e) {
			throw new RuntimeException("Error al leer configuración en ejecutarConsultas");
		} catch (DatabaseException e) {
			ConsultadorAplicacion.getLogger().error("error al conectar a bd en ejecutarConsultas: " + e.getMessage());
		} catch (ExcelException e) {
			ConsultadorAplicacion.getLogger().error("error al grabar excel en ejecutarConsultas: " + e.getMessage());
		} catch (ExcepcionConsultador e) {
			ConsultadorAplicacion.getLogger().error("error en ejecutarConsultas: " + e.getMessage());
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	String getHost() {
		return host;
	}

	String getPassword() {
		return password;
	}

	String getService() {
		return service;
	}

	String getUsuario() {
		return usuario;
	}

	public void setConsulta(Consulta consulta) {
		this.consulta = consulta;
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
