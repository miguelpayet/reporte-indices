package adaptador;

import config.ConfiguracionException;
import excel.Excel;
import excel.ExcelException;
import main.Consultador;
import main.ConsultadorAplicacion;
import main.ExcepcionConsultador;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class CuentaDatabase {

	private Consulta consulta;
	private Database db;
	private Excel excel = null;
	private String host;
	private String password;
	private String service;
	private String usuario;

	public CuentaDatabase() throws ConfiguracionException {
	}

	@SuppressWarnings("ThrowFromFinallyBlock")
	public void ejecutarConsultas() {
		try {
			ArrayList<Thread> hilos = new ArrayList<>();
			excel = new Excel(ConsultadorAplicacion.getConfiguracion().leerExcel());
			db = new Database(this);
			db.connect();
			List<Condicion> condiciones = consulta.getCondiciones();
			for (Condicion cond : condiciones) {
				ConsultadorAplicacion.getLogger().info("hoja: " + cond.getHoja());
				if (cond.getMes() != 0) {
					List<Condicion> condicionesMes = generarCondicionesMes(cond);
					for (Condicion c : condicionesMes) {
						ejecutarUnaConsulta(hilos, c);
					}
				} else {
					ejecutarUnaConsulta(hilos, cond);
				}
			}
			if (ConsultadorAplicacion.isUsarHilos()) {
				ConsultadorAplicacion.getLogger().info("uniendo threads");
				for (Thread thread : hilos) {
					try {
						thread.join();
					} catch (InterruptedException e) {
						ConsultadorAplicacion.getLogger().info(String.format("thread interrumpido: %s", e.getMessage()));
					}
				}
			}
		} catch (ConfiguracionException e) {
			throw new RuntimeException("Error al leer configuración en ejecutarConsultas");
		} catch (DatabaseException e) {
			ConsultadorAplicacion.getLogger().error("error al conectar a bd en ejecutarConsultas: " + e.getMessage());
		} catch (ExcepcionConsultador e) {
			ConsultadorAplicacion.getLogger().error("error al ejecutar consulta en ejecutarConsultas: " + e.getMessage());
		} catch (ExcelException e) {
			ConsultadorAplicacion.getLogger().error("error al grabar excel en ejecutarConsultas: " + e.getMessage());
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

	void ejecutarUnaConsulta(ArrayList<Thread> hilos, Condicion cond) throws DatabaseException, ExcepcionConsultador {
		Consultador lector = new Consultador(db.getConnection(), consulta.getQuery(), cond.getCondicion());
		lector.setExcel(excel.getCurrentSheet(cond.getHoja()));
		if (ConsultadorAplicacion.isUsarHilos()) {
			Thread thread = new Thread(lector);
			hilos.add(thread);
			thread.start();
		} else {
			lector.run();
		}
	}

	List<Condicion> generarCondicionesMes(Condicion unaCondicion) {
		List<Condicion> listaCondiciones = new ArrayList<>();
		YearMonth mes = YearMonth.of(2015, unaCondicion.getMes());
		for (int dia = 1; dia <= mes.lengthOfMonth(); dia++) {
			Condicion cond = new Condicion();
			cond.setHoja(unaCondicion.getHoja());
			String numeroDia = String.format("%2s", dia).replace(" ", "0");
			String numeroMes = String.format("%2s", unaCondicion.getMes()).replace(" ", "0");
			String fecha = String.format("to_date('%2s/%2s/2015', 'dd/mm/yyyy')", numeroDia, numeroMes);
			cond.setCondicion(String.format(unaCondicion.getCondicion(), fecha, fecha));
			listaCondiciones.add(cond);
		}
		return listaCondiciones;
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
