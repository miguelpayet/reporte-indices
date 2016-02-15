package main;

import adaptador.CuentaDatabase;
import config.ConfiguracionException;
import config.Configuracion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class ConsultadorGeneral {

	private static Configuracion configuracion = null;
	private static final Logger logger = LogManager.getLogger(ConsultadorGeneral.class);
	private ArrayList<CuentaDatabase> cuentas;

	public static Configuracion getConfiguracion() throws ConfiguracionException {
		if (configuracion == null) {
			configuracion = new Configuracion();
		}
		return configuracion;
	}

	public static Logger getLogger() {
		return logger;
	}

	private void init() throws ConfiguracionException {
		cuentas = ConsultadorGeneral.getConfiguracion().leerCuentas();
	}

	public void lanzarCuentas() {
		for (CuentaDatabase cuenta : cuentas) {
			try {
				ConsultadorGeneral lector = new ConsultadorGeneral(cuenta, adaptador);
				lector.leerInbox();
			} catch (LectorException e) {
				ConsultadorGeneral.logger.info("error al procesar lector: " + e.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		logger.info("inicio");
		ConsultadorGeneral pc = new ConsultadorGeneral();
		try {
			pc.init();
		} catch (ConfiguracionException e) {
			ConsultadorGeneral.logger.info("error: " + e.getMessage());
			e.printStackTrace();
		}
		pc.lanzarLectores();
		logger.info("final");
	}

}
