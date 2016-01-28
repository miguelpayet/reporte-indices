package main;

import config.ConfiguracionException;
import config.ConfiguracionProcesadorCorreos;
import lector.CuentaCorreo;
import lector.LectorCorreos;
import lector.LectorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class ProcesadorCorreos {

	private static ConfiguracionProcesadorCorreos configuracion;
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(ProcesadorCorreos.class);
	private ArrayList<CuentaCorreo> cuentas;

	public static Logger getLogger() {
		return logger;
	}

	private void init() throws ConfiguracionException {
		ConfiguracionProcesadorCorreos configuracion = new ConfiguracionProcesadorCorreos();
		cuentas = configuracion.leerCorreos();
	}

	public void lanzarLectores() {
		for (CuentaCorreo cuenta : cuentas) {
			try {
				LectorCorreos lector = new LectorCorreos(cuenta);
				lector.leerInbox();
			} catch (LectorException e) {
				ProcesadorCorreos.logger.info("error al procesar lector: " + e.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		logger.info("inicio");
		ProcesadorCorreos pc = new ProcesadorCorreos();
		try {
			pc.init();
		} catch (ConfiguracionException e) {
			ProcesadorCorreos.logger.info("error: " + e.getMessage());
		}
		pc.lanzarLectores();
		logger.info("final");
	}

}
