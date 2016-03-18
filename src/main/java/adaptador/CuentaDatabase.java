package adaptador;

import config.ConfiguracionException;
import excel.Excel;
import excel.ExcelException;
import main.Consultador;
import main.ConsultadorAplicacion;
import main.ExcepcionConsultador;
import org.apache.poi.ss.usermodel.Sheet;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.*;

public class CuentaDatabase {

	private Consulta consulta;
	private Database db;
	private int filaExcel;
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
			db = new Database(this);
			db.connect();
			List<Condicion> condiciones = consulta.getCondiciones();
			for (Condicion cond : condiciones) {
				try {
					excel = new Excel(ConsultadorAplicacion.getConfiguracion().leerExcel(), cond.getHoja());
					if (cond.getMes() != 0) {
						List<Condicion> condicionesMes = generarCondicionesMes(cond);
						for (Condicion c : condicionesMes) {
							logInicioCondicion(c);
							filaExcel = 0;
							Sheet hojaExcel = excel.getCurrentSheet(c.getHoja());
							ejecutarUnaConsulta(c, hojaExcel);
						}
					} else {
						logInicioCondicion(cond);
						filaExcel = 0;
						Sheet hojaExcel = excel.getCurrentSheet(cond.getHoja());
						ejecutarUnaConsulta(cond, hojaExcel);
					}
				} finally {
					try {
						if (excel != null) {
							ConsultadorAplicacion.getLogger().info(String.format("fin %s", cond.getHoja()));
							excel.grabar();
						}
					} catch (ExcelException e) {
						ConsultadorAplicacion.getLogger().error("error al grabar excel: " + e.getMessage());
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
		}
	}

	private void ejecutarUnaConsulta(Condicion cond, Sheet hoja) throws DatabaseException, ExcepcionConsultador {
		Consultador lector = new Consultador(db.getConnection(), consulta.getQuery(), cond.getCondicion(), filaExcel);
		lector.setExcel(hoja);
		lector.run();
		filaExcel = lector.getFilaExcel();
	}

	private String formatearFecha(Date unaFecha, SimpleDateFormat unFormato) {
		String fecha = unFormato.format(unaFecha);
		return String.format("to_date('%s','dd/mm/yyyy')", fecha);
	}

	private List<Condicion> generarCondicionesMes(Condicion unaCondicion) {
		HashMap<Integer, TreeSet<Date>> semanas = new HashMap<>();
		List<Condicion> listaCondiciones = new ArrayList<>();
		YearMonth mes = YearMonth.of(2015, unaCondicion.getMes());
		Calendar cal = new GregorianCalendar();
		cal.setMinimalDaysInFirstWeek(1);
		int nMes = mes.getMonthValue() - 1;
		for (int dia = 1; dia <= mes.lengthOfMonth(); dia++) {
			cal.set(2015, nMes, dia);
			TreeSet<Date> semana = semanas.get(cal.get(Calendar.WEEK_OF_MONTH));
			if (semana == null) {
				semana = new TreeSet<>();
				semanas.put(cal.get(Calendar.WEEK_OF_MONTH), semana);
			}
			semana.add(cal.getTime());
		}
		SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
		for (Map.Entry<Integer, TreeSet<Date>> semanaEntry : semanas.entrySet()) {
			TreeSet<Date> semana = semanaEntry.getValue();
			Condicion cond = new Condicion();
			cond.setHoja(String.format("%s-%s", unaCondicion.getHoja(), semanaEntry.getKey().toString()));
			String diaInicial = formatearFecha(semana.first(), formateador);
			String diaFinal = formatearFecha(semana.last(), formateador);
			cond.setCondicion(String.format(unaCondicion.getCondicion(), diaInicial, diaFinal));
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

	private void logInicioCondicion(Condicion cond) {
		ConsultadorAplicacion.getLogger().info(String.format("hoja: %s - condicion: %s", cond.getHoja(), cond.getCondicion()));
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
