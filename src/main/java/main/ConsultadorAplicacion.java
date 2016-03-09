package main;

import adaptador.CuentaDatabase;
import config.Configuracion;
import config.ConfiguracionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class ConsultadorAplicacion {

	private static Configuracion configuracion = null;
	private static final Logger logger = LogManager.getLogger(ConsultadorAplicacion.class);
	private static ArrayList<CuentaDatabase> cuentas;

	public static Configuracion getConfiguracion() throws ConfiguracionException {
		if (configuracion == null) {
			configuracion = new Configuracion();
		}
		return configuracion;
	}

	public static Logger getLogger() {
		return logger;
	}

	private static void init(String[] args) throws ConfiguracionException {
		cuentas = ConsultadorAplicacion.getConfiguracion().leerCuentas();
	}

	private static void lanzarCuentas() {
		cuentas.forEach(CuentaDatabase::ejecutarConsultas);
	}

	public static void main(String[] args) {
		logger.info("inicio");
		try {
			init(args);
		} catch (ConfiguracionException e) {
			ConsultadorAplicacion.logger.error("error en main: " + e.getMessage());
			e.printStackTrace();
		}
		lanzarCuentas();
		logger.info("final");
	}

}
