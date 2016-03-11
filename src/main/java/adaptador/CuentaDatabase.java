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

	@SuppressWarnings("ThrowFromFinallyBlock")
	public void ejecutarConsultas() {
		Excel excel = null;
		try {
			ArrayList<Thread> threads = new ArrayList<>();
			excel = new Excel(ConsultadorAplicacion.getConfiguracion().leerExcel());
			db = new Database(this);
			db.connect();
			List<Condicion> condiciones = consulta.getCondiciones();
			for (Condicion cond : condiciones) {
				ConsultadorAplicacion.getLogger().info("hoja: " + cond.getHoja());
				Consultador lector = new Consultador(db.getConnection(), consulta.getQuery(), cond.getCondicion());
				lector.setExcel(excel.getCurrentSheet(cond.getHoja()));
				if (ConsultadorAplicacion.isUsarHilos()) {
					Thread thread = new Thread(lector);
					threads.add(thread);
					thread.start();
				} else {
					lector.run();
				}
			}
			if (ConsultadorAplicacion.isUsarHilos()) {
				ConsultadorAplicacion.getLogger().info("uniendo threads");
				for (Thread thread : threads) {
					try {
						thread.join();
					} catch (InterruptedException e) {
						ConsultadorAplicacion.getLogger().info(String.format("thread interrumpido: %s", e.getMessage()));
					}
				}
			}
			if (!ConsultadorAplicacion.isUsarHilos()) {
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
		} finally {
			db.close();
			ConsultadorAplicacion.getLogger().info("grabar excel");
			try {
				if (excel != null) {
					excel.grabar();
				}
			} catch (ExcelException e) {
				ConsultadorAplicacion.getLogger().error("error excel en ejecutarConsultas: " + e.getMessage());
			}
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
